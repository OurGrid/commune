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
package br.edu.ufcg.lsd.commune.network.signature;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import br.edu.ufcg.lsd.commune.message.Message;
import br.edu.ufcg.lsd.commune.network.CommuneNetwork;
import br.edu.ufcg.lsd.commune.network.DiscardMessageException;
import br.edu.ufcg.lsd.commune.network.Protocol;

public class SignatureProtocol extends Protocol {

	private static final transient org.apache.log4j.Logger LOG = 
		org.apache.log4j.Logger.getLogger( SignatureProtocol.class );
	
	private String privateKey;
	
	public SignatureProtocol(CommuneNetwork communeNetwork, String privateKey) {
		super(communeNetwork);
		this.privateKey = privateKey;
	}


	@Override
	protected synchronized void onReceive(Message message) throws DiscardMessageException {
		if (message == null) {
			throw new DiscardMessageException();
		}

		if (message.getSequence() != null && message.getSequence().equals(0L) && !verifySignature(message)) {
			LOG.debug("Signature of message: " + message + " does not match");
			throw new DiscardMessageException();
		}
	}

	@Override
	protected synchronized void onSend(Message message) throws DiscardMessageException {
		if (message == null) {
			throw new DiscardMessageException();
		}
		
		if (message.getSequence() != null && message.getSequence().equals(0L)) {
			signEvent(message);
		}
	}
	
	public boolean verifySignature(Message message) {
		
		byte[] binarySignature = message.getSignature();
		String senderPubKeyStr = message.getSource().getPublicKey();
		
		if (binarySignature == null || senderPubKeyStr == null) {
			return false;
		}
		
		try {
			
			PublicKey publicKey = decodePublicKey(senderPubKeyStr);
			Signature signature = Signature.getInstance(SignatureConstants.SIGN_ALGORITHM);
			signature.initVerify(publicKey);
			byte[ ] messageSignableData = getMessageSignableData(message);
			
			signature.update(messageSignableData);
			boolean verify = signature.verify(binarySignature);
			return verify;
		} catch (NoSuchAlgorithmException e) {
			LOG.warn( e );
		} catch (InvalidKeySpecException e) {
			LOG.warn( e );
		} catch (InvalidKeyException e) {
			LOG.warn( e );
		} catch (SignatureException e) {
			LOG.debug( e );
		}
		
		return false;
	}
	
	private byte[] getMessageSignableData(Message message) {
		String source =  message.getSource().getContainerID().toString();
		String destination = message.getDestination().getContainerID().toString();
		String functionName = message.getFunctionName();
		Class<?>[ ] paramTypes = message.getParameterTypes();
		Object[ ] parameters = message.getParameterValues();
		Object sessionNumber = message.getSession();
		Object connSeq = message.getSequence();
		
		byte[] result = concat(
				getPropertyData(source),
				getPropertyData(destination),
				getPropertyData(functionName),
				getPropertyData(paramTypes),
				getPropertyData(parameters),
				getPropertyData(sessionNumber),
				getPropertyData(connSeq));
		
		return result;
	}
	
	private byte[] concat(byte[]... byteArrays) {
		
		int length = 0;
		
		for (int i = 0; i < byteArrays.length; i++) {
			length += byteArrays[i].length;
		}
		
		byte[] result = new byte[length];
		int k = 0;
		for (int i = 0; i < byteArrays.length; i++) {
			for (int j = 0; j < byteArrays[i].length; j++) {
				result[k++] = byteArrays[i][j];
			}
		}
		
		return result;
	}
	
	public byte[] getEventSignature(String privateKeyStr, Message message) {
		byte[] strArray = getMessageSignableData(message);
		try {
			PrivateKey privateKey = decodePrivateKey(privateKeyStr);
			Signature signature = Signature.getInstance(SignatureConstants.SIGN_ALGORITHM);
			signature.initSign(privateKey);
			signature.update(strArray);
			return signature.sign();
		} catch (NoSuchAlgorithmException e) {
			LOG.warn( e );
		} catch (InvalidKeyException e) {
			LOG.warn( e );
		} catch (SignatureException e) {
			LOG.debug( e );
		} catch (InvalidKeySpecException e) {
			LOG.debug( e );
		}
		return null;
	}
	
	private byte[] getPropertyData(Object obj) {
		
		if (obj == null || "".equals( obj )) {
			return new byte[]{};
		}
		
		ObjectOutputStream objectOOS = null;
		byte [] serialized = new byte[]{};
		
		try {
			
			ByteArrayOutputStream objectBAOS = new ByteArrayOutputStream();
			objectOOS = new ObjectOutputStream(objectBAOS);
			objectOOS.writeObject(obj);
			serialized = objectBAOS.toByteArray();

		} catch ( IOException e ) {
			throw new IllegalArgumentException("Object is not serializable", e);
		} finally {
			if ( objectOOS != null ) {
				try {
					objectOOS.close();
				} catch ( IOException e1 ) {}
			}
		}
		
		return serialized;
	}
	
	public void signEvent(Message message) {
		message.setSignature(getEventSignature(privateKey, message));
	}
	
	private PrivateKey decodePrivateKey(String privKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
		return Util.decodePrivateKey(privKeyStr);
	}
	
	private PublicKey decodePublicKey(String pubKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
		return Util.decodePublicKey(pubKeyStr);
	}

}
