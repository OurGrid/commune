package br.edu.ufcg.lsd.commune.processor.objectdeployer;

import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.network.DiscardMessageException;

public interface NotificationListener {

	void notifyFailure(ServiceID serviceID) throws DiscardMessageException;

	void notifyRecovery(ServiceID serviceID) throws DiscardMessageException;

}
