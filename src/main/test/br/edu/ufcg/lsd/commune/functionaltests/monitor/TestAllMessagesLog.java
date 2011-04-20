package br.edu.ufcg.lsd.commune.functionaltests.monitor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import br.edu.ufcg.lsd.commune.functionaltests.monitor.data.DeployableClass;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.data.SenderClass;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.data.Stub;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.util.TestDeployMonitorUtil;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.util.TestFTMessageLogUtil;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.util.TestInterestMessageLogUtil;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.util.TestServiceMessageLogUtil;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.message.Message;
import br.edu.ufcg.lsd.commune.processor.filetransfer.FileTransferProcessor;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.interest.InterestProcessor;
import br.edu.ufcg.lsd.commune.testinfra.util.TestWithTestableCommuneContainer;


public class TestAllMessagesLog extends TestWithTestableCommuneContainer {
	private TestDeployMonitorUtil deployMonitorUtil = new TestDeployMonitorUtil();
	
	private TestServiceMessageLogUtil testServiceMessageLogUtil = new TestServiceMessageLogUtil();

	private TestInterestMessageLogUtil testInterestMessageLogUtil = new TestInterestMessageLogUtil();

	private TestFTMessageLogUtil testFTMessageLogUtil = new TestFTMessageLogUtil();

	@Test
	public void testGetEmptyLog() throws Exception {
		module = deployMonitorUtil.createAndStartApplication(module);
		testServiceMessageLogUtil.getEmptyLog(module);
		testInterestMessageLogUtil.getEmptyLog(module);
		testFTMessageLogUtil.getEmptyLog(module);
	}
	
	@Test
	public void testSimpleGet() throws Exception {
		module = deployMonitorUtil.createAndStartApplication(module);
		
		//ServiceProcessor
		DeployableClass object = new DeployableClass();
		module.deploy(DeployableClass.OBJECT_NAME, object);
		
		DeploymentID source = createOtherMessageSource();
		DeploymentID target = testServiceMessageLogUtil.getObjectDeployment(module, DeployableClass.OBJECT_NAME).getDeploymentID();

		ServiceID stubSID = new ServiceID(source.getContainerID(), "stub1");
		DeploymentID stubDID = new DeploymentID(stubSID);
		Message message1 = new Message(source, target, "invoke");
		message1.addStubParameter(Stub.class, stubDID);

		module.deliverMessage(message1);
		
		//FileTransferProcessor
		SenderClass senderObject = new SenderClass();
		module.deploy(SenderClass.OBJECT_NAME, senderObject);
		
		DeploymentID destinationID = testFTMessageLogUtil.getObjectDeployment(module, SenderClass.OBJECT_NAME).getDeploymentID();
		DeploymentID listenerID = testFTMessageLogUtil.getObjectDeployment(module, SenderClass.OBJECT_NAME).getDeploymentID();
		
		OutgoingTransferHandle handle = new OutgoingTransferHandle(TestGetFileTransfers.TRANSFER_FILE_LOG_NAME,
				new File(TestGetFileTransfers.TRANSFER_FILE), "", destinationID);
		
		Message message2 = new Message(listenerID, destinationID, "sendFile", FileTransferProcessor.class.getName());
		message2.addParameter(OutgoingTransferHandle.class, handle);

		module.deliverMessage(message2);
		
		//InterestProcessor
		Message message3 = 
			new Message(target, source.getServiceID(), InterestProcessor.IS_IT_ALIVE_MESSAGE, 
					InterestProcessor.class.getName());

		module.deliverMessage(message3);
		
		//Consume Messages
		module.getInterestConsumer().consumeMessage();
		
		module.getServiceConsumer().consumeMessage();

		module.getFileTransferConsumer().consumeMessage();
		
		//verify
		testServiceMessageLogUtil.getMessagesLog(module, createMessageList(message1));
		testFTMessageLogUtil.getMessagesLog(module, createMessageList(message2));
		testInterestMessageLogUtil.getMessagesLog(module, createMessageList(message3));
	}
	
