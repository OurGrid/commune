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
 * In this state, the outgoing connection is downing and the incoming connection
 * is empty.  
 */
public class Downing_Empty extends CommunicationStateAdapter {


	public Downing_Empty(ConnectionManager connectionManager) {
		super(connectionManager);
	}

	
	@Override
	public void registerInterest(Communication communication) {
		// Maintain state
	}

	@Override
	public void release(Communication communication) {
		communication.invalidateIncoming();
		communication.setState(manager.empty_empty);
	}

	@Override
	public void notifyFailure(Communication communication) {
		communication.setOutgoingSession(generateSessionNumber());
		communication.setOutgoingSequence(0L);
		communication.setState(manager.down_empty);
	}
}
