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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.control.ModuleControl;
import br.edu.ufcg.lsd.commune.container.control.ModuleManager;
import br.edu.ufcg.lsd.commune.container.control.ModuleStatusProvider;
import br.edu.ufcg.lsd.commune.container.control.ServerModuleManager;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.data.DeployableClass;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.data.DeployableInterface;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.util.TestDeployMonitorUtil;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.util.TestGetDeployedObjectsUtil;
import br.edu.ufcg.lsd.commune.functionaltests.util.TestWithTestableCommuneContainer;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.monitor.CommuneMonitor;

public class TestGetDeployedObjects extends TestWithTestableCommuneContainer {
	
	private	TestDeployMonitorUtil deployMonitorUtil = new TestDeployMonitorUtil();
	private TestGetDeployedObjectsUtil testGetDeployedObjectsUtil = new TestGetDeployedObjectsUtil();
	
	@Test
	public void testSimpleGet() throws Exception {
		application = deployMonitorUtil.createAndStartApplication(application);
		
		Map<DeploymentID, Collection<Class<?>>> deployedObjects = new HashMap<DeploymentID, Collection<Class<?>>>();
		
		DeploymentID deploymentID1 = testGetDeployedObjectsUtil.getObjectDeployment(application, Module.CONTROL_OBJECT_NAME).getDeploymentID();
		List<Class<?>> list1 = new ArrayList<Class<?>>();
		list1.add(ModuleManager.class);
		list1.add(ServerModuleManager.class);
		list1.add(ModuleControl.class);
		list1.add(ModuleStatusProvider.class);
		deployedObjects.put(deploymentID1, list1);
		
		DeploymentID deploymentID2 = testGetDeployedObjectsUtil.getMonitorControllerDeployment(application).getDeploymentID();
		List<Class<?>> list2 = new ArrayList<Class<?>>();
		list2.add(CommuneMonitor.class);
		deployedObjects.put(deploymentID2, list2);
		
		testGetDeployedObjectsUtil.getDeployedObjects(application, deployedObjects);
	}
	
	@Test
	public void testDeployObject() throws Exception {
		application = deployMonitorUtil.createAndStartApplication(application);
		//Deploy another object
		application.getContainer().deploy(DeployableClass.OBJECT_NAME, new DeployableClass());
		
		Map<DeploymentID, Collection<Class<?>>> deployedObjects = new HashMap<DeploymentID, Collection<Class<?>>>();
		
		DeploymentID deploymentID1 = testGetDeployedObjectsUtil.getObjectDeployment(application, Module.CONTROL_OBJECT_NAME).getDeploymentID();
		List<Class<?>> list1 = new ArrayList<Class<?>>();
		list1.add(ModuleManager.class);
		list1.add(ServerModuleManager.class);
		list1.add(ModuleControl.class);
		list1.add(ModuleStatusProvider.class);
		deployedObjects.put(deploymentID1, list1);
		
		DeploymentID deploymentID2 = testGetDeployedObjectsUtil.getMonitorControllerDeployment(application).getDeploymentID();
		List<Class<?>> list2 = new ArrayList<Class<?>>();
		list2.add(CommuneMonitor.class);
		deployedObjects.put(deploymentID2, list2);
		
		DeploymentID deploymentID3 = testGetDeployedObjectsUtil.getObjectDeployment(application, DeployableClass.OBJECT_NAME).getDeploymentID();
		List<Class<?>> list3 = new ArrayList<Class<?>>();
		list3.add(DeployableInterface.class);
		deployedObjects.put(deploymentID3, list3);
		
		testGetDeployedObjectsUtil.getDeployedObjects(application, deployedObjects);
	}
	
