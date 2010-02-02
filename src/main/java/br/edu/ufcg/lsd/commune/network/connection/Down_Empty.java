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
public class Down_Empty extends ConnectionStateAdapter {

	
	public Down_Empty(ConnectionManager connectionManager) {
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
		connection.setState(manager.initialState);
	}
	
	@Override
	public void heartbeatOkSessionZeroSequence(Connection connection) {
		connection.setIncomingSequence(0L);
		connection.setState(manager.down_zero);
	}
	
	@Override
	public void updateStatusUp(Connection connection) {
		connection.setState(manager.uping_empty);
	}
	
	@Override
	public void updateStatusDown(Connection connection) {
		// Maintain state
	}
	
	@Override
	public void timeout(Connection connection) {
		// Maintain state
	}
}
