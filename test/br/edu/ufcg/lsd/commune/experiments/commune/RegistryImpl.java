package br.edu.ufcg.lsd.commune.experiments.commune;

import java.util.ArrayList;
import java.util.List;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.api.MonitoredBy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.experiments.Util;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class RegistryImpl implements Registry {

	
	private List<String> peers = new ArrayList<String>();
	private ServiceManager serviceManager;
	
	
	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}
	
	public void getPeerList(@MonitoredBy(value=REGISTRY_SERVICE) Peer peer) {
		ServiceID senderServiceID = serviceManager.getSenderServiceID();
		String peerID = senderServiceID.toString();
		
		if (!peers.contains(peerID)) {
			peers.add(peerID);
		}
		
		peer.updateList(peers);
		serviceManager.release(peer);
		
		Util.log("registry->getPeerList(" + senderServiceID.getUserName() + ")");
	}

	@RecoveryNotification
	public void peerIsUp(Peer peer) {}
	
	@FailureNotification
	public void peerIsDown(Peer peer) {}
}
