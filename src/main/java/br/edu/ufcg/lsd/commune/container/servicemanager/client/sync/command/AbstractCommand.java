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
package br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.command;

import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.SyncApplicationClient;

/**
 *
 */
public abstract class AbstractCommand<T extends SyncApplicationClient<?, ?>> implements Command {

	private T componentClient;

	public AbstractCommand(T componentClient) {
		this.componentClient = componentClient;
	}
	
	protected T getComponentClient() {
		return componentClient;
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.refactoring.common.ui.command.Command#run(java.lang.String[])
	 */
	public void run(String[] params) throws Exception {
		validateParams(params);
		execute(params);
	}
	
	protected boolean isComponentStarted() {
		return this.getComponentClient().getManager() != null;
	}
	
	protected abstract void validateParams(String[] params) throws Exception;
	
	protected abstract void execute(String[] params) throws Exception;
}
