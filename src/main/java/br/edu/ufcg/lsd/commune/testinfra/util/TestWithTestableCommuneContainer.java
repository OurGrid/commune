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
package br.edu.ufcg.lsd.commune.testinfra.util;

import static br.edu.ufcg.lsd.commune.testinfra.util.MessageMatcher.eqInterestMessage;
import static br.edu.ufcg.lsd.commune.testinfra.util.MessageMatcher.eqServiceMessage;
import static br.edu.ufcg.lsd.commune.testinfra.util.StubCollectionMatcher.eqCollectionRef;

import java.util.List;

import org.easymock.EasyMock;
import org.junit.After;

import br.edu.ufcg.lsd.commune.container.IMessageDeliverer;
import br.edu.ufcg.lsd.commune.container.IMessageSender;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.message.Message;
import br.edu.ufcg.lsd.commune.message.MessageParameter;
import br.edu.ufcg.lsd.commune.message.StubParameter;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;
import br.edu.ufcg.lsd.commune.processor.interest.InterestProcessor;
import br.edu.ufcg.lsd.commune.processor.interest.Monitor;
import br.edu.ufcg.lsd.commune.processor.interest.MonitorableStatus;

public class TestWithTestableCommuneContainer {

	
	protected TestableApplication application;

	
	@After
	public void cleanContainer() throws CommuneNetworkException {
		if (application != null) {
			application.stop();
		}
	}
	
	protected TestableApplication createApplication() 
			throws CommuneNetworkException, ProcessorStartException {
		
		TestContext context = Context.createRealContext();
		TestableApplication testableApplication = new TestableApplication(Context.A_CONTAINER_NAME, context);
		return testableApplication;
	}
	
	protected <T> T myEqRef(Class<T> clazz, ServiceID target) {
		return null; //eqRef(application.getContainer(), target);
	}

	protected <T> List<T> myEqListRef(Class<T> clazz, ServiceID... target) {
		return eqCollectionRef(application.getContainer(), target);
	}

	protected IMessageDeliverer setMessageDeliverer() {
		IMessageDeliverer messageDeliverer = EasyMock.createMock(IMessageDeliverer.class);
		application.getContainer().setMessageDelivererMock(messageDeliverer);
		return messageDeliverer;
	}

	protected IMessageSender setMessageSender() {
		IMessageSender messageSender = EasyMock.createMock(IMessageSender.class);
		application.getContainer().setMessageSenderMock(messageSender);
		return messageSender;
	}

	protected DeploymentID createDeploymentID(String serviceName) {
		ContainerID containerAddress = new ContainerID(Context.USER, Context.REAL_SERVER, Context.A_CONTAINER_NAME); 
		return new DeploymentID(containerAddress, serviceName);
	}

	protected ServiceID createServiceID(String serviceName) {
		ContainerID containerAddress = new ContainerID(Context.USER, Context.REAL_SERVER, Context.A_CONTAINER_NAME); 
		return new ServiceID(containerAddress, serviceName);
	}

	protected ServiceManager createServiceManager(Object object, DeploymentID deploymentID) {
		ObjectDeployment od = new ObjectDeployment(application.getContainer(), deploymentID, object);
		return new ServiceManager(application, application.getContainer().getContext(), od);
	}
	
	protected DeploymentID createOtherMessageSource() {
		ContainerID containerID = new ContainerID("otherUser", "otherServer", "otherModule", "otherPubKey");
		return new DeploymentID(new ServiceID(containerID, "otherService"));
	}
	
	protected void runInterestExecution(String monitorName, Object monitorObj, ServiceID monitoredID) {
		IMessageSender messageSender = setMessageSender();
		
		DeploymentID monitorDID = application.getDeploymentID(monitorName);
		messageSender.sendMessage(
				eqInterestMessage(monitorDID, monitoredID, InterestProcessor.IS_IT_ALIVE_MESSAGE));
		EasyMock.replay(messageSender);
		
		ObjectDeployment monitorDeployment = new ObjectDeployment(application.getContainer(), monitorDID, monitorObj);
		Monitor monitor = new Monitor(monitorDeployment, null, null);
		TestableInterestManager.runInterestExecution(monitor, monitoredID);

		EasyMock.verify(messageSender);
	}
	