	@Test
	public void testDeployAndUndeployObject() throws Exception {
		application = deployMonitorUtil.createAndStartApplication(application);
		//Deploy another object
		application.getContainer().deploy(DeployableClass.OBJECT_NAME, new DeployableClass());
		
		Map<DeploymentID, Collection<Class<?>>> deployedObjects = new HashMap<DeploymentID, Collection<Class<?>>>();
		
		DeploymentID deploymentID1 = testGetDeployedObjectsUtil.getObjectDeployment(application, Module.CONTROL_OBJECT_NAME).getDeploymentID();
		List<Class<?>> list1 = new ArrayList<Class<?>>();
		list1.add(ModuleManager.class);
		list1.add(ServerModuleManager.class);
		list1.add(ModuleControl.class);
		list1.add(ModuleStatusProvider.class);
		deployedObjects.put(deploymentID1, list1);
		
		DeploymentID deploymentID2 = testGetDeployedObjectsUtil.getMonitorControllerDeployment(application).getDeploymentID();
		List<Class<?>> list2 = new ArrayList<Class<?>>();
		list2.add(CommuneMonitor.class);
		deployedObjects.put(deploymentID2, list2);
		
		DeploymentID deploymentID3 = testGetDeployedObjectsUtil.getObjectDeployment(application, DeployableClass.OBJECT_NAME).getDeploymentID();
		List<Class<?>> list3 = new ArrayList<Class<?>>();
		list3.add(DeployableInterface.class);
		deployedObjects.put(deploymentID3, list3);
		
		testGetDeployedObjectsUtil.getDeployedObjects(application, deployedObjects);
		
		deployedObjects.remove(testGetDeployedObjectsUtil.getObjectDeployment(application, DeployableClass.OBJECT_NAME).getDeploymentID());
		//Undeploy the object
		application.getContainer().undeploy(DeployableClass.OBJECT_NAME);
		testGetDeployedObjectsUtil.getDeployedObjects(application, deployedObjects);
	}
	
	@Test
	public void testGetAndAfterDeployAndUndeployObject() throws Exception {
		application = deployMonitorUtil.createAndStartApplication(application);
		
		Map<DeploymentID, Collection<Class<?>>> deployedObjects = new HashMap<DeploymentID, Collection<Class<?>>>();
		
		DeploymentID deploymentID1 = testGetDeployedObjectsUtil.getObjectDeployment(application, Module.CONTROL_OBJECT_NAME).getDeploymentID();
		List<Class<?>> list1 = new ArrayList<Class<?>>();
		list1.add(ModuleManager.class);
		list1.add(ServerModuleManager.class);
		list1.add(ModuleControl.class);
		list1.add(ModuleStatusProvider.class);
		deployedObjects.put(deploymentID1, list1);
		
		DeploymentID deploymentID2 = testGetDeployedObjectsUtil.getMonitorControllerDeployment(application).getDeploymentID();
		List<Class<?>> list2 = new ArrayList<Class<?>>();
		list2.add(CommuneMonitor.class);
		deployedObjects.put(deploymentID2, list2);
		
		testGetDeployedObjectsUtil.getDeployedObjects(application, deployedObjects);
		
		//Deploy another object
		application.getContainer().deploy(DeployableClass.OBJECT_NAME, new DeployableClass());
		DeploymentID deploymentID3 = testGetDeployedObjectsUtil.getObjectDeployment(application, DeployableClass.OBJECT_NAME).getDeploymentID();
		List<Class<?>> list3 = new ArrayList<Class<?>>();
		list3.add(DeployableInterface.class);
		deployedObjects.put(deploymentID3, list3);
		
		testGetDeployedObjectsUtil.getDeployedObjects(application, deployedObjects);
		
		//Undeploy the object
		deployedObjects.remove(testGetDeployedObjectsUtil.getObjectDeployment(application, DeployableClass.OBJECT_NAME).getDeploymentID());
		application.getContainer().undeploy(DeployableClass.OBJECT_NAME);
		testGetDeployedObjectsUtil.getDeployedObjects(application, deployedObjects);
	}
}
