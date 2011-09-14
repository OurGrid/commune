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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import br.edu.ufcg.lsd.commune.message.Message;
import br.edu.ufcg.lsd.commune.processor.AbstractProcessor;
import br.edu.ufcg.lsd.commune.processor.MessageConsumer;

public class TestableMessageConsumer extends MessageConsumer {

	public TestableMessageConsumer(AbstractProcessor messageProcessor, 
			BlockingQueue<Message> messageQueue, CountDownLatch shutdownCountDownLatch) {
		super(messageProcessor, messageQueue, shutdownCountDownLatch);
	}

	@Override
	public void start() {
		this.isAlive = true;
	}
	
	@Override
	protected Thread createThread() {
		return null;
	}
	
	@Override
	protected Message removeMessage() {
		return messageQueue.poll();
	}
	
	@Override
	public boolean consumeMessage() {
		return super.consumeMessage();
	}
}
