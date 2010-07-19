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

import org.easymock.classextension.EasyMock;
import org.junit.Test;

import br.edu.ufcg.lsd.commune.functionaltests.data.invokewithremoteparameters.Object1;
import br.edu.ufcg.lsd.commune.functionaltests.data.invokewithremoteparameters.Stub;
import br.edu.ufcg.lsd.commune.functionaltests.data.registerinterest.InterestedObject1;
import br.edu.ufcg.lsd.commune.functionaltests.data.registerinterest.InterestedObject2;
import br.edu.ufcg.lsd.commune.functionaltests.data.registerinterest.InterestedObject3;
import br.edu.ufcg.lsd.commune.functionaltests.data.registerinterest.InterestedObject4;
import br.edu.ufcg.lsd.commune.functionaltests.data.registerinterest.Monitor2;
import br.edu.ufcg.lsd.commune.functionaltests.data.registerinterest.Stub1;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.message.Message;
import br.edu.ufcg.lsd.commune.testinfra.util.TestWithTestableCommuneContainer;

public class RecoveryNotification extends TestWithTestableCommuneContainer {


	@Test
	public void registerInterestSelf() throws Exception {
		application = createApplication();
		InterestedObject1 object = new InterestedObject1();
		application.deploy(InterestedObject1.MY_SERVICE_NAME, object);

		ContainerID containerID = 
			new ContainerID(InterestedObject1.USER, InterestedObject1.SERVER, InterestedObject1.CONTAINER, 
					InterestedObject1.PUBLIC_KEY);
		ServiceID monitoredID = new ServiceID(containerID, InterestedObject1.SERVICE);

		runInterestExecution(InterestedObject1.MY_SERVICE_NAME, object, monitoredID);
		
		sendUpdateStatusAvaliableAndRecover(monitoredID, InterestedObject1.MY_SERVICE_NAME, "toDO", Stub1.class);
	}
	
	@Test
	public void registerInterestOther() throws Exception {
		application = createApplication();
		InterestedObject2 object = new InterestedObject2();
		application.deploy(InterestedObject2.OTHER_SERVICE_NAME, new Monitor2());
		application.deploy(InterestedObject2.MY_SERVICE_NAME, object);

		ContainerID containerID = 
			new ContainerID(InterestedObject2.USER, InterestedObject2.SERVER, InterestedObject2.CONTAINER, 
					InterestedObject2.PUBLIC_KEY);
		ServiceID monitoredID = new ServiceID(containerID, InterestedObject2.SERVICE);

		runInterestExecution(InterestedObject2.OTHER_SERVICE_NAME, object, monitoredID);
		sendUpdateStatusAvaliableAndRecover(monitoredID, InterestedObject2.OTHER_SERVICE_NAME, "toDo", Stub1.class);
	}
	
	@Test
	public void twiceIsItAlive() throws Exception {
		application = createApplication();
		InterestedObject2 object = new InterestedObject2();
		application.deploy(InterestedObject2.OTHER_SERVICE_NAME, new Monitor2());
		application.deploy(InterestedObject2.MY_SERVICE_NAME, object);

		ContainerID containerID = 
			new ContainerID(InterestedObject3.USER, InterestedObject3.SERVER, InterestedObject3.CONTAINER, 
					InterestedObject3.PUBLIC_KEY);
		ServiceID monitoredID = new ServiceID(containerID, InterestedObject2.SERVICE);

		runInterestExecution(InterestedObject2.OTHER_SERVICE_NAME, object, monitoredID);
		runInterestExecution(InterestedObject2.OTHER_SERVICE_NAME, object, monitoredID);
		sendUpdateStatusAvaliableAndRecover(monitoredID, InterestedObject2.OTHER_SERVICE_NAME, "toDo", Stub1.class);
	}
	
	@Test
	public void registerInterestSelfWithDeploymentID() throws Exception {
		application = createApplication();
		InterestedObject3 object = new InterestedObject3();
		application.deploy(InterestedObject3.MY_SERVICE_NAME, object);

		ContainerID containerID = 
			new ContainerID(InterestedObject4.USER, InterestedObject4.SERVER, InterestedObject4.CONTAINER, 
					InterestedObject4.PUBLIC_KEY);
		ServiceID monitoredID = new ServiceID(containerID, InterestedObject3.SERVICE);

		runInterestExecution(InterestedObject3.MY_SERVICE_NAME, object, monitoredID);
		sendUpdateStatusAvaliableAndRecover(monitoredID, InterestedObject3.MY_SERVICE_NAME, "toDO", Stub1.class, true);
	}
	
