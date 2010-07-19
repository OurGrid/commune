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

import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.message.Message;

public abstract class AbstractTransfer implements Transfer {


	protected final Module module;

	private long previousAmountWrittenValue;
	private long lastDataChangeTime;
	private final long inactivityTimeout;
	private final TransferHandle handle;
	private Status currentStatus;
	private final File file;
	private final long fileSize;
	private final boolean notifyProgress;

	protected final DeploymentID listenerID;


	public AbstractTransfer(Module module, DeploymentID listenerID, long inactivityTimeout, TransferHandle handle, File file, long fileSize,
									boolean notifyProgress) {

		this.module = module;
		this.listenerID = listenerID;
		this.handle = handle;
		this.inactivityTimeout = inactivityTimeout;
		this.file = file;
		this.fileSize = fileSize;
		this.notifyProgress = notifyProgress;
		this.previousAmountWrittenValue = 0L;
	}


	protected boolean checkTimeoutAndNotifyProgress(FileTransfer transfer) {

		long diff = transfer.getAmountWritten() - this.previousAmountWrittenValue;
		long currentTime = System.currentTimeMillis();
		long elapsed = currentTime - lastDataChangeTime;
		if ( diff > 0 ) {
			lastDataChangeTime = currentTime;
			this.previousAmountWrittenValue = transfer.getAmountWritten();
			// compute rate in kbytes per second
			if ( notifyProgress ) {
				double transferRate = diff / 1024D / (elapsed / 1000D);

				TransferProgress transferProgress = new TransferProgress(getHandle(), file.getName(), fileSize, 
						transfer.getStatus(), transfer.getAmountWritten(), transfer.getProgress(), transferRate,
						this.getClass().isAssignableFrom(OutgoingTransfer.class));
				
				Message message = 
					createUpdateTransferProgressMessage(module.getContainerID(), listenerID, transferProgress);
				module.sendMessage(message);
				
			}
			return false;
		}
		return elapsed > inactivityTimeout;
	}


	public static Message createUpdateTransferProgressMessage(ContainerID containerID, DeploymentID listenerID,
			TransferProgress transferProgress) {
		Message message = new Message(containerID, listenerID, "updateTransferProgress"); 
		message.addParameter(TransferProgress.class, transferProgress);
		return message;
	}

	public boolean start() {
		setLastDataChangeTime();
		return true;
	}

	public Status getCurrentStatus() {
		return this.currentStatus;
	}

	public TransferHandle getHandle() {
		return this.handle;
	}

	protected File getFile() {
		return this.file;
	}

	protected long getInactivityTimeout() {
		return this.inactivityTimeout;
	}

	public void setCurrentStatus(Status currentStatus) {
		this.currentStatus = currentStatus;
	}

	protected void setLastDataChangeTime() {
		this.lastDataChangeTime = System.currentTimeMillis();
	}
	
	public DeploymentID getListenerID() {
		return listenerID;
	}
}