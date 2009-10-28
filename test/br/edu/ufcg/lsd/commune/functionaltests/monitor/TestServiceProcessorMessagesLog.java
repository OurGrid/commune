package br.edu.ufcg.lsd.commune.functionaltests.monitor;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import br.edu.ufcg.lsd.commune.functionaltests.monitor.data.DeployableClass;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.data.Stub;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.util.TestDeployMonitorUtil;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.util.TestServiceMessageLogUtil;
import br.edu.ufcg.lsd.commune.functionaltests.util.TestWithTestableCommuneContainer;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.message.Message;


public class TestServiceProcessorMessagesLog extends TestWithTestableCommuneContainer {
	private TestDeployMonitorUtil deployMonitorUtil = new TestDeployMonitorUtil();
	
	private TestServiceMessageLogUtil testServiceMessageLogUtil = new TestServiceMessageLogUtil();
	
	@Test
	public void testGetEmptyLog() throws Exception {
		application = deployMonitorUtil.createAndStartApplication(application);
		testServiceMessageLogUtil.getEmptyLog(application);
	}
	
	@Test
	public void testSimpleGet() throws Exception {
		application = deployMonitorUtil.createAndStartApplication(application);
		
		DeployableClass object = new DeployableClass();
		application.getContainer().deploy(DeployableClass.OBJECT_NAME, object);
		
		DeploymentID source = createOtherMessageSource();
		DeploymentID target = testServiceMessageLogUtil.getObjectDeployment(application, DeployableClass.OBJECT_NAME).getDeploymentID();

		ServiceID stubSID = new ServiceID(source.getContainerID(), "stub1");
		DeploymentID stubDID = new DeploymentID(stubSID);
		Message message = new Message(source, target, "invoke");
		message.addStubParameter(Stub.class, stubDID);

		application.getContainer().deliverMessage(message);
		
		application.getContainer().getServiceConsumer().consumeMessage();
		
		testServiceMessageLogUtil.getMessagesLog(application, createMessageList(message));
	}
	
	@Test
	public void testQueueOverflow() throws Exception {
		application = deployMonitorUtil.createAndStartApplication(application);
		
		DeployableClass object = new DeployableClass();
		application.getContainer().deploy(DeployableClass.OBJECT_NAME, object);
		
		DeploymentID source = createOtherMessageSource();
		DeploymentID target = testServiceMessageLogUtil.getObjectDeployment(application, DeployableClass.OBJECT_NAME).getDeploymentID();

		ServiceID stubSID = new ServiceID(source.getContainerID(), "stub1");
		DeploymentID stubDID = new DeploymentID(stubSID);
		
		//message to be removed
		Message message = new Message(source, target, "invoke");
		message.addStubParameter(Stub.class, stubDID);
		
		application.getContainer().deliverMessage(message);		
		
		application.getContainer().getServiceConsumer().consumeMessage();
		
		//verify
		testServiceMessageLogUtil.getMessagesLog(application, createMessageList(message));
		
		//remaining messages
		List<Message> list = new ArrayList<Message>();
		
		for (int i = 0; i < 30; i++) {
			message = new Message(source, target, "invoke");
			message.addStubParameter(Stub.class, stubDID);

			application.getContainer().deliverMessage(message);		
			
			application.getContainer().getServiceConsumer().consumeMessage();
			
			list.add(message);
		}
		
		//verify
		testServiceMessageLogUtil.getMessagesLog(application, list);
	}
	
	private List<Message> createMessageList(Message... messages) {
		List<Message> list = new ArrayList<Message>();
		
		for (Message message : messages) {
			list.add(message);
		}
		
		return list;
	}
}