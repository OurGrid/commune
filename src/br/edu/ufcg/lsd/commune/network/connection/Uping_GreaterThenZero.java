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

import br.edu.ufcg.lsd.commune.network.DiscardMessageException;

/**
 * In this state, the outgoing connection is uping and the incoming connection
 * has a sequence number greater then zero.  
 */
public class Uping_GreaterThenZero extends ConnectionStateAdapter {

	
	public Uping_GreaterThenZero(ConnectionManager connectionManager) {
		super(connectionManager);
	}

	
	@Override
	public void registerInterest(Connection connection) {
		// Maintain state
	}
	
	@Override
	public void release(Connection connection) {
		connection.setOutgoingSequence(null);
		connection.setOutgoingSession(null);
		connection.setIncomingSequence(null);
		connection.setIncomingSession(null);
		connection.setState(manager.initialState);
	}
	
	@Override
	public void heartbeatOkSessionZeroSequence(Connection connection) throws DiscardMessageException {
		gotoDowningEmpty(connection);
	}
	
	@Override
	public void heartbeatOkSessionOkSequence(Connection connection) {
		// Maintain state
	}

	@Override
	public void heartbeatOkSessionNonSequence(Connection connection) throws DiscardMessageException {
		gotoDowningEmpty(connection);
	}
	
	@Override
	public void heartbeatNonSessionZeroSequence(Connection connection) throws DiscardMessageException {
		gotoDowningEmpty(connection);
	}
	
	@Override
	public void heartbeatNonSessionOkSequence(Connection connection) throws DiscardMessageException {
		gotoDowningEmpty(connection);
	}
	
	@Override
	public void heartbeatNonSessionNonSequence(Connection connection) throws DiscardMessageException {
		gotoDowningEmpty(connection);
	}

	@Override
	public void updateStatusUp(Connection connection) {
		// Maintain state
	}
	
	@Override
	public void updateStatusDown(Connection connection) throws DiscardMessageException {
		gotoDowningEmpty(connection);
	}
	
	@Override
	public void updateStatusNonSession(Connection connection) throws DiscardMessageException {
		gotoDowningEmpty(connection);
	}
	
	@Override
	public void timeout(Connection connection) {
		connection.setIncomingSequence(null);
		connection.setIncomingSession(null);
		connection.setState(manager.downing_empty);
	}
	
	@Override
	public void messageOkSessionOkSequence(Connection connection) {
		// Maintain state
	}
	
	@Override
	public void messageWithCallbackOkSessionOkSequence(Connection connection) {
		connection.setState(manager.up_greatherThenZero);
	}
	
	@Override
	public void messageNonSequence(Connection connection) throws DiscardMessageException {
		gotoDowningEmpty(connection);
	}
	
	@Override
	public void messageNonSession(Connection connection) throws DiscardMessageException {
		gotoDowningEmpty(connection);
	}

	@Override
	public void notifyRecovery(Connection connection) {
		connection.setState(manager.up_greatherThenZero);
	}
	
	private void gotoDowningEmpty(Connection connection) throws DiscardMessageException {
		connection.setIncomingSequence(null);
		connection.setIncomingSession(null);
		connection.setState(manager.downing_empty);
		
		throw new DiscardMessageException();
	}
}