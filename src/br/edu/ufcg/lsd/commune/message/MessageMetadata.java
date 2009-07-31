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
package br.edu.ufcg.lsd.commune.message;

import java.io.Serializable;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class MessageMetadata implements Serializable {

	public static final long serialVersionUID = 1L;

    private  ConcurrentHashMap<String, Serializable> properties;
    
    public MessageMetadata() {
    	properties = new ConcurrentHashMap<String, Serializable>();
    }
    
    public Serializable getProperty(String name) {
        return properties.get(name);
    }

    public void setProperty(String name, Serializable value) {
        properties.put(name, value);
    }

    public void deleteProperty(String name) {
        properties.remove(name);
    }

    public Iterator<String> getPropertyNames() {
        return properties.keySet().iterator();
    }

    @Override
    public boolean equals(Object o) {
    	if (o instanceof MessageMetadata) {
    		MessageMetadata oMetadata = (MessageMetadata) o;
    		synchronized (this) {
        		return properties.equals(oMetadata.properties);				
			}
    	} else {
    		return false;
    	}
    }

    @Override
    public int hashCode() {
    	synchronized (this) {
        	return properties.hashCode();			
		}
    }
    
    @Override
    public String toString() {
    	synchronized (this) {
    		return this.properties.toString();
    	}
    }
}