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

import br.edu.ufcg.lsd.commune.Module;
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
import br.edu.ufcg.lsd.commune.processor.interest.InterestProcessor;
import br.edu.ufcg.lsd.commune.processor.interest.MonitorableStatus;
import br.edu.ufcg.lsd.commune.processor.interest.TimeoutListener;
import br.edu.ufcg.lsd.commune.processor.objectdeployer.NotificationListener;

public class ConnectionManager implements StubListener, TimeoutListener, NotificationListener {
	
	private Module module;
	private ReadWriteLock connectionLock = new ReentrantReadWriteLock(true);
	private Map<ContainerID, Communication> communications = new HashMap<ContainerID, Communication>();
	
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
	
	
	public ConnectionManager(Module module) {
		this.module = module;
	}

	
	public void receivingMessage(Message message) throws DiscardMessageException {
		
		CommuneAddress source = message.getSource();
		
		if (module.isLocal(source)) {
			//Do not verify session and sequence numbers for loopback messages
			
		} else {
			receivingRemoteMessage(message);
		}

	}
	
	public void sendingMessage(Message message) throws DiscardMessageException {
		
		CommuneAddress destination = message.getDestination();
		
		if (module.isLocal(destination)) {
			//Do not define session and sequence numbers for loopback messages
			
		} else {
			sendingRemoteMessage(message);
		}
	}

	public void sendingRemoteMessage(Message message) throws DiscardMessageException {
		try {
			connectionLock.writeLock().lock();
		
			CommuneAddress destination = message.getDestination();
			Communication communication = communications.get(destination.getContainerID());
			validateOnSend(communication, message);

			if (isFailureDetectorMessage(message)) {
				
				if (message.getFunctionName().equals(InterestProcessor.IS_IT_ALIVE_MESSAGE)) { //Outgoing - is it alive
					validateOutgoing(communication, message);
					message.setSession(communication.getOutgoingSession());
					message.setSequence(communication.getOutgoingSequence());
					
				} else { //Incoming - updateStatus
					validateIncoming(communication, message);
					message.setSession(communication.getIncomingSession());
					message.setSequence(communication.getIncomingSequence());
				}
				
			} else { //Outgoing - send application message
				validateOutgoing(communication, message);
				communication.incOutgoingSequenceNumber();
				message.setSession(communication.getOutgoingSession());
				message.setSequence(communication.getOutgoingSequence());
			}
			
		} finally {
			connectionLock.writeLock().unlock();
		}
	}

	private void validateOnSend(Communication communication, Message message) throws DiscardMessageException {
		if (communication == null) {
			throw new DiscardMessageException("Null communication while sending " + message.toString());
		}		
	}

	private void validateOutgoing(Communication communication, Message message) throws DiscardMessageException {
		if (!communication.isValidOutgoing()) {
			throw new DiscardMessageException("Invalid outgoing session " + communication);
		}
	}
	
	private void validateIncoming(Communication communication, Message message) throws DiscardMessageException {
		if (!communication.isValidIncoming()) {
			throw new DiscardMessageException("Invalid incoming session " + communication);
		}
	}
	
	private void validateOnReceive(Communication communication, Message message) throws DiscardMessageException {
		if (communication == null) {
			throw new DiscardMessageException("Null communication while receiving " + message.toString());
		}
	}

	private static boolean isFailureDetectorMessage(Message message) {
		return InterestProcessor.class.getName().equals(message.getProcessorType());
	}
	
	private Communication receivingRemoteMessage(Message message) throws DiscardMessageException {
		try {
			connectionLock.writeLock().lock();
		
			CommuneAddress source = message.getSource();
			Communication communication = communications.get(source.getContainerID());
			
			if (isFailureDetectorMessage(message)) {
				
				String messageName = message.getFunctionName();

				if (InterestProcessor.IS_IT_ALIVE_MESSAGE.equals(messageName)) { //Incoming - is it alive

					if (communication == null && isCreatingConnection(message)) {
						communication = initIncomingConnection(message);
						communications.put(source.getContainerID(), communication);
					}
					
					validateOnReceive(communication, message);
					receiveHeartbeat(message, communication);
					
				} else if (InterestProcessor.UPDATE_STATUS_MESSAGE.equals(messageName)) { //Outgoing - update status
					validateOnReceive(communication, message);
					validateOutgoing(communication, message);
					receiveUpdateStatus(message, communication);
				}

			} else { //Incoming - receive application message
				validateOnReceive(communication, message);
				validateIncoming(communication, message);
				receivingRemoteApplicationMessage(message, communication);
			}
			
			return communication;

		} finally {
			connectionLock.writeLock().unlock();
		}
	}

	private static boolean isCreatingConnection(Message message) {
		return InterestProcessor.IS_IT_ALIVE_MESSAGE.equals(message.getFunctionName())
			&& message.getSequence() == 0; 
	}

	private Communication initIncomingConnection(Message message) {
		Communication communication = new Communication();
		communication.setState(empty_empty);
		communication.setIncomingSequence(0L);
		communication.setIncomingSession(message.getSession());
		return communication;
	}

