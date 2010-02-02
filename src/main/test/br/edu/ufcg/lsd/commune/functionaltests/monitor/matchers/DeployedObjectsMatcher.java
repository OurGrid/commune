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
package br.edu.ufcg.lsd.commune.functionaltests.monitor.matchers;

import java.util.Collection;
import java.util.Map;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;

import br.edu.ufcg.lsd.commune.identification.DeploymentID;

public class DeployedObjectsMatcher implements IArgumentMatcher {
	Map<DeploymentID, Collection<Class<?>>> deployedObjects;
	
	public DeployedObjectsMatcher(Map<DeploymentID, Collection<Class<?>>> deployedObjects) {
		this.deployedObjects = deployedObjects;
	}

	/* (non-Javadoc)
	 * @see org.easymock.IArgumentMatcher#appendTo(java.lang.StringBuffer)
	 */
	public void appendTo(StringBuffer arg0) {
		
	}

	/* (non-Javadoc)
	 * @see org.easymock.IArgumentMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object arg0) {
		
		if (arg0 == null) {
			return false; 
		}

		return this.deployedObjects.equals(arg0);
	}

	public static Map<DeploymentID, Collection<Class<?>>> eqMatcher(Map<DeploymentID, Collection<Class<?>>> deployedObjects) {
		EasyMock.reportMatcher(new DeployedObjectsMatcher(deployedObjects));
		return null;
	}
}
