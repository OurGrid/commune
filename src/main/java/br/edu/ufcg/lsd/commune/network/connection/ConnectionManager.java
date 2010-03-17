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
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.message.Message;
import br.edu.ufcg.lsd.commune.message.MessageParameter;
import br.edu.ufcg.lsd.commune.message.MessageUtil;
import br.edu.ufcg.lsd.commune.message.StubParameter;
import br.edu.ufcg.lsd.commune.network.DiscardMessageException;
import br.edu.ufcg.lsd.commune.network.loopback.LoopbackRegistry;
import br.edu.ufcg.lsd.commune.processor.interest.InterestManager;
import br.edu.ufcg.lsd.commune.processor.interest.InterestProcessor;
import br.edu.ufcg.lsd.commune.processor.interest.MonitorableStatus;
import br.edu.ufcg.lsd.commune.processor.interest.TimeoutListener;
import br.edu.ufcg.lsd.commune.processor.objectdeployer.NotificationListener;

public class ConnectionManager implements StubListener, TimeoutListener, NotificationListener {
	
	private Container container;
	private ReadWriteLock connectionLock = new ReentrantReadWriteLock(true);
	private Map<ContainerID, Communication> connections = new HashMap<ContainerID, Communication>();
	
	//Communication states
	CommunicationState empty_empty 			= new Empty_Empty(this);
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
			//Do not verify session and sequence numbers for loopback messages
			
		} else {
			receivingRemoteMessage(message);
		}

	}
	
	public void sendingMessage(Message message) throws DiscardMessageException {
		
		CommuneAddress destination = message.getDestination();
		
		if (container.isLocal(destination)) {
			//Do not define session and sequence numbers for loopback messages
			
		} else {
			sendingRemoteMessage(message);
		}
	}

	public void sendingRemoteMessage(Message message) throws DiscardMessageException {
		try {
			connectionLock.writeLock().lock();
		
			CommuneAddress destination = message.getDestination();
			Communication connection = null;
			Long session = null;
			Long sequence = null;
			
			if (isFailureDetectorMessage(message)) {
				
				if (message.getFunctionName().equals(InterestProcessor.IS_IT_ALIVE_MESSAGE)) { //Outgoing
					connection = connections.get(destination.getContainerID());
					
					validateOnSend(connection, message);
					
					session = connection.getOutgoingSession();
					sequence = connection.getOutgoingSequence();
					
				} else { //Incoming
					connection = connections.get(destination.getContainerID());
					
					validateOnSend(connection, message);
					
					session = connection.getIncomingSession();
					sequence = connection.getIncomingSequence();
				}
				
			} else { //Outgoing
			
				connection = connections.get(destination.getContainerID());
				
				validateOnSend(connection, message);
				
				connection.incOutgoingSequenceNumber();
				session = connection.getOutgoingSession();
				sequence = connection.getOutgoingSequence();
			}
			
			message.setSequence(sequence);
			message.setSession(session);
		
		} finally {
			connectionLock.writeLock().unlock();
		}
	}

	private void validateOnSend(Communication connection, Message message) throws DiscardMessageException {
		if (connection == null) {
			throw new DiscardMessageException("Null connection while sending " + message.toString());
		}
	}

	private void validateOnReceive(Communication connection, Message message) throws DiscardMessageException {
		if (connection == null) {
			throw new DiscardMessageException("Null connection while receiving " + message.toString());
		}
	}

	private static boolean isFailureDetectorMessage(Message message) {
		return InterestProcessor.class.getName().equals(message.getProcessorType());
	}
	
	private Communication receivingRemoteMessage(Message message) throws DiscardMessageException {
		try {
			connectionLock.writeLock().lock();
		
			CommuneAddress source = message.getSource();
			Communication connection = null;
			
			if (isFailureDetectorMessage(message)) {
				
				String messageName = message.getFunctionName();

				if (InterestProcessor.IS_IT_ALIVE_MESSAGE.equals(messageName)) { //Incoming

					connection = connections.get(source.getContainerID());
					
					if (connection == null && isCreatingConnection(message)) {
						connection = initIncomingConnection(message);
						connections.put(source.getContainerID(), connection);
					}
					
					validateOnReceive(connection, message);
					
					receiveHeartbeat(message, connection);
					
				} else if (InterestProcessor.UPDATE_STATUS_MESSAGE.equals(messageName)) { //Outgoing
					connection = connections.get(source.getContainerID());
					
					validateOnReceive(connection, message);
					
					receiveUpdateStatus(message, connection);
				}

			} else { //Incoming
			
				connection = connections.get(source.getContainerID());
				
				validateOnReceive(connection, message);
				
				receivingRemoteApplicationMessage(message, connection);
			}
			
			return connection;

		} finally {
			connectionLock.writeLock().unlock();
		}
		
	}

	private static boolean isCreatingConnection(Message message) {
		return InterestProcessor.IS_IT_ALIVE_MESSAGE.equals(message.getFunctionName())
			&& message.getSequence() == 0; 
	}

	private Communication initIncomingConnection(Message message) {
		Communication connection = new Communication();
		connection.setIncomingSequence(0L);
		connection.setIncomingSession(message.getSession());
		connection.setState(empty_empty);
		return connection;
	}

	private void receiveHeartbeat(Message message, Communication connection) throws DiscardMessageException {
		Long expectedSession = connection.getIncomingSession();
		Long expectedSequence = connection.getIncomingSequence();

		Long messageSession = message.getSession();
		Long messageSequence = message.getSequence();
		
		validate(messageSession, messageSequence);
		
		CommunicationState state = connection.getState();
		
		if (expectedSession == null || messageSession.equals(expectedSession)) {
			
			if(messageSequence == 0) {
				if (expectedSession == null) {
					connection.setIncomingSession(messageSession);
				}

				state.heartbeatOkSessionZeroSequence(connection);
				
			} else if(messageSequence.equals(expectedSequence)) {
				state.heartbeatOkSessionOkSequence(connection);
				
			} else {
				state.heartbeatOkSessionNonSequence(connection);
			}
		} else {
			
			if(messageSequence == 0) {
				connection.setIncomingSession(message.getSession());
				state.heartbeatNonSessionZeroSequence(connection);
				
			} else if(messageSequence.equals(expectedSequence)) {
				state.heartbeatNonSessionOkSequence(connection);
				
			} else {
				state.heartbeatNonSessionNonSequence(connection);
			}
		}
	}


	private void validate(Long messageSession, Long messageSequence) throws DiscardMessageException {
		if (messageSession == null || messageSequence == null) {
			throw new DiscardMessageException();
		}
	}

	private void validate(Long messageSession) throws DiscardMessageException {
		if (messageSession == null) {
			throw new DiscardMessageException();
		}
	}

	private void receiveUpdateStatus(Message message, Communication connection) throws DiscardMessageException {
		Long expectedSession = connection.getOutgoingSession();
		Long messageSession = message.getSession();
		validate(messageSession);
		CommunicationState state = connection.getState();
		
		if (expectedSession.equals(messageSession)) {

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


	private void receivingRemoteApplicationMessage(Message message, Communication connection) throws DiscardMessageException {
		Long expectedSession = connection.getIncomingSession();
		connection.incIncomingSequenceNumber();
		Long expectedSequence = connection.getIncomingSequence();

		Long messageSession = message.getSession();
		Long messageSequence = message.getSequence();
		validate(messageSession, messageSequence);
		
		CommunicationState state = connection.getState();
		
		if (expectedSession.equals(messageSession)) {
			
			if(messageSequence.equals(expectedSequence)) {
				
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
			Communication connection = connections.get(stubServiceID.getContainerID());

			CommunicationState state = null;
			
			if (connection == null) {
				state = empty_empty;
				connection = initOutgoingConnection(stubReference);
				connections.put(stubServiceID.getContainerID(), connection);

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

			Communication connection = connections.remove(stubServiceID.getContainerID());
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

			Communication connection = connections.get(stubServiceID.getContainerID()); 
			connection.getState().timeout(connection);
			
		} finally {
			connectionLock.writeLock().unlock();
		}
	}


	public void notifyFailure(ServiceID serviceID) throws DiscardMessageException {
		try {
			connectionLock.writeLock().lock();

			if (LoopbackRegistry.isInLoopback(serviceID.getContainerID())) {
				return;
			}

			Communication connection = connections.get(serviceID.getContainerID()); 
			connection.getState().notifyFailure(connection);
			
		} finally {
			connectionLock.writeLock().unlock();
		}
	}

	public void notifyRecovery(ServiceID serviceID) throws DiscardMessageException {
		try {
			connectionLock.writeLock().lock();
			
			if (LoopbackRegistry.isInLoopback(serviceID.getContainerID())) {
				return;
			}

			Communication connection = connections.get(serviceID.getContainerID()); 
			connection.getState().notifyRecovery(connection);
			
		} finally {
			connectionLock.writeLock().unlock();
		}
	}


	public Communication getConnection(ContainerID containerID) {
		try {
			connectionLock.readLock().lock();

			return connections.get(containerID); 
			
		} finally {
			connectionLock.readLock().unlock();
		}
	}
	
	InterestManager getInterestManager() {
		return container.getInterestManager();
	}


	public void configure(Container container) {
		container.getStubRepository().addListener(this);
		container.getInterestManager().addListener(this);
		container.getServiceProcessor().addListener(this);
	}
}