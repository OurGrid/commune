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

import br.edu.ufcg.lsd.commune.functionaltests.data.objectdeployment.MyObject1;
import br.edu.ufcg.lsd.commune.functionaltests.data.objectdeployment.MyObject2_1;
import br.edu.ufcg.lsd.commune.functionaltests.data.objectdeployment.MyObject2_2;
import br.edu.ufcg.lsd.commune.functionaltests.data.objectdeployment.MyObject2_3;
import br.edu.ufcg.lsd.commune.functionaltests.data.objectdeployment.MyObject2_4;
import br.edu.ufcg.lsd.commune.functionaltests.data.objectdeployment.MyObject2_5_Sub;
import br.edu.ufcg.lsd.commune.functionaltests.data.objectdeployment.MySubInterfaceObject1;
import br.edu.ufcg.lsd.commune.functionaltests.data.objectdeployment.MySubObject1;
import br.edu.ufcg.lsd.commune.testinfra.util.Context;
import br.edu.ufcg.lsd.commune.testinfra.util.TestWithTestableCommuneContainer;

public class InvokeIsItAlive extends TestWithTestableCommuneContainer {

	
	//TODO Validation Tests
	
	@Test
	public void unavaliableObject() throws Exception {
		module = createApplication();

		sendIsItAliveToUnavaliableObject(Context.A_SERVICE_NAME);
	}
	
	@Test
	public void avaliableObject1() throws Exception {
		module = createApplication();
		module.deploy(Context.A_SERVICE_NAME, new MyObject1());
		
		sendIsItAliveToAvaliableObject(Context.A_SERVICE_NAME);
	}

	@Test
	public void avaliableAndUnavaliableObjects() throws Exception {
		module = createApplication();
		module.deploy(Context.A_SERVICE_NAME, new MyObject1());
		
		sendIsItAliveToAvaliableObject(Context.A_SERVICE_NAME);
		sendIsItAliveToUnavaliableObject(Context.OTHER_SERVICE_NAME);
	}

	@Test
	public void avaliableAndUnavaliableObjectsManyTimes() throws Exception {
		module = createApplication();
		module.deploy(Context.A_SERVICE_NAME, new MyObject1());
		
		sendIsItAliveToAvaliableObject(Context.A_SERVICE_NAME);
		sendIsItAliveToAvaliableObject(Context.A_SERVICE_NAME);

		sendIsItAliveToUnavaliableObject(Context.OTHER_SERVICE_NAME);
		
		sendIsItAliveToAvaliableObject(Context.A_SERVICE_NAME);
		sendIsItAliveToAvaliableObject(Context.A_SERVICE_NAME);
		sendIsItAliveToAvaliableObject(Context.A_SERVICE_NAME);
		sendIsItAliveToAvaliableObject(Context.A_SERVICE_NAME);

		sendIsItAliveToUnavaliableObject(Context.OTHER_SERVICE_NAME);
		sendIsItAliveToUnavaliableObject(Context.OTHER_SERVICE_NAME);
		sendIsItAliveToUnavaliableObject(Context.OTHER_SERVICE_NAME);
		
		sendIsItAliveToAvaliableObject(Context.A_SERVICE_NAME);
		sendIsItAliveToAvaliableObject(Context.A_SERVICE_NAME);
	}

	@Test
	public void avaliableObject2() throws Exception {
		module = createApplication();
		module.deploy(Context.A_SERVICE_NAME, new MySubObject1());
		
		sendIsItAliveToAvaliableObject(Context.A_SERVICE_NAME);
	}

	@Test
	public void avaliableObject3() throws Exception {
		module = createApplication();
		module.deploy(Context.A_SERVICE_NAME, new MySubInterfaceObject1());
		
		sendIsItAliveToAvaliableObject(Context.A_SERVICE_NAME);
	}
	
	@Test
	public void avaliableObject4() throws Exception {
		module = createApplication();
		module.deploy(Context.A_SERVICE_NAME, new MyObject2_1());
		
		sendIsItAliveToAvaliableObject(Context.A_SERVICE_NAME);
	}
	
	@Test
	public void avaliableObject5() throws Exception {
		module = createApplication();
		module.deploy(Context.A_SERVICE_NAME, new MyObject2_2());
		
		sendIsItAliveToAvaliableObject(Context.A_SERVICE_NAME);
	}
	
	@Test
	public void avaliableObject6() throws Exception {
		module = createApplication();
		module.deploy(Context.A_SERVICE_NAME, new MyObject2_3());
		
		sendIsItAliveToAvaliableObject(Context.A_SERVICE_NAME);
	}
	
	@Test
	public void avaliableObject7() throws Exception {
		module = createApplication();
		module.deploy(Context.A_SERVICE_NAME, new MyObject2_4<String>());
		
		sendIsItAliveToAvaliableObject(Context.A_SERVICE_NAME);
	}
	
	@Test
	public void avaliableObject8() throws Exception {
		module = createApplication();
		module.deploy(Context.A_SERVICE_NAME, new MyObject2_5_Sub());
		
		sendIsItAliveToAvaliableObject(Context.A_SERVICE_NAME);
	}
}