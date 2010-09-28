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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import br.edu.ufcg.lsd.commune.functionaltests.monitor.data.DeployableClass;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.data.DeployableClass2;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.data.InterestedObject1;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.data.Monitor1;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.data.Stub;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.data.Stub2;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.util.TestDeployMonitorUtil;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.util.TestGetStubsUtil;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.message.Message;
import br.edu.ufcg.lsd.commune.monitor.data.StubData;
import br.edu.ufcg.lsd.commune.testinfra.util.TestWithTestableCommuneContainer;

public class TestGetStubs extends TestWithTestableCommuneContainer {

	private TestDeployMonitorUtil deployMonitorUtil = new TestDeployMonitorUtil();
	private TestGetStubsUtil testGetStubsUtil = new TestGetStubsUtil();
	
	@Test
	public void testEmptyGet() throws Exception {
		module = deployMonitorUtil.createAndStartApplication(module);
		testGetStubsUtil.getEmptyListOfStubs(module);
	}
	
	@Test
	public void testGetWithOneStubByParameter() throws Exception {
		module = deployMonitorUtil.createAndStartApplication(module);
		DeployableClass object = new DeployableClass();
		module.deploy(DeployableClass.OBJECT_NAME, object);
		
		DeploymentID source = createOtherMessageSource();
		DeploymentID target = testGetStubsUtil.getObjectDeployment(module, DeployableClass.OBJECT_NAME).getDeploymentID();

		ServiceID stubSID = new ServiceID(source.getContainerID(), "stub1");
		DeploymentID stubDID = new DeploymentID(stubSID);
		Message message = new Message(source, target, "invoke");
		message.addStubParameter(Stub.class, stubDID);

		module.deliverMessage(message);
		
		module.getServiceConsumer().consumeMessage();
		
		testGetStubsUtil.getStubs(module, target, stubDID, stubSID, Stub.class);
	}
	
	@Test
	public void testGetWithOneStubByManager() throws Exception {
		module = deployMonitorUtil.createAndStartApplication(module);
		InterestedObject1 object = new InterestedObject1();
		module.deploy(InterestedObject1.OTHER_SERVICE_NAME, new Monitor1());
		module.deploy(InterestedObject1.MY_SERVICE_NAME, object);

		ContainerID containerID = 
			new ContainerID(InterestedObject1.USER, InterestedObject1.SERVER, InterestedObject1.CONTAINER);
		ServiceID monitoredID = new ServiceID(containerID, InterestedObject1.SERVICE);

		DeploymentID monitorDID = testGetStubsUtil.getObjectDeployment(module, InterestedObject1.OTHER_SERVICE_NAME).getDeploymentID();
		
		testGetStubsUtil.getStubs(module, monitorDID, null, monitoredID, Stub.class);
	}
	
	
	@Test
	public void testGetWithStubByParameterAndByManager() throws Exception {
		module = deployMonitorUtil.createAndStartApplication(module);
		
		//deploy by parameter
		DeployableClass deployableClass = new DeployableClass();
		module.deploy(DeployableClass.OBJECT_NAME, deployableClass);
		
		DeploymentID source = createOtherMessageSource();
		DeploymentID target = testGetStubsUtil.getObjectDeployment(module, DeployableClass.OBJECT_NAME).getDeploymentID();

		ServiceID stubSID = new ServiceID(source.getContainerID(), "stub1");
		DeploymentID stubDID = new DeploymentID(stubSID);
		Message message = new Message(source, target, "invoke");
		message.addStubParameter(Stub.class, stubDID);

		module.deliverMessage(message);
		
		module.getServiceConsumer().consumeMessage();
		
		//deploy by manager
		InterestedObject1 object = new InterestedObject1();
		module.deploy(InterestedObject1.OTHER_SERVICE_NAME, new Monitor1());
		module.deploy(InterestedObject1.MY_SERVICE_NAME, object);
		
		ContainerID containerID = 
			new ContainerID(InterestedObject1.USER, InterestedObject1.SERVER, InterestedObject1.CONTAINER);
		ServiceID monitoredID = new ServiceID(containerID, InterestedObject1.SERVICE);
		
		DeploymentID monitorDID2 = testGetStubsUtil.getObjectDeployment(module, InterestedObject1.OTHER_SERVICE_NAME).getDeploymentID();
		
		//verify stubs
		List<StubData> stubDatas = new ArrayList<StubData>();
		stubDatas.add(new StubData(monitorDID2, monitoredID, null, testGetStubsUtil.createClassList(Stub.class)));
		stubDatas.add(new StubData(target, stubSID, stubDID, testGetStubsUtil.createClassList(Stub.class)));
		
		testGetStubsUtil.getStubs(module, stubDatas);
	}
	
