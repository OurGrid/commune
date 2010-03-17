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
 * has a sequence number equal zero.  
 */
public class Up_Zero extends CommunicationStateAdapter {

	public Up_Zero(ConnectionManager connectionManager) {
		super(connectionManager);
	}

	@Override
	public void registerInterest(Communication connection) {
		// Maintain state
	}
	
	@Override
	public void release(Communication connection) {
		connection.setOutgoingSequence(null);
		connection.setOutgoingSession(null);
		connection.setIncomingSequence(null);
		connection.setIncomingSession(null);
		connection.setState(manager.empty_empty);
	}
	
	@Override
	public void heartbeatOkSessionZeroSequence(Communication connection) {
		// Maintain state
	}
	
	@Override
	public void heartbeatOkSessionOkSequence(Communication connection) throws DiscardMessageException {
		gotoDowningEmpty(connection);
	}
	
	@Override
	public void heartbeatOkSessionNonSequence(Communication connection) throws DiscardMessageException {
		gotoDowningEmpty(connection);
	}
	
	@Override
	public void heartbeatNonSessionZeroSequence(Communication connection) throws DiscardMessageException {
		gotoDowningEmpty(connection);
	}
	
	@Override
	public void heartbeatNonSessionOkSequence(Communication connection) throws DiscardMessageException {
		gotoDowningEmpty(connection);
	}
	
	@Override
	public void heartbeatNonSessionNonSequence(Communication connection) throws DiscardMessageException {
		gotoDowningEmpty(connection);
	}

	@Override
	public void updateStatusUp(Communication connection) {
		// Maintain state
	}
	
	@Override
	public void updateStatusDown(Communication connection) throws DiscardMessageException {
		gotoDowningEmpty(connection);
	}
	
	@Override
	public void updateStatusNonSession(Communication connection) throws DiscardMessageException {
		gotoDowningEmpty(connection);
	}
	
	@Override
	public void timeout(Communication connection) {
		connection.setIncomingSequence(null);
		connection.setIncomingSession(null);
		connection.setState(manager.downing_empty);
	}
	
	@Override
	public void messageOkSessionOkSequence(Communication connection) {
		connection.setState(manager.up_greatherThenZero);
	}
	
	@Override
	public void messageWithCallbackOkSessionOkSequence(Communication connection) {
		connection.setState(manager.up_greatherThenZero);
	}
	
	@Override
	public void messageNonSequence(Communication connection) throws DiscardMessageException {
		gotoDowningEmpty(connection);
	}
	
	@Override
	public void messageNonSession(Communication connection) throws DiscardMessageException {
		gotoDowningEmpty(connection);
	}
	
	private void gotoDowningEmpty(Communication connection) throws DiscardMessageException {
		connection.setIncomingSequence(null);
		connection.setIncomingSession(null);
		connection.setState(manager.downing_empty);
		
		throw new DiscardMessageException();
	}
}