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
 * has a sequence number equal zero.  
 */
public class Down_Zero extends ConnectionStateAdapter {

	public Down_Zero(ConnectionManager connectionManager) {
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
	public void heartbeatOkSessionZeroSequence(Connection connection) {
		// Maintain state
	}
	
	@Override
	public void heartbeatOkSessionOkSequence(Connection connection) {
		gotoDownZero(connection);
	}
	
	@Override
	public void heartbeatOkSessionNonSequence(Connection connection) {
		gotoDownZero(connection);
	}
	
	@Override
	public void heartbeatNonSessionZeroSequence(Connection connection) {
		gotoDownZero(connection);
	}
	
	@Override
	public void heartbeatNonSessionOkSequence(Connection connection) {
		gotoDownZero(connection);
	}
	
	@Override
	public void heartbeatNonSessionNonSequence(Connection connection) {
		gotoDownZero(connection);
	}
	
	@Override
	public void updateStatusUp(Connection connection) {
		connection.setState(manager.uping_zero);
	}
	
	@Override
	public void updateStatusDown(Connection connection) {
		gotoDownZero(connection);
	}
	
	@Override
	public void updateStatusNonSession(Connection connection) {
		gotoDownZero(connection);
	}
	
	@Override
	public void timeout(Connection connection) {
		gotoDownZero(connection);
	}
	
	@Override
	public void messageOkSessionOkSequence(Connection connection) {
		connection.setState(manager.down_greatherThenZero);
	}
	
	@Override
	public void messageWithCallbackOkSessionOkSequence(Connection connection) {
		connection.setState(manager.up_greatherThenZero);
	}
	
	@Override
	public void messageNonSequence(Connection connection) {
		gotoDownZero(connection);
	}
	
	@Override
	public void messageNonSession(Connection connection) {
		gotoDownZero(connection);
	}
	
	private void gotoDownZero(Connection connection) {
		connection.setIncomingSequence(null);
		connection.setIncomingSession(null);
		connection.setState(manager.down_zero);
		
		//TODO interromper mensagem
	}

}
