package br.edu.ufcg.lsd.commune.systemtest.tc1;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

public class AReceiver implements A {

	
	private boolean sendMessage;
	private boolean responseReceived;
	private boolean releaseOnFailure;
	private ServiceManager serviceManager;
	

	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
		serviceManager.registerInterest(TestConstants.A_SERVICE, TestConstants.B_ADDRESS, B.class, 3, 1);
	}
	
	public void response() {
		responseReceived = true;
	}
	
	@RecoveryNotification
	public void begin(B b) {
		if (sendMessage) {
			b.message();
		}
	}
	
	@FailureNotification
	public void finish(B b) {
		if(releaseOnFailure) {
			serviceManager.release(b);
		}
	}

	public void setSendMessage(boolean sendMessage) {
		this.sendMessage = sendMessage;
	}

	public boolean isResponseReceived() {
		return responseReceived;
	}

	public void setReleaseOnFailure(boolean releaseOnFailure) {
		this.releaseOnFailure = releaseOnFailure;
		
	}
}