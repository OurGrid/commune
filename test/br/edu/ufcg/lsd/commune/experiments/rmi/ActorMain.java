package br.edu.ufcg.lsd.commune.experiments.rmi;

import java.util.Map;

import br.edu.ufcg.lsd.commune.experiments.Util;


public class ActorMain {
	
	
	public static void main(String[] args) throws Exception {

		Map<Integer, String> properties = Util.parseExperimentProperties();
		
		Actor actor = new Actor(properties);
		actor.init();
	}
}