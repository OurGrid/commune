package br.edu.ufcg.lsd.commune.systemtest;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.network.CommuneNetwork;
import br.edu.ufcg.lsd.commune.network.NetworkBuilder;
import br.edu.ufcg.lsd.commune.network.certification.CertificationProtocol;
import br.edu.ufcg.lsd.commune.network.connection.ConnectionProtocol;
import br.edu.ufcg.lsd.commune.network.loopback.VirtualMachineLoopbackProtocol;

public class SystemTestNetworkBuilder extends NetworkBuilder {

    private ConnectionProtocol connectionProtocol;

	@Override
    public CommuneNetwork build(Module module) {
        return super.build(module);
    }
    
    
    /**
     * Do not create the loopback protocol to force the messages to cross all the protocol stack
     */
    @Override
    protected VirtualMachineLoopbackProtocol createLoopbackProtocol(Module module, 
    		CommuneNetwork communeNetwork) {
    	return null;
    }
    
    @Override
    protected ConnectionProtocol createConnectionProtocol() {
    	connectionProtocol = new ConnectionProtocol(communeNetwork);
		return connectionProtocol;
    }
    
    @Override
    protected CertificationProtocol createCertificationProtocol(Module module, CommuneNetwork communeNetwork) {
    	return new SystemTestCertificationProtocol(communeNetwork, module.getMyCertPath());
    }


	public ConnectionProtocol getConnectionProtocol() {
		return connectionProtocol;
	}
}