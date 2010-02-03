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
package br.edu.ufcg.lsd.commune.functionaltests.monitor.data;

import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.processor.filetransfer.IncomingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferProgress;

public class SenderClass implements SenderInterface {
	
	private ServiceManager serviceManager;

	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}
	
	public void sendFile(OutgoingTransferHandle handle) {
		serviceManager.startTransfer(handle, this);
	}

	public void outgoingTransferCancelled(OutgoingTransferHandle handle,
			long amountWritten) {
		
	}

	public void outgoingTransferCompleted(OutgoingTransferHandle handle,
			long amountWritten) {
		
	}

	public void outgoingTransferFailed(OutgoingTransferHandle handle,
			Exception failCause, long amountWritten) {
		
	}

	public void transferRejected(OutgoingTransferHandle handle) {
		
	}

	public void updateTransferProgress(TransferProgress transferProgress) {
		
	}

	public void incomingTransferCompleted(IncomingTransferHandle handle,
			long amountWritten) {
		
	}

	public void incomingTransferFailed(IncomingTransferHandle handle,
			Exception failCause, long amountWritten) {
		
	}

	public void transferRequestReceived(IncomingTransferHandle handle) {
		
	}
	
	public ServiceManager getServiceManager() {
		return serviceManager;
	}
}
