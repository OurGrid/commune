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

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.message.Message;

public class IncomingTransfer extends AbstractTransfer {

	public static final String INCOMING_TRANSFER_COMPLETED = "incomingTransferCompleted";


	private static transient org.apache.log4j.Logger LOG = 
		org.apache.log4j.Logger.getLogger( IncomingTransfer.class );

	
	private IncomingFileTransfer transfer;
	private DeploymentID listenerID;



	public IncomingTransfer(Module module, DeploymentID listenerID, File file, IncomingFileTransfer transfer,
									TransferHandle handle, long inactivityTimeout, long fileSize,
									boolean notifyProgress) {

		super(module, listenerID, inactivityTimeout, handle, file, fileSize, notifyProgress);
		this.transfer = transfer;
		this.listenerID = listenerID;
	}


	@Override
	public boolean start() {
		super.start();
		setCurrentStatus( transfer.getStatus() );
		try {
			getFile().setWritable(true);
			transfer.recieveFile( getFile() );
		} catch (XMPPException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	public boolean updateStatus() {
		Status newStatus = transfer.getStatus();
		
		if ( newStatus != getCurrentStatus() ) {

			LOG.debug( "Handle: " + getHandle() + ". Transfer changed status from: " + getCurrentStatus() + " to "
					+ newStatus );
			setCurrentStatus( newStatus );

			switch ( newStatus ) {

				case error:
					LOG.debug( "Handle: " + getHandle() + ". Error: " + transfer.getError() + ", Details: "
							+ transfer.getException() );
					
					Message message = createIncomingTransferFailed(transfer.getException());
					module.sendMessage(message);
					return true;
			}
		}

		if (newStatus == Status.complete) {
			
			File localFile = getHandle().getLocalFile();
			boolean fileComplete = localFile.length() == transfer.getAmountWritten();

			if (fileComplete) {
				Message message = new Message(module.getContainerID(), listenerID, INCOMING_TRANSFER_COMPLETED);
				message.addParameter(IncomingTransferHandle.class, getHandle());
				message.addParameter(long.class, transfer.getAmountWritten());
				module.sendMessage(message);
				return true;
			} 
		}
		
		if ( checkTimeoutAndNotifyProgress(transfer) ) {
			LOG.debug( "Handle: " + getHandle() + ". Incoming file transfer timed out. More than "
					+ getInactivityTimeout() + " milliseconds elapsed since the latest activity" );
			
			Message message = createIncomingTransferFailed(new Exception("Transfer timed out"));
			module.sendMessage(message);
		}
		
		return false;
	}


	Message createIncomingTransferFailed(Exception exception) {
		Message message = new Message(module.getContainerID(), listenerID, "incomingTransferFailed"); 
		message.addParameter(IncomingTransferHandle.class, getHandle());
		
		if (exception == null) {
			exception = new Exception();
		} else {
			exception = new Exception(exception.getMessage());
		}
		
		message.addParameter(Exception.class, exception);
		message.addParameter(long.class, transfer.getAmountWritten());
		return message;
	}


	public void cancel() {
		transfer.cancel();
	}
}