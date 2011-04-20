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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;


public class PropertiesFileParser implements ContextParser {

	private String fileName;

	public PropertiesFileParser(String fileName) {
		this.fileName = fileName;
	}
	
	public Map<Object, Object> parseContext() {
		Properties properties = new Properties();
		
				/** Get an abstraction for the properties file */
		File propertiesFile = new File( fileName );

		if (!propertiesFile.exists()) {
			return getDefaultProperties(propertiesFile);
		}
		
		/* load the properties file, if it exists */
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream( propertiesFile );
		} catch (FileNotFoundException e) {
			throw new CommuneRuntimeException("Context couldn't be loaded. " + fileName + 
					" does not exist. Please check this file. Exception: " + e.getMessage());
		}
		try {
			properties.load( fileInputStream );
		} catch ( IOException e ) {
			throw new CommuneRuntimeException("Context couldn't be loaded. " + fileName + 
					" is corrupted. Please check this file. Exception: " + e.getMessage());
		} catch (IllegalArgumentException iae ) {
			throw new CommuneRuntimeException("Context couldn't be loaded. " + fileName + 
					" isn't a properties file. Please use correct file format.");
		} finally {
			try {
				fileInputStream.close();
			} catch (IOException e) {
				throw new CommuneRuntimeException("Context couldn't be loaded. " + fileName + 
						" is corrupeed. Please check this file. Exception: " + e.getMessage());
			}
		}
		
		Map<Object, Object> responseMap = new LinkedHashMap<Object, Object>();
		
		for (Object key : properties.keySet()) {
			if (!properties.get(key).toString().trim().equals("")) {
				responseMap.put(key, properties.get(key));
			}
		}
		
		return responseMap;
	}

	private Map<Object, Object> getDefaultProperties(File propertiesFile) {
		
		Properties properties = new Properties();
		
		properties.putAll(new DefaultContextFactory().getDefaultProperties());
		try {
			FileUtils.touch(propertiesFile);
			
			FileOutputStream fileOutputStream = new FileOutputStream(propertiesFile);
			properties.store(fileOutputStream, null);
			fileOutputStream.close();
			
		} catch (Exception e) {
			//TODO log
			throw new CommuneRuntimeException("File could not be created: " + fileName);
		} 
		return properties;
	}

}
