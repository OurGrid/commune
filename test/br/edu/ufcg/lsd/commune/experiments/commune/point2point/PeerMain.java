package br.edu.ufcg.lsd.commune.experiments.commune.point2point;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.experiments.commune.Peer;
import br.edu.ufcg.lsd.commune.experiments.commune.PeerImpl;
import br.edu.ufcg.lsd.commune.experiments.commune.Util;

public class PeerMain {

	public static void main(String[] args) throws Exception {
		String server = args[0];
		String jvmNumber = "_" + args[1];
		
		String container = Peer.PEER_CONTAINER;
		String user = Peer.PEER_USERNAME + jvmNumber;
		
		Module module = Util.createModule(container, user, server);
		module.getContainer().deploy(Peer.PEER_SERVICE, new PeerImpl());
	}
}
