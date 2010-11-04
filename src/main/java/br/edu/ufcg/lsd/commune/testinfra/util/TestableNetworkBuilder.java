package br.edu.ufcg.lsd.commune.testinfra.util;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.network.CommuneNetwork;
import br.edu.ufcg.lsd.commune.network.NetworkBuilder;
import br.edu.ufcg.lsd.commune.network.Protocol;

public class TestableNetworkBuilder extends NetworkBuilder{
	
	public TestableNetworkBuilder(){
		super();
	}
	
	@Override
	protected Protocol createXMPPProtocol(Module module, CommuneNetwork communeNetwork) {
        TestableXMPPProtocol xmppProtocol = 
            new TestableXMPPProtocol(communeNetwork, module.getContainerID(), module.getContext(), module.getConnectionListener());
        return xmppProtocol;
	}

}
