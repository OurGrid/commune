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
package br.edu.ufcg.lsd.commune.context;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.container.InvalidContextPropertyException;
import br.edu.ufcg.lsd.commune.container.logging.LoggerProperties;
import br.edu.ufcg.lsd.commune.monitor.MonitorProperties;
import br.edu.ufcg.lsd.commune.network.signature.SignatureProperties;
import br.edu.ufcg.lsd.commune.network.signature.Util;
import br.edu.ufcg.lsd.commune.network.xmpp.XMPPProperties;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferProperties;
import br.edu.ufcg.lsd.commune.processor.interest.InterestProperties;

public class DefaultContextFactory implements ContextFactory {

	protected ContextParser parser;
	
	public DefaultContextFactory() {}

	public DefaultContextFactory(ContextParser parser) {
		this.parser = parser;
	}

	public ModuleContext createContext() {
		return createContext(true);
	}
	
	public ModuleContext createContext(boolean validate) {
		if (parser == null) {
			throw new NullPointerException("A ContextParser must be set.");
		}
		
		Map<Object, Object> properties = getDefaultProperties();
		
		properties.putAll(parser.parseContext());
		
		if (validate) {
			validate(properties);
		}	
		
		ModuleContext context = new ModuleContext(properties);
		
		return context;
	}

	public Map<Object, Object> getDefaultProperties() {
		
		Map<Object, Object> properties = new HashMap<Object, Object>();
		
		properties.put( XMPPProperties.PROP_XMPP_SERVERPORT, "5222" );
		properties.put( XMPPProperties.PROP_XMPP_SERVER_SECURE_PORT, "5223" );
		properties.put( XMPPProperties.PROP_CHECK_RESOURCE, "no" );
		
		properties.put( InterestProperties.PROP_WAN_HEARTBEAT_DELAY, "30" );
		properties.put( InterestProperties.PROP_WAN_DETECTION_TIME, "120" );
		properties.put( InterestProperties.PROP_LAN_HEARTBEAT_DELAY, "15" );
		properties.put( InterestProperties.PROP_LAN_DETECTION_TIME, "60" );
		properties.put( InterestProperties.PROP_LOCAL_HEARTBEAT_DELAY, "10" );
		properties.put( InterestProperties.PROP_LOCAL_DETECTION_TIME, "40" );
		properties.put( TransferProperties.PROP_FILE_TRANSFER_TIMEOUT, 
				TransferProperties.DEFAULT_FILE_TRANSFER_TIMEOUT );
		properties.put( TransferProperties.PROP_FILE_TRANSFER_MAX_OUT, 
				TransferProperties.DEFAULT_FILE_TRANSFER_MAX_OUT );
		properties.put( TransferProperties.PROP_FILE_TRANSFER_NOTIFY_PROGRESS, 
				TransferProperties.DEFAULT_FILE_TRANSFER_NOTIFY_PROGRESS );
		
		properties.put(XMPPProperties.PROP_USERNAME, getLocalHostName());
		properties.put(XMPPProperties.PROP_PASSWORD, "xmpp-password");
		properties.put(XMPPProperties.PROP_XMPP_SERVERNAME, "xmpp.ourgrid.org");
		
		properties.put(MonitorProperties.PROP_COMMUNE_MONITOR, "no");
		
    	KeyPair keyPair = Util.generateKeyPair(); 
        
        String privateKey = Util.encodeArrayToBase64String(keyPair.getPrivate().getEncoded());
        String publicKey = Util.encodeArrayToBase64String(keyPair.getPublic().getEncoded());
		
		properties.put(SignatureProperties.PROP_PRIVATE_KEY, privateKey);
		properties.put(SignatureProperties.PROP_PUBLIC_KEY, publicKey);
		
		properties.put(LoggerProperties.PROP_SYNC_LOGGER, "no");
		
		return properties;
	}

	public void setParser(ContextParser parser) {
		this.parser = parser;

	}

