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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import br.edu.ufcg.lsd.commune.container.ContainerUtils;
import br.edu.ufcg.lsd.commune.container.InvalidContextPropertyException;
import br.edu.ufcg.lsd.commune.network.xmpp.XMPPProperties;


public class ModuleContext {

	protected Properties currentProperties;

	//TODO YAGNI
	public static final String PREFIX = "commune.";

	private static final String LINE_SEPARATOR = System.getProperty( "line.separator" );

	public ModuleContext(Map<?, ?> properties) {
		currentProperties = new Properties( System.getProperties() );
		currentProperties.putAll(properties);
	}

	public String getProperty( String key ) {

		// Make sure the key is not null
		if (key == null) {
		    throw new NullPointerException("Key should not be null.");
		}
		
		String prop = (String) currentProperties.get( key.trim() );

		if ( prop != null ) {
			prop = prop.trim();
		}

		return prop;
	}

	public final boolean isEnabled( String propertyKey ) {
		String property = getProperty( propertyKey );
		return ContainerUtils.isEnabled(property);
	}

	public double parseDoubleProperty( String propName ) throws InvalidContextPropertyException {

		final String val = getProperty( propName );
		try {
			return Double.parseDouble( val );
		} catch ( NumberFormatException e ) {
			throw new InvalidContextPropertyException( propName, val, e );
		}
	}


	public int parseIntegerProperty( String propName ) throws InvalidContextPropertyException {

		final String val = getProperty( propName );
		try {
			return Integer.parseInt( val );
		} catch ( NumberFormatException e ) {
			throw new InvalidContextPropertyException( propName, val, e );
		}
	}


	public long parseLongProperty( String propName ) throws InvalidContextPropertyException {

		final String val = getProperty( propName );
		try {
			return Long.parseLong( val );
		} catch ( NumberFormatException e ) {
			throw new InvalidContextPropertyException( propName, val, e );
		}
	}
	
	public List<String> parseStringListProperty( String propName ) throws InvalidContextPropertyException {

		final String val = getProperty( propName );
		
		try {
			return ContainerUtils.parseStringList(val);
		} catch ( Exception e ) {
			throw new InvalidContextPropertyException( propName, val, e );
		}
	}

	@Override
	public String toString() {

		StringBuilder conf = new StringBuilder();

		conf.append( "\tXMPP login name: " );
		conf.append( getProperty( XMPPProperties.PROP_USERNAME ) );
		conf.append( LINE_SEPARATOR );

		conf.append( "\tXMPP server name: " );
		conf.append( getProperty( XMPPProperties.PROP_XMPP_SERVERNAME ) );
		conf.append( LINE_SEPARATOR );

		return conf.toString();
	}
	
	public Set<String> getPropertiesNames() {
		Set<String> responseSet = new HashSet<String>();
		for (Object propName : currentProperties.keySet()) {
			responseSet.add((String) propName);
		}
		return responseSet;
	}

	/**
	 * @return the currentProperties
	 */
	public Map<String, String> getProperties() {
		Map<String, String> properties = new HashMap<String, String>();
		
		for (Map.Entry<Object, Object> entry : currentProperties.entrySet()) {
			properties.put((String)entry.getKey(), (String) entry.getValue());
		}
		
		return properties;
	}
	
	
}