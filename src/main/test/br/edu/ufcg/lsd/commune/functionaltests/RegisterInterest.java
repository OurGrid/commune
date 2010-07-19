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

import org.junit.Test;

import br.edu.ufcg.lsd.commune.container.InvalidMonitoringException;
import br.edu.ufcg.lsd.commune.functionaltests.data.registerinterest.InterestedObject1;
import br.edu.ufcg.lsd.commune.functionaltests.data.registerinterest.InterestedObject2;
import br.edu.ufcg.lsd.commune.functionaltests.data.registerinterest.InterestedObject3;
import br.edu.ufcg.lsd.commune.functionaltests.data.registerinterest.InterestedObject4;
import br.edu.ufcg.lsd.commune.functionaltests.data.registerinterest.InterestedObject5;
import br.edu.ufcg.lsd.commune.functionaltests.data.registerinterest.InterestedObject6;
import br.edu.ufcg.lsd.commune.functionaltests.data.registerinterest.Monitor2;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.testinfra.util.TestWithTestableCommuneContainer;

public class RegisterInterest extends TestWithTestableCommuneContainer {


	@Test
	public void registerInterestSelf() throws Exception {
		application = createApplication();
		InterestedObject1 object = new InterestedObject1();
		application.deploy(InterestedObject1.MY_SERVICE_NAME, object);

		ContainerID containerID = 
			new ContainerID(InterestedObject1.USER, InterestedObject1.SERVER, InterestedObject1.CONTAINER);
		ServiceID monitoredID = new ServiceID(containerID, InterestedObject1.SERVICE);

		runInterestExecution(InterestedObject1.MY_SERVICE_NAME, object, monitoredID);
	}
	
	@Test
	public void registerInterestOther() throws Exception {
		application = createApplication();
		InterestedObject2 object = new InterestedObject2();
		Monitor2 monitor = new Monitor2();
		application.deploy(InterestedObject2.OTHER_SERVICE_NAME, monitor);
		application.deploy(InterestedObject2.MY_SERVICE_NAME, object);

		ContainerID containerID = 
			new ContainerID(InterestedObject2.USER, InterestedObject2.SERVER, InterestedObject2.CONTAINER);
		ServiceID monitoredID = new ServiceID(containerID, InterestedObject2.SERVICE);

		runInterestExecution(InterestedObject2.OTHER_SERVICE_NAME, monitor, monitoredID);
	}
	
	@Test
	public void twiceIsItAlive() throws Exception {
		application = createApplication();
		InterestedObject2 object = new InterestedObject2();
		application.deploy(InterestedObject2.OTHER_SERVICE_NAME, new Monitor2());
		application.deploy(InterestedObject2.MY_SERVICE_NAME, object);

		ContainerID containerID = 
			new ContainerID(InterestedObject2.USER, InterestedObject2.SERVER, InterestedObject2.CONTAINER);
		ServiceID monitoredID = new ServiceID(containerID, InterestedObject2.SERVICE);

		runInterestExecution(InterestedObject2.OTHER_SERVICE_NAME, object, monitoredID);
		
		runInterestExecution(InterestedObject2.OTHER_SERVICE_NAME, object, monitoredID);
	}
	
	@Test
	public void registerInterestSelfWithDeploymentID() throws Exception {
		application = createApplication();
		InterestedObject3 object = new InterestedObject3();
		application.deploy(InterestedObject3.MY_SERVICE_NAME, object);

		ContainerID containerID = 
			new ContainerID(InterestedObject3.USER, InterestedObject3.SERVER, InterestedObject3.CONTAINER);
		ServiceID monitoredID = new ServiceID(containerID, InterestedObject3.SERVICE);

		runInterestExecution(InterestedObject3.MY_SERVICE_NAME, object, monitoredID);
	}
	
	@Test
	public void registerInterestSelfWithDeploymentIDOnlyOnRecovery() throws Exception {
		application = createApplication();
		InterestedObject4 object = new InterestedObject4();
		application.deploy(InterestedObject4.MY_SERVICE_NAME, object);

		ContainerID containerID = 
			new ContainerID(InterestedObject4.USER, InterestedObject4.SERVER, InterestedObject4.CONTAINER);
		ServiceID monitoredID = new ServiceID(containerID, InterestedObject4.SERVICE);

		runInterestExecution(InterestedObject4.MY_SERVICE_NAME, object, monitoredID);
	}

	@Test(expected=InvalidMonitoringException.class)
	public void wrongDeploymentIDOrderOnRecovery() throws Exception {
		application = createApplication();
		InterestedObject5 object = new InterestedObject5();
		application.deploy(InterestedObject5.MY_SERVICE_NAME, object);
		application.getServiceConsumer().consumeMessage();

		ContainerID containerID = 
			new ContainerID(InterestedObject5.USER, InterestedObject5.SERVER, InterestedObject5.CONTAINER);
		ServiceID monitoredID = new ServiceID(containerID, InterestedObject5.SERVICE);

		runInterestExecution(InterestedObject5.MY_SERVICE_NAME, object, monitoredID);
	}

	@Test(expected=InvalidMonitoringException.class)
	public void wrongDeploymentIDOrderOnFailure() throws Exception {
		application = createApplication();
		InterestedObject6 object = new InterestedObject6();
		application.deploy(InterestedObject6.MY_SERVICE_NAME, object);
		application.getServiceConsumer().consumeMessage();

		ContainerID containerID = 
			new ContainerID(InterestedObject6.USER, InterestedObject6.SERVER, InterestedObject6.CONTAINER);
		ServiceID monitoredID = new ServiceID(containerID, InterestedObject6.SERVICE);

		runInterestExecution(InterestedObject6.MY_SERVICE_NAME, object, monitoredID);
	}

	//TODO Notification parameters
}