package br.edu.ufcg.lsd.commune.experiments.rmi;

import java.rmi.Naming;


public class ReactorMain {
	
	
	public static void main(String[] args) throws Exception {
		
		Integer myNumber = new Integer(args[0]);
			
		String myService = getReactorUrl(Reactor.LOCALHOST, myNumber);
		
		ReactorImpl peer = new ReactorImpl();
		Naming.rebind(myService, peer);
	}

	public static String getReactorUrl(String ip, Integer myNumber) {
		return Reactor.REBIND_PREFIX + ip + ":" + (Reactor.DEFAULT_PORT + myNumber) + "/" + 
			Reactor.REACTOR_SERVICE_PREFIX + myNumber;
	}
}