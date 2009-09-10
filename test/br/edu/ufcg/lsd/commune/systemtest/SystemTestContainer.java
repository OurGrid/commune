package br.edu.ufcg.lsd.commune.systemtest;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.Container;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.network.NetworkBuilder;
import br.edu.ufcg.lsd.commune.network.Protocol;

public class SystemTestContainer extends Container {

	public SystemTestContainer(Module application, String containerName,
			ModuleContext context) {
		super(application, containerName, context);
	}

	
	@Override
	public NetworkBuilder createNetworkBuilder() {
		return new SystemTestNetworkBuilder();
	}
	
    public void addProtocol(Protocol protocol) {
        communeNetwork.addProtocol(protocol);
    }
}