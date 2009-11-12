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

import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;

import br.edu.ufcg.lsd.commune.container.Container;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.message.Message;

public class OutgoingTransfer extends AbstractTransfer {

	private static transient org.apache.log4j.Logger LOG = 
		org.apache.log4j.Logger.getLogger( OutgoingTransfer.class );

	
	private OutgoingFileTransfer transfer;
	private final String destinationObjectID;
	private final String destinationFileName;
	private final String transferDescription;


	public OutgoingTransfer(Container container, DeploymentID listenerID, TransferHandle handle, OutgoingFileTransfer transfer,
									File file, String destinationObjectID, String destinationFileName, long inactivityTimeout, 
									boolean notifyProgress, String transferDescription) {

		super(container, listenerID, inactivityTimeout, handle, file, file.length(), notifyProgress);
		this.transfer = transfer;
		this.destinationObjectID = destinationObjectID;
		this.destinationFileName = destinationFileName;
		this.transferDescription = transferDescription;
	}


	@Override
	public boolean start() {
		super.start();

		// request description is composed by a handle number followed by the
		// destination objectID
		try {
			String messageToSend = getHandle() + " " + destinationObjectID;
			if (transferDescription != null) {
				messageToSend += " " + transferDescription;
			}
			if (destinationFileName != null) {
				messageToSend += " " + destinationFileName;
			}
			transfer.sendFile( getFile(), messageToSend );
			return true;
		} catch ( IllegalArgumentException e ) {
			handleTransferError( e );
		}
		return false;
	}

	private void handleTransferError( Exception e ) {
		Message message = new Message(container.getContainerID(), listenerID, "outgoingTransferFailed");
		message.addParameter(OutgoingTransferHandle.class, getHandle());
		message.addParameter(Exception.class, e);
		message.addParameter(long.class, 0L);
		
		container.sendMessage(message);
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
					Message message = new Message(container.getContainerID(), listenerID, "outgoingTransferCancelled");
					message.addParameter(OutgoingTransferHandle.class, outGoingHandle);
					message.addParameter(long.class, transfer.getBytesSent());
					
					container.sendMessage(message);
					return true;
					
				case error:
					message = new Message(container.getContainerID(), listenerID, "outgoingTransferFailed");
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
					
					container.sendMessage(message);
					return true;
					
				case refused:
					message = new Message(container.getContainerID(), listenerID, "transferRejected");
					message.addParameter(OutgoingTransferHandle.class, outGoingHandle);
					
					container.sendMessage(message);
					return true;
					
			}
			
		}

		if (newStatus == Status.complete) {
			
			boolean fileCompleted = transfer.getFileSize() == transfer.getBytesSent();
			if (fileCompleted) {
				Message message = new Message(container.getContainerID(), listenerID, "outgoingTransferCompleted");
				message.addParameter(OutgoingTransferHandle.class, outGoingHandle);
				message.addParameter(long.class, transfer.getBytesSent());
				container.sendMessage(message);
				return true;
			} 
		}
	
		if ( checkTimeoutAndNotifyProgress(transfer) ) {
			LOG.debug( "Handle: " + getHandle() + ". Outgoing file transfer timed out. More than "
					+ getInactivityTimeout() + " milliseconds elapsed since the latest activity" );
			Message message = new Message(container.getContainerID(), listenerID, "outgoingTransferFailed");
			message.addParameter(OutgoingTransferHandle.class, outGoingHandle);
			message.addParameter(Exception.class, new Exception( "Transfer timed out" ));
			message.addParameter(long.class, transfer.getAmountWritten());

			container.sendMessage(message);
		}
		return false;
	}


	Message createOutgoingTransferFailed(OutgoingTransferHandle outGoingHandle, Exception exception) {
		Message message;
		message = new Message(container.getContainerID(), listenerID, "outgoingTransferFailed");
		message.addParameter(OutgoingTransferHandle.class, outGoingHandle);
		message.addParameter(Exception.class, exception);
		message.addParameter(long.class, transfer.getBytesSent());
		return message;
	}

	public void cancel() {
		transfer.cancel();
	}
}