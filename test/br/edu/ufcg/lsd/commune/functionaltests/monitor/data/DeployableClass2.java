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
package br.edu.ufcg.lsd.commune.functionaltests.monitor.data;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.api.MonitoredBy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

public class DeployableClass2 implements DeployableInterface2 {
	
	private ServiceManager manager;
	
	@InvokeOnDeploy
	public void init(ServiceManager manager) {
		this.manager = manager;
	}
	
	public ServiceManager getManager() {
		return this.manager;
	}
	
	public void invoke(
			@MonitoredBy(OBJECT_NAME)Stub2 stub2) {

	}
	
	@RecoveryNotification
	public void stubIsUp(Stub2 stub2) {
		
	}

	@FailureNotification
	public void stubIsDown(Stub2 stub2) {
		
	}
}
