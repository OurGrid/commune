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

import java.io.Serializable;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;

/**
 *
 */
public class CertificateCRLPair implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7449699622310032029L;

	private final X509Certificate certificate;
	
	private final X509CRL crl;

	public CertificateCRLPair(X509Certificate certificate, 
			X509CRL crl) {
		
		this.certificate = certificate;
		this.crl = crl;
		
	}
	
	public CertificateCRLPair(X509Certificate certificate) {
		this(certificate, null);
	}


	/**
	 * @return the certificate
	 */
	public X509Certificate getCertificate() {
		return certificate;
	}

	/**
	 * @return the crl
	 */
	public X509CRL getCRL() {
		return crl;
	}
	
}