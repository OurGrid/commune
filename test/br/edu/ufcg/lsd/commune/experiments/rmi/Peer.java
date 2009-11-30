package br.edu.ufcg.lsd.commune.experiments.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Peer extends Remote {

	public static final int MESSAGES_PER_MINUTE = 30;
	public static final String PEER_1 = "Peer1";
	public static final String PEER_2 = "Peer2";

	public static final String PEER1_SERVERNAME = "rmi://150.165.85.5:1099/";
	public static final String PEER2_SERVERNAME = "rmi://150.165.85.X:1099/"; //TODO

	String ping(String text) throws RemoteException;
	
	void init() throws RemoteException;
}
