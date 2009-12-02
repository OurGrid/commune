package br.edu.ufcg.lsd.commune.experiments.rmi;

import java.rmi.Naming;


public class ReactorMain {
	
	
	public static void main(String[] args) throws Exception {
		
		Integer myNumber = new Integer(args[0]);
			
		String myService = Reactor.REACTOR_SERVICE_PREFIX + myNumber;
		
		ReactorImpl peer = new ReactorImpl();
		Naming.rebind(myService, peer);
	}
}