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
package br.edu.ufcg.lsd.commune.testinfra;

import org.easymock.classextension.EasyMock;
import org.easymock.classextension.IMocksControl;
import org.junit.Before;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.network.xmpp.XMPPProperties;
import br.edu.ufcg.lsd.commune.testinfra.util.TestContext;

/**
 *
 */
public abstract class AcceptanceTestCase {

	private TestContext context = buildComponentContext();
	
	//TODO Verify if it can be removed
	private IMocksControl mockControl;

	private IMocksControl niceControl;
	
	protected static final boolean NICE = true;
	
	protected static final boolean NOT_NICE = false;
	
	@Before
	protected void setUp() throws Exception {
		activateMockControls();
		resetActiveMocks();
	}
	
	/**
	 * @return The context associated to this AcceptanceTest
	 */
	protected TestContext getComponentContext() {
		if (context == null) {
			throw new IllegalStateException("Component context was not created.");
		}
		return context;
	}
	
	private TestContext buildComponentContext() {
		TestContext context = createComponentContext();
		
		//disable check resource property
		context.set(XMPPProperties.PROP_CHECK_RESOURCE, "no");
		
		return context;
	}
	
	protected abstract TestContext createComponentContext();
	
	protected void activateMockControls() {
		mockControl = EasyMock.createControl();
		niceControl = EasyMock.createNiceControl();
	}
	
	protected void replayActiveMocks() {

		mockControl.replay();
		niceControl.replay();
	}


	protected void verifyActiveMocks() {

		mockControl.verify();
	}


	protected void resetActiveMocks() {

		if ( mockControl != null ) {
			mockControl.reset();
		}

		if ( niceControl != null ) {
			niceControl.reset();
		}
	}
	
	public String getDefaultUser() {
		return context.getProperty(XMPPProperties.PROP_USERNAME);
	}

	public String getDefaultServer() {
		return context.getProperty(XMPPProperties.PROP_XMPP_SERVERNAME);
	}
	
	public boolean isModuleStarted(Module application, String moduleName) {
		String expectedModuleLocation = 
			AcceptanceTestUtil.getModuleLocation(getDefaultUser(), getDefaultServer(), moduleName);

		if (application == null) {
			return false;
		}
		
		ContainerID realContainerID = application.getContainerID();
		if (realContainerID == null) {
			return false;
		}
		
		String realModuleLocation = realContainerID.toString();
		return expectedModuleLocation.equals(realModuleLocation);
	}
	
	public boolean isBound(Module application, String objectName) {
		if (application.getContainerDAO().isStopped()) {
			return false;
		}
			
		ObjectDeployment objectDeployment = application.getObject(objectName);
		return objectDeployment != null;

	}
	
	public boolean isBound(Module application, String objectName, Class<?> type) {
		if (application == null) {
			return false;
		}

		if (application.getContainerDAO().isStopped()) {
			return false;
		}

		ObjectDeployment objectDeployment = application.getObject(objectName);
		return objectDeployment != null && type.isInstance(objectDeployment.getObject());
	}
	
	public boolean isBound(Module application, String moduleName, String objectName, long deploymentNumber, Class<?> type) {
		if (application.getContainerDAO().isStopped()) {
			return false;
		}

		ObjectDeployment objectDeployment = application.getObject(objectName);
		return objectDeployment != null && type.isInstance(objectDeployment.getObject()) && 
			objectDeployment.getDeploymentID().getDeploymentNumber() == deploymentNumber;
	}
	
	public DeploymentID getBoundDeploymentID(Module application, String objName) {
		ObjectDeployment objectDeployment = application.getObject(objName);
		return objectDeployment.getDeploymentID();
	}
	
	public DeploymentID publishTestObject(Module application, String moduleName, String objName, Object obj,Class<?> stubClass) {
		return AcceptanceTestUtil.publishTestObject(application, getDefaultUser(), getDefaultServer(), moduleName, objName, obj,
				stubClass);
	}
	
	protected <T> T getMock( boolean nice, Class<T> clazz ) {

		return nice ? niceControl.createMock( clazz ) : mockControl.createMock( clazz );
	}
	
}