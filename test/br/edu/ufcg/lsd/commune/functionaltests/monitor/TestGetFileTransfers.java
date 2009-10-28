/*
 * Copyright (C) 2008 Universidade Federal de Campina Grande
 *  
 * This file is part of Commune. 
 *
 * Commune is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package br.edu.ufcg.lsd.commune.functionaltests.monitor;

import static java.io.File.separator;

import java.io.File;

import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;
import org.junit.Test;

import br.edu.ufcg.lsd.commune.functionaltests.monitor.data.SenderClass;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.util.TestDeployMonitorUtil;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.util.TestGetFileTransfersUtil;
import br.edu.ufcg.lsd.commune.functionaltests.util.TestWithTestableCommuneContainer;
import br.edu.ufcg.lsd.commune.functionaltests.util.TestableFileTransferProcessor;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.message.Message;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;

public class TestGetFileTransfers extends TestWithTestableCommuneContainer {
	
	public static final String TRANSFER_FILE_LOG_NAME = "transferFile";
	public static final String TRANSFER_FILE = "tests" + separator + "monitor" + separator + "transferFile";
	
	private TestDeployMonitorUtil deployMonitorUtil = new TestDeployMonitorUtil();
	
	private TestGetFileTransfersUtil testGetFileTransfersUtil = new TestGetFileTransfersUtil();
	
	@Test
	public void testEmptyGet() throws Exception {
		application = deployMonitorUtil.createAndStartApplication(application);
		testGetFileTransfersUtil.getFileTransfers(application);
	}
	
	@Test
	public void testGetUnansweredTransfer() throws Exception {
		application = deployMonitorUtil.createAndStartApplication(application);
		
		SenderClass senderObject = new SenderClass();
		application.getContainer().deploy(SenderClass.OBJECT_NAME, senderObject);
		
		DeploymentID destinationID = testGetFileTransfersUtil.getObjectDeployment(application, SenderClass.OBJECT_NAME).getDeploymentID();
		DeploymentID listenerID = testGetFileTransfersUtil.getObjectDeployment(application, SenderClass.OBJECT_NAME).getDeploymentID();
		
		OutgoingTransferHandle handle = new OutgoingTransferHandle(TRANSFER_FILE_LOG_NAME, 
				new File(TRANSFER_FILE), "", destinationID);
		
		Message message = new Message(listenerID, destinationID, "sendFile");
		message.addParameter(OutgoingTransferHandle.class, handle);

		application.getContainer().deliverMessage(message);
		
		//expect send start transfer msg to FileTransfer
		application.getContainer().getServiceConsumer().consumeMessage();

		application.getContainer().getFileTransferConsumer().consumeMessage();
		
		testGetFileTransfersUtil.getFileTransfers(application, destinationID, listenerID, null, new File(TRANSFER_FILE), 0, false);
	}
	
	@Test
	public void testGetAnsweredTransfer() throws Exception {
		application = deployMonitorUtil.createAndStartApplication(application);
		
		SenderClass senderObject = new SenderClass();
		application.getContainer().deploy(SenderClass.OBJECT_NAME, senderObject);
		
		DeploymentID destinationID = testGetFileTransfersUtil.getObjectDeployment(application, SenderClass.OBJECT_NAME).getDeploymentID();
		DeploymentID listenerID = testGetFileTransfersUtil.getObjectDeployment(application, SenderClass.OBJECT_NAME).getDeploymentID();
		
		OutgoingTransferHandle handle = new OutgoingTransferHandle(TRANSFER_FILE_LOG_NAME, 
				new File(TRANSFER_FILE), "", destinationID);
		
		Message message = new Message(listenerID, destinationID, "sendFile");
		message.addParameter(OutgoingTransferHandle.class, handle);

		application.getContainer().deliverMessage(message);
		
		//expect send start transfer msg to FileTransfer
		application.getContainer().getServiceConsumer().consumeMessage();

		application.getContainer().getFileTransferConsumer().consumeMessage();
		
		testGetFileTransfersUtil.getFileTransfers(application, destinationID, listenerID, null, new File(TRANSFER_FILE), 0, false);
		
		TestableFileTransferProcessor fileTransferProcessor = (TestableFileTransferProcessor) application.getContainer().getFileTransferProcessor();
		fileTransferProcessor.setOutgoingTransferStatus(handle, Status.initial);
		
		testGetFileTransfersUtil.getFileTransfers(application, destinationID, listenerID, Status.initial, new File(TRANSFER_FILE), 0, false);
	}
}
