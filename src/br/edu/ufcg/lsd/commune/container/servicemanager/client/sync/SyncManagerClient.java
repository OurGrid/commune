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
package br.edu.ufcg.lsd.commune.container.servicemanager.client.sync;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.control.ControlOperationResult;
import br.edu.ufcg.lsd.commune.container.control.ServerModuleManager;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.ClientModule;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.ManagerClientService;

public class SyncManagerClient<T extends ServerModuleManager> extends ManagerClientService<T> {

	private BlockingQueue<Object> queue;
	private boolean callExit;

	private boolean controlRecovered = false;
	
	public void hereIsConfiguration(Map<String, String> configuration) {
		SyncContainerUtil.putResponseObject(queue, configuration);
	}

	public void hereIsUpTime(long uptime) {
		SyncContainerUtil.putResponseObject(queue, uptime);
	}

	public void operationSucceed(ControlOperationResult controlOperationResult) {
		SyncContainerUtil.putResponseObject(queue, controlOperationResult);
		if (callExit) {
			System.exit(0);
		}
	}
	
	public void callExitOnOperationSucceed(boolean callExit) {
		this.callExit = callExit;
	}
	
	public void setQueue(BlockingQueue<Object> queue) {
		this.queue = queue;
	}
	
	protected BlockingQueue<Object> getQueue() {
		return this.queue;
	}
	
	@Override
	@RecoveryNotification
	public void controlIsUp(T control) {
		if (!controlRecovered) {
			SyncContainerUtil.putResponseObject(queue, control);
			controlRecovered = true;
		} else {
			getApplicationClient().setManager(control);
		}
	}
	
	@Override
	@FailureNotification
	public void controlIsDown(T control) {
		ClientModule<T, ManagerClientService<T>> containerClient = getApplicationClient();
		containerClient.setManager(null);
		getServiceManager().release(control);
	}
	
}
