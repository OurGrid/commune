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
package br.edu.ufcg.lsd.commune.container.control;


import br.edu.ufcg.lsd.commune.api.Remote;

/**
 * This remote interface must be implemented by classes that want to initiate or stop
 * workers.
 *
 */

@Remote
public interface ModuleControl {

	/**
	 * This method initiate a specified worker received by parameter.
	 * @param client The worker that will be started. 
	 */
	void start( ModuleControlClient client );

	/**
	 * This method stops the specified worker. The callexit parameter determines if the System.exit
	 * method will be called. A forced shutdown means stop the worker without notifies any listener about
	 * the worker stopped. 
	 * @param callExit If true, this method will call the System.exit method. Otherwise, not.  
	 * @param force If true, this method will do a forced shutdown of this worker. Otherwise, the worker
	 * will be stopped normally, and the system will be notified about the shutdown of the one. 
	 * @param client The worker that will be stopped.
	 */
	void stop( boolean callExit, boolean force, ModuleControlClient client );
	
}
