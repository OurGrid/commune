package br.edu.ufcg.lsd.commune.systemtest.tc1;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

import static br.edu.ufcg.lsd.commune.systemtest.tc1.TestConstants.*;

public class AReceiver implements A {

	
	private boolean sendMessage;
	private boolean responseReceived;
	private boolean releaseOnFailure;
	private boolean useCallback;
	private ServiceManager serviceManager;
	

	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
		serviceManager.registerInterest(A_SERVICE, B_ADDRESS, B.class, DETECTION_TIME, HEARTBEAT_DELAY);
	}
	
	public void response() {
		responseReceived = true;
	}
	
	@RecoveryNotification
	public void begin(B b) {
		if (sendMessage) {
			if (useCallback) {
				b.message(this);
				
			} else {
				b.message();
			}
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

	public void setUseCallback(boolean useCallback) {
		this.useCallback = useCallback;
	}
}