	protected DeploymentID sendIsItAliveToAvaliableObject(String monitoredName) {
		IMessageSender messageSender = setMessageSender();
		
		DeploymentID monitoredDID = application.getDeploymentID(monitoredName);
		DeploymentID monitorDID = createOtherMessageSource();
		MessageParameter statusParam = new MessageParameter(MonitorableStatus.class, MonitorableStatus.AVAILABLE);
		
		messageSender.sendMessage(
				eqInterestMessage(monitoredDID, monitorDID, InterestProcessor.UPDATE_STATUS_MESSAGE, statusParam));
		EasyMock.replay(messageSender);
		
		Message message = 
			new Message(monitorDID, monitoredDID.getServiceID(), InterestProcessor.IS_IT_ALIVE_MESSAGE, 
					InterestProcessor.class.getName());
		application.getContainer().deliverMessage(message);
		application.getContainer().getInterestConsumer().consumeMessage();

		EasyMock.verify(messageSender);
		
		return monitorDID;
	}
	
	protected DeploymentID sendIsItAliveToUnavaliableObject(String monitoredName) {
		IMessageSender messageSender = setMessageSender();
		
		ServiceID monitoredID = createServiceID(monitoredName);
		DeploymentID monitorDID = createOtherMessageSource();
		MessageParameter statusParam = new MessageParameter(MonitorableStatus.class, MonitorableStatus.UNAVAILABLE);
		
		messageSender.sendMessage(
				eqInterestMessage(monitoredID, monitorDID, InterestProcessor.UPDATE_STATUS_MESSAGE, statusParam));
		EasyMock.replay(messageSender);
		
		Message message = 
			new Message(monitorDID, monitoredID, InterestProcessor.IS_IT_ALIVE_MESSAGE, 
					InterestProcessor.class.getName());
		application.getContainer().deliverMessage(message);
		application.getContainer().getInterestConsumer().consumeMessage();

		EasyMock.verify(messageSender);
		
		return monitorDID;
	}

	protected DeploymentID sendUpdateStatusAvaliableAndRecover(ServiceID monitoredID, String monitorName, 
			String recoveryFunctionName, Class<?> monitoredType) {
		return sendUpdateStatusAvaliableAndRecover(monitoredID, monitorName, recoveryFunctionName, monitoredType, false);
	}

	protected DeploymentID sendUpdateStatusAvaliableAndRecover(ServiceID monitoredID, String monitorName, 
			String recoveryFunctionName, Class<?> monitoredType, boolean notificationHasDeploymentID) {
		IMessageSender messageSender = setMessageSender();
		DeploymentID monitoredDID = new DeploymentID(monitoredID); 
		
		DeploymentID monitorDID = application.getDeploymentID(monitorName);
		
		MessageParameter stubParameter = new MessageParameter(monitoredType, new StubParameter(monitoredDID));
		
		if (notificationHasDeploymentID) {
			MessageParameter stubDID = new MessageParameter(DeploymentID.class, monitoredDID);
			messageSender.sendMessage(
					eqServiceMessage(monitorDID.getContainerID(), monitorDID, recoveryFunctionName, stubParameter, 
							stubDID));
			
		} else {
			messageSender.sendMessage(
					eqServiceMessage(monitorDID.getContainerID(), monitorDID, recoveryFunctionName, stubParameter));
		}
		
		EasyMock.replay(messageSender);
		
		Message message = 
			new Message(monitoredDID, monitorDID, InterestProcessor.UPDATE_STATUS_MESSAGE, 
					InterestProcessor.class.getName());
		message.addParameter(MonitorableStatus.class, MonitorableStatus.AVAILABLE);
		application.getContainer().deliverMessage(message);
		application.getContainer().getInterestConsumer().consumeMessage();

		EasyMock.verify(messageSender);
		
		return monitoredDID;
	}
	
