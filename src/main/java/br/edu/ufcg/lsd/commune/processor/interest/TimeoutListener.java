package br.edu.ufcg.lsd.commune.processor.interest;

import br.edu.ufcg.lsd.commune.identification.ServiceID;

public interface TimeoutListener {

	void timeout(ServiceID stubServiceID);

}
