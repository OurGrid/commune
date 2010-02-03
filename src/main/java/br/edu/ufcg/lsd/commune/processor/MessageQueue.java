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

import java.util.LinkedList;

import br.edu.ufcg.lsd.commune.message.Message;

public class MessageQueue {
	
	private LinkedList<Message> eventQueue;
	
	public MessageQueue() {
		eventQueue = new LinkedList<Message>();
	}

	public synchronized void put( Message event ) {
		eventQueue.addLast( event );
		notify();
	}

	public synchronized Message unblockingRemove() {
		if ( eventQueue.size() > 0 ) {
			return eventQueue.removeFirst();	
		}
		return null;
	}
	
	public synchronized Message blockingRemove() {
		try {
			while ( eventQueue.size() == 0 ) {
				wait();
			}
		} catch (InterruptedException e) {
		}
		return eventQueue.removeFirst();
	}
	
	public synchronized int size() {
		return eventQueue.size();
	}
}