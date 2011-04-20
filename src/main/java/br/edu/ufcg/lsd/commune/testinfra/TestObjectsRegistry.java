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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;


public class TestObjectsRegistry {
	
	
	private static Map<DeploymentID,Object> deploymentRegistry = new HashMap<DeploymentID,Object>();

	
	public static void reset(){
		deploymentRegistry = new HashMap<DeploymentID,Object>();
	}
	
	public static void publish(Object stub, DeploymentID deploymentID) {
		deploymentRegistry.put(deploymentID, stub);
	}

	public static Object getTestObject(DeploymentID deploymentID) {
		return deploymentRegistry.get(deploymentID);
	}
	
	public static DeploymentID getTestDeploymentID(Object object) {
		Set<DeploymentID> keySet = deploymentRegistry.keySet();
		for (DeploymentID deploymentID : keySet) {
			if (deploymentRegistry.get(deploymentID) == object) {
				return deploymentID;
			}
		}
		
		return null;
	}

	public static Object getTestObject(ServiceID serviceID) {
		Set<DeploymentID> keySet = deploymentRegistry.keySet();
		for (DeploymentID deploymentID : keySet) {
			if (deploymentID.getServiceID().equals(serviceID)) {
				return deploymentRegistry.get(deploymentID);
			}
		}
		
		return null;
	}

	public static DeploymentID getTestDeploymentID(ServiceID serviceID) {
		Set<DeploymentID> keySet = deploymentRegistry.keySet();
		for (DeploymentID deploymentID : keySet) {
			if (deploymentID.getServiceID().equals(serviceID)) {
				return deploymentID;
			}
		}
		
		return null;
	}

}