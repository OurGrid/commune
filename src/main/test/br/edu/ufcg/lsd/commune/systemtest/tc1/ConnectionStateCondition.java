package br.edu.ufcg.lsd.commune.systemtest.tc1;

import br.edu.ufcg.lsd.commune.network.connection.Connection;
import br.edu.ufcg.lsd.commune.network.connection.ConnectionProtocol;
import br.edu.ufcg.lsd.commune.systemtest.Condition;

public class ConnectionStateCondition implements Condition<ConnectionProtocol> {
	
	
	private final String remoteAddress;
	private final Class<?> state;

	public ConnectionStateCondition(String remoteAddress, Class<?> state) {
		this.remoteAddress = remoteAddress;
		this.state = state;
	}

	public boolean test(ConnectionProtocol protocol) {
		Connection connection = protocol.getConnection(remoteAddress);
		return connection != null && (state.isInstance(connection.getState()));
	}

}
