package br.edu.ufcg.lsd.commune.experiments.commune;

import java.util.Map;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.experiments.Util;

public class PeerMain {

	public static void main(String[] args) throws Exception {
		Map<Integer, String> properties = Util.parseExperimentProperties();
		
		for (int i = 0; i < args.length; i++) {
			Integer myNumber = new Integer(args[i]);
			
			String myUser = Peer.PEER_USERNAME + myNumber;
			String myServer = properties.get(myNumber);
			String myContainer = Peer.PEER_CONTAINER;
			
			Module module = Util.createModule(myContainer, myUser, myServer);
			module.getContainer().deploy(Peer.PEER_SERVICE, new PeerImpl(myNumber, properties));
			
			sleep();
		}
		
	}
	
	private static void sleep() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
