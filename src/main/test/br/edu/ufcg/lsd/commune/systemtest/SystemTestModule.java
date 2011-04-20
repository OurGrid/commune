package br.edu.ufcg.lsd.commune.systemtest;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.network.CommuneNetwork;
import br.edu.ufcg.lsd.commune.network.NetworkBuilder;
import br.edu.ufcg.lsd.commune.network.Protocol;
import br.edu.ufcg.lsd.commune.network.connection.ConnectionProtocol;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;

public class SystemTestModule extends Module {

	
	private SystemTestNetworkBuilder networkBuilder;

	
	public SystemTestModule(String containerName, ModuleContext context) 
			throws CommuneNetworkException, ProcessorStartException {
		super(containerName, context);
	}

	
    public void addProtocol(Protocol protocol) {
		getCommuneNetwork().addProtocol(protocol);
    }

	@Override
	public NetworkBuilder createNetworkBuilder() {
		networkBuilder = new SystemTestNetworkBuilder();
		return networkBuilder;
	}
	
    public CommuneNetwork getCommuneNetwork() {
    	return this.communeNetwork;
    }
    
	public ConnectionProtocol getConnectionProtocol() {
		return networkBuilder.getConnectionProtocol();
	}
}
