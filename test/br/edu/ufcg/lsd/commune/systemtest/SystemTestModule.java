package br.edu.ufcg.lsd.commune.systemtest;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.Container;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.network.CommuneNetwork;
import br.edu.ufcg.lsd.commune.network.Protocol;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;

public class SystemTestModule extends Module {

	public SystemTestModule(String containerName, ModuleContext context) 
			throws CommuneNetworkException, ProcessorStartException {
		super(containerName, context);
	}

	@Override
	protected Container createContainer(String containerName, ModuleContext context) {
		return new SystemTestContainer(this, containerName, context);
	}
	
    public void addProtocol(Protocol protocol) {
		getCommuneNetwork().addProtocol(protocol);
    }

	public CommuneNetwork getCommuneNetwork() {
        SystemTestContainer container = (SystemTestContainer) getContainer();
		return container.getCommuneNetwork();
	}
}
