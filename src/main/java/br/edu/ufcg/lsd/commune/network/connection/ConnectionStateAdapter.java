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
public class ConnectionStateAdapter implements ConnectionState {
	

	protected final ConnectionManager manager;

	public ConnectionStateAdapter(ConnectionManager connectionManager) {
		this.manager = connectionManager;
	}


	public void heartbeatNonSessionNonSequence(Connection connection) throws DiscardMessageException {
		throw new DiscardMessageException();
	}

	public void heartbeatNonSessionOkSequence(Connection connection) throws DiscardMessageException {
		throw new DiscardMessageException();
	}

	public void heartbeatNonSessionZeroSequence(Connection connection) throws DiscardMessageException {
		throw new DiscardMessageException();
	}

	public void heartbeatOkSessionNonSequence(Connection connection) throws DiscardMessageException {
		throw new DiscardMessageException();
	}

	public void heartbeatOkSessionOkSequence(Connection connection) throws DiscardMessageException {
		throw new DiscardMessageException();
	}

	public void heartbeatOkSessionZeroSequence(Connection connection) throws DiscardMessageException {
		throw new DiscardMessageException();
	}

	public void messageWithCallbackOkSessionOkSequence(Connection connection) throws DiscardMessageException {
		throw new DiscardMessageException();
	}

	public void registerInterest(Connection connection) {}

	public void release(Connection connection) {}

	public void updateStatusDown(Connection connection) throws DiscardMessageException {
		throw new DiscardMessageException();
	}

	public void updateStatusNonSession(Connection connection) throws DiscardMessageException {
		throw new DiscardMessageException();
	}

	public void updateStatusUp(Connection connection) throws DiscardMessageException {
		throw new DiscardMessageException();
	}

	public void messageNonSequence(Connection connection) throws DiscardMessageException {
		throw new DiscardMessageException();
	}

	public void messageNonSession(Connection connection) throws DiscardMessageException {
		throw new DiscardMessageException();
	}

	public void messageOkSessionOkSequence(Connection connection) throws DiscardMessageException {
		throw new DiscardMessageException();
	}

	public void timeout(Connection connection) {}

	public void notifyFailure(Connection connection) throws DiscardMessageException {
		throw new DiscardMessageException();
	}

	public void notifyRecovery(Connection connection) throws DiscardMessageException {
		throw new DiscardMessageException();
	}

	protected static Long generateSessionNumber() {
		SecureRandom random = new SecureRandom();
		long toReturn = random.nextLong();
		return (toReturn >= 0 ? toReturn : -1 * toReturn);
	}

}
