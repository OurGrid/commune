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

import java.util.concurrent.Semaphore;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.IMessageDeliverer;
import br.edu.ufcg.lsd.commune.container.IMessageSender;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.network.ConnectionListener;
import br.edu.ufcg.lsd.commune.network.ConnectionListenerAdapter;
import br.edu.ufcg.lsd.commune.network.NetworkBuilder;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;
import br.edu.ufcg.lsd.commune.processor.filetransfer.FileTransferProcessor;
import br.edu.ufcg.lsd.commune.processor.interest.InterestProcessor;
import br.edu.ufcg.lsd.commune.processor.objectdeployer.ServiceProcessor;

public class TestableModule extends Module {

	private TestableInterestProcessor interestProcessor;
	private TestableServiceProcessor serviceProcessor;
	private TestableFileTransferProcessor fileTransferProcessor;
	private ConnectionListener listener;

	protected NetworkBuilder networkBuilder = createNetworkBuilder();
	
	public TestableModule(String containerName, ModuleContext context) 
			throws CommuneNetworkException, ProcessorStartException {
		
	    this(containerName, context, createSemaphore());
	}
	
	private static Semaphore createSemaphore() {
	    Semaphore semaphore = new Semaphore(1);
	    try {
		semaphore.acquire();
	    } catch (InterruptedException e) {
		throw new RuntimeException(e);
	    }
	    return semaphore;
	}

	public TestableModule(String containerName, ModuleContext context, final Semaphore semaphore) 
	throws CommuneNetworkException, ProcessorStartException {

	    super(containerName, context, new ConnectionListenerAdapter() {
		@Override
		public void connected() {
		    semaphore.release();
		}
	    });
	    
	    try {
		semaphore.acquire();
	    } catch (InterruptedException e) {
		throw new RuntimeException(e);
	    }
	}

	public TestableModule(String containerName, ModuleContext context, ConnectionListener listener) 
	throws CommuneNetworkException, ProcessorStartException {
		super(containerName, context, listener);
		this.listener = listener;
	}

	public DeploymentID getDeploymentID(String serviceName) {
		return getObject(serviceName).getDeploymentID();
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
	
	@Override
	public NetworkBuilder createNetworkBuilder() {
		return new TestableNetworkBuilder();
	}
}
