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
package br.edu.ufcg.lsd.commune;

import br.edu.ufcg.lsd.commune.container.ContainerContext;
import br.edu.ufcg.lsd.commune.container.control.ApplicationServerController;
import br.edu.ufcg.lsd.commune.container.control.ApplicationServerManager;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;

public class ApplicationServer extends Application {

	public ApplicationServer(String containerName, ContainerContext context)
			throws CommuneNetworkException, ProcessorStartException {
		super(containerName, context);
	}
	
	@Override
	protected ApplicationServerManager createApplicationManager() {
		return new ApplicationServerController();
	}

}