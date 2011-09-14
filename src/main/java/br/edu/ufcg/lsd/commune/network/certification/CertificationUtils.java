/*
 * Copyright (C) 2008 Universidade Federal de Campina Grande
 *  
 * This file is part of Commune. 
 *
 * Commune is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package br.edu.ufcg.lsd.commune.network.certification;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import sun.security.provider.certpath.X509CertPath;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLoggerFactory;
import br.edu.ufcg.lsd.commune.network.ProtocolException;

/**
 */
@SuppressWarnings("restriction")
public class CertificationUtils {

	private static final String CERT_EXTENSION = ".cer";
	private static final String CRL_EXTENSION = ".crl";
	
	private static final CommuneLogger LOG = CommuneLoggerFactory.getInstance().gimmeALogger(
			CertificationUtils.class);
	
	private CertificationUtils() {}
	
	/**
	 * @param senderCertificateChain
	 * @return
	 */
	public static boolean isCertificateValid(X509CertPath senderCertificateChain) {
		
		for (X509Certificate certificate : senderCertificateChain.getCertificates()) {
			if (!isCertificateValid(certificate)) {
				return false;
			}
		}
		
		return true;
	}

	
	/**
	 * @param certificate2
	 * @return
	 * @throws ProtocolException 
	 */
	public static boolean isCertPathIssuedByCA(
			X509CertPath certPath, Collection<CertificateCRLPair> cAsData) {
		
		for (X509Certificate certificate : certPath.getCertificates()) {
			if (isCertificateIssuedByCA(certificate, cAsData)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * @param certificate2
	 * @return
	 * @throws ProtocolException 
	 */
	public static boolean isCertPathIssuedByCA(
			X509CertPath senderCertPath, List<X509Certificate> cAsCertificates) {
		
		for (X509Certificate certificate : senderCertPath.getCertificates()) {
			if (isCertificateIssuedByCACertificate(certificate, cAsCertificates)) {
				return true;
			}
		}
		
		return false;
	}
	
	
	/**
	 * Checks whether the current date is within the certificate's validity period. 
	 * @param senderCertificate
	 * @return
	 */
	private static boolean isCertificateValid(X509Certificate senderCertificate) {
		
		try {
			senderCertificate.checkValidity();
		} catch (GeneralSecurityException e) {
			return false;
		} 
		
		return true;
	}

	/**
	 * Checks whether the given certificate is on this CRL.
	 * If this CRL is <code>null</code>, it is assumed that the certificate is not revoked.
	 * @param senderCertificate
	 * @return
	 */
	private static boolean isCerticateRevoked(X509Certificate senderCertificate, X509CRL crl) {
		if (crl == null) {
			return false;
		}
		return crl.isRevoked(senderCertificate);
	}
	
	/**
	 * Checks whether the certificate is issued by a CA and  
	 * not revoked on its correspondent CRL 
	 * @param senderCertificate
	 * @param asData 
	 * @return
	 * @throws ProtocolException 
	 */
	private static boolean isCertificateIssuedByCA(X509Certificate senderCertificate, 
			Collection<CertificateCRLPair> cAsData ) {
		
		if (cAsData.isEmpty()) {
			return true;
		}
		
		LOG.debug("Checking senderCertificate: " +  getCertificateLogString(senderCertificate));
		
		for (CertificateCRLPair cAPair : cAsData) {
			
			X509Certificate cACertificate = cAPair.getCertificate();
			
			LOG.debug("Checking senderCertificate against CA: " +  getCertificateLogString(cACertificate));
			
			if (senderCertificate.getIssuerX500Principal().equals(
					cACertificate.getSubjectX500Principal()) || 
					senderCertificate.getSubjectX500Principal().equals(
							cACertificate.getSubjectX500Principal())) {
				
				try {
					senderCertificate.verify(cACertificate.getPublicKey());
					
					X509CRL crl = cAPair.getCRL();
					if (!isCerticateRevoked(senderCertificate, crl)) {
						LOG.debug("SenderCertificate is issued by " + getCertificateLogString(cACertificate));
						return true;
					}
					
				} catch (GeneralSecurityException e) {}
				
			}
		}
		
		LOG.debug("SenderCertificate is not issued");
		return false;
	}

	private static String getCertificateLogString(X509Certificate cACertificate) {
		String string = "[Subject: " + cACertificate.getSubjectX500Principal().getName() + ", " +
				"Issuer: " + (cACertificate.getIssuerX500Principal() == null ? "null" : cACertificate.getIssuerX500Principal().getName()) + "]";
		return string;
	}
	
	/**
	 * Checks whether the certificate is issued by a CA and  
	 * not revoked on its correspondent CRL 
	 * @param senderCertificate
	 * @param asData 
	 * @return
	 * @throws ProtocolException 
	 */
	private static boolean isCertificateIssuedByCACertificate(X509Certificate senderCertificate, 
			Collection<X509Certificate> cAsData ) {
		
		if (cAsData.isEmpty()) {
			return true;
		}
		
		for (X509Certificate cACertificate : cAsData) {
			if (senderCertificate.getIssuerX500Principal().getName().equals(
					cACertificate.getSubjectX500Principal().getName()) || 
					senderCertificate.getSubjectX500Principal().getName().equals(
							cACertificate.getSubjectX500Principal().getName())) {
				try {
					senderCertificate.verify(cACertificate.getPublicKey());
					return true;
				} catch (GeneralSecurityException e) {}
			}
		}
		
		return false;
	}
	
	public static List<CertificateCRLPair> loadCAsData(String certificatePath) {
		
		List<CertificateCRLPair> cAsData = new ArrayList<CertificateCRLPair>();
		
		Collection<X509CRL> loadedCRLs = loadCRLs(certificatePath);
		Collection<X509Certificate> loadedCertificates = loadCertificates(certificatePath);
		
		for (X509Certificate certificate : loadedCertificates) {
			if (loadedCRLs.isEmpty()) {
				cAsData.add(new CertificateCRLPair(certificate));
			} else {
				for (X509CRL crl : loadedCRLs) {
					if (crl.getIssuerDN().equals(certificate.getIssuerDN())) {
						cAsData.add(new CertificateCRLPair(certificate, crl));
					} else {
						cAsData.add(new CertificateCRLPair(certificate));
					}
				}
			}
			
		}
		
		return cAsData;
	}
	
	public static Collection<X509Certificate> loadCertificates(String certificatePath) {
		
		Collection<X509Certificate> cAsCertificates = new ArrayList<X509Certificate>();
		
		LOG.debug("Loading certificates from directory: " + certificatePath);
		
		for (File certFile : listFiles(CERT_EXTENSION, certificatePath)) {
			
			LOG.debug("Loading certificate from file: " + certFile.getAbsolutePath());
			
			Collection<? extends Certificate> certificates = generateCACertificates(certFile);
			if (certificates != null) {
				for (Certificate certificate : certificates) {
					X509Certificate cACertificate = (X509Certificate) certificate;
					cAsCertificates.add(cACertificate);
				}
			}
			
		}
		
		return cAsCertificates;
	}
	
	public static Collection<X509CRL> loadCRLs(String certificatePath) {
		Collection<X509CRL> cAsCRLs = new ArrayList<X509CRL>();
		
		LOG.debug("Loading CRL from directory: " + certificatePath);
		
		for (File crlFile : listFiles(CRL_EXTENSION, certificatePath)) {
			
			LOG.debug("Loading CRL from file: " + crlFile.getAbsolutePath());
			
			X509CRL x509CRL = generateCRL(crlFile);
			if (x509CRL != null) {
				cAsCRLs.add(x509CRL);
			}
		}
		
		return cAsCRLs;
	}
	
	public static X509Certificate generateCACertificate(File certFile) {
		
		FileInputStream fileInputStream;
		
		try {
			fileInputStream = new FileInputStream(certFile);
		} catch (FileNotFoundException e1) {
			return null;
		}
		
		try {
			return (X509Certificate) getCertificateFactory().generateCertificate(
					fileInputStream);
		} catch (Throwable e) {
			return null;
		} finally {
			
			try {
				fileInputStream.close();
			} catch (IOException e) {
				return null;
			}
			
		}
	}
	
	public static Collection<? extends Certificate> generateCACertificates(File certFile) {
		
		FileInputStream fileInputStream;
		
		try {
			fileInputStream = new FileInputStream(certFile);
		} catch (FileNotFoundException e1) {
			return null;
		}
		
		try {
			Collection<? extends Certificate> generateCertificate = getCertificateFactory().generateCertificates(
					fileInputStream);
			
			LOG.debug("Certificate sucessfully loaded from file: " + certFile);
			
			return generateCertificate;
		} catch (Throwable e) {
			
			LOG.error("Error while loading certificate from file: " + certFile, e);
			
			return null;
		} finally {
			
			try {
				fileInputStream.close();
			} catch (IOException e) {
				return null;
			}
			
		}
	}
	
	private static CertificateFactory getCertificateFactory() throws CertificateException {
		return CertificateFactory.getInstance("X509");
	}
	
	private static File[] listFiles(final String extension, String certificatePath) {
		
		if (certificatePath == null) {
			return new File[]{};
		}
		
		File[] listFiles = new File(certificatePath).listFiles(new FileFilter() {

			public boolean accept(File pathname) {
				if (!pathname.getName().endsWith(extension)) {
					return false;
				}
				return true;
			}
		});
		
		return listFiles;
	}
	
	public static X509CRL generateCRL(File crlFile) {
		
		FileInputStream fileInputStream;
		
		try {
			fileInputStream = new FileInputStream(crlFile);
		} catch (FileNotFoundException e1) {
			return null;
		}
		
		try {
			return (X509CRL) getCertificateFactory().generateCRL(fileInputStream);
		} catch (Throwable e) {
			return null;
		} finally {
			
			try {
				fileInputStream.close();
			} catch (IOException e) {
				return null;
			}
			
		}
	}
	
	public static String getCertSubjectDN(X509CertPath certPath) {
		return getCertSubjectDN(certPath.getCertificates().get(0));
	}

	public static String getCertSubjectDN(X509Certificate certificate) {
		return certificate.getSubjectX500Principal().getName();
	}
}
