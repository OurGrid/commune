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
package br.edu.ufcg.lsd.commune.functionaltests.data.registerinterest;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;

public class InterestedObject5 implements InterestInterface1 {

	
	public static final String MY_SERVICE_NAME = "myServiceName";
	public static final String USER = "userX";
	public static final String SERVER = "serverX";
	public static final String CONTAINER = "containerX";
	public static final String SERVICE = "serviceX";
	

	
	@InvokeOnDeploy
	public void init(ServiceManager manager) {
		manager.registerInterest(MY_SERVICE_NAME, USER + "@" + SERVER + "/" + CONTAINER + "/" + SERVICE, Stub1.class);
	}
	
	@RecoveryNotification
	public void toDO(DeploymentID stub1ID, Stub1 stub1) {
		
	}

	@FailureNotification
	public void toDone(Stub1 stub1) {
		
	}

}
