package br.edu.ufcg.lsd.commune.systemtest.tc1;

import br.edu.ufcg.lsd.commune.network.connection.ConnectionProtocol;
import br.edu.ufcg.lsd.commune.network.connection.ConnectionState;
import br.edu.ufcg.lsd.commune.systemtest.Condition;

public class ConnectionStateCondition implements Condition<ConnectionProtocol> {
	
	
	private final String senderAddress;
	private final ConnectionState state;

	public ConnectionStateCondition(String senderAddress, ConnectionState state) {
		this.senderAddress = senderAddress;
		this.state = state;
	}

	public boolean test(ConnectionProtocol t) {
		// TODO Auto-generated method stub
		return false;
	}

}