	@Test
	public void testGetTheSameStubByParameterAndByManager() throws Exception {
		//by parameter
		module = deployMonitorUtil.createAndStartApplication(module);
		DeployableClass object = new DeployableClass();
		module.deploy(DeployableClass.OBJECT_NAME, object);
		
		DeploymentID source = createOtherMessageSource();
		DeploymentID target = testGetStubsUtil.getObjectDeployment(module, DeployableClass.OBJECT_NAME).getDeploymentID();

		ServiceID stubSID = new ServiceID(source.getContainerID(), "stub1");
		DeploymentID stubDID = new DeploymentID(stubSID);
		Message message = new Message(source, target, "invoke");
		message.addStubParameter(Stub.class, stubDID);

		module.deliverMessage(message);
		
		module.getServiceConsumer().consumeMessage();
		
		//by manager
		module.deploy(DeployableClass.OTHER_OBJECT_NAME, new Monitor1());
		object.getManager().registerInterest(DeployableClass.OTHER_OBJECT_NAME, stubSID.toString(), Stub.class);
		
		DeploymentID monitorDID = testGetStubsUtil.getObjectDeployment(module, DeployableClass.OTHER_OBJECT_NAME).getDeploymentID();
		
		testGetStubsUtil.getStubs(module, monitorDID, stubDID, stubSID, Stub.class);
	}
	
	@Test
	public void testGetTheSameStubByManagerAndByParameter() throws Exception {
		module = deployMonitorUtil.createAndStartApplication(module);
		//by manager
		DeployableClass object = new DeployableClass();
		module.deploy(DeployableClass.OBJECT_NAME, object);
		
		DeploymentID source = createOtherMessageSource();
		DeploymentID target = testGetStubsUtil.getObjectDeployment(module, DeployableClass.OBJECT_NAME).getDeploymentID();
		
		ServiceID stubSID = new ServiceID(source.getContainerID(), "stub1");
		DeploymentID stubDID = new DeploymentID(stubSID);
		
		module.deploy(DeployableClass.OTHER_OBJECT_NAME, new Monitor1());
		object.getManager().registerInterest(DeployableClass.OTHER_OBJECT_NAME, stubSID.toString(), Stub.class);
		
		//by parameter
		Message message = new Message(source, target, "invoke");
		message.addStubParameter(Stub.class, stubDID);

		module.deliverMessage(message);
		
		module.getServiceConsumer().consumeMessage();
		
		testGetStubsUtil.getStubs(module, target, stubDID, stubSID, Stub.class);
	}
	
	@Test
	public void testGetWithOneStubByManagerWithRecoveryNotification() throws Exception {
		module = deployMonitorUtil.createAndStartApplication(module);
		InterestedObject1 object = new InterestedObject1();
		module.deploy(InterestedObject1.OTHER_SERVICE_NAME, new Monitor1());
		module.deploy(InterestedObject1.MY_SERVICE_NAME, object);

		ContainerID containerID = 
			new ContainerID(InterestedObject1.USER, InterestedObject1.SERVER, InterestedObject1.CONTAINER, 
					InterestedObject1.PUBLIC_KEY);
		ServiceID monitoredID = new ServiceID(containerID, InterestedObject1.SERVICE);

		runInterestExecution(InterestedObject1.OTHER_SERVICE_NAME, object, monitoredID);
		DeploymentID monitoredDID = sendUpdateStatusAvaliableAndRecover(monitoredID, InterestedObject1.OTHER_SERVICE_NAME, "toDo", Stub.class);
		
		DeploymentID monitorDID = testGetStubsUtil.getObjectDeployment(module, InterestedObject1.OTHER_SERVICE_NAME).getDeploymentID();
		
		testGetStubsUtil.getStubs(module, monitorDID, monitoredDID, monitoredID, Stub.class);
	}
	
