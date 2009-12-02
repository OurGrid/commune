package br.edu.ufcg.lsd.commune.experiments.commune;

import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.experiments.Util;

public class ReactorImpl implements Reactor {

	
	private ServiceManager serviceManager;

	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}

	public void ping(Actor actor) {
		actor.pong();
		serviceManager.release(actor);
		Util.log("ping()");
	}
}