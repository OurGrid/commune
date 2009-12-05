package br.edu.ufcg.lsd.commune.experiments.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;



public class ReactorMain {
	
	
	public static void main(String[] args) throws Exception {
		Registry registry = LocateRegistry.getRegistry();
		
		for (String arg : args) {
			Integer myNumber = new Integer(arg);
			String myService = getName(myNumber);
			
			ReactorImpl reactor = new ReactorImpl();
			Reactor stub = (Reactor) UnicastRemoteObject.exportObject(reactor, 0);
			
			registry.rebind(myService, stub);
			System.out.println("Bound: " + stub + " with name " + myService);
		} 
	}

	public static String getName(Integer myNumber) {
		return Reactor.REACTOR_SERVICE_PREFIX + myNumber;
	}
		
}