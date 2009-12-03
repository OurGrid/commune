package br.edu.ufcg.lsd.commune.experiments.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Reactor extends Remote {

	public static final int MESSAGES_PER_SECOND = 30;

	public static final String REACTOR_SERVICE_PREFIX = "Peer";
	public static final String REACTOR_IP_SUFIX = ":1099/";
	public static final String REACTOR_IP_PREFIX = "rmi://";
	
	public static final String LOCALHOST = "localhost";
	
	public static final String REBIND_PREFIX = "rmi://";
	public static final int DEFAULT_PORT = 1099;

	String ping() throws RemoteException;
	
}