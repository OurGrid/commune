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

public abstract class TransferHandle implements Serializable, Comparable<TransferHandle> {

	
	private static final long serialVersionUID = 1L;

	private final Long id;
	
	private final String logicalFileName;
	private final String description;
	private final long fileSize;

	private File localFile;
	private boolean receiveProgressUpdate; //TODO synchronize
	private long inactivityTimeout; //TODO synchronize

	public TransferHandle(Long id, String logicalFileName, long fileSize, String description) {
		this.id = id;
		this.logicalFileName = logicalFileName;
		this.description = description;
		this.fileSize = fileSize;
	}

	public Long getId() {
		return id;
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
		return id.hashCode();
	}

	@Override
	public boolean equals( Object obj ) {
		if ( this == obj )
			return true;
		if ( !(obj instanceof TransferHandle) )
			return false;
		final TransferHandle other = (TransferHandle) obj;
		return this.id.equals(other.id);
	}

	@Override
	public String toString() {
		return String.valueOf( id );
	}
	
	protected static Long randomID() {
		return Long.valueOf((long) (Math.random() * Long.MAX_VALUE));
	}
	
	public abstract ContainerID getOppositeID();

	public String getLogicalFileName() {
		return logicalFileName;
	}

	public void setLocalFile(File localFile) {
		this.localFile = localFile;
	}

	public File getLocalFile() {
		return localFile;
	}

	public long getFileSize() {
		return fileSize;
	} 
	
	public int compareTo(TransferHandle h) {
		return this.id.compareTo(h.id);
	}
}