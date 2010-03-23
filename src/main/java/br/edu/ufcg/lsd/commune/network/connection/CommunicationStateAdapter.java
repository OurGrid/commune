/*
 * Copyright (C) 2009 Universidade Federal de Campina Grande
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
package br.edu.ufcg.lsd.commune.network.connection;

import java.security.SecureRandom;

import br.edu.ufcg.lsd.commune.network.DiscardMessageException;

/**
 * By default, ignores all transitions
 * 
 * TODO Drop the ignored messages
 * 
 */
public class CommunicationStateAdapter implements CommunicationState {
	

	protected final ConnectionManager manager;

	public CommunicationStateAdapter(ConnectionManager connectionManager) {
		this.manager = connectionManager;
	}


	public void heartbeatNonSessionNonSequence(Communication connection) throws DiscardMessageException {
		throw new DiscardMessageException();
	}

	public void heartbeatNonSessionOkSequence(Communication connection) throws DiscardMessageException {
		throw new DiscardMessageException();
	}

	public void heartbeatNonSessionZeroSequence(Communication connection) throws DiscardMessageException {
		throw new DiscardMessageException();
	}

	public void heartbeatOkSessionNonSequence(Communication connection) throws DiscardMessageException {
		throw new DiscardMessageException();
	}

	public void heartbeatOkSessionOkSequence(Communication connection) throws DiscardMessageException {
		throw new DiscardMessageException();
	}

	public void heartbeatOkSessionZeroSequence(Communication connection) throws DiscardMessageException {
		throw new DiscardMessageException();
	}

	public void messageWithCallbackOkSessionOkSequence(Communication connection) throws DiscardMessageException {
		throw new DiscardMessageException();
	}

	public void registerInterest(Communication connection) {}

	public void release(Communication connection) {}

	public void updateStatusDown(Communication connection) throws DiscardMessageException {
		throw new DiscardMessageException();
	}

	public void updateStatusNonSession(Communication connection) throws DiscardMessageException {
		throw new DiscardMessageException();
	}

	public void updateStatusUp(Communication connection) throws DiscardMessageException {
		throw new DiscardMessageException();
	}

	public void messageNonSequence(Communication connection) throws DiscardMessageException {
		throw new DiscardMessageException();
	}

	public void messageNonSession(Communication connection) throws DiscardMessageException {
		throw new DiscardMessageException();
	}

	public void messageOkSessionOkSequence(Communication connection) throws DiscardMessageException {
		throw new DiscardMessageException();
	}

	public void timeout(Communication connection) {}

	public void notifyFailure(Communication connection) throws DiscardMessageException {
		throw new DiscardMessageException();
	}

	public void notifyRecovery(Communication connection) throws DiscardMessageException {
		throw new DiscardMessageException();
	}

	protected static Long generateSessionNumber() {
		SecureRandom random = new SecureRandom();
		long toReturn = random.nextLong();
		return (toReturn >= 0 ? toReturn : -1 * toReturn);
	}

	protected void forceNotifyFailure(Communication communication) {
		manager.forceNotifyFailure(communication.getStubReference().getStubServiceID());
	}
}