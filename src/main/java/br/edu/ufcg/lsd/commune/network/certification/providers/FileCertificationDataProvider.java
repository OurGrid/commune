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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;

import sun.security.provider.certpath.X509CertPath;
import br.edu.ufcg.lsd.commune.context.ContainerContextUtils;
import br.edu.ufcg.lsd.commune.context.ModuleContext;

/**
 *
 */
public class FileCertificationDataProvider implements CertificationDataProvider {

	private X509CertPath myCertPath;
	private ModuleContext context;

	public FileCertificationDataProvider(ModuleContext context) throws CertificateException, IOException {
		this.context = context;
		this.myCertPath = generateMyCertPath();
	}

	private X509CertPath generateMyCertPath() throws CertificateException, IOException {

		String myCertificateFilePath = context.getProperty(
				FileCertificationProperties.PROP_MYCERTIFICATE_FILEPATH);

		File certFile = new File(ContainerContextUtils.normalizeFilePath(context, myCertificateFilePath));
		return generateMyCertPath(certFile);
	}

	/* (non-Javadoc)
	 * @see br.edu.ufcg.lsd.commune.network.certification.providers.CertificationDataProvider#getMyCertificateChain()
	 */
	public X509CertPath getMyCertificatePath(){
		return this.myCertPath;
	}

	/**
	 * @param certFile
	 * @return
	 * @throws CertificateException
	 * @throws IOException
	 */
	private X509CertPath generateMyCertPath(File certFile) throws CertificateException, IOException {

		CertPath certPath = generatePKCS7CertPath(certFile);
		if (certPath == null) {
			certPath = generateListCertPath(certFile);
		}
		return (X509CertPath) certPath;
	}

	private CertPath generateListCertPath(File certFile) throws CertificateException, IOException {
		
		CertificateFactory certificateFactory = getCertificateFactory();
		FileInputStream fileInputStream = new FileInputStream(certFile);
		
		CertPath certPath = certificateFactory.generateCertPath(
				new ArrayList<Certificate>(certificateFactory.generateCertificates(
						fileInputStream)));
		
		fileInputStream.close();
		
		return certPath;
	}

	private CertPath generatePKCS7CertPath(File certFile) throws CertificateException, IOException {
		
		CertificateFactory certificateFactory = getCertificateFactory();
		FileInputStream fileInputStream = new FileInputStream(certFile);
		
		try {
			return certificateFactory.generateCertPath(fileInputStream, "PKCS7");
		} catch (Throwable e) {
			return null; 
		} finally {
			fileInputStream.close();
		}
	}
	
	private CertificateFactory getCertificateFactory() throws CertificateException {
		return CertificateFactory.getInstance("X509");
	}
}