	@Test
	public void registerInterestSelfWithDeploymentIDOnlyOnRecovery() throws Exception {
		application = createApplication();
		InterestedObject4 object = new InterestedObject4();
		application.deploy(InterestedObject4.MY_SERVICE_NAME, object);

		ContainerID containerID = 
			new ContainerID(InterestedObject4.USER, InterestedObject4.SERVER, InterestedObject4.CONTAINER, 
					InterestedObject4.PUBLIC_KEY);
		ServiceID monitoredID = new ServiceID(containerID, InterestedObject4.SERVICE);

		runInterestExecution(InterestedObject4.MY_SERVICE_NAME, object, monitoredID);
		sendUpdateStatusAvaliableAndRecover(monitoredID, InterestedObject4.MY_SERVICE_NAME, "toDO", Stub1.class, true);
	}
	
	@Test
	public void registerInterestSelfTwiceUpdateStatus() throws Exception {
		application = createApplication();
		InterestedObject1 object = new InterestedObject1();
		application.deploy(InterestedObject1.MY_SERVICE_NAME, object);

		ContainerID containerID = 
			new ContainerID(InterestedObject1.USER, InterestedObject1.SERVER, InterestedObject1.CONTAINER, 
					InterestedObject1.PUBLIC_KEY);
		ServiceID monitoredID = new ServiceID(containerID, InterestedObject1.SERVICE);

		runInterestExecution(InterestedObject1.MY_SERVICE_NAME, object, monitoredID);
		
		sendUpdateStatusAvaliableAndRecover(monitoredID, InterestedObject1.MY_SERVICE_NAME, "toDO", Stub1.class);
		sendUpdateStatusAvaliable(monitoredID, InterestedObject1.MY_SERVICE_NAME);
	}
	
	@Test
	public void registerInterestOtherTwiceUpdateStatus() throws Exception {
		application = createApplication();
		InterestedObject2 object = new InterestedObject2();
		application.deploy(InterestedObject2.OTHER_SERVICE_NAME, new Monitor2());
		application.deploy(InterestedObject2.MY_SERVICE_NAME, object);

		ContainerID containerID = 
			new ContainerID(InterestedObject2.USER, InterestedObject2.SERVER, InterestedObject2.CONTAINER, 
					InterestedObject2.PUBLIC_KEY);
		ServiceID monitoredID = new ServiceID(containerID, InterestedObject2.SERVICE);

		runInterestExecution(InterestedObject2.OTHER_SERVICE_NAME, object, monitoredID);
		sendUpdateStatusAvaliableAndRecover(monitoredID, InterestedObject2.OTHER_SERVICE_NAME, "toDo", Stub1.class);
		sendUpdateStatusAvaliable(monitoredID, InterestedObject2.OTHER_SERVICE_NAME);
	}
	
	@Test
	public void invokeWithOneRemoteParameter() throws Exception {
		application = createApplication();
		Object1 mock = EasyMock.createMock(Object1.class);
		Object1 object = new Object1();
		object.setMock(mock);
		
		application.deploy(Object1.MY_SERVICE_NAME, object);
		
		DeploymentID source = createOtherMessageSource();
		DeploymentID target = application.getDeploymentID(Object1.MY_SERVICE_NAME);

		ServiceID stubSID = new ServiceID(source.getContainerID(), "stub1");
		DeploymentID stubDID = new DeploymentID(stubSID);
		Message message = new Message(source, target, "invoke");
		message.addStubParameter(Stub.class, stubDID);

		application.deliverMessage(message);
		
		mock.invoke(myEqRef(Stub.class, stubSID));
		EasyMock.replay(mock);
		
		application.getServiceConsumer().consumeMessage();
		EasyMock.verify(mock);
		
		EasyMock.reset(mock);
		EasyMock.replay(mock);
		
		runInterestExecution(Object1.MY_SERVICE_NAME, object, stubSID);

		EasyMock.verify(mock);

		EasyMock.reset(mock);
		EasyMock.replay(mock);

		sendUpdateStatusAvaliable(stubSID, Object1.MY_SERVICE_NAME);

		EasyMock.verify(mock);
	}

}