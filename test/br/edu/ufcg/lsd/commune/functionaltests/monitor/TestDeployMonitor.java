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

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Test;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.control.ModuleControlClient;
import br.edu.ufcg.lsd.commune.container.control.ServerModuleManager;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.matchers.ControlOperationResultMatcher;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.util.TestDeployMonitorUtil;
import br.edu.ufcg.lsd.commune.functionaltests.util.Context;
import br.edu.ufcg.lsd.commune.functionaltests.util.TestContext;
import br.edu.ufcg.lsd.commune.functionaltests.util.TestWithTestableCommuneContainer;
import br.edu.ufcg.lsd.commune.functionaltests.util.TestableApplication;
import br.edu.ufcg.lsd.commune.monitor.MonitorConstants;


public class TestDeployMonitor extends TestWithTestableCommuneContainer {
	
	private TestDeployMonitorUtil deployMonitorUtil = new TestDeployMonitorUtil();

	@Test
	public void testDeployingMonitor() throws Exception {
		TestContext context = deployMonitorUtil.createBasicContext();
		application = new TestableApplication(Context.A_CONTAINER_NAME, context);
		
		ObjectDeployment object = application.getObject(MonitorConstants.COMMUNE_MONITOR_CONTROLLER);
		assertNull(object);
		
		application = deployMonitorUtil.startApplication(application);
		
		object = application.getObject(MonitorConstants.COMMUNE_MONITOR_CONTROLLER);
		assertNotNull(object);
	}
	
	@Test
	public void testNotDeployingMonitor() throws Exception {
		TestContext context = Context.createRealContext();
		application = new TestableApplication(Context.A_CONTAINER_NAME, context);
		
		ObjectDeployment object = application.getObject(MonitorConstants.COMMUNE_MONITOR_CONTROLLER);
		assertNull(object);

		ObjectDeployment control = application.getObject(Module.CONTROL_OBJECT_NAME);
		
		ModuleControlClient appClient = EasyMock.createMock(ModuleControlClient.class);
		appClient.operationSucceed(ControlOperationResultMatcher.noError());
		
		EasyMock.replay(appClient);
		
		((ServerModuleManager)control.getObject()).start(appClient);
		
		object = application.getObject(MonitorConstants.COMMUNE_MONITOR_CONTROLLER);

		EasyMock.verify(appClient);
		assertNull(object);

	}

}
