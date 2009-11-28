package br.edu.ufcg.lsd.commune.experiments.rmi.point2point;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface Registry extends Remote {

	public static final String REGISTRY_SERVERNAME = "rmi://localhost:1099/";
	public static final String REGISTRY_SERVICE = "REGISTRY_IMPL";

	void join(String myName, Peer myRef) throws RemoteException;

	Peer getOtherPeer(String myName) throws RemoteException;
}
