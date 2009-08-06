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
package br.edu.ufcg.lsd.commune.container.servicemanager.client;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.control.ModuleManagerClient;
import br.edu.ufcg.lsd.commune.container.control.ServerModuleManager;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

public abstract class ManagerClientService<T extends ServerModuleManager> implements ModuleManagerClient {

	private ServiceManager serviceManager;

	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}

	/**
	 * @return the serviceManager
	 */
	protected ServiceManager getServiceManager() {
		return serviceManager;
	}
	
	@RecoveryNotification
	public abstract void controlIsUp(T control);
	
	@FailureNotification
	public abstract void controlIsDown(T control);
	
	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected ClientModule<T, ManagerClientService<T>> getApplicationClient() {
		return (ClientModule<T, ManagerClientService<T>>) getServiceManager().getApplication();
	}

}
