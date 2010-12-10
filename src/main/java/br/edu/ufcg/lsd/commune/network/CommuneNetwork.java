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
package br.edu.ufcg.lsd.commune.network;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.IMessageSender;
import br.edu.ufcg.lsd.commune.message.Message;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.network.xmpp.XMPPProtocol;

public class CommuneNetwork implements IMessageSender {
	
	private Protocol applicationProtocol;
	private Protocol xmppProtocol;
	private List<Protocol> protocolChain = new ArrayList<Protocol>();
	private Module module;
	
	public CommuneNetwork(Module module) {
		this.module = module;
	}
	
	public void init(Protocol applicationProtocol, Protocol xmppProtocol) {
		this.applicationProtocol = applicationProtocol;
		this.xmppProtocol = xmppProtocol;
		this.protocolChain.add(this.applicationProtocol);
		this.protocolChain.add(this.xmppProtocol);
		applicationProtocol.setNextProtocol(xmppProtocol);
		xmppProtocol.setPreviousProtocol(applicationProtocol);
	}

	public void addProtocol(Protocol protocol) {

		if (protocol != null) {
			Protocol previousProtocol = this.protocolChain.get(this.protocolChain.size() - 2);
			previousProtocol.setNextProtocol(protocol);

			protocol.setPreviousProtocol(previousProtocol);
			protocol.setNextProtocol(xmppProtocol);

			xmppProtocol.setPreviousProtocol(protocol);

			this.protocolChain.add(this.protocolChain.size() - 1, protocol);

		} else {
			throw new NullPointerException();
		}
	}

	public void sendMessage(Message message) {		
		this.protocolChain.get(0).sendMessage(message);		
	}

    public void start() throws CommuneNetworkException {
    	for (Iterator<Protocol> iterator = protocolChain.iterator(); iterator.hasNext();) {
			final Protocol protocol = iterator.next();
			protocol.addCreationListener(new ProtocolCreationListener() {
				
				public void started() {
					try {
						if(protocol.nextProtocol != null){
							protocol.nextProtocol.start();
						}
					} catch (CommuneNetworkException e) {
						module.getConnectionListener().connectionFailed(e);
					}
				}
			});
		}
    	if(!protocolChain.isEmpty()){
    		protocolChain.iterator().next().start();
    	}
    }
    
    public void addProtocolChainStartedListener(ProtocolCreationListener listener){
    	if(!protocolChain.isEmpty()){
    		protocolChain.get(protocolChain.size()-1).addCreationListener(listener);
    	}
    }

    public void shutdown() throws CommuneNetworkException {
    	for (Iterator<Protocol> iterator = protocolChain.iterator(); iterator.hasNext();) {
			Protocol protocol = iterator.next();
			protocol.shutdown();
		}
    }

//	public void notifyError(ProtocolException handlerException) {
//		container.notifyError(handlerException);
//	}

	public void deliverMessage(Message message) {
		module.deliverMessage(message);
	}
	
	public XMPPProtocol getXMPPProtocol() {
		return (XMPPProtocol) this.xmppProtocol;
	}
	
	public Module getModule() {
		return module; 
	}
}