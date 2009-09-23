package br.edu.ufcg.lsd.commune.systemtest.tc1;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.api.MonitoredBy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import static br.edu.ufcg.lsd.commune.systemtest.tc1.TestConstants.*;

public class BReceiver implements B {

	
	private boolean sendResponse;
	private boolean releaseOnFailure;
	private ServiceManager serviceManager;
	
	
	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}

	
	public void message() {
		
	}

	public void message(
			@MonitoredBy(value = B_SERVICE, detectionTime = DETECTION_TIME, heartBeatDelay = HEARTBEAT_DELAY) 
			A a) {
		
		if (sendResponse) {
			a.response();
		}
	}

	public void setSendResponse(boolean sendResponse) {
		this.sendResponse = sendResponse;
	}
	
	public void setReleaseOnFailure(boolean releaseOnFailure) {
		this.releaseOnFailure = releaseOnFailure;
		
	}

	
	@RecoveryNotification
	public void recover(A a) {
		
	}
	
	@FailureNotification
	public void fail(A a) {
		if(releaseOnFailure) {
			serviceManager.release(a);
		}
	}
}