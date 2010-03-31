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
 * In this state, the outgoing connection is down and the incoming connection
 * has a sequence number equal zero.  
 */
public class Down_Zero extends CommunicationStateAdapter {

	public Down_Zero(ConnectionManager connectionManager) {
		super(connectionManager);
	}

	
	@Override
	public void registerInterest(Communication communication) {
		// Maintain state
	}

	@Override
	public void release(Communication communication) {
		communication.invalidate();
		communication.setState(manager.empty_empty);
	}

	@Override
	public void heartbeatOkSessionZeroSequence(Communication communication) {
		// Maintain state
	}
	
	@Override
	public void heartbeatOkSessionOkSequence(Communication communication) throws DiscardMessageException {
		gotoDownEmpty(communication);
	}
	
	@Override
	public void heartbeatOkSessionNonSequence(Communication communication) throws DiscardMessageException {
		gotoDownEmpty(communication);
	}
	
	@Override
	public void heartbeatNonSessionZeroSequence(Communication communication) throws DiscardMessageException {
		gotoDownEmpty(communication);
	}
	
	@Override
	public void heartbeatNonSessionOkSequence(Communication communication) throws DiscardMessageException {
		gotoDownEmpty(communication);
	}
	
	@Override
	public void heartbeatNonSessionNonSequence(Communication communication) throws DiscardMessageException {
		gotoDownEmpty(communication);
	}
	
	@Override
	public void updateStatusUp(Communication communication) {
		communication.setState(manager.uping_zero);
	}
	
	@Override
	public void updateStatusDown(Communication communication) throws DiscardMessageException {
		gotoDownEmpty(communication);
	}
	
	@Override
	public void updateStatusNonSession(Communication communication) throws DiscardMessageException {
		gotoDownEmpty(communication);
	}
	
	@Override
	public void timeout(Communication communication) {
		communication.invalidateIncoming();
		communication.setState(manager.down_empty);
	}
	
	@Override
	public void messageOkSessionOkSequence(Communication communication) {
		communication.setState(manager.down_greatherThenZero);
	}
	
	@Override
	public void messageWithCallbackOkSessionOkSequence(Communication communication) {
		communication.setState(manager.up_greatherThenZero);
	}
	
	@Override
	public void messageNonSequence(Communication communication) throws DiscardMessageException {
		gotoDownEmpty(communication);
	}
	
	@Override
	public void messageNonSession(Communication communication) throws DiscardMessageException {
		gotoDownEmpty(communication);
	}
	
	private void gotoDownEmpty(Communication communication) throws DiscardMessageException {
		communication.invalidateIncoming();
		communication.setState(manager.down_empty);
		throw new DiscardMessageException();
	}

}
