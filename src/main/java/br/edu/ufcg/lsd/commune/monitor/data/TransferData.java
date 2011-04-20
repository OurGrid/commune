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
package br.edu.ufcg.lsd.commune.monitor.data;

import java.io.File;

import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;

import br.edu.ufcg.lsd.commune.identification.DeploymentID;

public class TransferData {
	
	private DeploymentID destinationID;
	
	private DeploymentID listenerID;
	
	private Status status;
	
	private long id;
	
	private File file;
	
	private long inactivityTimeout;
	
	private boolean receiveProgressUpdate;
	
	private int queuePosition;
	
	private boolean isIncoming;
	
	public TransferData(DeploymentID destinationID, DeploymentID listenerID, Status status, File file, long id,
			long inactivityTimeout, int queuePosition, boolean receiveProgressUpdate, boolean isIncoming) {
		
		this.destinationID = destinationID;
		this.listenerID = listenerID;
		this.status = status;
		this.file = file;
		this.id = id;
		this.inactivityTimeout = inactivityTimeout;
		this.queuePosition = queuePosition;
		this.receiveProgressUpdate = receiveProgressUpdate;
		this.isIncoming = isIncoming;
	}

	public DeploymentID getDestinationID() {
		return destinationID;
	}

	public void setDestinationID(DeploymentID destinationID) {
		this.destinationID = destinationID;
	}

	public DeploymentID getListenerID() {
		return listenerID;
	}

	public void setListenerID(DeploymentID listenerID) {
		this.listenerID = listenerID;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public long getInactivityTimeout() {
		return inactivityTimeout;
	}

	public void setInactivityTimeout(long inactivityTimeout) {
		this.inactivityTimeout = inactivityTimeout;
	}

	public boolean isReceiveProgressUpdate() {
		return receiveProgressUpdate;
	}

	public void setReceiveProgressUpdate(boolean receiveProgressUpdate) {
		this.receiveProgressUpdate = receiveProgressUpdate;
	}

	public int getQueuePosition() {
		return queuePosition;
	}

	public void setQueuePosition(int queuePosition) {
		this.queuePosition = queuePosition;
	}

	public boolean isIncoming() {
		return isIncoming;
	}

	public void setIncoming(boolean isIncoming) {
		this.isIncoming = isIncoming;
	}
}
