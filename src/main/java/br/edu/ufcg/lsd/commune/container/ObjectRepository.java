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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.message.MessageUtil;

public class ObjectRepository {

	private Module module;
    private Map<String, ObjectDeployment> deployedObjects;
    private ReadWriteLock objectsLock = new ReentrantReadWriteLock(true);

    
    public ObjectRepository(Module module) {
    	try {
    		this.objectsLock.writeLock().lock();
    		
	        this.module = module;
	        this.deployedObjects = new HashMap<String, ObjectDeployment>();

    	} finally {
    		this.objectsLock.writeLock().unlock();
    	}
    }

    public void addObject(ObjectDeployment objectDeployment) {
    	try {
    		this.objectsLock.writeLock().lock();
    		
    		verifyRemoteInterfaces(objectDeployment.getObject());
    		this.deployedObjects.put(objectDeployment.getDeploymentID().getServiceName(), objectDeployment);
    		
    	} finally {
    		this.objectsLock.writeLock().unlock();
    	}
    }

    private void verifyRemoteInterfaces(Object object) {
    	Class<?> objectClass = object.getClass();
    	boolean isRemote = false;
    	boolean isMonitor = false;
    	
    	if (MessageUtil.isRemoteType(objectClass)) {
    		List<Class<?>> verifiedClasses = new ArrayList<Class<?>>(); //Serialization
    		
    		for (Class<?> remoteInterface : MessageUtil.getRemoteInterfaces(objectClass)) {
    			
    			if (MessageUtil.hasRemoteInterface(remoteInterface)) { //TODO strange
    				ProxyUtil.verifyInterfaceMethods(remoteInterface, verifiedClasses);
    			}
    		}
    		
    		isRemote = true;
    		
    	} 
    		
    	if (MonitorHelper.isMonitor(object)) {
    		isMonitor = true;
    	}
    	
    	if (!isRemote && !isMonitor) {
    		throw new InvalidDeploymentException( 
    				"The class '" + objectClass.getName() + "' is not a remote type neither a monitor" );
    	}
	}

	public ObjectDeployment getTarget(DeploymentID deploymentID) {
		try {
			this.objectsLock.readLock().lock();
			
			if (!isLocal(deploymentID)) {
				return null;
			}
			
			ObjectDeployment deployment = this.deployedObjects.get( deploymentID.getServiceID().getServiceName() );

			if ( deployment == null ) {
				return null;
			}
			
			DeploymentID deployedID = deployment.getDeploymentID();
			if ( (deployedID == null) || !(deployedID.getDeploymentNumber().equals(deploymentID.getDeploymentNumber())) ) {
				return null;
			}

			return deployment;

		} finally {
			this.objectsLock.readLock().unlock();
		}
    }

    private boolean isLocal(DeploymentID deploymentID) {
    	if (deploymentID == null) {
    		return false;
    	}
    	
        return this.module.getContainerID().equals(deploymentID.getServiceID().getContainerID());
    }

    public ObjectDeployment removeObject(ObjectDeployment object) {
        
		try {
			this.objectsLock.writeLock().lock();

			Collection<ObjectDeployment> values = this.deployedObjects.values();
			for (ObjectDeployment existentObject : values ) {
				if (existentObject.equals(object)) {
					return this.deployedObjects.remove( existentObject.getDeploymentID().getServiceName() );
				}
			}
			
			return null;
		} finally {
			this.objectsLock.writeLock().unlock();
		}
    }

    public void removeObject(String serviceName) {
    	try {
    		this.objectsLock.writeLock().lock();
    		
    		ObjectDeployment object = deployedObjects.remove(serviceName);
    		
    		if (object != null) {
    			object.invalidate();
    		}
    		
    	} finally {
    		this.objectsLock.writeLock().unlock();
    	}
    }

    public ObjectDeployment get(String serviceName) {
    	try {
    		this.objectsLock.readLock().lock();
    		
    		return this.deployedObjects.get(serviceName);
    	
    	} finally {
    		this.objectsLock.readLock().unlock();
    	}
        
    }

    public DeploymentID getDeploymentID(Object value) {
    	try {
    		this.objectsLock.readLock().lock();
    	
	    	Collection<ObjectDeployment> values = this.deployedObjects.values();
	    	
	    	for (Iterator<ObjectDeployment> iterator = values.iterator(); iterator.hasNext();) {
				ObjectDeployment objectDeployment = iterator.next();
				
				if (objectDeployment.getObject() == value || objectDeployment.getProxy() == value) {
					return objectDeployment.getDeploymentID();
				}
			}
	    	
	    	return null;

    	} finally {
    		this.objectsLock.readLock().unlock();
    	}
    }
    
    public Collection<ObjectDeployment> getObjects() {
    	try {
    		this.objectsLock.readLock().lock();
    		
    		return this.deployedObjects.values();
    	
    	} finally {
    		this.objectsLock.readLock().unlock();
    	}
    }

	public Object getProxy(Object value) {
    	try {
    		this.objectsLock.readLock().lock();
    	
	    	Collection<ObjectDeployment> values = this.deployedObjects.values();
	    	
	    	for (Iterator<ObjectDeployment> iterator = values.iterator(); iterator.hasNext();) {
				ObjectDeployment objectDeployment = iterator.next();
				
				if (objectDeployment.getObject() == value) {
					return objectDeployment.getProxy();
				}
			}
	    	
	    	return null;

    	} finally {
    		this.objectsLock.readLock().unlock();
    	}
	}

	public void removeAll() {
		try {
			this.objectsLock.writeLock().lock();

			this.deployedObjects.clear();
			
		} finally {
			this.objectsLock.writeLock().unlock();
		}
	}
}