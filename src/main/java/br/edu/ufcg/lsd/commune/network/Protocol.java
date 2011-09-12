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
package br.edu.ufcg.lsd.commune.network;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import br.edu.ufcg.lsd.commune.message.Message;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;


public abstract class Protocol {

	protected static final Lock protocolLock = new ReentrantLock();

	protected List<ProtocolCreationListener> listeners = new ArrayList<ProtocolCreationListener>();
	protected Protocol nextProtocol = null;
	protected Protocol previousProtocol = null;
	protected CommuneNetwork communeNetwork; 

    
	public Protocol(CommuneNetwork communeNetwork) {
		this.communeNetwork = communeNetwork;
	}

	public final void sendMessage(Message message) {
		
		try {
			protocolLock.lock();
			onSend(message);
			callNext(message);
		} catch (ProtocolException protocolException) {
			notifyError(protocolException);
		} finally {
			protocolLock.unlock();
		}
	}

	public final void receiveMessage(Message message) {
		
		try {
			protocolLock.lock();
			onReceive(message);
            callPrevious(message);
		} catch (ProtocolException protocolException) {
			notifyError(protocolException);
		} finally {
			protocolLock.unlock();
		}
		
	}

	protected final void callPrevious(Message message) {
		if (this.previousProtocol != null) {
			this.previousProtocol.receiveMessage(message);
		}
	}

	protected final void callNext(Message message) {
		if (this.nextProtocol != null) {
			this.nextProtocol.sendMessage(message);
		}
	}

	protected abstract void onReceive(Message message) throws ProtocolException;

	protected abstract void onSend(Message message) throws ProtocolException;

	protected void notifyError(ProtocolException protocolException) {
//TODO		communeNetwork.notifyError(protocolException);
	}

	protected final void setNextProtocol(Protocol nextProtocol) {
		this.nextProtocol = nextProtocol;
	}

	protected final void setPreviousProtocol(Protocol previousProtocol) {
		this.previousProtocol = previousProtocol;
	}
	
	public void start() throws CommuneNetworkException {
		protocolStarted();
	}

	protected void protocolStarted() {
		for (ProtocolCreationListener listener : this.listeners) {
			listener.started();
		}
	}
	
	public void shutdown() throws CommuneNetworkException {}
	
	public void addCreationListener(ProtocolCreationListener listener) {
		this.listeners.add(listener);
	}
}