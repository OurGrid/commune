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


import java.util.ArrayList;
import java.util.List;

import org.easymock.classextension.EasyMock;
import org.junit.Test;

import br.edu.ufcg.lsd.commune.functionaltests.data.invokewithremoteparameters.Object1;
import br.edu.ufcg.lsd.commune.functionaltests.data.invokewithremoteparameters.Stub;
import br.edu.ufcg.lsd.commune.functionaltests.data.remotecollectionparameters.RemoteObject1;
import br.edu.ufcg.lsd.commune.functionaltests.data.remotecollectionparameters.RemoteParameter;
import br.edu.ufcg.lsd.commune.functionaltests.util.TestWithTestableCommuneContainer;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.message.Message;

public class InvokeWithRemoteParameters extends TestWithTestableCommuneContainer {


	private DeploymentID source;
	private DeploymentID target;

	
	@Test
	public void invokeWithOneRemoteParameter() throws Exception {
		application = createApplication();
		Object1 mock = EasyMock.createMock(Object1.class);
		Object1 object = new Object1();
		object.setMock(mock);
		
		application.getContainer().deploy(Object1.MY_SERVICE_NAME, object);
		
		source = createOtherMessageSource();
		target = application.getDeploymentID(Object1.MY_SERVICE_NAME);

		ServiceID stubSID = new ServiceID(source.getContainerID(), "stub1");
		DeploymentID stubDID = new DeploymentID(stubSID);
		Message message = new Message(source, target, "invoke");
		message.addStubParameter(Stub.class, stubDID);

		application.getContainer().deliverMessage(message);
		
		mock.invoke(myEqRef(Stub.class, stubSID));
		EasyMock.replay(mock);
		
		application.getContainer().getServiceConsumer().consumeMessage();
		EasyMock.verify(mock);
		
		EasyMock.reset(mock);
		EasyMock.replay(mock);
		
		runInterestExecution(Object1.MY_SERVICE_NAME, object, stubSID);

		EasyMock.verify(mock);
	}
	
	@Test
	public void invokeWithOneRemoteCollectionParameter() throws Exception {
		application = createApplication();
		RemoteObject1 mock = EasyMock.createMock(RemoteObject1.class);
		RemoteObject1 object = new RemoteObject1();
		object.setMock(mock);

		application.getContainer().deploy(RemoteObject1.MY_SERVICE_NAME, object);
		
		source = createOtherMessageSource();
		target = application.getDeploymentID(RemoteObject1.MY_SERVICE_NAME);

		ServiceID stub1SID = new ServiceID(source.getContainerID(), "stub1");
		DeploymentID stub1DID = new DeploymentID(stub1SID);
		ServiceID stub2SID = new ServiceID(source.getContainerID(), "stub2");
		DeploymentID stub2DID = new DeploymentID(stub2SID);
		ServiceID stub3SID = new ServiceID(source.getContainerID(), "stub3");
		DeploymentID stub3DID = new DeploymentID(stub3SID);
		
		List<DeploymentID> parametersList = new ArrayList<DeploymentID>();
		parametersList.add(stub1DID);
		parametersList.add(stub2DID);
		parametersList.add(stub3DID);
		
		Message message = new Message(source, target, "list");
		message.addStubList(RemoteParameter.class, parametersList);

		application.getContainer().deliverMessage(message);

		mock.list(myEqListRef(RemoteParameter.class, stub1SID, stub2SID, stub3SID));
		EasyMock.replay(mock);

		application.getContainer().getServiceConsumer().consumeMessage();
		EasyMock.verify(mock);
		
		EasyMock.reset(mock);
		EasyMock.replay(mock);
		
		runInterestExecution(RemoteObject1.MY_SERVICE_NAME, object, stub1SID);
		runInterestExecution(RemoteObject1.MY_SERVICE_NAME, object, stub2SID);
		runInterestExecution(RemoteObject1.MY_SERVICE_NAME, object, stub3SID);

		EasyMock.verify(mock);

	}

}