	private void receiveHeartbeat(Message message, Communication communication) throws DiscardMessageException {
		Long expectedSession = defineExpectedSessionReceivingHeartbeat(message, communication);
		Long expectedSequence = communication.getIncomingSequence();
		Long messageSession = message.getSession();
		Long messageSequence = message.getSequence();
		CommunicationState state = communication.getState();
		
		validate(messageSession, messageSequence);
		
		if (messageSession.equals(expectedSession)) { //Session ok
			if(messageSequence == 0) {
				state.heartbeatOkSessionZeroSequence(communication);
				
			} else if(messageSequence.equals(expectedSequence)) {
				state.heartbeatOkSessionOkSequence(communication);
				
			} else {
				state.heartbeatOkSessionNonSequence(communication);
			}
			
		} else { //Other session
			if(messageSequence == 0) {
				changeSession(communication, message.getSession());
				state.heartbeatNonSessionZeroSequence(communication);
				
			} else if(messageSequence.equals(expectedSequence)) {
				state.heartbeatNonSessionOkSequence(communication);
				
			} else {
				state.heartbeatNonSessionNonSequence(communication);
			}
		}
	}

	private Long defineExpectedSessionReceivingHeartbeat(Message message, Communication communication) {
		Long expectedSession = communication.getIncomingSession();
		if (expectedSession == null) {
			expectedSession = message.getSession();
			changeSession(communication, expectedSession);
		}
		return expectedSession;
	}

	private void changeSession(Communication communication, Long newSession) {
		communication.setIncomingSession(newSession);	
		communication.setIncomingSequence(0L);
	}

	private void validate(Long messageSession, Long messageSequence) throws DiscardMessageException {
		if (messageSession == null || messageSequence == null) {
			throw new DiscardMessageException();  //TODO create a verbose message
		}
	}

	private void validate(Long messageSession) throws DiscardMessageException {
		if (messageSession == null) {
			throw new DiscardMessageException(); //TODO verbose message
		}
	}

	private void receiveUpdateStatus(Message message, Communication connection) throws DiscardMessageException {
		Long messageSession = message.getSession();
		validate(messageSession);
		CommunicationState state = connection.getState();
		
		if (connection.getOutgoingSession().equals(messageSession)) {
			boolean isUp = proccessUpdateStatusParameters(message);
			if (isUp) {
				state.updateStatusUp(connection);
			} else {
				state.updateStatusDown(connection);
			}
		} else {
			state.updateStatusNonSession(connection);
		}
	}

	private boolean proccessUpdateStatusParameters(Message message) throws DiscardMessageException {
		if (message.getParameterValues().length == 1) {
			Object value = message.getParameterValues()[0];
			if (MonitorableStatus.AVAILABLE.equals(value)) {
				return true;
			} else {
				return false;
			}
		}
			
		throw new DiscardMessageException("Invalid update status message: " + message);
	}


	private void receivingRemoteApplicationMessage(Message message, Communication communication) throws DiscardMessageException {
		Long expectedSession = communication.getIncomingSession();
		communication.incIncomingSequenceNumber();
		Long expectedSequence = communication.getIncomingSequence();

		Long messageSession = message.getSession();
		Long messageSequence = message.getSequence();
		validate(messageSession, messageSequence);
		
		CommunicationState state = communication.getState();
		
		if (expectedSession.equals(messageSession)) {
			
			if(expectedSequence.equals(messageSequence)) {
				
				if(hasCallback(message)) {
					
					if (!communication.isValidOutgoing() && 
							!module.isLocal(message.getSource())) {
						communication.setOutgoingSession(CommunicationStateAdapter.generateSessionNumber());
						communication.setOutgoingSequence(0L);
					}
					
					state.messageWithCallbackOkSessionOkSequence(communication);

				} else {
					state.messageOkSessionOkSequence(communication);
				}
				
			} else {
				state.messageNonSequence(communication);
			}
		} else {
			state.messageNonSession(communication);
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

			ContainerID containerID = stubReference.getStubServiceID().getContainerID();
			Communication communication = communications.get(containerID);

			if (communication == null) {
				communication = initOutgoingConnection();
				communications.put(containerID, communication);
			} 
			
			communication.setStubReference(stubReference);

			if (stubReference.getStubDeploymentID() == null) { //Is not register parameter interest
				CommunicationState state = communication.getState();
				state.registerInterest(communication);
			} 
			
		} finally {
			connectionLock.writeLock().unlock();
		}
	}

	private Communication initOutgoingConnection() {
		Communication connection = new Communication();
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

			Communication communication = communications.get(stubServiceID.getContainerID()); 
			communication.getState().timeout(communication);
			
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

			Communication connection = communications.get(serviceID.getContainerID()); 
			connection.getState().notifyFailure(connection);
			
		} finally {
			connectionLock.writeLock().unlock();
		}
	}

	public void notifyRecovery(ServiceID serviceID) throws DiscardMessageException {
		try {
			connectionLock.writeLock().lock();
			
			//There is not connection between nodes in the same JVM
			if (LoopbackRegistry.isInLoopback(serviceID.getContainerID())) {
				return;
			}

			Communication communication = communications.get(serviceID.getContainerID()); 
			
			if(communication.isValidOutgoing()) {
				communication.getState().notifyRecovery(communication);
			} else {
				throw new DiscardMessageException();
			}
			
			
		} finally {
			connectionLock.writeLock().unlock();
		}
	}


	public Communication getConnection(ContainerID containerID) {
		try {
			connectionLock.readLock().lock();

			return communications.get(containerID); 
			
		} finally {
			connectionLock.readLock().unlock();
		}
	}
	
	void forceNotifyFailure(ServiceID serviceID) {
		module.getInterestManager().sendNotifyFailureMessage(serviceID);
	}


	public void configure(Module module) {
		module.getStubRepository().addListener(this);
		module.getInterestManager().addListener(this);
		module.getServiceProcessor().addListener(this);
	}
}