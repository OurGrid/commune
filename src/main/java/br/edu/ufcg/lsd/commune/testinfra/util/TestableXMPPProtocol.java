package br.edu.ufcg.lsd.commune.testinfra.util;

import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.network.CommuneNetwork;
import br.edu.ufcg.lsd.commune.network.ConnectionListener;
import br.edu.ufcg.lsd.commune.network.xmpp.XMPPProtocol;

public class TestableXMPPProtocol extends XMPPProtocol {

	public TestableXMPPProtocol(CommuneNetwork communicationLayer, ContainerID identification, 
			ModuleContext context, ConnectionListener connectionListener) {
		super(communicationLayer, identification, context, connectionListener);
	}
	
	@Override
	protected int getSleepTime() {
		return 1000;
	}

}
