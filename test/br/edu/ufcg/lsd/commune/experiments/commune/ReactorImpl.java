package br.edu.ufcg.lsd.commune.experiments.commune;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.api.MonitoredBy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.experiments.Util;

public class ReactorImpl implements Reactor {

	
	private ServiceManager serviceManager;

	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}

	public void ping(@MonitoredBy(REACTOR_SERVICE)Actor actor) {
		actor.pong();
		Util.log("ping()");
	}
	
	@RecoveryNotification
	public void actorIsUp(Actor actor) {}

	@FailureNotification
	public void actorIsDown(Actor actor) {
		serviceManager.release(actor);
	}	
}