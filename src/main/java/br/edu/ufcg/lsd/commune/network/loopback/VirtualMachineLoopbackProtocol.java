/*
 * Copyright (C) 2008 Universidade Federal de Campina Grande
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
package br.edu.ufcg.lsd.commune.network.loopback;

import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.message.Message;
import br.edu.ufcg.lsd.commune.network.CommuneNetwork;
import br.edu.ufcg.lsd.commune.network.DiscardMessageException;
import br.edu.ufcg.lsd.commune.network.Protocol;

public class VirtualMachineLoopbackProtocol extends Protocol {

	protected Message message;
	protected ContainerID id;
	

	public VirtualMachineLoopbackProtocol(CommuneNetwork communeNetwork, ContainerID id) {
		super(communeNetwork);
		this.id = id;
	}


	public void setID(ContainerID ID) {
		this.id = ID;
	}
	
	public ContainerID getID() {
		return this.id;
	}
	
	public Message getLastMessage() {
		return this.message;
	}
	
	public void shutdown(){
		LoopbackRegistry.removeModule(id);
	}

    @Override
    public void start() {
		LoopbackRegistry.addModule(id, this);
		protocolStarted();
    }

    @Override
    protected void onReceive(Message message) {}

    @Override
    protected void onSend(Message message) throws DiscardMessageException {
        this.message = message;
        if (message == null)
			return;
		LoopbackRegistry.sendMessage(message);
    }
}