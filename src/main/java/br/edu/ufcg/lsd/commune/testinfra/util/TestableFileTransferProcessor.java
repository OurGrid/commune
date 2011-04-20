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
package br.edu.ufcg.lsd.commune.testinfra.util;

import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.processor.MessageConsumer;
import br.edu.ufcg.lsd.commune.processor.filetransfer.FileTransferProcessor;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransfer;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;

public class TestableFileTransferProcessor extends FileTransferProcessor implements TestableProcessor {

	public TestableFileTransferProcessor(Module module) {
		super(module);
	}

	@Override
	protected MessageConsumer createMessageConsumer() {
		return new TestableMessageConsumer(this, messageQueue, shutdownCountDownLatch);
	}
	
	public MessageConsumer getMessageConsumer() {
		return this.messageConsumer;
	}
	
	public void setOutgoingTransferStatus(OutgoingTransferHandle handle, Status newStatus) {
		OutgoingTransfer transfer = getTransferManager().getOutgoingManager().getTransfer(handle);
		transfer.setCurrentStatus(newStatus);
		transfer.updateStatus();
	}
}
