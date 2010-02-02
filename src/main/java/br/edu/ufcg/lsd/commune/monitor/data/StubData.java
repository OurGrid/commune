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
package br.edu.ufcg.lsd.commune.monitor.data;

import java.util.Collection;

import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class StubData {
	
	private DeploymentID monitorDeploymentID;

	private ServiceID stubServiceID;
	
	private DeploymentID stubDeploymentID;
	
	private Collection<Class<?>> proxies;
	
	public StubData(DeploymentID monitorDeploymentID, ServiceID stubServiceID, 
			DeploymentID stubDeploymentID, Collection<Class<?>> proxies) {
		this.monitorDeploymentID = monitorDeploymentID;
		this.stubServiceID = stubServiceID;
		this.stubDeploymentID = stubDeploymentID;
		this.proxies = proxies;
	}
	
	public DeploymentID getMonitorDeploymentID() {
		return monitorDeploymentID;
	}

	public ServiceID getStubServiceID() {
		return stubServiceID;
	}

	public DeploymentID getStubDeploymentID() {
		return stubDeploymentID;
	}

	public Collection<Class<?>> getProxies() {
		return proxies;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (!(obj instanceof StubData)) {
			return false;
		}
		
		StubData other = (StubData) obj;
		
		return this.stubServiceID.equals(other.stubServiceID) &&
			   equalsStubDeploymentID(other) &&
			   this.proxies.equals(other.proxies);
	}
	
	private boolean equalsStubDeploymentID(StubData stubData) {
		return this.stubDeploymentID == null ? stubData == null : this.stubDeploymentID.equals(stubData);
	}
}