	@Test
	public void testGetWithOneStubByManagerWithRecoveryAndFailureNotification() throws Exception {
		module = deployMonitorUtil.createAndStartApplication(module);
		InterestedObject1 object = new InterestedObject1();
		module.deploy(InterestedObject1.OTHER_SERVICE_NAME, new Monitor1());
		module.deploy(InterestedObject1.MY_SERVICE_NAME, object);

		ContainerID containerID = 
			new ContainerID(InterestedObject1.USER, InterestedObject1.SERVER, InterestedObject1.CONTAINER, 
					InterestedObject1.PUBLIC_KEY);
		ServiceID monitoredID = new ServiceID(containerID, InterestedObject1.SERVICE);

		runInterestExecution(InterestedObject1.OTHER_SERVICE_NAME, object, monitoredID);
		DeploymentID monitoredDID = sendUpdateStatusAvaliableAndRecover(monitoredID, InterestedObject1.OTHER_SERVICE_NAME, "toDo", Stub.class);
		
		runInterestExecution(InterestedObject1.OTHER_SERVICE_NAME, object, monitoredID);
		Message message = sendUpdateStatusUnvaliableAndFail(monitoredDID, InterestedObject1.OTHER_SERVICE_NAME, "toDone", Stub.class);
		module.deliverMessage(message);
		
		module.getServiceConsumer().consumeMessage();
		
		DeploymentID monitorDID = testGetStubsUtil.getObjectDeployment(module, InterestedObject1.OTHER_SERVICE_NAME).getDeploymentID();
		
		testGetStubsUtil.getStubs(module, monitorDID, null, monitoredID, Stub.class);
	}
	
	@Test
	public void testGetWithOneStubByParameterWithFailureNotification() throws Exception {
		module = deployMonitorUtil.createAndStartApplication(module);
		DeployableClass object = new DeployableClass();
		module.deploy(DeployableClass.OBJECT_NAME, object);
		
		DeploymentID source = createOtherMessageSource();
		DeploymentID target = testGetStubsUtil.getObjectDeployment(module, DeployableClass.OBJECT_NAME).getDeploymentID();

		ServiceID stubSID = new ServiceID(source.getContainerID(), "stub1");
		DeploymentID stubDID = new DeploymentID(stubSID);
		Message message = new Message(source, target, "invoke");
		message.addStubParameter(Stub.class, stubDID);

		module.deliverMessage(message);
		
		module.getServiceConsumer().consumeMessage();
		
		runInterestExecution(DeployableClass.OBJECT_NAME, object, stubSID);
		Message failureNotificationMessage = sendUpdateStatusUnvaliableAndFail(stubDID, DeployableClass.OBJECT_NAME, "stubIsDown", Stub.class);
		module.deliverMessage(failureNotificationMessage);
		
		module.getServiceConsumer().consumeMessage();
		
		testGetStubsUtil.getStubs(module, target, null, stubSID, Stub.class);
	}
	
	@Test
	public void testGetOneStubByParameterWithFailureAndRecoveryNotification() throws Exception {
		module = deployMonitorUtil.createAndStartApplication(module);
		DeployableClass object = new DeployableClass();
		module.deploy(DeployableClass.OBJECT_NAME, object);
		
		DeploymentID source = createOtherMessageSource();
		DeploymentID target = testGetStubsUtil.getObjectDeployment(module, DeployableClass.OBJECT_NAME).getDeploymentID();

		ServiceID stubSID = new ServiceID(source.getContainerID(), "stub1");
		DeploymentID stubDID = new DeploymentID(stubSID);
		Message message = new Message(source, target, "invoke");
		message.addStubParameter(Stub.class, stubDID);

		module.deliverMessage(message);
		
		module.getServiceConsumer().consumeMessage();
		
		runInterestExecution(DeployableClass.OBJECT_NAME, object, stubSID);
		//notify failure
		Message failureNotificationMessage = sendUpdateStatusUnvaliableAndFail(stubDID, DeployableClass.OBJECT_NAME, "stubIsDown", Stub.class);
		module.deliverMessage(failureNotificationMessage);
		
		module.getServiceConsumer().consumeMessage();
		
		//notify recovery
		stubDID = sendUpdateStatusAvaliableAndRecover(stubSID, DeployableClass.OBJECT_NAME, "stubIsUp", Stub.class);
		
		testGetStubsUtil.getStubs(module, target, stubDID, stubSID, Stub.class);
	}
	
