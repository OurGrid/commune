package br.edu.ufcg.lsd.commune.systemtest;

import br.edu.ufcg.lsd.commune.container.Container;
import br.edu.ufcg.lsd.commune.network.CommuneNetwork;
import br.edu.ufcg.lsd.commune.network.NetworkBuilder;
import br.edu.ufcg.lsd.commune.network.Protocol;

public class SystemTestNetworkBuilder extends NetworkBuilder {

    @Override
    public CommuneNetwork build(Container container) {
        return super.build(container);
    }
    
    
    public void addProtocol(Protocol protocol) {
        communeNetwork.addProtocol(protocol);
    }
}