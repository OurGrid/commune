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
package br.edu.ufcg.lsd.commune.container.servicemanager.actions;

import java.io.Serializable;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.control.ModuleManager;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.message.Message;

/**
 * Runnable to be scheduled on scheduleAction method from <code>CommuneContainer</code>
 * It contains:
 * 	- The action name
 *  - The handler containing running parameters
 *  - A reference for the CommuneContainer
 * 
 * @param <T> Object that handles parameters for the run() method.
 */
public class RepetitionRunnable implements Runnable {

	private String actionName;
	private Serializable handler;
	private ModuleManager componentControl;
	private final Module module;
	
	public RepetitionRunnable(Module module, ModuleManager componentControl, String actionName, 
			Serializable handler) {
		this.module = module;
		this.componentControl = componentControl;
		this.actionName = actionName;
		this.handler = handler;
	}

	/**
	 * Calls run() method of the <code>RepeatedAction</code> 
	 * referenced by its action name, passing the handler as parameter
	 */
	public void run() {
		DeploymentID stubDeploymentID = module.getLocalObjectDeploymentID(componentControl);
		Message message = new Message(module.getContainerID(), stubDeploymentID, "runAction");
		message.addParameter(String.class, actionName);
		message.addParameter(Serializable.class, handler);
		
		module.sendMessage(message);
	}

	/**
	 * @return Returns the actionName.
	 */
	public String getActionName() {
		return actionName;
	}

	/**
	 * @return Returns the handler.
	 */
	public Serializable getHandler() {
		return handler;
	}
	
	
}
