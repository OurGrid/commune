package br.edu.ufcg.lsd.commune.systemtest.tc1;

import br.edu.ufcg.lsd.commune.network.connection.Connection;
import br.edu.ufcg.lsd.commune.network.connection.ConnectionProtocol;
import br.edu.ufcg.lsd.commune.systemtest.Condition;

public class NullConnectionCondition implements Condition<ConnectionProtocol> {
	
	
	private final String remoteAddress;

	public NullConnectionCondition(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public boolean test(ConnectionProtocol protocol) {
		Connection connection = protocol.getConnection(remoteAddress);
		return connection == null;
	}

}
