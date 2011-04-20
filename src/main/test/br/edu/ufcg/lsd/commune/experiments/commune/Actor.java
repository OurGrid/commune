package br.edu.ufcg.lsd.commune.experiments.commune;

import br.edu.ufcg.lsd.commune.api.Remote;

@Remote
public interface Actor {

	public static final String ACTOR_CONTAINER = "ACTOR";
	public static final String ACTOR_USERNAME = "actor_experiment_commune";
	public static final String ACTOR_SERVERNAME = "xmpp.ourgrid.org";
	public static final String ACTOR_SERVICE = "ACTOR_IMPL";

	void pong(); 
	
}
