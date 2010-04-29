package br.edu.ufcg.lsd.commune.experiments.commune;

import java.util.Map;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.experiments.Util;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class ActorImpl implements Actor {

	
	private long counter = 0;
	private long begin;
	private final Map<Integer, String> properties;
	private Reactor reactor;
	
	
	public ActorImpl(Map<Integer, String> properties) {
		this.properties = properties;
	}


	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		for (Integer reactorNumber : properties.keySet()) {
			String reactorServer = properties.get(reactorNumber);
			String reactorAddress = 
				Reactor.REACTOR_USERNAME + reactorNumber + "@" + reactorServer + "/" + Reactor.REACTOR_CONTAINER + "/"
				+ Reactor.REACTOR_SERVICE; 
			serviceManager.registerInterest(Actor.ACTOR_SERVICE, reactorAddress, Reactor.class, 600, 60);
		}
	}

	@RecoveryNotification
	public void reactorIsUp(Reactor reactor, DeploymentID reactorDID) {
		ServiceID reactorServiceID = reactorDID.getServiceID();
		Util.log("reactorIsUp(" + reactorServiceID.getUserName() + ")");
		counter = 0;
		begin = System.nanoTime();
		
		reactor.ping(this);
	}

	@FailureNotification
	public void reactorIsDown(Reactor reactor, DeploymentID reactorDID) {
		ServiceID reactorServiceID = reactorDID.getServiceID();
		counter = 0;
		reactor = null;
		Util.log("reactorIsDown(" + reactorServiceID.getUserName() + ")");
	}

	public void pong() {
		counter++;
		
		Long finish = System.nanoTime();
		Long elapsed = finish - begin;
		
		System.out.println(counter + ";" + elapsed);

		if (reactor != null){
			begin = System.nanoTime();
			reactor.ping(this);
		}
			
		Util.log("pong()");
	}
}