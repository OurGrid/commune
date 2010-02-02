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
 *  Provides basic operations for an entity that seeks to find the status of another entities
 *  or properties related to it.
 *  
 *  @see ModuleStatusProviderClient.
 */
@Remote
public interface ModuleStatusProvider {

	/**
	 * Get the entity's up time.
	 * @param client The client of ApplicationStatusProvider.
	 */
	void getUpTime( ModuleStatusProviderClient client );

	/**
	 * Get the entity's actual configuration map.
	 * @param client The client of ApplicationStatusProvider.
	 */
	void getConfiguration( ModuleStatusProviderClient client );
	
}
