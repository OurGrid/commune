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
package br.edu.ufcg.lsd.commune.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.api.MonitoredBy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.api.Remote;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.ObjectRepository;
import br.edu.ufcg.lsd.commune.container.StubReference;
import br.edu.ufcg.lsd.commune.container.StubRepository;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.message.Message;
import br.edu.ufcg.lsd.commune.message.MessageUtil;
import br.edu.ufcg.lsd.commune.monitor.data.StubData;
import br.edu.ufcg.lsd.commune.monitor.data.TransferData;
import br.edu.ufcg.lsd.commune.processor.filetransfer.FileTransferProcessor;
import br.edu.ufcg.lsd.commune.processor.filetransfer.IncomingTransfer;
import br.edu.ufcg.lsd.commune.processor.filetransfer.IncomingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.IncomingTransfersManager;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransfer;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransfersManager;
import br.edu.ufcg.lsd.commune.processor.interest.Interest;
import br.edu.ufcg.lsd.commune.processor.interest.InterestProcessor;
import br.edu.ufcg.lsd.commune.processor.objectdeployer.ServiceProcessor;

@Remote
public class CommuneMonitorController implements CommuneMonitor {
	
	private ServiceManager serviceManager;

	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}
	
	//get deployed objects
	private Map<DeploymentID, Collection<Class<?>>> getDeployedObjects() {
		ObjectRepository objectRepository = this.serviceManager.getApplication().getObjectRepository();
		Collection<ObjectDeployment> objectDeployments = objectRepository.getObjects();
		
		Map<DeploymentID, Collection<Class<?>>> deployedObjects = new HashMap<DeploymentID, Collection<Class<?>>>();
		
		for (ObjectDeployment objectDeployment : objectDeployments) {
			Collection<Class<?>> classes = MessageUtil.getRemoteInterfaces(objectDeployment.getObject().getClass());
			deployedObjects.put(objectDeployment.getDeploymentID(), classes);
		}
		
		return deployedObjects;
	}

	public void getDeployedObjects(@MonitoredBy(MonitorConstants.COMMUNE_MONITOR_CONTROLLER)CommuneMonitorClient client) {
		Map<DeploymentID, Collection<Class<?>>> deployedObjects = getDeployedObjects();
		client.hereAreDeployedObjects(deployedObjects);
	}
	
	//get file transfers
	private Collection<TransferData> getOutgoingFileTransfers() {
		Collection<TransferData> transferDatas = new ArrayList<TransferData>();
		
		OutgoingTransfersManager outgoingManager = this.serviceManager.getApplication().getTransferManager().getOutgoingManager();
		Collection<OutgoingTransfer> transfers = outgoingManager.getTransfers();
		for (OutgoingTransfer transfer : transfers) {
			OutgoingTransferHandle handle = (OutgoingTransferHandle) transfer.getHandle();
			
			transferDatas.add(new TransferData(handle.getDestinationID(), transfer.getListenerID(), 
					transfer.getCurrentStatus(), handle.getLocalFile(), 
					handle.getId(), handle.getInactivityTimeout(), 0, handle.isReceiveProgressUpdate(), false));
		}
		
		List<OutgoingTransfer> queuedTransfers = outgoingManager.getQueuedTransfers();
		for (int i = 0; i < queuedTransfers.size(); i++) {
			OutgoingTransfer queuedTransfer = queuedTransfers.get(i);

			OutgoingTransferHandle handle = (OutgoingTransferHandle) queuedTransfer.getHandle();
			
			transferDatas.add(new TransferData(handle.getDestinationID(), queuedTransfer.getListenerID(), 
					queuedTransfer.getCurrentStatus(), handle.getLocalFile(), 
					handle.getId(), handle.getInactivityTimeout(), i + 1, handle.isReceiveProgressUpdate(), false));
			
		}
		
		return transferDatas;
	}
	
	private Collection<TransferData> getIncomingFileTransfers() {
		Collection<TransferData> transferDatas = new ArrayList<TransferData>();
		
		IncomingTransfersManager incomingManager = this.serviceManager.getApplication().getTransferManager().getIncomingManager();
		Collection<IncomingTransfer> transfers = incomingManager.getTransfers();
		for (IncomingTransfer transfer : transfers) {
			IncomingTransferHandle handle = (IncomingTransferHandle) transfer.getHandle();
			
			transferDatas.add(new TransferData(null, transfer.getListenerID(), transfer.getCurrentStatus(), handle.getLocalFile(), 
					handle.getId(), handle.getInactivityTimeout(), 0, handle.isReceiveProgressUpdate(), true));
		}
		
		return transferDatas;
	}
	
	private Collection<TransferData> getFileTransfers() {
		Collection<TransferData> transferDatas = getOutgoingFileTransfers();
		transferDatas.addAll(getIncomingFileTransfers());
		return transferDatas;
	}
	
	public void getFileTransfers(@MonitoredBy(MonitorConstants.COMMUNE_MONITOR_CONTROLLER)CommuneMonitorClient client) {
		Collection<TransferData> transferDatas = getFileTransfers();
		client.hereAreFileTransfers(transferDatas);
	}
	
	//get stubs
	private Collection<StubData> getStubs() {
		StubRepository stubRepository = this.serviceManager.getApplication().getStubRepository();
		Collection<StubReference> stubReferences = stubRepository.getStubReferences();
		
		Collection<StubData> stubDatas = new ArrayList<StubData>();
		
		for (StubReference stubReference : stubReferences) {
			ServiceID stubSID = stubReference.getStubServiceID();
			DeploymentID stubDID = stubReference.getStubDeploymentID();
			Collection<Class<?>> proxies = stubReference.getProxies();
			
			Interest interest = this.serviceManager.getApplication().getInterestManager().getInterest(stubSID);
			DeploymentID monitorDID = interest.getInterested().getDeploymentID();
			
			stubDatas.add(new StubData(monitorDID, stubSID, stubDID, proxies));
		}
		
		return stubDatas;
	}

	public void getStubs(@MonitoredBy(MonitorConstants.COMMUNE_MONITOR_CONTROLLER)CommuneMonitorClient client) {
		Collection<StubData> stubDatas = getStubs();
		client.hereAreStubs(stubDatas);
	}
	
	//get ServiceProcessor messages log
	private Collection<Message> getServiceMessagesLog() {
		ServiceProcessor service = (ServiceProcessor) this.serviceManager.getApplication().getServiceProcessor();
		
		Collection<Message> messages = new ArrayList<Message>();
		messages.addAll(service.getIncomingQueueMessages());
		messages.addAll(service.getOutgoingQueueMessages());
		
		return messages;
	}
	
	public void getServiceMessagesLog(@MonitoredBy(MonitorConstants.COMMUNE_MONITOR_CONTROLLER)CommuneMonitorClient client) {
		client.hereIsServiceMessagesLog(getServiceMessagesLog());
	}
	
	//get FileTransferProcessor messages log
	private Collection<Message> getFileTransferMessagesLog() {
		FileTransferProcessor fileTransfer = (FileTransferProcessor) this.serviceManager.getApplication().getFileTransferProcessor();
		
		Collection<Message> messages = new ArrayList<Message>();
		messages.addAll(fileTransfer.getIncomingQueueMessages());
		messages.addAll(fileTransfer.getOutgoingQueueMessages());
		
		return messages;
	}
	
	public void getFileTransferMessagesLog(@MonitoredBy(MonitorConstants.COMMUNE_MONITOR_CONTROLLER)CommuneMonitorClient client) {
		client.hereIsFileTransferMessagesLog(getFileTransferMessagesLog());
	}
	
	//get InterestProcessor messages log
	private Collection<Message> getInterestMessagesLog() {
		InterestProcessor interest = (InterestProcessor) this.serviceManager.getApplication().getInterestProcessor();
		
		Collection<Message> messages = new ArrayList<Message>();
		messages.addAll(interest.getIncomingQueueMessages());
		messages.addAll(interest.getOutgoingQueueMessages());
		
		return messages;
	}
	
	public void getInterestMessagesLog(@MonitoredBy(MonitorConstants.COMMUNE_MONITOR_CONTROLLER)CommuneMonitorClient client) {
		client.hereIsInterestMessagesLog(getInterestMessagesLog());
	}
	
	@RecoveryNotification
	public void clientIsUp(CommuneMonitorClient client) {}
	
	@FailureNotification
	public void clientIsDown(CommuneMonitorClient client) {}

}
