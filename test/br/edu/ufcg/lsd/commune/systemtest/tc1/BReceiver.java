package br.edu.ufcg.lsd.commune.systemtest.tc1;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.MonitoredBy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import static br.edu.ufcg.lsd.commune.systemtest.tc1.TestConstants.*;

public class BReceiver implements B {

	
	private boolean sendResponse;
	
	
	public void message() {
		
	}

	public void message(@MonitoredBy(B_SERVICE) A a) {
		if (sendResponse) {
			a.response();
		}
	}

	public void setSendResponse(boolean sendResponse) {
		this.sendResponse = sendResponse;
	}
	
	@RecoveryNotification
	public void recover(A a) {
		
	}
	
	@FailureNotification
	public void fail(A a) {
		
	}
}