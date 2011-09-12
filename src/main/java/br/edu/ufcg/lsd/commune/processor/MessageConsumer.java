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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.InvalidIdentificationException;
import br.edu.ufcg.lsd.commune.message.Message;

public class MessageConsumer implements Runnable {

	protected static final String SHUTDOWN_MESSAGE = "commune.shutdownEventHandler";

	protected BlockingQueue<Message> messageQueue;
	private Thread thread;
	protected boolean isAlive;
	private AbstractProcessor messageProcessor;
	private CountDownLatch shutdownCountDownLatch;
	
	public MessageConsumer(AbstractProcessor messageProcessor, BlockingQueue<Message> messageQueue,
			CountDownLatch shutdownCountDownLatch) {
		this.messageProcessor = messageProcessor;
		this.messageQueue = messageQueue;
		this.isAlive = false;
		this.thread = createThread();
		this.shutdownCountDownLatch = shutdownCountDownLatch;
	}

	public void start() {
		this.isAlive = true;
		this.thread.start();
	}

	public void run() {

		while (consumeMessage()) {}
		this.isAlive = false;
		this.shutdownCountDownLatch.countDown();
	}
	
	protected boolean consumeMessage() {
		Message message = removeMessage();
		
		String functionName = message.getFunctionName();
		if (SHUTDOWN_MESSAGE.equals(functionName)) {
			return false;
		}
		
		this.messageProcessor.consumeMessage(message);
		return true;
	}

	protected Thread createThread() {
		Thread thread = new Thread(this);
		thread.setName(messageProcessor.getThreadName() + thread.getId());
		return thread;
	}

	protected Message removeMessage() {
		try {
			return messageQueue.take();
		} catch (InterruptedException e) {
			return null;
		}
	}

	public void shutdown() {
		try {
			this.messageQueue.put(getShutdownEvent());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public boolean isAlive() {
		return this.isAlive;
	}

	private Message getShutdownEvent() {
		Message event = null;
		try {
			ContainerID ap = new ContainerID( "this", "this", "this", "fake_key" );
			DeploymentID myID = new DeploymentID( ap, "this" );
			event = new Message( myID, myID, SHUTDOWN_MESSAGE );
		} catch (InvalidIdentificationException e) {
			throw new CommuneRuntimeException();
		}
		return event;
	}
}