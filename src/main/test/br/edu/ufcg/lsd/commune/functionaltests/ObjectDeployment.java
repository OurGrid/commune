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

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.InvalidDeploymentException;
import br.edu.ufcg.lsd.commune.functionaltests.data.objectdeployment.MyObject1;
import br.edu.ufcg.lsd.commune.functionaltests.data.objectdeployment.MyObject2_1;
import br.edu.ufcg.lsd.commune.functionaltests.data.objectdeployment.MyObject2_2;
import br.edu.ufcg.lsd.commune.functionaltests.data.objectdeployment.MyObject2_3;
import br.edu.ufcg.lsd.commune.functionaltests.data.objectdeployment.MyObject2_4;
import br.edu.ufcg.lsd.commune.functionaltests.data.objectdeployment.MyObject2_5_Sub;
import br.edu.ufcg.lsd.commune.functionaltests.data.objectdeployment.MyObject3;
import br.edu.ufcg.lsd.commune.functionaltests.data.objectdeployment.MyObject4;
import br.edu.ufcg.lsd.commune.functionaltests.data.objectdeployment.MyObject5;
import br.edu.ufcg.lsd.commune.functionaltests.data.objectdeployment.MyObject6;
import br.edu.ufcg.lsd.commune.functionaltests.data.objectdeployment.MySubInterfaceObject1;
import br.edu.ufcg.lsd.commune.functionaltests.data.objectdeployment.MySubObject1;
import br.edu.ufcg.lsd.commune.testinfra.util.Context;
import br.edu.ufcg.lsd.commune.testinfra.util.TestContext;
import br.edu.ufcg.lsd.commune.testinfra.util.TestWithApplication;

public class ObjectDeployment extends TestWithApplication {

	
	@Test(expected=IllegalArgumentException.class)
	public void validateNullObject() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		application.deploy(Context.A_SERVICE_NAME, null);
	}
	
	@Test(expected=InvalidDeploymentException.class)
	public void validateNotRemoteObject() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		application.deploy(Context.A_SERVICE_NAME, new Object());
	}

	@Test
	public void objectDeployment1() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		application.deploy(Context.A_SERVICE_NAME, new MyObject1());
	}

	@Test
	public void objectDeployment2() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		application.deploy(Context.A_SERVICE_NAME, new MySubObject1());
	}

	@Test
	public void objectDeployment3() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		application.deploy(Context.A_SERVICE_NAME, new MySubInterfaceObject1());
	}
	
	@Test
	public void objectDeployment4() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		application.deploy(Context.A_SERVICE_NAME, new MyObject2_1());
	}
	
	@Test
	public void objectDeployment5() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		application.deploy(Context.A_SERVICE_NAME, new MyObject2_2());
	}
	
	@Test
	public void objectDeployment6() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		application.deploy(Context.A_SERVICE_NAME, new MyObject2_3());
	}
	
	@Test
	public void objectDeployment7() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		application.deploy(Context.A_SERVICE_NAME, new MyObject2_4<String>());
	}
	
	@Test
	public void objectDeployment8() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		application.deploy(Context.A_SERVICE_NAME, new MyObject2_5_Sub());
	}
	
	@Test(expected=InvalidDeploymentException.class)
	public void objectDeployment9() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		application.deploy(Context.A_SERVICE_NAME, new MyObject3());
	}
	
	@Test(expected=InvalidDeploymentException.class)
	public void objectDeployment10() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		application.deploy(Context.A_SERVICE_NAME, new MyObject4());
	}
	
	@Test(expected=InvalidDeploymentException.class)
	public void objectDeployment11() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		application.deploy(Context.A_SERVICE_NAME, new MyObject5());
	}
	
	@Test(expected=InvalidDeploymentException.class)
	public void objectDeployment12() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		application.deploy(Context.A_SERVICE_NAME, new MyObject6());
	}
	
}