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
package br.edu.ufcg.lsd.commune.functionaltests.util;

import br.edu.ufcg.lsd.commune.container.Container;
import br.edu.ufcg.lsd.commune.container.ContainerContext;
import br.edu.ufcg.lsd.commune.container.IMessageDeliverer;
import br.edu.ufcg.lsd.commune.container.IMessageSender;
import br.edu.ufcg.lsd.commune.processor.filetransfer.FileTransferProcessor;
import br.edu.ufcg.lsd.commune.processor.interest.InterestProcessor;
import br.edu.ufcg.lsd.commune.processor.objectdeployer.ServiceProcessor;

public class TestableContainer extends Container {
	
	private TestableInterestProcessor interestProcessor;
	private TestableServiceProcessor serviceProcessor;
	private TestableFileTransferProcessor fileTransferProcessor;
	
	public TestableContainer(TestableApplication communeContainer, String containerName, ContainerContext context) {
		super(communeContainer, containerName, context);
	}

	public void setMessageDelivererMock(IMessageDeliverer messageDeliverer) {
		this.messageDeliverer = messageDeliverer;
	}

	public void setMessageSenderMock(IMessageSender messageSender) {
		this.messageSender = messageSender;
	}

	@Override
	protected InterestProcessor createInterestProcessor() {
		interestProcessor = new TestableInterestProcessor(this);
		return interestProcessor;
	}
	
	public TestableMessageConsumer getInterestConsumer() {
		return (TestableMessageConsumer)this.interestProcessor.getMessageConsumer();
	}
	
	@Override
	protected ServiceProcessor createServiceProcessor() {
		serviceProcessor = new TestableServiceProcessor(this);
		return serviceProcessor;
	}
	
	public TestableMessageConsumer getServiceConsumer() {
		return (TestableMessageConsumer)this.serviceProcessor.getMessageConsumer();
	}

	@Override
	protected FileTransferProcessor createFileTransferProcessor() {
		fileTransferProcessor = new TestableFileTransferProcessor(this);
		return fileTransferProcessor;
	}
	
	public TestableMessageConsumer getFileTransferConsumer() {
		return (TestableMessageConsumer)this.fileTransferProcessor.getMessageConsumer();
	}
	
	public FileTransferProcessor getFileTransferProcessor() {
		return this.fileTransferProcessor;
	}
}
