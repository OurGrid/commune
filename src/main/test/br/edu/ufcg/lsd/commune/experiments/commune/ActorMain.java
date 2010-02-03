package br.edu.ufcg.lsd.commune.experiments.commune;

import java.util.Map;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.experiments.Util;

public class ActorMain {

	public static void main(String[] args) throws Exception {
		Map<Integer, String> properties = Util.parseExperimentProperties();
		
		String myUser = Actor.ACTOR_USERNAME;
		String myServer = Actor.ACTOR_SERVERNAME;
		String myContainer = Actor.ACTOR_CONTAINER;
		
		Module module = Util.createModule(myContainer, myUser, myServer);
		module.getContainer().deploy(Actor.ACTOR_SERVICE, new ActorImpl(properties));
	}
}