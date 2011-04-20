package br.edu.ufcg.lsd.commune.network;

public interface ConnectionListener {

	void connectionFailed(Exception e);
	
	void connected();
	
	void disconnected();
	
	void reconnected();

	void reconnectedFailed();
}
