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
package br.edu.ufcg.lsd.commune.functionaltests;

import static br.edu.ufcg.lsd.commune.testinfra.util.MessageMatcher.eqServiceMessage;

import org.easymock.EasyMock;
import org.junit.Test;

import br.edu.ufcg.lsd.commune.container.IMessageDeliverer;
import br.edu.ufcg.lsd.commune.container.InvalidDeploymentException;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.functionaltests.data.invokeondeploy.MyObject1_1;
import br.edu.ufcg.lsd.commune.functionaltests.data.invokeondeploy.MyObject1_2;
import br.edu.ufcg.lsd.commune.functionaltests.data.invokeondeploy.MyObject2_1;
import br.edu.ufcg.lsd.commune.functionaltests.data.invokeondeploy.MyObject2_2;
import br.edu.ufcg.lsd.commune.functionaltests.data.invokeondeploy.MyObject2_3_Sub;
import br.edu.ufcg.lsd.commune.functionaltests.data.invokeondeploy.MyObject3;
import br.edu.ufcg.lsd.commune.functionaltests.data.invokeondeploy.MyObject4;
import br.edu.ufcg.lsd.commune.functionaltests.data.invokeondeploy.MyObject5;
import br.edu.ufcg.lsd.commune.functionaltests.data.invokeondeploy.MyObject6;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.message.MessageParameter;
import br.edu.ufcg.lsd.commune.testinfra.util.Context;
import br.edu.ufcg.lsd.commune.testinfra.util.TestWithTestableCommuneContainer;

public class InvokeOnDeploy extends TestWithTestableCommuneContainer {

	@Test(expected=InvalidDeploymentException.class)
	public void controlIsNotRemoteObject1() throws Exception {
		module = createApplication();
		module.deploy(Context.A_SERVICE_NAME, new MyObject1_1());
	}

	@Test(expected=InvalidDeploymentException.class)
	public void controlIsNotRemoteObject2() throws Exception {
		module = createApplication();
		module.deploy(Context.A_SERVICE_NAME, new MyObject1_2());
	}
	
	@Test
	public void methodWithoutParameters() throws Exception {
		module = createApplication();
		IMessageDeliverer messageDeliverer = setMessageDeliverer();
		
		DeploymentID destinationAddress = createDeploymentID(Context.A_SERVICE_NAME);
		
		messageDeliverer.deliverMessage(eqServiceMessage(destinationAddress, destinationAddress, Context.A_METHOD));
		EasyMock.replay(messageDeliverer);
		
		module.deploy(Context.A_SERVICE_NAME, new MyObject2_1());
		EasyMock.verify(messageDeliverer);
	}
	
	@Test(expected=InvalidDeploymentException.class)
	public void notRemoteMethod() throws Exception {
		module = createApplication();
		module.deploy(Context.A_SERVICE_NAME, new MyObject2_2());
	}

	@Test
	public void superClassMethod() throws Exception {
		module = createApplication();
		IMessageDeliverer messageDeliverer = setMessageDeliverer();

		DeploymentID destinationAddress = createDeploymentID(Context.A_SERVICE_NAME);

		messageDeliverer.deliverMessage(eqServiceMessage(destinationAddress, destinationAddress, Context.A_METHOD));
		EasyMock.replay(messageDeliverer);
		
		module.deploy(Context.A_SERVICE_NAME, new MyObject2_3_Sub());
		EasyMock.verify(messageDeliverer);
	}
	
	@Test
	public void invokeOnDeployWithDeploymentID() throws Exception {
		module = createApplication();
		IMessageDeliverer messageDeliverer = setMessageDeliverer();
		
		DeploymentID destinationAddress = createDeploymentID(Context.A_SERVICE_NAME);
		
		MessageParameter deploymentID = new MessageParameter(DeploymentID.class, destinationAddress);
		messageDeliverer.deliverMessage(
				eqServiceMessage(destinationAddress, destinationAddress, Context.A_METHOD, deploymentID));
		EasyMock.replay(messageDeliverer);
		
		module.deploy(Context.A_SERVICE_NAME, new MyObject3());
		EasyMock.verify(messageDeliverer);
	}
	
	@Test(expected=InvalidDeploymentException.class)
	public void invokeOnDeployWithInvalidParameter() throws Exception {
		module = createApplication();
		module.deploy(Context.A_SERVICE_NAME, new MyObject4());
	}

	@Test
	public void invokeOnDeployWithDeploymentIDAndServiceManager() throws Exception {
		MyObject5 object = new MyObject5();

		module = createApplication();
		IMessageDeliverer messageDeliverer = setMessageDeliverer();

		DeploymentID destinationAddress = createDeploymentID(Context.A_SERVICE_NAME);
		MessageParameter deploymentIDParam = new MessageParameter(DeploymentID.class, destinationAddress);
		
		ServiceManager serviceManager = createServiceManager(object, destinationAddress);
		MessageParameter serviceManagerParam = new MessageParameter(ServiceManager.class, serviceManager);
		
		messageDeliverer.deliverMessage(
				eqServiceMessage(destinationAddress, destinationAddress, Context.A_METHOD, deploymentIDParam, 
						serviceManagerParam));
		EasyMock.replay(messageDeliverer);
		
		module.deploy(Context.A_SERVICE_NAME, object);
		EasyMock.verify(messageDeliverer);
	}

	@Test
	public void invokeOnDeployWithServiceManagerAndDeploymentID() throws Exception {
		MyObject6 object = new MyObject6();
		module = createApplication();
		IMessageDeliverer messageDeliverer = setMessageDeliverer();

		DeploymentID destinationAddress = createDeploymentID(Context.A_SERVICE_NAME);
		MessageParameter deploymentIDParam = new MessageParameter(DeploymentID.class, destinationAddress);
		
		ServiceManager serviceManager = createServiceManager(object, destinationAddress);
		MessageParameter serviceManagerParam = new MessageParameter(ServiceManager.class, serviceManager);
		
		messageDeliverer.deliverMessage(
				eqServiceMessage(destinationAddress, destinationAddress, Context.A_METHOD, serviceManagerParam, 
						deploymentIDParam));
		EasyMock.replay(messageDeliverer);
		
		module.deploy(Context.A_SERVICE_NAME, object);
		EasyMock.verify(messageDeliverer);
	}

}