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

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.message.Message;
import br.edu.ufcg.lsd.commune.network.CommuneNetwork;
import br.edu.ufcg.lsd.commune.network.DiscardMessageException;
import br.edu.ufcg.lsd.commune.network.Protocol;

public class ConnectionProtocol extends Protocol {

	private ConnectionManager connectionManager;
	
	public ConnectionProtocol(CommuneNetwork communeNetwork) {
		super(communeNetwork);
		connectionManager = new ConnectionManager(communeNetwork.getModule());
	}


	protected synchronized void onReceive(Message message) throws DiscardMessageException {
		if (message == null) {
			throw new DiscardMessageException();
		}

		connectionManager.receivingMessage(message);
	}

	protected synchronized void onSend(Message message) throws DiscardMessageException {
		if (message == null) {
			throw new DiscardMessageException();
		}

		connectionManager.sendingMessage(message);

	}

	public Communication getCommunication(String address) {
		ServiceID serviceID = ServiceID.parse(address);
		return connectionManager.getConnection(serviceID.getContainerID());
	}
	public void configure(Module module) {
		connectionManager.configure(module);
		
	}

}
