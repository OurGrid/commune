package br.edu.ufcg.lsd.commune.systemtest.tc1;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

public class AReceiver implements A {


	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		serviceManager.registerInterest(TestConstants.A_SERVICE, TestConstants.B_ADDRESS, B.class);
	}
	
	public void response() {}
	
	@RecoveryNotification
	public void begin(B b) {
		b.message();
	}
	
	@FailureNotification
	public void finish(B b) {}
}
