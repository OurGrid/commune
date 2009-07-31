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
package br.edu.ufcg.lsd.commune.processor.filetransfer;

import java.io.File;
import java.io.Serializable;

import br.edu.ufcg.lsd.commune.identification.ContainerID;

public abstract class TransferHandle implements Serializable {

	
	private static final long serialVersionUID = 1L;

	
	private final long id;
	private final String localFileName;
	private final String destinationFileName;
	private final String description;
	private long inactivityTimeout; //TODO synchronize
	private boolean receiveProgressUpdate; //TODO synchronize
	private File file; //TODO synchronize


	public TransferHandle(long id, String localFileName, 
			String destinationFileName, String description) {
		this.id = id;
		this.localFileName = localFileName;
		this.destinationFileName = destinationFileName;
	
		this.description = description;
		
		if (localFileName != null && localFileName.length() > 0) {
			this.setFile(new File(localFileName));
		}
	}


	public long getId() {
		return id;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getLocalFileName() {
		return localFileName;
	}
	
	public String getDestinationFileName() {
		return destinationFileName;
	}

	public String getDescription() {
		return description;
	}

	public long getInactivityTimeout() {
		return inactivityTimeout;
	}
	
	public void setInactivityTimeout(long inactivityTimeout) {
		this.inactivityTimeout = inactivityTimeout;
	}

	public void setReceiveProgressUpdate(boolean receiveProgressUpdate) {
		this.receiveProgressUpdate = receiveProgressUpdate;
	}


	public boolean isReceiveProgressUpdate() {
		return receiveProgressUpdate;
	}

	@Override
	public int hashCode() {
		return new Long( id ).hashCode();
	}

	@Override
	public boolean equals( Object obj ) {
		if ( this == obj )
			return true;
		if ( !(obj instanceof TransferHandle) )
			return false;
		final TransferHandle other = (TransferHandle) obj;
		if ( this.id != other.id )
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.valueOf( id );
	}
	
	protected static long randomID() {
		return (long) (Math.random() * Long.MAX_VALUE);
	}
	
	public abstract ContainerID getOppositeID(); 
}