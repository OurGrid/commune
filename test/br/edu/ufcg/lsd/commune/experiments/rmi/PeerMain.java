package br.edu.ufcg.lsd.commune.experiments.rmi;

import java.rmi.Naming;
import java.util.Map;

import br.edu.ufcg.lsd.commune.experiments.Util;


public class PeerMain {
	
	
	public static void main(String[] args) throws Exception {
		
		Map<Integer, String> properties = Util.parseExperimentProperties();
		
		for (int i = 0; i < args.length; i++) {
			Integer myNumber = new Integer(args[0]);
			
			String myService = Peer.PEER_SERVICE_PREFIX + myNumber;
			
			PeerImpl peer = new PeerImpl(myNumber, properties);
			Naming.rebind(myService, peer);
			peer.init();
		}
	}
}