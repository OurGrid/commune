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

/**
 * In this state, the outgoing connection is down and the incoming connection
 * is empty.  
 */
public class Down_Empty extends CommunicationStateAdapter {

	
	public Down_Empty(ConnectionManager connectionManager) {
		super(connectionManager);
	}

	
	@Override
	public void registerInterest(Communication communication) {
		// Maintain state
	}
	
	@Override
	public void release(Communication communication) {
		communication.invalidateOutgoing();
		communication.setState(manager.empty_empty);
	}
	
	@Override
	public void heartbeatOkSessionZeroSequence(Communication communication) {
		communication.setIncomingSequence(0L);
		communication.setState(manager.down_zero);
	}
	
	@Override
	public void updateStatusUp(Communication communication) {
		communication.setState(manager.uping_empty);
	}
	
	@Override
	public void updateStatusDown(Communication communication) {
		// Maintain state
	}
	
	@Override
	public void timeout(Communication communication) {
		// Maintain state
	}
}
