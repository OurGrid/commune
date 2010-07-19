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
import java.util.concurrent.locks.ReentrantLock;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;

public class TransferManager {

	private static final long serialVersionUID = 1L;
	private static transient org.apache.log4j.Logger LOG = 
		org.apache.log4j.Logger.getLogger( TransferManager.class );

	
	private IncomingTransfersManager incomingManager;
	private OutgoingTransfersManager outgoingManager;
	private final int maxOut;
	private ModuleContext context;


	public TransferManager(int maxOut, ModuleContext context) {
		this.context = context;
		this.maxOut = maxOut;
	}

	
	public void start(Module module, XMPPConnection connection) {
		ReentrantLock lock = new ReentrantLock();
		
		FileTransferManager manager = new FileTransferManager(connection);
		incomingManager = new IncomingTransfersManager(module, manager, lock);
		
		int responseTimeout = context.parseIntegerProperty(TransferProperties.PROP_FILE_TRANSFER_TIMEOUT) * 1000;
		outgoingManager = new OutgoingTransfersManager(manager, maxOut, responseTimeout, lock);
		
		incomingManager.start(connection);
		outgoingManager.start();
	}

	public void addFileTransferInterested(TransferReceiver receiver, DeploymentID receiverID) {
		LOG.debug( "Transfer receiver added. ID: " + receiverID );
		incomingManager.addFileTransferInterested(receiver, receiverID);
	}

	public void removeFileTransferInterested(DeploymentID receiverID) {
		incomingManager.removeFileTransferInterested(receiverID);
		LOG.debug( "Transfer receiver removed. ID: " + receiverID);
	}

	public void transferRequest(FileTransferRequest ftr) {
		LOG.debug("Incoming transfer request. File name: " + ftr.getFileName());
		incomingManager.fileTransferRequest(ftr);
	}

	public void accept(IncomingTransferHandle handle, DeploymentID listenerID) {

		handle.setInactivityTimeout(context.parseLongProperty(TransferProperties.PROP_FILE_TRANSFER_TIMEOUT) * 1000);
		handle.setReceiveProgressUpdate(context.isEnabled(TransferProperties.PROP_FILE_TRANSFER_NOTIFY_PROGRESS));
		
		LOG.debug( "Destination file: " + handle.getLocalFile() + ", handle: " + handle.getId() + 
				", timeout: " + handle.getInactivityTimeout() );
		incomingManager.accept(handle, listenerID);
	}
	
	public void cancelIncomingTransfer(IncomingTransferHandle handle) {
		LOG.debug("Handle: " + handle);
		incomingManager.cancelTransfer(handle);
	}
	
	public void reject(IncomingTransferHandle handle) {
		LOG.debug("Handle: " + handle);
		incomingManager.reject(handle);
	}
	
	public void startTransfer(OutgoingTransferHandle handle, DeploymentID listenerID, Module module) {
		File file = handle.getLocalFile();
		DeploymentID destination = handle.getDestinationID();
		
		handle.setInactivityTimeout(context.parseLongProperty(TransferProperties.PROP_FILE_TRANSFER_TIMEOUT) * 1000);
		handle.setReceiveProgressUpdate(context.isEnabled(TransferProperties.PROP_FILE_TRANSFER_NOTIFY_PROGRESS));
		
		LOG.debug( "File: " + file.getAbsolutePath() + " to " + destination + ", handle: " + handle.getId() + 
				", size: " + file.length() );
		outgoingManager.startTransfer(handle, listenerID, module);
		
	}

	public void cancelOutgoingTransfer(OutgoingTransferHandle handle) {
		LOG.debug("Cancelling outgoing transfer, Handle: " + handle);
		outgoingManager.cancelTransfer(handle);
	}

	public void shutdown() {
		incomingManager.shutdown();
		outgoingManager.shutdown();
	}

	public IncomingTransfersManager getIncomingManager() {
		return incomingManager;
	}


	public OutgoingTransfersManager getOutgoingManager() {
		return outgoingManager;
	}	
}