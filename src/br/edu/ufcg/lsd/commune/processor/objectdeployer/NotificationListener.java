package br.edu.ufcg.lsd.commune.processor.objectdeployer;

import br.edu.ufcg.lsd.commune.identification.ServiceID;

public interface NotificationListener {

	void notifyFailure(ServiceID serviceID);

	void notifyRecovery(ServiceID serviceID);

}
