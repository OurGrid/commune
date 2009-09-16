package br.edu.ufcg.lsd.commune.systemtest.tc1;

import static br.edu.ufcg.lsd.commune.systemtest.BlockerConfiguration.DO_NOT_BLOCK_FUNCTION;
import static br.edu.ufcg.lsd.commune.systemtest.BlockerConfiguration.DO_NOT_BLOCK_SEQUENCE;
import static br.edu.ufcg.lsd.commune.systemtest.tc1.TestConstants.A_ADDRESS;
import static br.edu.ufcg.lsd.commune.systemtest.tc1.TestConstants.A_CONTAINER;
import static br.edu.ufcg.lsd.commune.systemtest.tc1.TestConstants.A_SERVICE;
import static br.edu.ufcg.lsd.commune.systemtest.tc1.TestConstants.A_USER;
import static br.edu.ufcg.lsd.commune.systemtest.tc1.TestConstants.B_ADDRESS;
import static br.edu.ufcg.lsd.commune.systemtest.tc1.TestConstants.B_CONTAINER;
import static br.edu.ufcg.lsd.commune.systemtest.tc1.TestConstants.B_SERVICE;
import static br.edu.ufcg.lsd.commune.systemtest.tc1.TestConstants.B_USER;
import static br.edu.ufcg.lsd.commune.systemtest.tc1.TestConstants.HEARTBEAT_FUNCTION_NAME;
import static br.edu.ufcg.lsd.commune.systemtest.tc1.TestConstants.UPDATTE_STATUS_FUNCTION_NAME;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.edu.ufcg.lsd.commune.context.DefaultContextFactory;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.context.PropertiesParser;
import br.edu.ufcg.lsd.commune.network.certification.CertificationProperties;
import br.edu.ufcg.lsd.commune.network.certification.providers.FileCertificationDataProvider;
import br.edu.ufcg.lsd.commune.network.certification.providers.FileCertificationProperties;
import br.edu.ufcg.lsd.commune.network.connection.ConnectionProtocol;
import br.edu.ufcg.lsd.commune.network.connection.Down_Empty;
import br.edu.ufcg.lsd.commune.network.connection.Empty_Zero;
import br.edu.ufcg.lsd.commune.network.connection.Up_Empty;
import br.edu.ufcg.lsd.commune.network.connection.Uping_Empty;
import br.edu.ufcg.lsd.commune.network.xmpp.XMPPProperties;
import br.edu.ufcg.lsd.commune.systemtest.BlockerConfiguration;
import br.edu.ufcg.lsd.commune.systemtest.Condition;
import br.edu.ufcg.lsd.commune.systemtest.ConditionChecker;
import br.edu.ufcg.lsd.commune.systemtest.MessageBlocker;
import br.edu.ufcg.lsd.commune.systemtest.SystemTestModule;


public class ConnectionProtocolBlockingTest {
	

	private SystemTestModule a_module;
	private SystemTestModule b_module;
	
	
	@Before
	public void startModules() throws Exception {
		a_module = createModule(A_CONTAINER, A_USER);
		b_module = createModule(B_CONTAINER, B_USER);
	}
	
	@After
	public void stopModules() throws Exception {
		a_module.stop();
		b_module.stop();
	}
 	

	@Test
	public void blockFirstHeartbeat() throws Exception {
		createBlocker(b_module, HEARTBEAT_FUNCTION_NAME, DO_NOT_BLOCK_SEQUENCE, DO_NOT_BLOCK_FUNCTION, 
				DO_NOT_BLOCK_SEQUENCE);
		
		a_module.getContainer().deploy(A_SERVICE, new AReceiver());
		
		Condition<ConnectionProtocol> A2B_down_connection = new ConnectionStateCondition(B_ADDRESS, Down_Empty.class);
		
		ConditionChecker<ConnectionProtocol> checker = 
			new ConditionChecker<ConnectionProtocol>(a_module.getConnectionProtocol(), A2B_down_connection);
		Assert.assertTrue(checker.waitUntilCondition(1000, 5));
	}

	@Test
	public void blockFirstUpdateStatus() throws Exception {
		createBlocker(a_module, UPDATTE_STATUS_FUNCTION_NAME, DO_NOT_BLOCK_SEQUENCE, DO_NOT_BLOCK_FUNCTION, 
				DO_NOT_BLOCK_SEQUENCE);
		
		a_module.getContainer().deploy(A_SERVICE, new AReceiver());
		b_module.getContainer().deploy(B_SERVICE, new BReceiver());

		Condition<ConnectionProtocol> A2B_seq0_rev_connection = new ConnectionStateCondition(A_ADDRESS, Empty_Zero.class);
		ConditionChecker<ConnectionProtocol> checker = 
			new ConditionChecker<ConnectionProtocol>(b_module.getConnectionProtocol(), A2B_seq0_rev_connection);
		Assert.assertTrue(checker.waitUntilCondition(1000, 5));

		Condition<ConnectionProtocol> A2B_uping_connection = new ConnectionStateCondition(B_ADDRESS, Uping_Empty.class);
		checker = new ConditionChecker<ConnectionProtocol>(a_module.getConnectionProtocol(), A2B_uping_connection);
		Assert.assertTrue(checker.doNotOccurs(1000, 5));
	}

	@Test
	public void waitForConnectionUp() throws Exception {
		AReceiver aReceiver = new AReceiver();
		aReceiver.setSendMessage(false);
		a_module.getContainer().deploy(A_SERVICE, aReceiver);
		b_module.getContainer().deploy(B_SERVICE, new BReceiver());

		Condition<ConnectionProtocol> A2B_up_connection = new ConnectionStateCondition(B_ADDRESS, Up_Empty.class);
		ConditionChecker<ConnectionProtocol> checker = 
			new ConditionChecker<ConnectionProtocol>(a_module.getConnectionProtocol(), A2B_up_connection);
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
