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

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.message.Message;
import br.edu.ufcg.lsd.commune.message.StubParameter;
import br.edu.ufcg.lsd.commune.network.CommuneNetwork;
import br.edu.ufcg.lsd.commune.network.xmpp.XMPPProtocol;
import br.edu.ufcg.lsd.commune.processor.AbstractProcessor;

public class FileTransferProcessor extends AbstractProcessor {


	public static final String ACCEPT_TRANSFER = "acceptTransfer";
	public static final String REJECT_TRANSFER = "rejectTransfer";
	public static final String START_TRANSFER = "startTransfer";
	public static final String CANCEL_INCOMING_TRANSFER = "cancelIncomingTransfer";
	public static final String CANCEL_OUTGOING_TRANSFER = "cancelOutgoingTransfer";
	
	
	protected TransferManager transferManager;

	public FileTransferProcessor(Module module) {
		super(module);
		transferManager = createTransferManager();
	}


	protected TransferManager createTransferManager() {
		int maxOut = getModule().getContext().parseIntegerProperty(TransferProperties.PROP_FILE_TRANSFER_MAX_OUT );
//		boolean notifyProgress = getContainer().getContext().isEnabled(TransferProperties.PROP_FILE_TRANSFER_NOTIFY_PROGRESS );
		return new TransferManager(maxOut, getModule().getContext());
	}

	public void start() {
		super.start();
		CommuneNetwork communicationLayer = (CommuneNetwork) getModule().getMessageSender();
		XMPPProtocol xmppProtocol = communicationLayer.getXMPPProtocol();
		transferManager.start(getModule(), xmppProtocol.getConnection());
	}

	@Override
	public void shutdown() {
		super.shutdown();
		transferManager.shutdown();
	}


	@Override
	public void processMessage(Message message) {
		
		msgLogger.debug("Before " + message.toString());

		if (ACCEPT_TRANSFER.equals(message.getFunctionName())) {
			IncomingTransferHandle handle = (IncomingTransferHandle) message.getParameters().get(0).getValue();
			StubParameter stubP = (StubParameter) message.getParameters().get(1).getValue();
			
			DeploymentID id = stubP.getId();
			transferManager.accept(handle, id);

		} else if (REJECT_TRANSFER.equals(message.getFunctionName())) {
			IncomingTransferHandle handle = (IncomingTransferHandle) message.getParameters().get(0).getValue();
			transferManager.reject(handle);

		} else if (START_TRANSFER.equals(message.getFunctionName())) {
			OutgoingTransferHandle handle = (OutgoingTransferHandle) message.getParameters().get(0).getValue();
			StubParameter stubP = (StubParameter) message.getParameters().get(1).getValue();
			
			DeploymentID id = stubP.getId();
			transferManager.startTransfer(handle, id, getModule());

		} else if (CANCEL_INCOMING_TRANSFER.equals(message.getFunctionName())) {
			IncomingTransferHandle handle = (IncomingTransferHandle) message.getParameters().get(0).getValue();
			transferManager.cancelIncomingTransfer(handle);

		} else if (CANCEL_OUTGOING_TRANSFER.equals(message.getFunctionName())) {
			OutgoingTransferHandle handle = (OutgoingTransferHandle) message.getParameters().get(0).getValue();
			transferManager.cancelOutgoingTransfer(handle);
		}
		
		msgLogger.debug("After " + message.toString());

	}

	public TransferManager getTransferManager() {
		return transferManager;
	}


	public String getThreadName() {
		return "FT";
	}

}