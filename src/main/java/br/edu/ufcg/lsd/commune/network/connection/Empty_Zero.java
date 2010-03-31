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
 * In this state, the outgoing connection is empty and the incoming connection
 * has a sequence number equal zero.  
 */
public class Empty_Zero extends CommunicationStateAdapter {

	public Empty_Zero(ConnectionManager connectionManager) {
		super(connectionManager);
	}

	
	@Override
	public void registerInterest(Communication communication) {
		communication.setOutgoingSession(generateSessionNumber());
		communication.setOutgoingSequence(0L);
		communication.setState(manager.down_zero); 
	}
	
	@Override
	public void heartbeatOkSessionZeroSequence(Communication communication) {
		//Maintain state
	}
	
	@Override
	public void heartbeatOkSessionOkSequence(Communication communication) throws DiscardMessageException {
		gotoInitial(communication);
	}

	@Override
	public void heartbeatOkSessionNonSequence(Communication communication) throws DiscardMessageException {
		gotoInitial(communication);
	}
	
	@Override
	public void heartbeatNonSessionZeroSequence(Communication communication) throws DiscardMessageException {
		gotoInitial(communication);
	}

	@Override
	public void heartbeatNonSessionOkSequence(Communication communication) throws DiscardMessageException {
		gotoInitial(communication);
	}
	
	@Override
	public void heartbeatNonSessionNonSequence(Communication communication) throws DiscardMessageException {
		gotoInitial(communication);
	}
	
	@Override
	public void updateStatusUp(Communication communication) throws DiscardMessageException {
		gotoInitial(communication);
	}
	
	@Override
	public void updateStatusDown(Communication communication) throws DiscardMessageException {
		gotoInitial(communication);
	}

	@Override
	public void updateStatusNonSession(Communication communication) throws DiscardMessageException {
		gotoInitial(communication);
	}
	
	@Override
	public void messageOkSessionOkSequence(Communication communication) {
		communication.setState(manager.empty_greatherThenZero);
	}
	
	@Override
	public void messageWithCallbackOkSessionOkSequence(Communication communication) {
		communication.setState(manager.up_greatherThenZero);
	}
	
	@Override
	public void messageNonSequence(Communication communication) throws DiscardMessageException {
		gotoInitial(communication);
	}
	
	@Override
	public void messageNonSession(Communication communication) throws DiscardMessageException {
		gotoInitial(communication);
	}
	
	private void gotoInitial(Communication communication) throws DiscardMessageException {
		communication.invalidateIncoming();
		communication.setState(manager.empty_empty);
		
		throw new DiscardMessageException();
	}
}