package br.edu.ufcg.lsd.commune.experiments.commune;

import java.util.Map;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.experiments.Util;

public class ReactorMain {

	public static void main(String[] args) throws Exception {
		Map<Integer, String> properties = Util.parseExperimentProperties();
		
		Integer myNumber = new Integer(args[0]);
		
		String myUser = Reactor.REACTOR_USERNAME + myNumber;
		String myServer = properties.get(myNumber);
		String myContainer = Reactor.REACTOR_CONTAINER;
		
		Module module = Util.createModule(myContainer, myUser, myServer);
		module.deploy(Reactor.REACTOR_SERVICE, new ReactorImpl());
	}
}