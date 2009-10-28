package br.edu.ufcg.lsd.commune.functionaltests.monitor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import br.edu.ufcg.lsd.commune.functionaltests.monitor.data.SenderClass;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.util.TestDeployMonitorUtil;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.util.TestFTMessageLogUtil;
import br.edu.ufcg.lsd.commune.functionaltests.util.TestWithTestableCommuneContainer;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.message.Message;
import br.edu.ufcg.lsd.commune.processor.filetransfer.FileTransferProcessor;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;


public class TestFileTransferMessagesLog extends TestWithTestableCommuneContainer {
	private TestDeployMonitorUtil deployMonitorUtil = new TestDeployMonitorUtil();
	
	private TestFTMessageLogUtil testFTMessageLogUtil = new TestFTMessageLogUtil();
	
	@Test
	public void testGetEmptyLog() throws Exception {
		application = deployMonitorUtil.createAndStartApplication(application);
		testFTMessageLogUtil.getEmptyLog(application);
	}
	
	@Test
	public void testSimpleGet() throws Exception {
		application = deployMonitorUtil.createAndStartApplication(application);
		
		SenderClass senderObject = new SenderClass();
		application.getContainer().deploy(SenderClass.OBJECT_NAME, senderObject);
		
		DeploymentID destinationID = testFTMessageLogUtil.getObjectDeployment(application, SenderClass.OBJECT_NAME).getDeploymentID();
		DeploymentID listenerID = testFTMessageLogUtil.getObjectDeployment(application, SenderClass.OBJECT_NAME).getDeploymentID();
		
		OutgoingTransferHandle handle = new OutgoingTransferHandle(TestGetFileTransfers.TRANSFER_FILE_LOG_NAME,
				new File(TestGetFileTransfers.TRANSFER_FILE), "", destinationID);
		
		Message message = new Message(listenerID, destinationID, "sendFile", FileTransferProcessor.class.getName());
		message.addParameter(OutgoingTransferHandle.class, handle);

		application.getContainer().deliverMessage(message);
		
		//expect send start transfer msg to FileTransfer
		application.getContainer().getFileTransferConsumer().consumeMessage();
		
		testFTMessageLogUtil.getMessagesLog(application, createMessageList(message));
	}
	
	@Test
	public void testQueueOverflow() throws Exception {
		application = deployMonitorUtil.createAndStartApplication(application);
		
		SenderClass senderObject = new SenderClass();
		application.getContainer().deploy(SenderClass.OBJECT_NAME, senderObject);
		
		DeploymentID destinationID = testFTMessageLogUtil.getObjectDeployment(application, SenderClass.OBJECT_NAME).getDeploymentID();
		DeploymentID listenerID = testFTMessageLogUtil.getObjectDeployment(application, SenderClass.OBJECT_NAME).getDeploymentID();
		
		OutgoingTransferHandle handle = new OutgoingTransferHandle(TestGetFileTransfers.TRANSFER_FILE_LOG_NAME, 
				new File(TestGetFileTransfers.TRANSFER_FILE), "", destinationID);

		//message to be removed
		Message message = new Message(listenerID, destinationID, "sendFile", FileTransferProcessor.class.getName());
		message.addParameter(OutgoingTransferHandle.class, handle);
		
		application.getContainer().deliverMessage(message);		
		
		application.getContainer().getFileTransferConsumer().consumeMessage();
		
		//verify
		testFTMessageLogUtil.getMessagesLog(application, createMessageList(message));
		
		//remaining messages
		List<Message> list = new ArrayList<Message>();
		
		for (int i = 0; i < 30; i++) {
			message = new Message(listenerID, destinationID, "sendFile", FileTransferProcessor.class.getName());
			message.addParameter(OutgoingTransferHandle.class, handle);

			application.getContainer().deliverMessage(message);		
			
			application.getContainer().getFileTransferConsumer().consumeMessage();
			
			list.add(message);
		}
		
		//verify
		testFTMessageLogUtil.getMessagesLog(application, list);
	}
	
	private List<Message> createMessageList(Message... messages) {
		List<Message> list = new ArrayList<Message>();
		
		for (Message message : messages) {
			list.add(message);
		}
		
		return list;
	}
}
