/*
 * Copyright (C) 2009 Universidade Federal de Campina Grande
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
package br.edu.ufcg.lsd.commune.network.connection;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import br.edu.ufcg.lsd.commune.container.Container;
import br.edu.ufcg.lsd.commune.container.StubListener;
import br.edu.ufcg.lsd.commune.container.StubReference;
import br.edu.ufcg.lsd.commune.identification.CommuneAddress;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.message.Message;
import br.edu.ufcg.lsd.commune.message.MessageParameter;
import br.edu.ufcg.lsd.commune.message.MessageUtil;
import br.edu.ufcg.lsd.commune.message.StubParameter;
import br.edu.ufcg.lsd.commune.network.DiscardMessageException;
import br.edu.ufcg.lsd.commune.processor.interest.InterestProcessor;
import br.edu.ufcg.lsd.commune.processor.interest.MonitorableStatus;
import br.edu.ufcg.lsd.commune.processor.interest.TimeoutListener;
import br.edu.ufcg.lsd.commune.processor.objectdeployer.NotificationListener;

public class ConnectionManager implements StubListener, TimeoutListener, NotificationListener {
	
	private Container container;
	private ReadWriteLock connectionLock = new ReentrantReadWriteLock(true);
	private Map<ServiceID, Communication> communications = new HashMap<ServiceID, Communication>();
	
	//Connection states
	CommunicationState empty_empty 				= new Empty_Empty(this);
	CommunicationState empty_zero 				= new Empty_Zero(this);
	CommunicationState empty_greatherThenZero 	= new Empty_GreaterThenZero(this);
	CommunicationState down_empty 				= new Down_Empty(this);
	CommunicationState down_zero 				= new Down_Zero(this);
	CommunicationState down_greatherThenZero 	= new Down_GreaterThenZero(this);
	CommunicationState up_empty 				= new Up_Empty(this);
	CommunicationState up_zero 					= new Up_Zero(this);
	CommunicationState up_greatherThenZero 		= new Up_GreaterThenZero(this);
	CommunicationState uping_empty 				= new Uping_Empty(this);
	CommunicationState uping_zero 				= new Uping_Zero(this);
	CommunicationState uping_greatherThenZero 	= new Uping_GreaterThenZero(this);
	CommunicationState downing_empty 			= new Downing_Empty(this);
	
	
	public ConnectionManager(Container container) {
		this.container = container;
	}

	
	public void receivingMessage(Message message) throws DiscardMessageException {
		
		CommuneAddress source = message.getSource();
		
		if (container.isLocal(source)) {
			//Do not verify session and sequence numbers for local messages
			
		} else {
			receivingRemoteMessage(message);
			
		}

	}
	
	public void sendingMessage(Message message) {
		
		if (container.isLocal(message.getDestination())) {
			//Do not define session and sequence numbers for local messages
			
		} else {
			sendingRemoteMessage(message);
		}
	}

	public void sendingRemoteMessage(Message message) {
		try {
			connectionLock.writeLock().lock();
		
			CommuneAddress destination = message.getDestination();
			Communication communication = null;
			
			if (isFailureDetectorMessage(message)) {
				
				communication = communications.get(destination);
				
				if (isIsItAlive(message)) { //Outgoing
					message.setSequence(communication.getOutgoingSequence());
					message.setSession(communication.getOutgoingSession());
					
				} else { //Update status - Incoming 
					message.setSequence(communication.getIncomingSequence());
					message.setSession(communication.getIncomingSession());
				}
				
			} else { //Application message - Outgoing
				DeploymentID destinationDeploymentID = (DeploymentID)destination;
				communication = communications.get(destinationDeploymentID.getServiceID());
				
				communication.incOutcoingSequenceNumber();
				message.setSequence(communication.getOutgoingSequence());
				message.setSession(communication.getOutgoingSession());
			}
			
		} finally {
			connectionLock.writeLock().unlock();
		}
	}

	private static boolean isFailureDetectorMessage(Message message) {
		return InterestProcessor.class.getName().equals(message.getProcessorType());
	}
	
	private static boolean isIsItAlive(Message message) {
		return InterestProcessor.IS_IT_ALIVE_MESSAGE.equals(message.getFunctionName());
	}

	private void receivingRemoteMessage(Message message) throws DiscardMessageException {
		try {
			connectionLock.writeLock().lock();
		
			CommuneAddress destination = message.getDestination();
			Communication communication = null;
			
			if (isFailureDetectorMessage(message)) {
				
				communication = communications.get(destination);
				
				if (communication == null && isCreatingConnection(message)) {
					communication = initIncomingConnection(message);
					communications.put((ServiceID) destination, communication);
				}
				
				receivingRemoteControlMessage(message, communication);
				
				
			} else {
			
				DeploymentID destinationDeploymentID = (DeploymentID)destination;
				communication = communications.get(destinationDeploymentID.getServiceID());
				
				receivingRemoteApplicationMessage(message, communication);
			}

		} finally {
			connectionLock.writeLock().unlock();
		}
		
	}

	private static boolean isCreatingConnection(Message message) {
		return isIsItAlive(message) && message.getSequence() == 0; 
	}


	private Communication initIncomingConnection(Message message) {
		Communication connection = new Communication();
		connection.setIncomingSequence(0L);
		connection.setIncomingSession(message.getSession());
		connection.setState(empty_empty);
		return connection;
	}

	private void receivingRemoteControlMessage(Message message, Communication connection) throws DiscardMessageException {
		String messageName = message.getFunctionName();

		if (InterestProcessor.IS_IT_ALIVE_MESSAGE.equals(messageName)) {
			
			receiveHeartbeat(message, connection);
			
		} else if (InterestProcessor.UPDATE_STATUS_MESSAGE.equals(messageName)) {
			receiveUpdateStatus(message, connection);
		}
	}

	private void receiveHeartbeat(Message message, Communication connection) throws DiscardMessageException { //Incoming
		Long expectedSession = connection.getIncomingSession();
		Long expectedSequence = connection.getIncomingSequence();

		long messageSession = message.getSession();
		long messageSequence = message.getSequence();
		
		CommunicationState state = connection.getState();
		
		if (expectedSession == messageSession) {
			
			if(messageSequence == 0) {
				state.heartbeatOkSessionZeroSequence(connection);
				
			} else if(expectedSequence == messageSequence) {
				state.heartbeatOkSessionOkSequence(connection);
				
			} else {
				state.heartbeatOkSessionNonSequence(connection);
			}
		} else {
			
			if(messageSequence == 0) {
				connection.setIncomingSession(message.getSession());
				state.heartbeatNonSessionZeroSequence(connection);
				
			} else if(expectedSequence == messageSequence) {
				state.heartbeatNonSessionOkSequence(connection);
				
			} else {
				state.heartbeatNonSessionNonSequence(connection);
			}
		}
	}


	private void receiveUpdateStatus(Message message, Communication connection) throws DiscardMessageException { //Outgoing
		Long expectedSession = connection.getOutgoingSession();
		long messageSession = message.getSession();
		CommunicationState state = connection.getState();
		
		if (expectedSession == messageSession) {

			if (message.getParameterValues().length == 1) {
				
				Object value = message.getParameterValues()[0];
				
				if (MonitorableStatus.AVAILABLE.equals(value)) {
					state.updateStatusUp(connection);
				
				} else {
					state.updateStatusDown(connection);
				}
			}
		} else {
			state.updateStatusNonSession(connection);
		}
	}


	private void receivingRemoteApplicationMessage(Message message, Communication connection) throws DiscardMessageException { //Incoming
		Long expectedSession = connection.getIncomingSession();
		connection.incIncomingSequenceNumber();
		Long expectedSequence = connection.getIncomingSequence();

		long messageSession = message.getSession();
		long messageSequence = message.getSequence();
		
		CommunicationState state = connection.getState();
		
		if (expectedSession == messageSession) {
			
			if(messageSequence == expectedSequence) {
				
				if(hasCallback(message)) {
					state.messageWithCallbackOkSessionOkSequence(connection);

				} else {
					state.messageOkSessionOkSequence(connection);
				}
				
			} else {
				state.messageNonSequence(connection);
			}
		} else {
			state.messageNonSession(connection);
		}
		
	}


	private boolean hasCallback(Message message) {
		DeploymentID source = (DeploymentID) message.getSource();
		
		for (MessageParameter messageParameter : message.getParameters()) {
			Object parameterValue = messageParameter.getValue();
			
			if (parameterValue == null) {
				continue;
			}
			
			if (MessageUtil.isStubParameter(parameterValue)) {
				StubParameter stubParam = (StubParameter) parameterValue;
				if (source.equals(stubParam.getId())) {
					return true;
				}
			}
		}
		
		return false;
	}


	public void stubCreated(StubReference stubReference) {
		try {
			connectionLock.writeLock().lock();

			ServiceID stubServiceID = stubReference.getStubServiceID();
			Communication connection = communications.get(stubServiceID);

			CommunicationState state = null;
			
			if (connection == null) {
				state = empty_empty;
				connection = initOutgoingConnection(stubReference);
				communications.put(stubServiceID, connection);

			} else {
				state = connection.getState();
				connection.setStubReference(stubReference);
			}

			if (stubReference.getStubDeploymentID() == null) {
				state.registerInterest(connection);
			} 
			
		} finally {
			connectionLock.writeLock().unlock();
		}
	}

	private Communication initOutgoingConnection(StubReference stubReference) {
		Communication connection = new Communication();
		connection.setStubReference(stubReference);
		connection.setState(empty_empty);
		return connection;
	}
	
	public void stubReleased(ServiceID stubServiceID) {
		try {
			connectionLock.writeLock().lock();

			Communication connection = communications.remove(stubServiceID.getContainerID());
			if (connection != null) {
				connection.getState().release(connection);
			}
			
		} finally {
			connectionLock.writeLock().unlock();
		}
	}

	public void timeout(ServiceID stubServiceID) {
		try {
			connectionLock.writeLock().lock();

			Communication connection = communications.get(stubServiceID); 
			connection.getState().timeout(connection);
			
		} finally {
			connectionLock.writeLock().unlock();
		}
	}


	public void notifyFailure(ServiceID serviceID) throws DiscardMessageException {
		try {
			connectionLock.writeLock().lock();

			Communication connection = communications.get(serviceID); 
			connection.getState().notifyFailure(connection);
			
		} finally {
			connectionLock.writeLock().unlock();
		}
	}

	public void notifyRecovery(ServiceID serviceID) throws DiscardMessageException {
		try {
			connectionLock.writeLock().lock();

			Communication connection = communications.get(serviceID); 
			connection.getState().notifyRecovery(connection);
			
		} finally {
			connectionLock.writeLock().unlock();
		}
	}
	
	public void configure(Container container) {
		container.getStubRepository().addListener(this);
		container.getInterestManager().addListener(this);
		container.getServiceProcessor().addListener(this);
	}
}