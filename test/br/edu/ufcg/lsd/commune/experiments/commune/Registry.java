package br.edu.ufcg.lsd.commune.experiments.commune;

import br.edu.ufcg.lsd.commune.api.Remote;

@Remote
public interface Registry {

	public static final String REGISTRY_SERVERNAME = "150.165.85.5";
	public static final String REGISTRY_USERNAME = "registry_experiment_commune";
	public static final String REGISTRY_SERVICE = "REGISTRY_IMPL";
	public static final String REGISTRY_CONTAINER = "REGISTRY";


	void getPeerList(Peer peer);
}
