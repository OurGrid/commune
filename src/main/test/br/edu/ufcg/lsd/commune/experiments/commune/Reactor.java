package br.edu.ufcg.lsd.commune.experiments.commune;

import br.edu.ufcg.lsd.commune.api.Remote;

@Remote
public interface Reactor {

	public static final String REACTOR_CONTAINER = "REACTOR";
	public static final String REACTOR_USERNAME = "reactor_experiment_commune";
	public static final String REACTOR_SERVICE = "REACTOR_IMPL";

	void ping(Actor actor);
	
}