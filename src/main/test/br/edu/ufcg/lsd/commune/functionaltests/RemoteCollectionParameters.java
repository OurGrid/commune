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

import br.edu.ufcg.lsd.commune.functionaltests.data.remotecollectionparameters.RemoteObject1;
import br.edu.ufcg.lsd.commune.testinfra.util.TestWithTestableCommuneContainer;

public class RemoteCollectionParameters extends TestWithTestableCommuneContainer {

	@Test
	public void deployWithRemoteCollectionParameters() throws Exception {
		application = createApplication();
		application.getContainer().deploy(RemoteObject1.MY_SERVICE_NAME, new RemoteObject1());
	}
	
	//TODO Validations
	
}