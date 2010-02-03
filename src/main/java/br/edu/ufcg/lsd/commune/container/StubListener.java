package br.edu.ufcg.lsd.commune.container;

import br.edu.ufcg.lsd.commune.identification.ServiceID;

public interface StubListener {

	void stubCreated(StubReference stubReference);

	void stubReleased(ServiceID stubServiceID);
	
}
