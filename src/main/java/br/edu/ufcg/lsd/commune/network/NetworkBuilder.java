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
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.network.application.ApplicationProtocol;
import br.edu.ufcg.lsd.commune.network.certification.CertificationProtocol;
import br.edu.ufcg.lsd.commune.network.connection.ConnectionProtocol;
import br.edu.ufcg.lsd.commune.network.loopback.VirtualMachineLoopbackProtocol;
import br.edu.ufcg.lsd.commune.network.signature.SignatureProperties;
import br.edu.ufcg.lsd.commune.network.signature.SignatureProtocol;
import br.edu.ufcg.lsd.commune.network.xmpp.XMPPProtocol;

public class NetworkBuilder {

    protected CommuneNetwork communeNetwork;
	private ConnectionProtocol connectionProtocol;

    public CommuneNetwork build(Container container) {
        ModuleContext context = container.getContext();
        communeNetwork = new CommuneNetwork(container);
        
        Protocol xmppProtocol = createXMPPProtocol(container, communeNetwork);
        Protocol applicationProtocol = createApplicationProtocol(container, communeNetwork);
        communeNetwork.init(applicationProtocol, xmppProtocol);
        
        connectionProtocol = createConnectionProtocol();
        if (connectionProtocol != null) {
        	communeNetwork.addProtocol(connectionProtocol);
        }

        VirtualMachineLoopbackProtocol virtualMachineLoopbackProtocol = 
            createLoopbackProtocol(container, communeNetwork);
        if (virtualMachineLoopbackProtocol != null) {
            communeNetwork.addProtocol(virtualMachineLoopbackProtocol);
        }
        
        String privateKey = context.getProperty(SignatureProperties.PROP_PRIVATE_KEY);
        SignatureProtocol signatureProtocol = createSignatureProtocol(communeNetwork, privateKey);
        if (signatureProtocol != null) {
            communeNetwork.addProtocol(signatureProtocol);
        }
        
        CertificationProtocol certificationProtocol = createCertificationProtocol(container, communeNetwork);
        if (certificationProtocol != null) {
            communeNetwork.addProtocol(certificationProtocol);
        }
        
         return communeNetwork;
    }

	protected ConnectionProtocol createConnectionProtocol() {
		return new ConnectionProtocol(communeNetwork);
	}

    protected CertificationProtocol createCertificationProtocol(Container container, CommuneNetwork communeNetwork) {
        return new CertificationProtocol(communeNetwork, container.getMyCertPath());
    }

    protected SignatureProtocol createSignatureProtocol(CommuneNetwork communeNetwork, String privateKey) {
        return new SignatureProtocol(communeNetwork, privateKey);
    }

    protected VirtualMachineLoopbackProtocol createLoopbackProtocol(Container container, CommuneNetwork communeNetwork) {
        return new VirtualMachineLoopbackProtocol(communeNetwork, container.getContainerID());
    }

    protected ApplicationProtocol createApplicationProtocol(Container container, CommuneNetwork communeNetwork) {
        return new ApplicationProtocol(communeNetwork, container.getContainerID());
    }

    protected Protocol createXMPPProtocol(Container container, CommuneNetwork communeNetwork) {
        XMPPProtocol xmppProtocol = 
            new XMPPProtocol(communeNetwork, container.getContainerID(), container.getContext());
        return xmppProtocol;
    }

	public void configure(Container container) {
		if (connectionProtocol != null) {
			connectionProtocol.configure(container);
		}
	}

}