	@Test
	public void testGetWithTwoStubByParameter() throws Exception {
		module = deployMonitorUtil.createAndStartApplication(module);
		
		//first stub
		DeployableClass object = new DeployableClass();
		module.deploy(DeployableClass.OBJECT_NAME, object);
		
		DeploymentID source = createOtherMessageSource();
		DeploymentID target = testGetStubsUtil.getObjectDeployment(module, DeployableClass.OBJECT_NAME).getDeploymentID();

		ServiceID stubSID = new ServiceID(source.getContainerID(), "stub1");
		DeploymentID stubDID = new DeploymentID(stubSID);
		Message message = new Message(source, target, "invoke");
		message.addStubParameter(Stub.class, stubDID);

		module.deliverMessage(message);
		
		module.getServiceConsumer().consumeMessage();
		
		//second stub
		DeployableClass2 object2 = new DeployableClass2();
		module.deploy(DeployableClass2.OBJECT_NAME, object2);
		
		DeploymentID source2 = createOtherMessageSource();
		DeploymentID target2 = testGetStubsUtil.getObjectDeployment(module, DeployableClass2.OBJECT_NAME).getDeploymentID();

		ServiceID stubSID2 = new ServiceID(source2.getContainerID(), "stub2");
		DeploymentID stubDID2 = new DeploymentID(stubSID2);
		Message message2 = new Message(source2, target2, "invoke");
		message2.addStubParameter(Stub2.class, stubDID2);

		module.deliverMessage(message2);
		
		module.getServiceConsumer().consumeMessage();
		
		//verify stubs
		List<StubData> stubDatas = new ArrayList<StubData>();
		stubDatas.add(new StubData(target2, stubSID2, stubDID2, testGetStubsUtil.createClassList(Stub2.class)));
		stubDatas.add(new StubData(target, stubSID, stubDID, testGetStubsUtil.createClassList(Stub.class)));
		
		testGetStubsUtil.getStubs(module, stubDatas);
	}
	
	@Test
	public void testGetStubWithTwoInterests() throws Exception {
		module = deployMonitorUtil.createAndStartApplication(module);
		
		//first interest
		InterestedObject1 object = new InterestedObject1();
		module.deploy(InterestedObject1.OTHER_SERVICE_NAME, new Monitor1());
		module.deploy(InterestedObject1.MY_SERVICE_NAME, object);

		ContainerID containerID = 
			new ContainerID(InterestedObject1.USER, InterestedObject1.SERVER, InterestedObject1.CONTAINER);
		ServiceID monitoredID = new ServiceID(containerID, InterestedObject1.SERVICE);

		DeploymentID monitorDID1 = testGetStubsUtil.getObjectDeployment(module, InterestedObject1.OTHER_SERVICE_NAME).getDeploymentID();
		
		testGetStubsUtil.getStubs(module, monitorDID1, null, monitoredID, Stub.class);
		
		//second interest at the same stub, the monitor will change
		DeployableClass object2 = new DeployableClass();
		module.deploy(DeployableClass.OBJECT_NAME, object2);
		object2.getManager().registerInterest(DeployableClass.OBJECT_NAME, InterestedObject1.USER + "@" + InterestedObject1.SERVER + "/" + 
				InterestedObject1.CONTAINER + "/" + InterestedObject1.SERVICE, Stub.class);
		
		//new monitor deploymentID
		DeploymentID monitorDID2 = testGetStubsUtil.getObjectDeployment(module, DeployableClass.OBJECT_NAME).getDeploymentID();
		
		testGetStubsUtil.getStubs(module, monitorDID2, null, monitoredID, Stub.class);
	}
	
	@Test
	public void testGetWithStubsByParameter() throws Exception {
		module = deployMonitorUtil.createAndStartApplication(module);
		DeployableClass object = new DeployableClass();
		module.deploy(DeployableClass.OBJECT_NAME, object);
		
		DeploymentID source = createOtherMessageSource();
		DeploymentID target = testGetStubsUtil.getObjectDeployment(module, DeployableClass.OBJECT_NAME).getDeploymentID();

		ServiceID stubSID = new ServiceID(source.getContainerID(), "stub1");
		DeploymentID stubDID = new DeploymentID(stubSID);
		
		ServiceID stubSID2 = new ServiceID(source.getContainerID(), "stub2");
		DeploymentID stubDID2 = new DeploymentID(stubSID2);
		
		Message message = new Message(source, target, "invoke2");
		message.addStubParameter(Stub.class, stubDID);
		message.addStubParameter(Stub2.class, stubDID2);

		module.deliverMessage(message);
		
		module.getServiceConsumer().consumeMessage();
		
		List<StubData> stubDatas = new ArrayList<StubData>();
		stubDatas.add(new StubData(target, stubSID2, stubDID2, testGetStubsUtil.createClassList(Stub2.class)));
		stubDatas.add(new StubData(target, stubSID, stubDID, testGetStubsUtil.createClassList(Stub.class)));
		
		testGetStubsUtil.getStubs(module, stubDatas);
	}
}
