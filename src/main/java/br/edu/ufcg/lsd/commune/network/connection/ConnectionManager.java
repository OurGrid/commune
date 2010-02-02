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
	private Map<ContainerID, Connection> connections = new HashMap<ContainerID, Connection>();
	
	//Connection states
	ConnectionState initialState 			= new InitialState(this);
	ConnectionState empty_zero 				= new Empty_Zero(this);
	ConnectionState empty_greatherThenZero 	= new Empty_GreaterThenZero(this);
	ConnectionState down_empty 				= new Down_Empty(this);
	ConnectionState down_zero 				= new Down_Zero(this);
	ConnectionState down_greatherThenZero 	= new Down_GreaterThenZero(this);
	ConnectionState up_empty 				= new Up_Empty(this);
	ConnectionState up_zero 				= new Up_Zero(this);
	ConnectionState up_greatherThenZero 	= new Up_GreaterThenZero(this);
	ConnectionState uping_empty 			= new Uping_Empty(this);
	ConnectionState uping_zero 				= new Uping_Zero(this);
	ConnectionState uping_greatherThenZero 	= new Uping_GreaterThenZero(this);
	ConnectionState downing_empty 			= new Downing_Empty(this);
	
	
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
	
	public void sendingMessage(Message message) {
		
		CommuneAddress destination = message.getDestination();
		
		if (container.isLocal(destination)) {
			//Do not define session and sequence numbers for loopback messages
			
		} else {
			sendingRemoteMessage(message);
		}
	}

	public void sendingRemoteMessage(Message message) {
		try {
			connectionLock.writeLock().lock();
		
			CommuneAddress destination = message.getDestination();
			Connection connection = null;
			Long session = null;
			Long sequence = null;
			
			if (isFailureDetectorMessage(message)) {
				
				if (message.getFunctionName().equals(InterestProcessor.IS_IT_ALIVE_MESSAGE)) {
					connection = connections.get(destination.getContainerID());
					session = connection.getOutgoingSession();
					sequence = connection.getOutgoingSequence();
					
				} else {
					connection = connections.get(destination.getContainerID());
					session = connection.getIncomingSession();
					sequence = connection.getIncomingSequence();
				}
				
			} else {
			
				connection = connections.get(destination.getContainerID());
				
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

	private static boolean isFailureDetectorMessage(Message message) {
		return InterestProcessor.class.getName().equals(message.getProcessorType());
	}
	
	private Connection receivingRemoteMessage(Message message) throws DiscardMessageException {
		try {
			connectionLock.writeLock().lock();
		
			CommuneAddress source = message.getSource();
			Connection connection = null;
			
			if (isFailureDetectorMessage(message)) {
				
				String messageName = message.getFunctionName();

				if (InterestProcessor.IS_IT_ALIVE_MESSAGE.equals(messageName)) {

					connection = connections.get(source.getContainerID());
					
					if (connection == null && isCreatingConnection(message)) {
						connection = initIncomingConnection(message);
						connections.put(source.getContainerID(), connection);
					}
					
					receiveHeartbeat(message, connection);
					
				} else if (InterestProcessor.UPDATE_STATUS_MESSAGE.equals(messageName)) {
					connection = connections.get(source.getContainerID());
					receiveUpdateStatus(message, connection);
				}

			} else {
			
				connection = connections.get(source.getContainerID());
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


	private Connection initIncomingConnection(Message message) {
		Connection connection = new Connection();
		connection.setIncomingSequence(0L);
		connection.setIncomingSession(message.getSession());
		connection.setState(initialState);
		return connection;
	}



	private void receiveHeartbeat(Message message, Connection connection) throws DiscardMessageException {
		Long expectedSession = connection.getIncomingSession();
		Long expectedSequence = connection.getIncomingSequence();

		Long messageSession = message.getSession();
		Long messageSequence = message.getSequence();
		
		validate(messageSession, messageSequence);
		
		ConnectionState state = connection.getState();
		
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

	private void receiveUpdateStatus(Message message, Connection connection) throws DiscardMessageException {
		Long expectedSession = connection.getOutgoingSession();
		Long messageSession = message.getSession();
		validate(messageSession);
		ConnectionState state = connection.getState();
		
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


	private void receivingRemoteApplicationMessage(Message message, Connection connection) throws DiscardMessageException {
		Long expectedSession = connection.getIncomingSession();
		connection.incIncomingSequenceNumber();
		Long expectedSequence = connection.getIncomingSequence();

		Long messageSession = message.getSession();
		Long messageSequence = message.getSequence();
		validate(messageSession, messageSequence);
		
		ConnectionState state = connection.getState();
		
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
			Connection connection = connections.get(stubServiceID.getContainerID());

			ConnectionState state = null;
			
			if (connection == null) {
				state = initialState;
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

	private Connection initOutgoingConnection(StubReference stubReference) {
		Connection connection = new Connection();
		connection.setStubReference(stubReference);
		connection.setState(initialState);
		return connection;
	}
	
	public void stubReleased(ServiceID stubServiceID) {
		try {
			connectionLock.writeLock().lock();

			Connection connection = connections.remove(stubServiceID.getContainerID());
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

			Connection connection = connections.get(stubServiceID.getContainerID()); 
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

			Connection connection = connections.get(serviceID.getContainerID()); 
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

			Connection connection = connections.get(serviceID.getContainerID()); 
			connection.getState().notifyRecovery(connection);
			
		} finally {
			connectionLock.writeLock().unlock();
		}
	}


	public Connection getConnection(ContainerID containerID) {
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