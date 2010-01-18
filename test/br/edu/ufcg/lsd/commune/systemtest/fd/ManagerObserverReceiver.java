package br.edu.ufcg.lsd.commune.systemtest.fd;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.control.ModuleManager;

public class ManagerObserverReceiver implements ManagerObserver {

	boolean recovered = false;
	
	@RecoveryNotification
	public void notifyManagerRecovery(ModuleManager moduleManager) {
		System.out.println("Recovery");
		recovered = true;
	}
	
	@FailureNotification
	public void notifyManagerFailure(ModuleManager moduleManager) {
		System.out.println("Failure");
		recovered = false;
	}

	public boolean hasRecovered() {
		return recovered;
	}
}
