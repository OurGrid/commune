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
package br.edu.ufcg.lsd.commune.container.servicemanager.dao;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;

/**
 * Super type of all Data access objects
 */
public abstract class DAO {
	
	private final Module application;
	
	public DAO(Module container) {
		this.application = container;
	}

	protected CommuneLogger getLog() {
		return application.getLogger(getClass());
	}
	
	protected <T extends DAO> T getDAO(Class<T> clazz) {
		return application.getDAO(clazz);
	}
	
	/**
	 * Serialize an object in a file
	 * @param filePath Path to the file where the object will be serialized
	 * @param toBeSaved Object that will be serialized
	 * @param objectTitle A title of the object to be used in error messages
	 */
	protected void serializeObject(String filePath, Object toBeSaved, String objectTitle) throws IOException{
		
		ObjectOutputStream out = null;

		try {
			out = new ObjectOutputStream( new FileOutputStream( filePath ) );
			out.writeObject( toBeSaved );

		} catch ( IOException e ) {
			throw e;
			
		} finally {
			
			if ( out != null ) {
				out.close();
			}
		}
	}
	
	/**
	 * Load an object from a file
	 * @param filePath Path to the file where the object is serialized
	 * @param objectTitle A title of the object to be used in error messages
	 * @return The object loaded from file or null if a problem occur
	 */
	protected Object loadObject(String filePath, String objectTitle) throws IOException, ClassNotFoundException {
		
		ObjectInputStream in = null;
		
		try {
			in = new ObjectInputStream( new FileInputStream( filePath ) );
			Object loadedObject = in.readObject();

			return loadedObject;
			
		} catch ( IOException e ) {
			throw e;
			
		} catch (ClassNotFoundException e) {
			throw e;
			
		} finally {
			
			if (in != null) {
				in.close();
			}
		}
	}

}