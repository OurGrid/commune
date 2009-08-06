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
package br.edu.ufcg.lsd.commune.functionaltests.util;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.Container;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;

public class TestableApplication extends Module {

	
	public TestableApplication(String containerName, ModuleContext context) 
			throws CommuneNetworkException, ProcessorStartException {
		super(containerName, context);
	}


	@Override
	protected Container createContainer(String containerName, ModuleContext context) {
		return new TestableContainer(this, containerName, context);
	}
	
	public TestableContainer getContainer() {
		return (TestableContainer) container;
	}

	public DeploymentID getDeploymentID(String serviceName) {
		return getObject(serviceName).getDeploymentID();
	}
}
