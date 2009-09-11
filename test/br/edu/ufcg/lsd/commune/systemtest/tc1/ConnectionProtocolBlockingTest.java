package br.edu.ufcg.lsd.commune.systemtest.tc1;

import static br.edu.ufcg.lsd.commune.systemtest.BlockerConfiguration.DO_NOT_BLOCK_SEQUENCE;
import static br.edu.ufcg.lsd.commune.systemtest.BlockerConfiguration.DO_NOT_BLOCK_FUNCTION;

import static br.edu.ufcg.lsd.commune.systemtest.tc1.TestConstants.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import br.edu.ufcg.lsd.commune.context.DefaultContextFactory;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.context.PropertiesParser;
import br.edu.ufcg.lsd.commune.network.certification.CertificationProperties;
import br.edu.ufcg.lsd.commune.network.certification.providers.FileCertificationDataProvider;
import br.edu.ufcg.lsd.commune.network.certification.providers.FileCertificationProperties;
import br.edu.ufcg.lsd.commune.network.connection.Connection;
import br.edu.ufcg.lsd.commune.network.connection.ConnectionProtocol;
import br.edu.ufcg.lsd.commune.network.connection.Down_Empty;
import br.edu.ufcg.lsd.commune.network.xmpp.XMPPProperties;
import br.edu.ufcg.lsd.commune.systemtest.BlockerConfiguration;
import br.edu.ufcg.lsd.commune.systemtest.Condition;
import br.edu.ufcg.lsd.commune.systemtest.ConditionChecker;
import br.edu.ufcg.lsd.commune.systemtest.MessageBlocker;
import br.edu.ufcg.lsd.commune.systemtest.SystemTestModule;


public class ConnectionProtocolBlockingTest {
	

	@Test
	public void blockHeartbeats() throws Exception {
		
		SystemTestModule A_module = createModule(A_CONTAINER, A_USER);
		
		SystemTestModule B_module = createModule(B_CONTAINER, B_USER);
		createBlocker(B_module, HEARTBEAT_FUNCTION_NAME, DO_NOT_BLOCK_SEQUENCE, DO_NOT_BLOCK_FUNCTION, 
				DO_NOT_BLOCK_SEQUENCE);
		
		A_module.getContainer().deploy(A_SERVICE, new AReceiver());
		
		Condition<ConnectionProtocol> A2B_down_connection = new Condition<ConnectionProtocol>() {

			public boolean test(ConnectionProtocol protocol) {
				Connection connection = protocol.getConnection(B_ADDRESS);
				
				return connection != null && (connection.getState() instanceof Down_Empty);
			}
			
		};
		
		ConditionChecker<ConnectionProtocol> checker = 
			new ConditionChecker<ConnectionProtocol>(A_module.getConnectionProtocol(), A2B_down_connection);
		
		Assert.assertTrue(checker.waitUntilCondition(1000, 5));
		
	}
	
	private void createBlocker(SystemTestModule module, String recFunc, int recSeq, String sendFunc, int sendSeq) {
		MessageBlocker protocol = new MessageBlocker(module.getCommuneNetwork());
		module.addProtocol(protocol);
		
		BlockerConfiguration receiverBlocker = new BlockerConfiguration();
		receiverBlocker.setFunctionName(recFunc);
		receiverBlocker.setSequenceNumber(recSeq);
		protocol.setReceiverBlocker(receiverBlocker);

		BlockerConfiguration senderBlocker = new BlockerConfiguration();
		senderBlocker.setFunctionName(sendFunc);
		senderBlocker.setSequenceNumber(sendSeq);
		protocol.setSenderBlocker(senderBlocker);
	}
	
	private SystemTestModule createModule(String container, String user) throws Exception {
		return new SystemTestModule(container, createModuleContext(user));
	}
	
	private ModuleContext createModuleContext(String username) {
		Map<String, String> properties = new HashMap<String,String>();
		properties.put(XMPPProperties.PROP_USERNAME, username);
		properties.put(CertificationProperties.PROP_CERT_PROVIDER_CLASS, 
				FileCertificationDataProvider.class.getName());
		properties.put(FileCertificationProperties.PROP_MYCERTIFICATE_FILEPATH, 
				"data" + File.separator + "certification" + File.separator + "testLSD.cer");
		
		DefaultContextFactory factory = new DefaultContextFactory(new PropertiesParser(properties));
		return factory.createContext();
	}
}
