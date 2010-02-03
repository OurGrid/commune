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
package br.edu.ufcg.lsd.commune.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.container.Container;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLoggerFactory;
import br.edu.ufcg.lsd.commune.message.Message;
import br.edu.ufcg.lsd.commune.monitor.MonitorProperties;

public abstract class AbstractProcessor implements MessageProcessor {

	
	protected static final transient CommuneLogger msgLogger = 
		CommuneLoggerFactory.getInstance().getMessagesLogger();
	protected static final transient CommuneLogger console = 
		CommuneLoggerFactory.getInstance().getConsoleLogger();

	
    private Container container;

    protected MessageQueue messageQueue;
    protected CountDownLatch shutdownCountDownLatch; 
    protected MessageConsumer messageConsumer;
    protected List<Message> incomingMessageQueue;
    protected List<Message> outgoingMessageQueue;
    
    private static final int QUEUE_LIMIT = 30;
    
    protected AbstractProcessor(Container container) {
    	if (container == null) {
    		throw new CommuneRuntimeException( "You need to set a container!" );
    	}
    	
    	this.container = container;
        this.shutdownCountDownLatch = new CountDownLatch(1);
        this.messageQueue = new MessageQueue();
        this.messageConsumer = createMessageConsumer();
        
        if (container.getContext().isEnabled(MonitorProperties.PROP_COMMUNE_MONITOR)) {
        	this.incomingMessageQueue = new ArrayList<Message>(QUEUE_LIMIT);
        	this.outgoingMessageQueue = new ArrayList<Message>(QUEUE_LIMIT);
        }
	}

	protected MessageConsumer createMessageConsumer() {
		return new MessageConsumer(this, messageQueue, shutdownCountDownLatch);
	}

    public void start() {
    	this.messageConsumer.start();
    }

    public void receiveMessage(Message message){
    	this.messageQueue.put(message);
    }
    
    public void shutdown() {
    	this.messageConsumer.shutdown();
    }
    
    public Container getContainer() {
        return this.container;
    }
    
    private void logIncomingMessage(Message message) {
		if (this.incomingMessageQueue != null) {
			if (incomingMessageQueue.size() == QUEUE_LIMIT) {
				incomingMessageQueue.remove(0);
			}
			
			this.incomingMessageQueue.add(message);
		}   	
    }

	public void consumeMessage(Message message) {
		logIncomingMessage(message);
		processMessage(message);
	}

	public abstract void processMessage(Message message);
	
	private void logOutgoingMessage(Message message) {
		if (this.outgoingMessageQueue != null) {
			if (outgoingMessageQueue.size() == QUEUE_LIMIT) {
				outgoingMessageQueue.remove(0);
			}
			
			this.outgoingMessageQueue.add(message);
		}
	}

	public void sendMessage(Message message) {
		logOutgoingMessage(message);
		getContainer().sendMessage(message);
	}

	protected ObjectDeployment getObjectDeployment(String serviceName) {
		return getContainer().getObjectRepository().get(serviceName);
	}
	
	public void setMessageConsumer(MessageConsumer messageConsumer) {
		this.messageConsumer = messageConsumer;
	}
	
	public List<Message> getIncomingQueueMessages() {
		return this.incomingMessageQueue;
	}
	
	public List<Message> getOutgoingQueueMessages() {
		return this.outgoingMessageQueue;
	}

	public abstract String getThreadName();
}