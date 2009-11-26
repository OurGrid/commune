package br.edu.ufcg.lsd.commune.experiments.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface Registry extends Remote {

	public static final String REGISTRY_SERVERNAME = "150.165.85.5";
	public static final String REGISTRY_USERNAME = "registry_experiment_commune";
	public static final String REGISTRY_SERVICE = "REGISTRY_IMPL";
	public static final String REGISTRY_CONTAINER = "REGISTRY";


	void getPeerList() throws RemoteException;//Peer peer);
}
