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
import br.edu.ufcg.lsd.commune.network.ConnectionListener;
import br.edu.ufcg.lsd.commune.network.ConnectionListenerAdapter;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;

public abstract class SyncApplicationClient<A extends ServerModuleManager, B extends SyncManagerClient<A>> 
	extends ClientModule<A, B> {

	protected BlockingQueue<Object> queue;
	
	public SyncApplicationClient(String containerName,
			ModuleContext context) throws CommuneNetworkException,
			ProcessorStartException {
		this(containerName, context, true);
	}
	
	@SuppressWarnings("unchecked")
	public SyncApplicationClient(String containerName,
			ModuleContext context, boolean waitForever) throws CommuneNetworkException,
			ProcessorStartException {
		super(containerName, context);
		
		Object response;
		
		if(waitForever){
			response = SyncContainerUtil.waitForeverForResponseObject(queue);
		}else{
			response = SyncContainerUtil.waitForResponseObject(queue, getManagerObjectType(), getQueueTimeout());
		}
		if(response == null){
		    throw new CommuneNetworkException("Component is not started.");
		}else if(response instanceof CommuneNetworkException){
			throw (CommuneNetworkException) response;
		}
		setManager((A) response);
	}

	@Override
	protected void moduleCreated() {
		this.queue = new ArrayBlockingQueue<Object>(1);
		setConnectionListener(createConnectionListener());
	}
	
	private ConnectionListener createConnectionListener() {
		return new ConnectionListenerAdapter() {
			@Override
			public void connectionFailed(Exception e) {
				SyncContainerUtil.putResponseObject(queue, e);
			}
		};
	}

	@Override
	protected void deploymentDone() {
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
		String strQueueTimeout = getContext().getProperty(CommuneClientProperties.PROP_CLIENT_QUEUE_TIMEOUT);
		long queueTimeout = strQueueTimeout == null ? SyncContainerUtil.POLLING_TIMEOUT : Long.valueOf(strQueueTimeout);
		
		return queueTimeout;
	}
}
