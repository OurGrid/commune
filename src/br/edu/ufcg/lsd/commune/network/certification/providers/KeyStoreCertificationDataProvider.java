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
package br.edu.ufcg.lsd.commune.network.certification.providers;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import sun.security.provider.certpath.X509CertPath;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.network.ProtocolException;

/**
 * Provides certification data using {@link KeyStore}
 * It does not support CRL, which can not be stored in {@link KeyStore}
 */
public class KeyStoreCertificationDataProvider implements
		CertificationDataProvider {

	private static final String KEYSTORE_TYPE = "JKS";
	
	private final ModuleContext context;

	private X509CertPath myCertPath;
	
	public KeyStoreCertificationDataProvider(ModuleContext context) 
	throws GeneralSecurityException, IOException {
		this.context = context;
		init();
	}

	private void init() throws GeneralSecurityException, IOException {
		myCertPath = getMyCertificatePath(loadKeyStore());
	}

	/**
	 * @param keyStore 
	 * @return
	 * @throws GeneralSecurityException 
	 */
	public X509CertPath getMyCertificatePath(KeyStore keyStore) throws GeneralSecurityException {
		
		Certificate[] certificateChain = keyStore.getCertificateChain(
					keyStore.aliases().nextElement());
		
		verifyX509Certificate(certificateChain);
		
		X509CertPath certPath = (X509CertPath) CertificateFactory.getInstance("X509").generateCertPath(
					Arrays.asList(certificateChain));
		
		return certPath;
	}

	/**
	 * @param certificateChain
	 * @throws GeneralSecurityException 
	 */
	private void verifyX509Certificate(Certificate[] certificateChain) throws GeneralSecurityException {
		for (Certificate certificate : certificateChain) {
			verifyX509Certificate(certificate);
		}
	}

	/**
	 * @param certificate
	 * @throws GeneralSecurityException 
	 */
	private void verifyX509Certificate(Certificate certificate) throws GeneralSecurityException
	 {
		if (!(certificate instanceof X509Certificate)) {
			throw new GeneralSecurityException("Loaded certificate is not a X.509 certificate.");
		}
	}
	
	/**
	 * @return 
	 * @throws IOException 
	 * @throws GeneralSecurityException 
	 * @throws ProtocolException
	 */
	private KeyStore loadKeyStore() throws GeneralSecurityException, IOException {
		
		char[] keyStorePass = context.getProperty(
				KeyStoreCertificationProperties.PROP_KEYSTORE_PASS).toCharArray();
		
		return loadKeyStore(context.getProperty(
				KeyStoreCertificationProperties.PROP_MY_KEYSTORE_PATH), keyStorePass);
	}
	
	/**
	 * @param keyStorePath 
	 * @param keyStore 
	 * @throws ProtocolException
	 * @throws KeyStoreException 
	 * @throws IOException 
	 * @throws CertificateException 
	 * @throws NoSuchAlgorithmException 
	 */
	private KeyStore loadKeyStore(String keyStorePath, char[] password) 
	throws GeneralSecurityException, IOException {

		KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);		

		FileInputStream fis = new FileInputStream(keyStorePath);
		keyStore.load(fis, password);
		
		return keyStore;

	}

	public X509CertPath getMyCertificatePath() {
		return myCertPath;
	}
}
