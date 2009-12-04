package br.edu.ufcg.lsd.commune.experiments.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;



public class ReactorMain {
	
	
	public static void main(String[] args) throws Exception {
		
		Integer myNumber = new Integer(args[0]);
			
		String myService = getReactorUrl(Reactor.LOCALHOST, myNumber);
		
		ReactorImpl peer = new ReactorImpl();
		
		Reactor reactor = (Reactor) UnicastRemoteObject.exportObject(peer);
		Registry registry = LocateRegistry.getRegistry();
		registry.rebind(myService, reactor);
	}

	public static String getReactorUrl(String ip, Integer myNumber) {
		return Reactor.REBIND_PREFIX + ip + ":" + (Reactor.DEFAULT_PORT + myNumber) + "/" + 
			Reactor.REACTOR_SERVICE_PREFIX + myNumber;
	}
}