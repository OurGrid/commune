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
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject1;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject10;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject11;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject12;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject13;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject14_1;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject14_2;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject14_3;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject15;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject16;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject17;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject18;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject19;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject2;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject20;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject21;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject22;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject23;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject24;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject25;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject26;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject27;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject28;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject29;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject3;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject30;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject31;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject32;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject33;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject34;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject35;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject36;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject37;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject38;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject39;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject4;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject40;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject41;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject42;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject43;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject44;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject45;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject46;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject47;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject48;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject49;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject5;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject50;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject51;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject52;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject53;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject54;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject55;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject56;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject57;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject6;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject7;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject8;
import br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment.MyObject9;
import br.edu.ufcg.lsd.commune.testinfra.util.Context;
import br.edu.ufcg.lsd.commune.testinfra.util.TestContext;
import br.edu.ufcg.lsd.commune.testinfra.util.TestWithApplication;

/**
 *
 */
public class ParametersDeployment extends TestWithApplication {

	/**
	 * Remote methods with primitive parameters
	 * @throws Exception
	 */
	@Test
	public void parametersDeployment1() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(3000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject14_1());
	}
	
	/**
	 * Remote methods with primitive parameters and random local methods
	 * (Returning objects and throwing exceptions)
	 * @throws Exception
	 */
	@Test
	public void parametersDeployment2() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject14_2());
	}
	
	/**
	 * Not Serializable parameter
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment3() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject15());
	}
	
	/**
	 * Method with a Serializable parameter and a not serializable one. 
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment4() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject16());
	}
	
	/**
	 * Remote methods with primitive parameters and random local methods
	 * (Returning objects and throwing exceptions)
	 * Non serializable fields.
	 * @throws Exception
	 */
	@Test
	public void parametersDeployment5() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject14_3());
	}
	
	/**
	 * Parameter has a non serializable field.
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment6() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject1());
	}
	
	/**
	 * Parameter has a serializable field.
	 * @throws Exception
	 */
	@Test
	public void parametersDeployment7() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject2());
	}
	
	/**
	 * Parameter has both a serializable field and a non serializable one.
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment8() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject3());
	}
	
	/**
	 * Parameter has a serializable field that has a non serializable field
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment9() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject4());
	}
	
	/**
	 * Parameter has a serializable field that has a serializable field
	 * @throws Exception
	 */
	@Test
	public void parametersDeployment10() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject5());
	}
	
	/**
	 * Parameter has a cycle in its hierarchy but with only serializable fields.
	 * @throws Exception
	 */
	@Test
	public void parametersDeployment11() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject7());
	}
	
	/**
	 * Parameter has a cycle in its hierarchy with a non serializable field.
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment12() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject8());
	}
	
	/**
	 * Parameter is a remote interface.
	 * @throws Exception
	 */
	@Test
	public void parametersDeployment13() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject9());
	}
	
	/**
	 * Parameter is a non serializable remote object.
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment14() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject10());
	}
	
	/**
	 * Parameter is a serializable remote object.
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment15() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject11());
	}
	
	/**
	 * Parameter has a remote object field.
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment16() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject6());
	}
	
	/**
	 * Parameter has a remote interface field.
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment17() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject12());
	}
	
	/**
	 * Parameter is a set with serializable items.
	 * @throws Exception
	 */
	@Test
	public void parametersDeployment18() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject13());
	}
	
	/**
	 * Parameter is a set with non serializable items.
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment19() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject17());
	}
	
	/**
	 * Parameter is a set with primitives items
	 * @throws Exception
	 */
	@Test
	public void parametersDeployment20() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject18());
	}
	
	/**
	 * Parameter is a set with unknown type items
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment21() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject22());
	}
	
	/**
	 * Parameter is a set with non specified type items
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment22() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject23());
	}
	
	/**
	 * Parameter is a set with unknown type items that extends a serializable class
	 * @throws Exception
	 */
	@Test
	public void parametersDeployment23() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject24());
	}
	
	/**
	 * Parameter is a set with unknown type items that extends a non serializable class
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment24() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject25());
	}
	
	/**
	 * Parameter is a list with serializable items.
	 * @throws Exception
	 */
	@Test
	public void parametersDeployment25() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject19());
	}
	
	/**
	 * Parameter is a list with non serializable items.
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment26() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject20());
	}
	
	/**
	 * Parameter is a list with primitives itens
	 * @throws Exception
	 */
	@Test
	public void parametersDeployment27() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject21());
	}
	
	/**
	 * Parameter is a list with unknown type items
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment28() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject26());
	}
	
	/**
	 * Parameter is a list with non specified type items
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment29() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject27());
	}
	
	/**
	 * Parameter is a list with unknown type items that extends a serializable class
	 * @throws Exception
	 */
	@Test
	public void parametersDeployment30() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject28());
	}
	
	/**
	 * Parameter is a list with unknown type items that extends a non serializable class
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment31() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject29());
	}
	
	/**
	 * Parameter is a set with Stub items with a '@MonitoredBy'.
	 * @throws Exception
	 */
	@Test
	public void parametersDeployment32() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject30());
	}
	
	/**
	 * Parameter is a set with Stub items without a '@MonitoredBy'.
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment33() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject31());
	}
	
	/**
	 * Parameter is a list with Stub items with a '@MonitoredBy'.
	 * @throws Exception
	 */
	@Test
	public void parametersDeployment34() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject32());
	}
	
	/**
	 * Parameter is a list with Stub items without a '@MonitoredBy'.
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment35() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject33());
	}
	
	/**
	 * Parameter is a map with a serializable key and non serializable items.
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment36() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject34());
	}
	
	/**
	 * Parameter is a map with a non serializable key and non serializable items.
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment37() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject35());
	}
	
	/**
	 * Parameter is a map with a non serializable key and serializable items.
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment38() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject36());
	}
	
	/**
	 * Parameter is a map with a serializable key and serializable items.
	 * @throws Exception
	 */
	@Test
	public void parametersDeployment39() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject37());
	}
	
	/**
	 * Parameter is a list with unknown type items that extends a List type
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment40() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject38());
	}
	
	/**
	 * Parameter is a set with unknown type items that extends a Set type
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment41() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject39());
	}
	
	/**
	 * Parameter is a map with a Stub key and non Stub items.
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment42() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject40());
	}
	
	/**
	 * Parameter is a map with a Stub key and Stub items.
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment43() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject41());
	}
	
	/**
	 * Parameter is a map with a Serializable key and Stub items without a '@MonitoredBy'.
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment44() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject42());
	}
	
	/**
	 * Parameter is a map with a Serializable key and Stub items with a '@MonitoredBy'.
	 * @throws Exception
	 */
	@Test
	public void parametersDeployment45() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject43());
	}
	
	/**
	 * Parameter is an array of serializable items.
	 * @throws Exception
	 */
	@Test
	public void parametersDeployment46() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject44());
	}
	
	/**
	 * Parameter is an array of non serializable items.
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment47() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject45());
	}
	
	/**
	 * Parameter is a map with a Stub key and Stub items with a '@monitoredBy'.
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment48() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject46());
	}
	
	/**
	 * Parameter is an array of Stub items without a '@MonitoredBy'.
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment49() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject47());
	}
	
	/**
	 * Parameter is an array of Stub items with a '@MonitoredBy'.
	 * @throws Exception
	 */
	@Test
	public void parametersDeployment50() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject48());
	}
	
	/**
	 * Parameter is an array of Serializable items with a Stub field.
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment51() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject49());
	}
	
	/**
	 * Parameter is an List of Serializable items with a Stub field.
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment52() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject51());
	}
	
	/**
	 * Parameter is an set of Serializable items with a Stub field.
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment53() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject52());
	}
	
	/**
	 * Parameter is an array of primitives items.
	 * @throws Exception
	 */
	@Test
	public void parametersDeployment54() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject50());
	}
	
	/**
	 * Parameter is a set with unknown type items that extends a stub type without a '@monitorable'
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment55() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject53());
	}
	
	/**
	 * Parameter is a set with unknown type items that extends a stub type with a '@monitorable'
	 * @throws Exception
	 */
	@Test
	public void parametersDeployment56() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject54());
	}
	
	/**
	 * Parameter is Serializable and has a List of serializable items
	 * @throws Exception
	 */
	@Test
	public void parametersDeployment57() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject55());
	}
	
	/**
	 * Parameter is Serializable and has a List of non serializable items
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment58() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject56());
	}
	
	/**
	 * Parameter is Serializable and has a List of stubs
	 * @throws Exception
	 */
	@Test(expected=InvalidDeploymentException.class)
	public void parametersDeployment59() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Module(Context.A_MODULE_NAME, context);
		Thread.sleep(1000);
		application.deploy(Context.A_SERVICE_NAME, new MyObject57());
	}
}
