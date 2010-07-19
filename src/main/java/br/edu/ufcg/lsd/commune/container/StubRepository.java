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
package br.edu.ufcg.lsd.commune.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class StubRepository {

	
	private Module module;
	private Map<ServiceID, StubReference> stubsPerId = new HashMap<ServiceID, StubReference>();
	private ReadWriteLock stubLock = new ReentrantReadWriteLock(true);
	private List<StubListener> listeners = new ArrayList<StubListener>();
	
	
	public StubRepository(Module module) {
		this.module = module;
	}

	
	public <T> StubReference createStub(ServiceID identification, Class<T> interfaceType) throws IllegalArgumentException {
		
		StubReference stubReference = null;
		
		try {
			stubLock.writeLock().lock();
			
			stubReference = stubsPerId.get(identification);
			
			if (stubReference == null) {
				stubReference = 
					createStub(this.module, identification, interfaceType.getClassLoader(), interfaceType);
				stubsPerId.put(identification, stubReference);
				fireStubCreated(stubReference);
				
			} else if (!stubReference.getReferenceTypes().contains(interfaceType)) {
				addProxy(identification, interfaceType);
			}
			
		} finally {
			stubLock.writeLock().unlock();
		}
		
		return stubReference;
	}


	public void addProxy(ServiceID identification, Class<?> interfaceType) {
		try {

			stubLock.writeLock().lock();
		
			StubReference stubReference = stubsPerId.get(identification);
			Object proxy = 
				ProxyUtil.createProxy(this.module, identification, interfaceType.getClassLoader(), interfaceType); 
	
			stubReference.addProxy(interfaceType, proxy);
	
		} finally {
			stubLock.writeLock().unlock();
		}
	}
	
	public <T> StubReference createStub(DeploymentID identification, Class<T> interfaceType) throws IllegalArgumentException {
		StubReference stubReference = null;
		
		try {
			stubLock.writeLock().lock();
			
			stubReference = stubsPerId.get(identification.getServiceID());
			
			if (stubReference == null) {
				stubReference = 
					createStub(this.module, identification.getServiceID(), interfaceType.getClassLoader(), interfaceType);
				stubsPerId.put(identification.getServiceID(), stubReference);
				fireStubCreated(stubReference);
				
			} else if (!stubReference.getReferenceTypes().contains(interfaceType)) {
				addProxy(identification.getServiceID(), interfaceType);
			}
			stubReference.setStubDeploymentID(identification);
			
		} finally {
			stubLock.writeLock().unlock();
		}
		
		return stubReference;
	}

	private StubReference getStubReference(Object stub) {
		
		try {
			stubLock.readLock().lock();
			Set<ServiceID> keySet = stubsPerId.keySet();
			for (ServiceID stubServiceID : keySet) {
				StubReference myStub = stubsPerId.get(stubServiceID);
				if (myStub.containsProxy(stub)) {
					return myStub;
				}
			}
			
		} finally {
			stubLock.readLock().unlock();
		}
		
		return null;
	}

	public DeploymentID getStubDeploymentID(Object stub) {
		try {
			stubLock.readLock().lock();
			
			StubReference stubReference = getStubReference(stub);
			
			if (stubReference == null) {
				return null; //throw new CommuneRuntimeException("Stub not found");
			}
			
			return stubReference.getStubDeploymentID();
			
		} finally {
			stubLock.readLock().unlock();
		}
	}

	public DeploymentID getStubDeploymentID(ServiceID serviceID) {
		try {
			stubLock.readLock().lock();
			
			StubReference stubReference = getStub(serviceID);
			
			if (stubReference == null) {
				return null; //throw new CommuneRuntimeException("Stub not found");
			}
			
			return stubReference.getStubDeploymentID();
			
		} finally {
			stubLock.readLock().unlock();
		}
	}

	public ServiceID getStubServiceID(Object stub) {
		try {
			stubLock.readLock().lock();
			
			StubReference stubReference = getStubReference(stub);
			
			if (stubReference == null) {
				return null; //throw new CommuneRuntimeException("Stub not found");
			}
			
			return stubReference.getStubServiceID();
			
		} finally {
			stubLock.readLock().unlock();
		}
	}
	
	public StubReference getStub(ServiceID stubServiceID) {
		try {
			stubLock.readLock().lock();
			return stubsPerId.get(stubServiceID);
			
		} finally {
			stubLock.readLock().unlock();
		}
	}
	
	public Collection<StubReference> getStubReferences() {
		try {
			stubLock.readLock().lock();
			return stubsPerId.values();
			
		} finally {
			stubLock.readLock().unlock();
		}
	}

	public void removeStub(Object stub) {
		try {
			stubLock.writeLock().lock();
			
			ServiceID stubServiceID = getStubServiceID(stub);
			removeAndInvalidateStub(stubServiceID);
			
		} finally {
			stubLock.writeLock().unlock();
		}
	}


	public void removeStub(ServiceID stubServiceID) {
		try {
			stubLock.writeLock().lock();
			
			removeAndInvalidateStub(stubServiceID);
			
		} finally {
			stubLock.writeLock().unlock();
		}
	}

	private void removeAndInvalidateStub(ServiceID stubServiceID) {
		StubReference stubReference = stubsPerId.remove(stubServiceID);
		
		if (stubReference != null) {
			stubReference.invalidate(); 
			fireStubReleased(stubServiceID);
		}
	}

	public void setStubDown(Object stub) {
		try {
			stubLock.writeLock().lock();
			
			Set<ServiceID> keySet = stubsPerId.keySet();
			for (ServiceID serviceID : keySet) {
				StubReference stubReference = stubsPerId.get(serviceID);
				
				if (stubReference.containsProxy(stub)) {
					stubReference.setDown();
					return;
				}
			}
			
		} finally {
			stubLock.writeLock().unlock();
		}
	}

	public boolean isStubUp(ServiceID stubServiceID) {
		try {
			stubLock.readLock().lock();
			return stubsPerId.get(stubServiceID).getStubDeploymentID() != null;
			
		} finally {
			stubLock.readLock().unlock();
		}
	}

	public void setStubDeploymentID(DeploymentID targetDeploymentID) {
		try {
			stubLock.writeLock().lock();

			StubReference stubReference = stubsPerId.get(targetDeploymentID.getServiceID());
			stubReference.setStubDeploymentID(targetDeploymentID);
			
		} finally {
			stubLock.writeLock().unlock();
		}
	}

	private StubReference createStub(Module sourceModule, ServiceID stubSID, ClassLoader classloader, 
			Class<?> interfaceType) throws IllegalArgumentException{
	
		Object proxy = ProxyUtil.createProxy(sourceModule, stubSID, classloader, interfaceType); 

	    StubReference stubReference = new StubReference(stubSID);
	    stubReference.addProxy(interfaceType, proxy);

	    return stubReference;
	}
	
		public void createTestStub(Object stub, DeploymentID deploymentID, boolean setUp) {
			createTestStub(stub, stub.getClass(), deploymentID, setUp);
		}
	
	public void createTestStub(Object stub, Class<?> stubClass, DeploymentID deploymentID, boolean setUp) {
	try {
		stubLock.writeLock().lock();
		ServiceID serviceID = deploymentID.getServiceID();
		StubReference stubReference = new StubReference(serviceID);
		stubReference.addProxy(stubClass, stub);
		stubReference.setStubDeploymentID(deploymentID);
		if (!setUp) {
			stubReference.setDown();
		}
		stubsPerId.put(serviceID, stubReference);
	} finally {
			stubLock.writeLock().unlock();
	}
	}


	public void removeAll() {
		try {
			stubLock.writeLock().lock();
			stubsPerId.clear();
			
		} finally {
			stubLock.writeLock().unlock();
		}
	}
	
	public void addListener(StubListener listener) {
		listeners.add(listener);
	}
	
	/**
	 *  Fires the stub creation event to the listeners.
	 *  This event occurs when the application registers interest
	 *  in the remote object, or when the application receives
	 *  a remote object callback. 
	 * @param stubReference The just created stub reference
	 */
	private void fireStubCreated(StubReference stubReference){
		for (StubListener listener : listeners) {
			listener.stubCreated(stubReference);
		}
	}
	

	private void fireStubReleased(ServiceID stubServiceID) {
		for (StubListener listener : listeners) {
			listener.stubReleased(stubServiceID);
		}
	}

}