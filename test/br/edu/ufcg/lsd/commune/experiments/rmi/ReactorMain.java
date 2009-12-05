package br.edu.ufcg.lsd.commune.experiments.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;



public class ReactorMain {
	
	
	public static void main(String[] args) throws Exception {
		
		Integer myNumber = new Integer(args[0]);
		String myService = getName(myNumber);
		
		ReactorImpl reactor = new ReactorImpl();
		Reactor stub = (Reactor) UnicastRemoteObject.exportObject(reactor, 0);
		
		Registry registry = LocateRegistry.getRegistry();
		registry.rebind(myService, stub);
	}

	public static String getName(Integer myNumber) {
		return Reactor.REACTOR_SERVICE_PREFIX + myNumber;
	}
		
}