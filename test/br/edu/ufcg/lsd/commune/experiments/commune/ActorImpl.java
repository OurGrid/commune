package br.edu.ufcg.lsd.commune.experiments.commune;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.experiments.Util;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class ActorImpl implements Actor {

	
	private ServiceManager serviceManager;
	private Map<Integer,Long> upReactorsCounter = new HashMap<Integer,Long>();
	private Map<Integer,Reactor> upReactors = new HashMap<Integer,Reactor>();
	
	private int counter = 0;
	private final Map<Integer, String> properties;
	private Actor myself;
	
	
	public ActorImpl(Map<Integer, String> properties) {
		this.properties = properties;
	}


	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
		myself = (Actor) serviceManager.getLocalProxy(ACTOR_SERVICE);

		for (Integer reactorNumber : properties.keySet()) {
			String reactorServer = properties.get(reactorNumber);
			String reactorAddress = 
				Reactor.REACTOR_USERNAME + reactorNumber + "@" + reactorServer + "/" + Reactor.REACTOR_CONTAINER + "/"
				+ Reactor.REACTOR_SERVICE; 
			serviceManager.registerInterest(Actor.ACTOR_SERVICE, reactorAddress, Reactor.class, 600, 60);
		}
		
		new Thread(createRunnable()).start();
	}

	@RecoveryNotification
	public void reactorIsUp(Reactor reactor, DeploymentID reactorDID) {
		ServiceID reactorServiceID = reactorDID.getServiceID();
		Integer reactorNumber = getReactorNumber(reactorServiceID);
		
		upReactors.put(reactorNumber, reactor);
			
		Util.log("reactorIsUp(" + reactorServiceID.getUserName() + ")");
	}

	private Integer getReactorNumber(ServiceID reactorServiceID) {
		String reactorNumberStr = reactorServiceID.getUserName().replace(Reactor.REACTOR_USERNAME, "");
		return new Integer(reactorNumberStr);
	}
	
	@FailureNotification
	public void reactorIsDown(Reactor reactor, DeploymentID reactorDID) {
		ServiceID reactorServiceID = reactorDID.getServiceID();
		Integer reactorNumber = getReactorNumber(reactorServiceID);
		
		upReactors.remove(reactorNumber);
		upReactorsCounter.put(reactorNumber, null);

		Util.log("reactorIsDown(" + reactorServiceID.getUserName() + ")");
	}

	public void pong() {
		ServiceID reactorServiceID = serviceManager.getSenderServiceID();
		Integer reactorNumber = getReactorNumber(reactorServiceID);
		counter++;

		int size = upReactors.size();
		
		Long begin = upReactorsCounter.get(reactorNumber);
		
		if (begin == null) {
			return;
		}
		
		Long finish = System.nanoTime();
		upReactorsCounter.put(reactorNumber, null);

		Long elapsed = finish - begin;
		
		System.out.println(counter + ";" + size + ";" + elapsed);
			
		Util.log("pong()");
	}

	protected Runnable createRunnable() {
		return new Runnable() {
			public void run() {
				
				try {
					while(true) {
						myself.sendPing();
						Util.sleep((1000) / Actor.MESSAGES_PER_SECOND);
						Util.log("run()");
					}
				} catch (Throwable t) {
					System.err.println("Throwable: " + t);
				}
			}
		};
	}

	public void sendPing() {
		choosePeerAndSendPing();
	}
	
	private void choosePeerAndSendPing() {
		int size = upReactors.size();
		
		if (size == 0) {
			return;
		}
		
		int i = (int) (Math.random() * size);
		
		List<Integer> keyList = new ArrayList<Integer>(upReactors.keySet());
		Integer key = keyList.get(i);
		Reactor peer = upReactors.get(key);
		
		Long counter = upReactorsCounter.get(key);
		if (counter == null) {
			counter = System.nanoTime();
		}
		upReactorsCounter.put(key, counter);
		
		peer.ping(this);
	}
}