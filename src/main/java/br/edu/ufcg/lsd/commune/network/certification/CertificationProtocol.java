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

import java.security.GeneralSecurityException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import sun.security.provider.certpath.X509CertPath;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.message.Message;
import br.edu.ufcg.lsd.commune.network.CommuneNetwork;
import br.edu.ufcg.lsd.commune.network.DiscardMessageException;
import br.edu.ufcg.lsd.commune.network.Protocol;
import br.edu.ufcg.lsd.commune.network.ProtocolException;

@SuppressWarnings("restriction")
public class CertificationProtocol extends Protocol {

	protected final X509CertPath myCertificatePath;
	private final Map<ContainerID, X509CertPath> certificateCache;
	
	/**
	 * @param communeNetwork
	 * @param context 
	 * @throws ProtocolException 
	 */
	public CertificationProtocol(CommuneNetwork communeNetwork, 
			X509CertPath certPath) {
		
		super(communeNetwork);
		this.myCertificatePath = certPath;
		this.certificateCache = new HashMap<ContainerID, X509CertPath>();
	}

	@Override
	protected void onSend(Message message) throws ProtocolException {
		if (message == null) {
			throw new DiscardMessageException();
		}
		
		if (message.getSequence() != null && message.getSequence().equals(0L)) {
			message.setSenderCertificatePath(myCertificatePath);
		}
	}

	@Override
	protected void onReceive(Message message) throws ProtocolException {
		if (message == null) {
			throw new DiscardMessageException();
		}
		
		X509CertPath senderCertificateChain = message.getSenderCertificatePath();
		
		if (senderCertificateChain == null) {
			
			X509CertPath certificateChain = certificateCache.get(
					message.getSource().getContainerID());
			
			if (certificateChain == null) {
				throw new DiscardMessageException();
			}
			
			message.setSenderCertificatePath(certificateChain);
			
		} else {
			
			if (!isChainConsistent(senderCertificateChain)) {
				throw new DiscardMessageException();
			}

			certificateCache.put(message.getSource().getContainerID(), 
					senderCertificateChain);
		}
	}

	/**
	 * Works down the chain, for every certificate in the chain,
	 * verifies that the subject of the certificate is the issuer of the
	 * next certificate in the chain.
	 * 
	 * @param senderCertificateChain
	 * @return
	 */
	private boolean isChainConsistent(X509CertPath senderCertificatePath) {
		
		X509Certificate[] senderCertificateChain = senderCertificatePath.getCertificates().toArray(
				new X509Certificate[]{});
		
		Principal principalLast = null;
		for (int i = 0; i < senderCertificateChain.length; i++)	{
			
			X509Certificate x509certificate = senderCertificateChain[i];
			Principal principalIssuer = x509certificate.getIssuerDN();
			Principal principalSubject = x509certificate.getSubjectDN();
			if (principalLast != null) {
				if (principalIssuer.equals(principalLast)){
					try	{
						PublicKey publickey = senderCertificateChain[i - 1].getPublicKey();
						senderCertificateChain[i].verify(publickey);
					} catch (GeneralSecurityException generalsecurityexception){
						return false;
					}
				} else {
					return false;
				}
			}
			principalLast = principalSubject;
		}

		return true;
	}

}
