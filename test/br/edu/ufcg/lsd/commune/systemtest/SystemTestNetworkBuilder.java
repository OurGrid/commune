package br.edu.ufcg.lsd.commune.systemtest;

import br.edu.ufcg.lsd.commune.container.Container;
import br.edu.ufcg.lsd.commune.network.CommuneNetwork;
import br.edu.ufcg.lsd.commune.network.NetworkBuilder;
import br.edu.ufcg.lsd.commune.network.certification.CertificationProtocol;
import br.edu.ufcg.lsd.commune.network.connection.ConnectionProtocol;
import br.edu.ufcg.lsd.commune.network.loopback.VirtualMachineLoopbackProtocol;

public class SystemTestNetworkBuilder extends NetworkBuilder {

    private ConnectionProtocol connectionProtocol;

	@Override
    public CommuneNetwork build(Container container) {
        return super.build(container);
    }
    
    
    /**
     * Do not create the loopback protocol to force the messages to cross all the protocol stack
     */
    @Override
    protected VirtualMachineLoopbackProtocol createLoopbackProtocol(Container container, 
    		CommuneNetwork communeNetwork) {
    	return null;
    }
    
    @Override
    protected ConnectionProtocol createConnectionProtocol() {
    	connectionProtocol = new ConnectionProtocol(communeNetwork);
		return connectionProtocol;
    }
    
    @Override
    protected CertificationProtocol createCertificationProtocol(Container container, CommuneNetwork communeNetwork) {
    	return new SystemTestCertificationProtocol(communeNetwork, container.getMyCertPath());
    }


	public ConnectionProtocol getConnectionProtocol() {
		return connectionProtocol;
	}
}