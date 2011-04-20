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

import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;

public class Util {
	
	private static Base64 base64 = new Base64();
	
	public static KeyPair generateKeyPair() {
		KeyPairGenerator keyGen;
		try {
			keyGen = KeyPairGenerator.getInstance( SignatureConstants.KEY_GEN_ALGORITHM );
		} catch (NoSuchAlgorithmException e) {
			//We're assuming that we are always using a valid algorithm
			throw new CommuneRuntimeException(e);
		}
		keyGen.initialize( SignatureConstants.KEYSIZE );
		return keyGen.genKeyPair();
	}
	
	public static PublicKey decodePublicKey(String pubKeyStr) throws InvalidKeySpecException {
		byte[] binaryArray = decodeStringOnBase64(pubKeyStr);
		KeyFactory keyFactory;
		try {
			keyFactory = KeyFactory.getInstance(SignatureConstants.KEY_GEN_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			//We're assuming that we are always instantiating a valid algorithm
			throw new CommuneRuntimeException(e);
		}
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(binaryArray);
        return keyFactory.generatePublic(publicKeySpec);
	}
	
	public static PrivateKey decodePrivateKey(String privKeyStr) throws InvalidKeySpecException {
		byte[] binaryArray = decodeStringOnBase64(privKeyStr);
		KeyFactory keyFactory;
		try {
			keyFactory = KeyFactory.getInstance(SignatureConstants.KEY_GEN_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			//We're assuming that we are always instantiating a valid algorithm
			throw new CommuneRuntimeException(e);
		}
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(binaryArray);
        return keyFactory.generatePrivate(privateKeySpec);
	}
	
	public static byte[] decodeStringOnBase64(String str) {
		byte[] encodedBase64 = str.getBytes();
		return decodeArrayOnBase64(encodedBase64);
	}
	
	public static byte[] decodeArrayOnBase64(byte[] encodedBase64) {
		return base64.decode(encodedBase64);
	}
	
	public static String encodeArrayToBase64String(byte[] binaryArray) {
		try {
			return new String(encodeArrayToBase64(binaryArray), "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new CommuneRuntimeException(e);
		}
	}
	
	public static byte[] encodeArrayToBase64(byte[] binaryArray) {
		return base64.encode(binaryArray);
	}
	
	public static String encodeArrayToHexadecimalString(byte[] binaryArray) {
		final int radix = 16;
		String result = "";
		for(byte b : binaryArray) {
			int unsignedByte = b + 128; 
			result += Integer.toString(unsignedByte, radix);
		}
		return result;
	}

	public static void main(String[] args) {
		KeyPair keyPair = generateKeyPair();
		System.out.println(encodeArrayToBase64String(keyPair.getPrivate().getEncoded()));
		System.out.println(encodeArrayToBase64String(keyPair.getPublic().getEncoded()));
	}
}