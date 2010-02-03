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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;

import br.edu.ufcg.lsd.commune.identification.CommuneAddress;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.monitor.data.StubData;

public class StubDataMatcher implements IArgumentMatcher {
	
	private Collection<StubData> stubDatas;
	
	public StubDataMatcher(Collection<StubData> stubDatas) {
		this.stubDatas = stubDatas;
	}
	
	public StubDataMatcher(DeploymentID monitorDID, DeploymentID deploymentID, ServiceID serviceID, List<Class<?>> classes) {
		Collection<StubData> stubDatas = new ArrayList<StubData>();
		stubDatas.add(new StubData(monitorDID, serviceID, deploymentID, classes));
		
		this.stubDatas = stubDatas;
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
		
		if (arg0 == null || !(arg0 instanceof Collection)) {
			return false;
		}
		
		Collection<StubData> collection = (Collection<StubData>) arg0;

		if (this.stubDatas.size() != collection.size()) {
			return false;
		}
		
		Iterator<StubData> collectionIterator = collection.iterator();
		Iterator<StubData> stubDatasIterator = stubDatas.iterator();
		
		while (collectionIterator.hasNext() && stubDatasIterator.hasNext()) {
			StubData otherStubData = collectionIterator.next();
			StubData stubData = stubDatasIterator.next();
			
			if (canCompareStubClasses(stubData, otherStubData)) {
				Collection<Class<?>> classCollection = stubData.getProxies();
				Collection<Class<?>> otherClassCollection = otherStubData.getProxies();
				
				if (classCollection.size() != otherClassCollection.size()) {
					return false;
				}
					
				Iterator<Class<?>> classCollectionIterator = classCollection.iterator();
							
				while (classCollectionIterator.hasNext()) {
					boolean contains = false;
					Class<?> clazz = classCollectionIterator.next();
					Iterator<Class<?>> otherClassCollectionIterator = stubData.getProxies().iterator();
					
					while (otherClassCollectionIterator.hasNext()) {
						if (clazz.getName().equals(otherClassCollectionIterator.next().getName())) {
							contains = true;
							break;
						}
					}
					
					if (!contains) {
						return false;
					}
				}				
			}
		}
		
		return true;
	}
	
	private boolean equalsCommuneAddress(CommuneAddress communeAddress, CommuneAddress otherCommuneAddress) {
		return communeAddress == null ? otherCommuneAddress == null : communeAddress.equals(otherCommuneAddress);
	}
	
	private boolean canCompareStubClasses(StubData stubData, StubData otherStubData) {
		return equalsCommuneAddress(stubData.getMonitorDeploymentID(), otherStubData.getMonitorDeploymentID()) &&
		equalsCommuneAddress(stubData.getStubDeploymentID(), otherStubData.getStubDeploymentID()) &&
		equalsCommuneAddress(stubData.getStubServiceID(), otherStubData.getStubServiceID());
	}

	public static Collection<StubData> eqMatcher(DeploymentID monitorDID, DeploymentID deploymentID, ServiceID serviceID, List<Class<?>> classes) {
		EasyMock.reportMatcher(new StubDataMatcher(monitorDID, deploymentID, serviceID, classes));
		return null;
	}
	
	public static Collection<StubData> eqMatcher(Collection<StubData> stubDatas) {
		EasyMock.reportMatcher(new StubDataMatcher(stubDatas));
		return null;
	}
}
