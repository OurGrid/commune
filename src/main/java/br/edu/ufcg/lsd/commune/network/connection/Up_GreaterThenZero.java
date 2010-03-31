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
 * In this state, the outgoing connection is up and the incoming connection
 * has a sequence number greater then zero.  
 */
public class Up_GreaterThenZero extends CommunicationStateAdapter {

	
	public Up_GreaterThenZero(ConnectionManager connectionManager) {
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
	public void heartbeatOkSessionZeroSequence(Communication communication) throws DiscardMessageException {
		gotoDowningEmpty(communication);
	}
	
	@Override
	public void heartbeatOkSessionOkSequence(Communication communication) {
		// Maintain state
	}

	@Override
	public void heartbeatOkSessionNonSequence(Communication communication) throws DiscardMessageException {
		gotoDowningEmpty(communication);
	}
	
	@Override
	public void heartbeatNonSessionZeroSequence(Communication communication) throws DiscardMessageException {
		gotoDowningEmpty(communication);
	}
	
	@Override
	public void heartbeatNonSessionOkSequence(Communication communication) throws DiscardMessageException {
		gotoDowningEmpty(communication);
	}
	
	@Override
	public void heartbeatNonSessionNonSequence(Communication communication) throws DiscardMessageException {
		gotoDowningEmpty(communication);
	}

	@Override
	public void updateStatusUp(Communication communication) {
		// Maintain state
	}
	
	@Override
	public void updateStatusDown(Communication communication) throws DiscardMessageException {
		gotoDowningEmpty(communication);
	}
	
	@Override
	public void updateStatusNonSession(Communication communication) throws DiscardMessageException {
		gotoDowningEmpty(communication);
	}
	
	@Override
	public void timeout(Communication communication) {
		communication.invalidateIncoming();
		communication.setState(manager.downing_empty);
	}
	
	@Override
	public void messageOkSessionOkSequence(Communication communication) {
		// Maintain state
	}
	
	@Override
	public void messageWithCallbackOkSessionOkSequence(Communication communication) {
		// Maintain state
	}
	
	@Override
	public void messageNonSequence(Communication communication) throws DiscardMessageException {
		gotoDowningEmpty(communication);
	}
	
	@Override
	public void messageNonSession(Communication communication) throws DiscardMessageException {
		gotoDowningEmpty(communication);
	}

	private void gotoDowningEmpty(Communication communication) throws DiscardMessageException {
		communication.invalidateIncoming();
		communication.setState(manager.downing_empty);
		forceNotifyFailure(communication);
		throw new DiscardMessageException();
	}
}