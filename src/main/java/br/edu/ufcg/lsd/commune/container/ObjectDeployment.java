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
package br.edu.ufcg.lsd.commune.container;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;

public class ObjectDeployment {

	private Module module;
	private DeploymentID deploymentID;
	private Object object;
	private ServiceManager serviceManager;
	private Object proxy;
	private Class<?> objectClass;
	
	public ObjectDeployment(Module module, DeploymentID deploymentID,
			Object object) {

		this.module = module;
		this.deploymentID = deploymentID;
		this.object = object;
		this.objectClass = object.getClass();
	}

	public Class<?> getObjectClass() {
		return objectClass;
	}
	
	public Module getModule() {
		return module;
	}
	
	public DeploymentID getDeploymentID() {
		return deploymentID;
	}
	
	public Object getObject() {
		return object;
	}
	
	void invalidate() {
		this.module = null;
		this.deploymentID = null;
		this.object = null;
		this.serviceManager = null;
		this.proxy = null;
	}

	public void setServiceManager(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}

	public ServiceManager getServiceManager() {
		return serviceManager;
	}

	public Object getProxy() {
		return proxy;
	}

	public void setProxy(Object proxy) {
		this.proxy = proxy;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((deploymentID == null) ? 0 : deploymentID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ObjectDeployment other = (ObjectDeployment) obj;
		if (deploymentID == null) {
			if (other.deploymentID != null)
				return false;
		} else if (!deploymentID.equals(other.deploymentID))
			return false;
		return true;
	}
	
	
}