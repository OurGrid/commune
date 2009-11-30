package br.edu.ufcg.lsd.commune.experiments.commune;

import br.edu.ufcg.lsd.commune.api.Remote;

@Remote
public interface Peer {

	public static final int INSTANCES_PER_JVM = 10;
	public static final String PEER_CONTAINER = "PEER";
	public static final String PEER_USERNAME = "peer_experiment_commune";
	public static final String PEER_SERVICE = "PEER_IMPL";
	public static final int MESSAGES_PER_MINUTE = 30;

	void ping();
	
	void pong(); 
	
}
