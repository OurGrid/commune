package br.edu.ufcg.lsd.commune.functionaltests.monitor;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import br.edu.ufcg.lsd.commune.functionaltests.monitor.data.DeployableClass;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.util.TestDeployMonitorUtil;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.util.TestInterestMessageLogUtil;
import br.edu.ufcg.lsd.commune.functionaltests.util.TestWithTestableCommuneContainer;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.message.Message;
import br.edu.ufcg.lsd.commune.processor.interest.InterestProcessor;


public class TestInterestProcessorMessagesLog extends TestWithTestableCommuneContainer {
	private TestDeployMonitorUtil deployMonitorUtil = new TestDeployMonitorUtil();
	
	private TestInterestMessageLogUtil testInterestMessageLogUtil = new TestInterestMessageLogUtil();
	
	@Test
	public void testGetEmptyLog() throws Exception {
		application = deployMonitorUtil.createAndStartApplication(application);
		testInterestMessageLogUtil.getEmptyLog(application);
	}
	
	@Test
	public void testSimpleGet() throws Exception {
		application = deployMonitorUtil.createAndStartApplication(application);
		
		DeployableClass object = new DeployableClass();
		application.getContainer().deploy(DeployableClass.OBJECT_NAME, object);
		
		DeploymentID source = createOtherMessageSource();
		DeploymentID target = testInterestMessageLogUtil.getObjectDeployment(application, DeployableClass.OBJECT_NAME).getDeploymentID();

		Message message = 
			new Message(target, source.getServiceID(), InterestProcessor.IS_IT_ALIVE_MESSAGE, 
					InterestProcessor.class.getName());

		application.getContainer().deliverMessage(message);
		
		application.getContainer().getInterestConsumer().consumeMessage();
		
		testInterestMessageLogUtil.getMessagesLog(application, createMessageList(message));
	}
	
	@Test
	public void testQueueOverflow() throws Exception {
		application = deployMonitorUtil.createAndStartApplication(application);
		
		DeployableClass object = new DeployableClass();
		application.getContainer().deploy(DeployableClass.OBJECT_NAME, object);
		
		DeploymentID source = createOtherMessageSource();
		DeploymentID target = testInterestMessageLogUtil.getObjectDeployment(application, DeployableClass.OBJECT_NAME).getDeploymentID();

		//message to be removed
		Message message = 
			new Message(target, source.getServiceID(), InterestProcessor.IS_IT_ALIVE_MESSAGE, 
					InterestProcessor.class.getName());
		
		application.getContainer().deliverMessage(message);		
		
		application.getContainer().getInterestConsumer().consumeMessage();
		
		//verify
		testInterestMessageLogUtil.getMessagesLog(application, createMessageList(message));
		
		//remaining messages
		List<Message> list = new ArrayList<Message>();
		
		for (int i = 0; i < 30; i++) {
			message = 
				new Message(target, source.getServiceID(), InterestProcessor.IS_IT_ALIVE_MESSAGE, 
						InterestProcessor.class.getName());

			application.getContainer().deliverMessage(message);		
			
			application.getContainer().getInterestConsumer().consumeMessage();
			
			list.add(message);
		}
		
		//verify
		testInterestMessageLogUtil.getMessagesLog(application, list);
	}
	
	private List<Message> createMessageList(Message... messages) {
		List<Message> list = new ArrayList<Message>();
		
		for (Message message : messages) {
			list.add(message);
		}
		
		return list;
	}
}