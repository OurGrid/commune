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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import br.edu.ufcg.lsd.commune.container.control.ControlOperationResult;
import br.edu.ufcg.lsd.commune.container.control.ServerModuleManager;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.ClientModule;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.CommuneClientProperties;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;

public abstract class SyncApplicationClient<A extends ServerModuleManager, B extends SyncManagerClient<A>> 
	extends ClientModule<A, B> {

	protected BlockingQueue<Object> queue;
	
	public SyncApplicationClient(String containerName,
			ModuleContext context) throws CommuneNetworkException,
			ProcessorStartException {
		super(containerName, context);
		setManager(SyncContainerUtil.waitForResponseObject(queue, getManagerObjectType(), getQueueTimeout()));
	}

	@Override
	protected void deploymentDone() {
		this.queue = new ArrayBlockingQueue<Object>(1);
		getManagerClient().setQueue(queue);
	}
	
	public ControlOperationResult start() {
		getManager().start(getManagerClient());
		return SyncContainerUtil.waitForResponseObject(queue, ControlOperationResult.class, getQueueTimeout());
	}

	public ControlOperationResult stop( boolean callExit, boolean force) {
		getManager().stop(callExit, force, getManagerClient());
		return SyncContainerUtil.waitForResponseObject(queue, ControlOperationResult.class, getQueueTimeout());
	}
	
	public long getUpTime() {
		getManager().getUpTime(getManagerClient());
		return SyncContainerUtil.waitForResponseObject(queue, Long.class, getQueueTimeout());
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getConfiguration() {
		getManager().getConfiguration(getManagerClient());
		return SyncContainerUtil.waitForResponseObject(queue, Map.class, getQueueTimeout());
	}

	public void putOnQueue(Object obj) throws InterruptedException {
		queue.put(obj);
	}
	
	public void callExitOnOperationSucceed(boolean callExit) {
		getManagerClient().callExitOnOperationSucceed(callExit);
	}
	
		
	protected long getQueueTimeout() {
		String strQueueTimeout = getContainer().getContext().getProperty(CommuneClientProperties.PROP_CLIENT_QUEUE_TIMEOUT);
		long queueTimeout = strQueueTimeout == null ? SyncContainerUtil.POLLING_TIMEOUT : Long.valueOf(strQueueTimeout);
		
		return queueTimeout;
	}
}
