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

import br.edu.ufcg.lsd.commune.container.Container;
import br.edu.ufcg.lsd.commune.container.ContainerContext;
import br.edu.ufcg.lsd.commune.network.application.ApplicationProtocol;
import br.edu.ufcg.lsd.commune.network.certification.CertificationProtocol;
import br.edu.ufcg.lsd.commune.network.loopback.VirtualMachineLoopbackProtocol;
import br.edu.ufcg.lsd.commune.network.signature.SignatureProperties;
import br.edu.ufcg.lsd.commune.network.signature.SignatureProtocol;
import br.edu.ufcg.lsd.commune.network.xmpp.XMPPProtocol;

public class NetworkBuilder {

	public CommuneNetwork build(Container container) {
		
		ContainerContext context = container.getContext();
		CommuneNetwork communeNetwork = new CommuneNetwork(container);
		
		XMPPProtocol xmppProtocol = 
			new XMPPProtocol(communeNetwork, container.getContainerID(), container.getContext());

		ApplicationProtocol applicationProtocol = 
			new ApplicationProtocol(communeNetwork, container.getContainerID());
		
		communeNetwork.init(applicationProtocol, xmppProtocol);

		VirtualMachineLoopbackProtocol virtualMachineLoopbackProtocol = 
			new VirtualMachineLoopbackProtocol(communeNetwork, container.getContainerID());
		communeNetwork.addProtocol(virtualMachineLoopbackProtocol);
		
    	String privateKey = context.getProperty(SignatureProperties.PROP_PRIVATE_KEY);
		SignatureProtocol signatureProtocol = 
			new SignatureProtocol(communeNetwork, privateKey);
		communeNetwork.addProtocol(signatureProtocol);
		
		CertificationProtocol certificationProtocol = new CertificationProtocol(
					communeNetwork, container.getMyCertPath());
		
		communeNetwork.addProtocol(certificationProtocol);
		
 		return communeNetwork;
	}
}