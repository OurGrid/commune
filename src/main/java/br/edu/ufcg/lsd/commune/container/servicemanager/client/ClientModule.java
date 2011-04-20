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

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.control.ClientModuleController;
import br.edu.ufcg.lsd.commune.container.control.ClientModuleManager;
import br.edu.ufcg.lsd.commune.container.control.ServerModuleManager;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.network.ConnectionListener;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;
import br.edu.ufcg.lsd.commune.processor.interest.InterestRequirements;

public abstract class ClientModule<A extends ServerModuleManager, B extends ManagerClientService<A>> extends Module {

	private InitializationContext<A, B> initializationContext;
	private ContainerID serverContainerID;

	private A control;
	
	private static final String MANAGER_CLIENT = "MANAGER_CLIENT";
	
	public ClientModule(String containerName, ModuleContext context)
			throws CommuneNetworkException, ProcessorStartException {
		super(containerName, context);
		createContext();
	}

	private void createContext() {
		initializationContext = createInitializationContext();
		serverContainerID = new ContainerID(
				getContainerID().getUserName(), 
				getContainerID().getServerName(), 
				initializationContext.getServerContainerName(), 
				getContainerID().getPublicKey());
	}
	
	public ClientModule(String containerName, ModuleContext context, ConnectionListener listener)
	throws CommuneNetworkException, ProcessorStartException {
		super(containerName, context, listener);
		createContext();
	}

	@Override
	protected void connectionCreated() {
		init();
		deploymentDone();
		registerInterestOnManager();
	}

	protected void deploymentDone() {}

	private void registerInterestOnManager() {
		InterestRequirements requirements = new InterestRequirements(4, 2);
		ServiceID controlID = new ServiceID(serverContainerID, Module.CONTROL_OBJECT_NAME);
		
		registerInterest(MANAGER_CLIENT, getManagerObjectType(), controlID, 
				requirements);
	}

	private void init() {
		deploy(MANAGER_CLIENT, initializationContext.createManagerClient());
	}

	protected abstract InitializationContext<A, B> createInitializationContext();

	/**
	 * @return the control
	 */
	public A getManager() {
		return control;
	}

	/**
	 * @param control the control to set
	 */
	public void setManager(A control) {
		this.control = control;
	}
	
	@SuppressWarnings("unchecked")
	public B getManagerClient() {
		return (B) getObjectRepository().get(MANAGER_CLIENT).getObject();
	}
	
	@Override
	protected ClientModuleManager createApplicationManager() {
		return new ClientModuleController();
	}
	
	protected Class<A> getManagerObjectType() {
		return initializationContext.getManagerObjectType();
	}
	
	public boolean isServerApplicationUp() {
		return this.control != null;
	}
}
