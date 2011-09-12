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

import br.edu.ufcg.lsd.commune.Module;
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

    public CommuneNetwork build(Module module) {
        ModuleContext context = module.getContext();
        communeNetwork = new CommuneNetwork(module);
        
        Protocol xmppProtocol = createXMPPProtocol(module, communeNetwork);
        Protocol applicationProtocol = createApplicationProtocol(module, communeNetwork);
        communeNetwork.init(applicationProtocol, xmppProtocol);
        
        connectionProtocol = createConnectionProtocol();
        if (connectionProtocol != null) {
        	communeNetwork.addProtocol(connectionProtocol);
        }
        
        VirtualMachineLoopbackProtocol virtualMachineLoopbackProtocol = 
            createLoopbackProtocol(module, communeNetwork);
        if (virtualMachineLoopbackProtocol != null) {
            communeNetwork.addProtocol(virtualMachineLoopbackProtocol);
        }
        
        String privateKey = context.getProperty(SignatureProperties.PROP_PRIVATE_KEY);
        SignatureProtocol signatureProtocol = createSignatureProtocol(communeNetwork, privateKey);
        if (signatureProtocol != null) {
        	communeNetwork.addProtocol(signatureProtocol);
        }
        
        CertificationProtocol certificationProtocol = createCertificationProtocol(module, communeNetwork);
        if (certificationProtocol != null) {
        	communeNetwork.addProtocol(certificationProtocol);
        }
        
        return communeNetwork;
    }

	protected ConnectionProtocol createConnectionProtocol() {
		return new ConnectionProtocol(communeNetwork);
	}

    protected CertificationProtocol createCertificationProtocol(Module module, CommuneNetwork communeNetwork) {
        return new CertificationProtocol(communeNetwork, module.getMyCertPath());
    }

    protected SignatureProtocol createSignatureProtocol(CommuneNetwork communeNetwork, String privateKey) {
        return new SignatureProtocol(communeNetwork, privateKey);
    }

    protected VirtualMachineLoopbackProtocol createLoopbackProtocol(Module module, CommuneNetwork communeNetwork) {
        return new VirtualMachineLoopbackProtocol(communeNetwork, module.getContainerID());
    }

    protected ApplicationProtocol createApplicationProtocol(Module module, CommuneNetwork communeNetwork) {
        return new ApplicationProtocol(communeNetwork, module.getContainerID());
    }

    protected Protocol createXMPPProtocol(Module module, CommuneNetwork communeNetwork) {
        XMPPProtocol xmppProtocol = 
            new XMPPProtocol(communeNetwork, module.getContainerID(), module.getContext(), module.getConnectionListener());
        return xmppProtocol;
    }

	public void configure(Module module) {
		if (connectionProtocol != null) {
			connectionProtocol.configure(module);
		}
	}

}