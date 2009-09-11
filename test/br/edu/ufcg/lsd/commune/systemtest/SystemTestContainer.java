package br.edu.ufcg.lsd.commune.systemtest;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.Container;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.network.CommuneNetwork;
import br.edu.ufcg.lsd.commune.network.NetworkBuilder;
import br.edu.ufcg.lsd.commune.network.connection.ConnectionProtocol;

public class SystemTestContainer extends Container {

	private SystemTestNetworkBuilder networkBuilder;

	public SystemTestContainer(Module application, String containerName,
			ModuleContext context) {
		super(application, containerName, context);
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