	private static String getLocalHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return "xmpp-username";
		}
	}
	
	public void validate(Map<Object, Object> properties) {
		String missingProperties = "";
		
		if (properties.get( XMPPProperties.PROP_USERNAME ) == null) {
			missingProperties += XMPPProperties.PROP_USERNAME;
		}
		if (properties.get( XMPPProperties.PROP_PASSWORD ) == null) {
			missingProperties += ", " + XMPPProperties.PROP_PASSWORD;
		}
		if (properties.get( XMPPProperties.PROP_XMPP_SERVERNAME ) == null) {
			missingProperties += ", " + XMPPProperties.PROP_XMPP_SERVERNAME;
		}
		if (properties.get( SignatureProperties.PROP_PRIVATE_KEY ) == null) {
			missingProperties += ", " + SignatureProperties.PROP_PRIVATE_KEY;
		}
		if (properties.get( SignatureProperties.PROP_PUBLIC_KEY ) == null) {
			missingProperties += ", " + SignatureProperties.PROP_PUBLIC_KEY;
		}
		if (properties.get( MonitorProperties.PROP_COMMUNE_MONITOR ) == null ) {
			missingProperties += ", " + MonitorProperties.PROP_COMMUNE_MONITOR;
		}
		
		if (!missingProperties.equals( "" )) {
			throw new CommuneRuntimeException("Context could not be loaded. " +
					"The following mandatory properties are missing: " + missingProperties);
		}
		
		validateProperties(properties);
	}

	private void validateProperties(Map<Object, Object> properties) throws InvalidContextPropertyException {
		int serverPort = parseIntegerProperty(
				XMPPProperties.PROP_XMPP_SERVERPORT, properties);
		if (serverPort <= 0 || serverPort > 65536) {
			throw new InvalidContextPropertyException(
					XMPPProperties.PROP_XMPP_SERVERPORT, Integer
							.toString(serverPort), null);
		}

		int secureServerPort = parseIntegerProperty(XMPPProperties.PROP_XMPP_SERVER_SECURE_PORT, properties);
		if (secureServerPort <= 0 || secureServerPort > 65536) {
			throw new InvalidContextPropertyException(
					XMPPProperties.PROP_XMPP_SERVER_SECURE_PORT, Integer
							.toString(secureServerPort), null);
		}

		int wanHeartBeatDelay = parseIntegerProperty(InterestProperties.PROP_WAN_HEARTBEAT_DELAY, properties);
		if (wanHeartBeatDelay <= 0) {
			throw new InvalidContextPropertyException(
					InterestProperties.PROP_WAN_HEARTBEAT_DELAY, Integer
							.toString(wanHeartBeatDelay), null);
		}

		int wanDetectionTime = parseIntegerProperty(InterestProperties.PROP_WAN_DETECTION_TIME, properties);
		if (wanDetectionTime <= 0) {
			throw new InvalidContextPropertyException(
					InterestProperties.PROP_WAN_DETECTION_TIME, Integer
							.toString(wanDetectionTime), null);
		}

		int lanHeartBeatDelay = parseIntegerProperty(InterestProperties.PROP_LAN_HEARTBEAT_DELAY, properties);
		if (lanHeartBeatDelay <= 0) {
			throw new InvalidContextPropertyException(
					InterestProperties.PROP_LAN_HEARTBEAT_DELAY, Integer
							.toString(lanHeartBeatDelay), null);
		}

		int lanDetectionTime = parseIntegerProperty(InterestProperties.PROP_LAN_DETECTION_TIME, properties);
		if (lanDetectionTime <= 0) {
			throw new InvalidContextPropertyException(
					InterestProperties.PROP_LAN_DETECTION_TIME, Integer
							.toString(lanDetectionTime), null);
		}

		int localHeartBeatDelay = parseIntegerProperty(InterestProperties.PROP_LOCAL_HEARTBEAT_DELAY, properties);
		if (localHeartBeatDelay <= 0) {
			throw new InvalidContextPropertyException(
					InterestProperties.PROP_LOCAL_HEARTBEAT_DELAY, Integer
							.toString(localHeartBeatDelay), null);
		}

		int localDetectionTime = parseIntegerProperty(InterestProperties.PROP_LOCAL_DETECTION_TIME, properties);
		if (localDetectionTime <= 0) {
			throw new InvalidContextPropertyException(
					InterestProperties.PROP_LOCAL_DETECTION_TIME, Integer
							.toString(localDetectionTime), null);
		}

		long ftTimeout = parseLongProperty(TransferProperties.PROP_FILE_TRANSFER_TIMEOUT, properties);
		if (ftTimeout <= 0) {
			throw new InvalidContextPropertyException(
					TransferProperties.PROP_FILE_TRANSFER_TIMEOUT, Long
							.toString(ftTimeout), new Exception(
							"Value cannot be less than 0"));
		}

		int ftMaxSim = parseIntegerProperty(TransferProperties.PROP_FILE_TRANSFER_MAX_OUT, properties);
		if (ftMaxSim < 1) {
			throw new InvalidContextPropertyException(
					TransferProperties.PROP_FILE_TRANSFER_TIMEOUT, Integer
							.toString(ftMaxSim), new Exception(
							"Value must be greater than 0"));
		}

		String password = getProperty(XMPPProperties.PROP_PASSWORD, properties);
		if ("".equals(password)) {
			throw new InvalidContextPropertyException(
					XMPPProperties.PROP_PASSWORD, "", null);
		}

		String userName = getProperty(XMPPProperties.PROP_USERNAME, properties);
		String serverName = getProperty(XMPPProperties.PROP_XMPP_SERVERNAME, properties);

		URI uri = null;

		try {
			uri = new URI("xmpp://" + userName + "@" + serverName);
		} catch (Exception e) {
			throw new InvalidContextPropertyException(
					XMPPProperties.PROP_USERNAME + " and "
							+ XMPPProperties.PROP_XMPP_SERVERNAME, userName + "@"
							+ serverName, e);
		}
		if (!userName.equals(uri.getUserInfo())) {
			throw new InvalidContextPropertyException(
					XMPPProperties.PROP_USERNAME, userName, null);
		}
		if (!serverName.equals(uri.getHost()) || uri.getPort() != -1) {
			throw new InvalidContextPropertyException(
					XMPPProperties.PROP_XMPP_SERVERNAME, serverName, null);
		}
		if (uri.getFragment() != null || uri.getQuery() != null) {
			throw new InvalidContextPropertyException(
					XMPPProperties.PROP_XMPP_SERVERNAME, serverName, null);
		}

		String privKey = getProperty(SignatureProperties.PROP_PRIVATE_KEY, properties);

		try {
			Util.decodePrivateKey(privKey);
		} catch (Exception e) {
			throw new InvalidContextPropertyException(SignatureProperties.PROP_PRIVATE_KEY, privKey, null);
		}

		String pubKey = getProperty(SignatureProperties.PROP_PUBLIC_KEY, properties);

		try {
			Util.decodePublicKey(pubKey);
		} catch (Exception e) {
			throw new InvalidContextPropertyException(SignatureProperties.PROP_PUBLIC_KEY, pubKey, null);
		}
		
	}

	private String getProperty(String propKey, Map<Object, Object> properties) {
		return (String) properties.get(propKey);
	}

	private long parseLongProperty( String propName, Map<Object, Object> properties ) 
		throws InvalidContextPropertyException {

		final String val = getProperty( propName, properties );
		try {
			return Long.parseLong( val );
		} catch ( NumberFormatException e ) {
			throw new InvalidContextPropertyException( propName, val, e );
		}
	}
	
	private int parseIntegerProperty(String propKey, Map<Object, Object> properties) 
		throws InvalidContextPropertyException {
		
		final String val = getProperty( propKey, properties );
		try {
			return Integer.parseInt( val );
		} catch ( NumberFormatException e ) {
			throw new InvalidContextPropertyException( propKey, val, e );
		}
	}
}