	@Test
	public void testOverflowAllQueues() throws Exception {
		module = deployMonitorUtil.createAndStartApplication(module);
		
		//ServiceProcessor
		DeployableClass object = new DeployableClass();
		module.deploy(DeployableClass.OBJECT_NAME, object);
		
		DeploymentID source = createOtherMessageSource();
		DeploymentID target = testServiceMessageLogUtil.getObjectDeployment(module, DeployableClass.OBJECT_NAME).getDeploymentID();

		ServiceID stubSID = new ServiceID(source.getContainerID(), "stub1");
		DeploymentID stubDID = new DeploymentID(stubSID);
		Message message1 = new Message(source, target, "invoke");
		message1.addStubParameter(Stub.class, stubDID);

		module.deliverMessage(message1);
		
		module.getServiceConsumer().consumeMessage();
		
		testServiceMessageLogUtil.getMessagesLog(module, createMessageList(message1));
		
		List<Message> list1 = new ArrayList<Message>();
		
		for (int i = 0; i < 30; i++) {
			message1 = new Message(source, target, "invoke");
			message1.addStubParameter(Stub.class, stubDID);

			module.deliverMessage(message1);		
			
			module.getServiceConsumer().consumeMessage();
			
			list1.add(message1);
		}
		
		//FileTransferProcessor
		SenderClass senderObject = new SenderClass();
		module.deploy(SenderClass.OBJECT_NAME, senderObject);
		
		DeploymentID destinationID = testFTMessageLogUtil.getObjectDeployment(module, SenderClass.OBJECT_NAME).getDeploymentID();
		DeploymentID listenerID = testFTMessageLogUtil.getObjectDeployment(module, SenderClass.OBJECT_NAME).getDeploymentID();
		
		OutgoingTransferHandle handle = new OutgoingTransferHandle(TestGetFileTransfers.TRANSFER_FILE_LOG_NAME, 
				new File(TestGetFileTransfers.TRANSFER_FILE), "", destinationID);
		
		Message message2 = new Message(listenerID, destinationID, "sendFile", FileTransferProcessor.class.getName());
		message2.addParameter(OutgoingTransferHandle.class, handle);

		module.deliverMessage(message2);
		
		module.getFileTransferConsumer().consumeMessage();
		
		testFTMessageLogUtil.getMessagesLog(module, createMessageList(message2));
		
		List<Message> list2 = new ArrayList<Message>();
		
		for (int i = 0; i < 30; i++) {
			message2 = new Message(listenerID, destinationID, "sendFile", FileTransferProcessor.class.getName());
			message2.addParameter(OutgoingTransferHandle.class, handle);

			module.deliverMessage(message2);		
			
			module.getFileTransferConsumer().consumeMessage();
			
			list2.add(message2);
		}
		
		//InterestProcessor
		Message message3 = 
			new Message(target, source.getServiceID(), InterestProcessor.IS_IT_ALIVE_MESSAGE, 
					InterestProcessor.class.getName());

		module.deliverMessage(message3);
		
		module.getInterestConsumer().consumeMessage();
		
		testInterestMessageLogUtil.getMessagesLog(module, createMessageList(message3));
		
		List<Message> list3 = new ArrayList<Message>();
		
		for (int i = 0; i < 30; i++) {
			message3 = 
				new Message(target, source.getServiceID(), InterestProcessor.IS_IT_ALIVE_MESSAGE, 
						InterestProcessor.class.getName());

			module.deliverMessage(message3);		
			
			module.getInterestConsumer().consumeMessage();
			
			list3.add(message3);
		}
		
		//verify
		testServiceMessageLogUtil.getMessagesLog(module, list1);
		testFTMessageLogUtil.getMessagesLog(module, list2);
		testInterestMessageLogUtil.getMessagesLog(module, list3);
		
	}
	
	private List<Message> createMessageList(Message... messages) {
		List<Message> list = new ArrayList<Message>();
		
		for (Message message : messages) {
			list.add(message);
		}
		
		return list;
	}
}
