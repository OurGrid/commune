package br.edu.ufcg.lsd.commune.functionaltests.data;

import br.edu.ufcg.lsd.commune.network.ConnectionListener;

public class EmptyConnectionListener implements ConnectionListener {

	public void connectionFailed(Exception e) {}

	public void connected() {}

	public void disconnected() {}

	public void reconnected() {}

	public void reconnectedFailed() {}

}
