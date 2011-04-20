package br.edu.ufcg.lsd.commune.functionaltests;

import org.easymock.EasyMock;
import org.jivesoftware.smack.XMPPException;
import org.junit.Test;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.functionaltests.xmpp.util.XMPPServerUtil;
import br.edu.ufcg.lsd.commune.network.ConnectionListener;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.network.xmpp.XMPPProperties;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;
import br.edu.ufcg.lsd.commune.testinfra.util.Context;
import br.edu.ufcg.lsd.commune.testinfra.util.TestContext;
import br.edu.ufcg.lsd.commune.testinfra.util.TestWithTestableCommuneContainer;
import br.edu.ufcg.lsd.commune.testinfra.util.TestableModule;

/* 
 * To run the tests you will need to download one of these Openfire packages:
 * 		- For windows users you will need to download "openfire_3_6_4.zip"
 * 		  without java JRE
 * 		
 * 		- For linux users you will need to download "openfire_3_6_4.tar.gz"
 *  	  without java JRE
 *  
 *	After the download, you will unzip the file on the project root.
 *	Before the first test execution, run the openfire manually and configure the setup at
 *	localhost:9090 in the browser.
 *		-Server settings: Doamin = localhost
 *		-Database settings: Embedded Database
 *		-Profile settings: Default
 *		-Admin account: The default login is admin and default password is admin
 *
 *	Now you can run the test normally.
 *
 */

public class XMPPReconnection extends TestWithTestableCommuneContainer {

	@Test
	public void testReconnectionStartingModuleAfterXMPPCreation() throws CommuneNetworkException, ProcessorStartException, InterruptedException, XMPPException {

		ConnectionListener connectionListener = EasyMock.createMock(ConnectionListener.class);

		connectionListener.connected();
		connectionListener.disconnected();
		connectionListener.reconnected();
		connectionListener.reconnectedFailed();

		EasyMock.replay(connectionListener);
		
		Module module = createApplication(connectionListener);

		Thread.sleep(10000);
		
		XMPPServerUtil xmppServerUtil = new XMPPServerUtil();
		xmppServerUtil.startServer();
		
		Thread.sleep(15000);
		
		xmppServerUtil.stopServer();

		Thread.sleep(10000);

		xmppServerUtil.startServer();

		Thread.sleep(20000);

		xmppServerUtil.stopServer();
		
		Thread.sleep(20000);

		EasyMock.verify(connectionListener);
		
	}
	
	@Test
	public void testReconnectionStartingModuleBeforeXMPPCreation() throws CommuneNetworkException, ProcessorStartException, InterruptedException, XMPPException {


		XMPPServerUtil xmppServerUtil = new XMPPServerUtil();
		xmppServerUtil.startServer();

		ConnectionListener connectionListener = EasyMock.createMock(ConnectionListener.class);

		connectionListener.connected();
		connectionListener.disconnected();
		connectionListener.reconnected();
		connectionListener.reconnectedFailed();

		EasyMock.replay(connectionListener);

		Module module = createApplication(connectionListener);

		Thread.sleep(15000);

		xmppServerUtil.stopServer();

		Thread.sleep(10000);

		xmppServerUtil.startServer();

		Thread.sleep(20000);

		xmppServerUtil.stopServer();

		Thread.sleep(20000);

		EasyMock.verify(connectionListener);

	}
	
	protected Module createApplication(ConnectionListener connectionListener)
			throws CommuneNetworkException, ProcessorStartException,
			InterruptedException {
		
		TestContext context = Context.createRealContext();
		context.set(XMPPProperties.PROP_XMPP_SERVERNAME, "localhost");
		
		TestableModule testableApplication = new TestableModule(Context.A_MODULE_NAME, context, connectionListener);

		Thread.sleep(10000);
		
		return testableApplication;
		
	}
}