	protected DeploymentID sendUpdateStatusAvaliable(ServiceID monitoredID, String monitorName) {
		IMessageSender messageSender = setMessageSender();
		EasyMock.replay(messageSender);
		
		DeploymentID monitoredDID = new DeploymentID(monitoredID); 
		DeploymentID monitorDID = application.getDeploymentID(monitorName);
		Message message = 
			new Message(monitoredDID, monitorDID, InterestProcessor.UPDATE_STATUS_MESSAGE, 
					InterestProcessor.class.getName());
		message.addParameter(MonitorableStatus.class, MonitorableStatus.AVAILABLE);
		application.getContainer().deliverMessage(message);
		application.getContainer().getInterestConsumer().consumeMessage();

		EasyMock.verify(messageSender);
		
		return monitoredDID;
	}

	protected Message sendUpdateStatusUnvaliableAndFail(DeploymentID monitoredDID, String monitorName, 
			String failureFunctionName, Class<?> monitoredType) {
		return sendUpdateStatusUnvaliableAndFail(monitoredDID, monitorName, failureFunctionName, monitoredType, false);
	}

	protected Message sendUpdateStatusUnvaliableAndFail(DeploymentID monitoredDID, String monitorName, 
			String failureFunctionName, Class<?> monitoredType, boolean notificationHasDeploymentID) {

		DeploymentID monitorDID = application.getDeploymentID(monitorName);
		Message failureNotificationMessage = new Message(monitorDID.getContainerID(), monitorDID, failureFunctionName);
		failureNotificationMessage.addStubParameter(monitoredType, monitoredDID);
		
		IMessageSender messageSender = setMessageSender();
		
		MessageParameter stubParameter = new MessageParameter(monitoredType, new StubParameter(monitoredDID));
		
		if (notificationHasDeploymentID) {
			MessageParameter stubDID = new MessageParameter(DeploymentID.class, monitoredDID);
			messageSender.sendMessage(
					eqServiceMessage(monitorDID.getContainerID(), monitorDID, failureFunctionName, stubParameter, 
							stubDID));
			failureNotificationMessage.addParameter(DeploymentID.class, stubDID);
			
		} else {
			messageSender.sendMessage(
					eqServiceMessage(monitorDID.getContainerID(), monitorDID, failureFunctionName, stubParameter));
		}
		
		EasyMock.replay(messageSender);
		
		Message message = 
			new Message(monitoredDID.getServiceID(), monitorDID, InterestProcessor.UPDATE_STATUS_MESSAGE, 
					InterestProcessor.class.getName());
		message.addParameter(MonitorableStatus.class, MonitorableStatus.UNAVAILABLE);
		application.getContainer().deliverMessage(message);
		application.getContainer().getInterestConsumer().consumeMessage();

		EasyMock.verify(messageSender);
		
		return failureNotificationMessage;
	}
	
	protected void sendUpdateStatusUnvaliable(ServiceID monitoredDID, String monitorName) {

		DeploymentID monitorDID = application.getDeploymentID(monitorName);

		IMessageSender messageSender = setMessageSender();
		
		EasyMock.replay(messageSender);
		
		Message message = 
			new Message(monitoredDID, monitorDID, InterestProcessor.UPDATE_STATUS_MESSAGE, 
					InterestProcessor.class.getName());
		message.addParameter(MonitorableStatus.class, MonitorableStatus.UNAVAILABLE);
		application.getContainer().deliverMessage(message);
		application.getContainer().getInterestConsumer().consumeMessage();

		EasyMock.verify(messageSender);
	}

}