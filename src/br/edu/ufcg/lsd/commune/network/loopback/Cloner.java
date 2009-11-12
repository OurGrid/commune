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
package br.edu.ufcg.lsd.commune.network.loopback;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Cloner {

	@SuppressWarnings("unchecked")
	public static <T> T clone( T objectToBeCloned ) throws CloneNotSupportedException {

		ObjectOutputStream objectOOS = null;
		ObjectInputStream objectOIS = null;

		try {
			ByteArrayOutputStream objectBAOS = new ByteArrayOutputStream();
			objectOOS = new ObjectOutputStream( objectBAOS );
			objectOOS.writeObject( objectToBeCloned );

			ByteArrayInputStream objectBAIS = new ByteArrayInputStream( objectBAOS.toByteArray() );
			objectOIS = new ObjectInputStream( objectBAIS );
			return (T) objectOIS.readObject();

		} catch ( NotSerializableException e ) {
			throw new CloneNotSupportedException( "Class " + e.getMessage() + " is not serializable" );
		} catch ( IOException e ) {
			throw new CloneNotSupportedException( "The given object could not be cloned." + e.getMessage() );
		} catch ( ClassNotFoundException e ) {
			throw new CloneNotSupportedException( "The given object could not be cloned." + e.getMessage() );
		} finally {

			if ( objectOOS != null ) {
				try {
					objectOOS.close();
				} catch ( IOException e1 ) {}
			}

			if ( objectOIS != null ) {
				try {
					objectOIS.close();
				} catch ( IOException e1 ) {}
			}
		}
	}
}