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

import static br.edu.ufcg.lsd.commune.processor.filetransfer.FileTransferConstants.*;
import java.io.File;

import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.message.Message;

public class OutgoingTransfer extends AbstractTransfer {

	private static transient org.apache.log4j.Logger LOG = 
		org.apache.log4j.Logger.getLogger( OutgoingTransfer.class );

	
	private OutgoingFileTransfer transfer;
	private final String destinationDeploymentID;
	private final String transferDescription;


	public OutgoingTransfer(Module module, DeploymentID listenerID, TransferHandle handle, OutgoingFileTransfer transfer,
									File file, String destinationDeploymentID, long inactivityTimeout, 
									boolean notifyProgress, String transferDescription) {

		super(module, listenerID, inactivityTimeout, handle, file, file.length(), notifyProgress);
		this.transfer = transfer;
		this.destinationDeploymentID = destinationDeploymentID;
		this.transferDescription = transferDescription;
	}


	@Override
	public boolean start() {
		super.start();

		// request description is composed by a handle number followed by the
		// destination objectID
		try {
			File file = getFile();
			String messageToSend = HANDLE_PROPERTY + PROPERTY_SEPARATOR + getHandle() + PROPERTIES_SEPARATOR + 
				DESTINATION_PROPERTY + PROPERTY_SEPARATOR + destinationDeploymentID + PROPERTIES_SEPARATOR + 
				EXECUTABLE_PROPERTY + PROPERTY_SEPARATOR + file.canExecute() + PROPERTIES_SEPARATOR + 
				READABLE_PROPERTY + PROPERTY_SEPARATOR + file.canRead() + PROPERTIES_SEPARATOR + 
				WRITABLE_PROPERTY + PROPERTY_SEPARATOR + file.canWrite();
			if (transferDescription != null) {
				messageToSend += PROPERTIES_SEPARATOR + DESCRIPTION_PROPERTY + PROPERTY_SEPARATOR + transferDescription;
			}
			transfer.sendFile( file, messageToSend );
			return true;
		} catch ( Exception e ) {
			handleTransferError( e );
		}
		return false;
	}

	private void handleTransferError( Exception e ) {
		Message message = new Message(module.getContainerID(), listenerID, "outgoingTransferFailed");
		message.addParameter(OutgoingTransferHandle.class, getHandle());
		message.addParameter(Exception.class, e);
		message.addParameter(long.class, 0L);
		
		module.sendMessage(message);
	}

	public boolean updateStatus() {

		Status newStatus = transfer.getStatus();
		OutgoingTransferHandle outGoingHandle = (OutgoingTransferHandle) getHandle();

		if ( newStatus != getCurrentStatus() ) {

			LOG.debug( "Handle: " + getHandle() + ". Transfer changed status from: " + getCurrentStatus() + " to "
					+ newStatus );
			setCurrentStatus( newStatus );

			switch ( newStatus ) {

				case cancelled:
					Message message = new Message(module.getContainerID(), listenerID, "outgoingTransferCancelled");
					message.addParameter(OutgoingTransferHandle.class, outGoingHandle);
					message.addParameter(long.class, transfer.getBytesSent());
					
					module.sendMessage(message);
					return true;
					
				case error:
					message = new Message(module.getContainerID(), listenerID, "outgoingTransferFailed");
					message.addParameter(OutgoingTransferHandle.class, outGoingHandle);
					Exception xmppException = transfer.getException();
					
					Exception paramExc = null;
					if (xmppException == null) {
						paramExc = new Exception();
					} else {
						paramExc = new Exception(xmppException.getMessage());
					}
					
					message.addParameter(Exception.class, paramExc);
					message.addParameter(long.class, transfer.getBytesSent());
					
					module.sendMessage(message);
					return true;
					
				case refused:
					message = new Message(module.getContainerID(), listenerID, "transferRejected");
					message.addParameter(OutgoingTransferHandle.class, outGoingHandle);
					
					module.sendMessage(message);
					return true;
					
			}
			
		}

		if (newStatus == Status.complete) {
			
			boolean fileCompleted = transfer.getFileSize() == transfer.getBytesSent();
			if (fileCompleted) {
				Message message = new Message(module.getContainerID(), listenerID, "outgoingTransferCompleted");
				message.addParameter(OutgoingTransferHandle.class, outGoingHandle);
				message.addParameter(long.class, transfer.getBytesSent());
				module.sendMessage(message);
				return true;
			} 
		}
	
		if ( checkTimeoutAndNotifyProgress(transfer) ) {
			LOG.debug( "Handle: " + getHandle() + ". Outgoing file transfer timed out. More than "
					+ getInactivityTimeout() + " milliseconds elapsed since the latest activity" );
			Message message = new Message(module.getContainerID(), listenerID, "outgoingTransferFailed");
			message.addParameter(OutgoingTransferHandle.class, outGoingHandle);
			message.addParameter(Exception.class, new Exception( "Transfer timed out" ));
			message.addParameter(long.class, transfer.getAmountWritten());

			module.sendMessage(message);
		}
		return false;
	}


	Message createOutgoingTransferFailed(OutgoingTransferHandle outGoingHandle, Exception exception) {
		Message message;
		message = new Message(module.getContainerID(), listenerID, "outgoingTransferFailed");
		message.addParameter(OutgoingTransferHandle.class, outGoingHandle);
		message.addParameter(Exception.class, exception);
		message.addParameter(long.class, transfer.getBytesSent());
		return message;
	}

	public void cancel() {
		transfer.cancel();
	}
}