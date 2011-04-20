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
package br.edu.ufcg.lsd.commune.container.servicemanager.client.async;

import br.edu.ufcg.lsd.commune.container.control.ServerModuleManager;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.ClientModule;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.ManagerClientService;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.network.ConnectionListener;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;

public abstract class AsyncApplicationClient<A extends ServerModuleManager, B extends ManagerClientService<A>> extends 
	ClientModule<A, B>{

	public AsyncApplicationClient(String containerName,
			ModuleContext context) throws CommuneNetworkException,
			ProcessorStartException {
		super(containerName, context);
	}
	
	public AsyncApplicationClient(String containerName,
			ModuleContext context, ConnectionListener listener) throws CommuneNetworkException,
			ProcessorStartException {
		super(containerName, context, listener);
	}

	public void start() {
		getManager().start(getManagerClient());
	}

	public void stop( boolean callExit, boolean force) {
		getManager().stop(callExit, force, getManagerClient());
	}
	
	public void getUpTime() {
		getManager().getUpTime(getManagerClient());
	}

	public void getConfiguration() {
		getManager().getConfiguration(getManagerClient());
	}
}
