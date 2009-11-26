package br.edu.ufcg.lsd.commune.experiments.commune.scale;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.experiments.commune.Peer;
import br.edu.ufcg.lsd.commune.experiments.commune.PeerImpl;
import br.edu.ufcg.lsd.commune.experiments.commune.Util;

public class PeerMain {

	public static void main(String[] args) throws Exception {
		String server = args[0];
		String jvmNumber = "_" + args[1] + "_";
		
		for (int i = 0; i < Peer.INSTANCES_PER_JVM; i++) {
			String container = Peer.PEER_CONTAINER;
			String user = Peer.PEER_USERNAME + jvmNumber + i;
			
			Module module = Util.createModule(container, user, server);
			module.getContainer().deploy(Peer.PEER_SERVICE, new PeerImpl());
		}
	}
}
