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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLoggerFactory;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class StubReference {

	
	private static CommuneLogger logger = CommuneLoggerFactory.getInstance().gimmeALogger(StubReference.class);
	
	
	private final ServiceID stubServiceID;
	private DeploymentID stubDeploymentID;
	private Map<Class<?>, Object> proxies = new HashMap<Class<?>, Object>();
    private ReadWriteLock stateLock = new ReentrantReadWriteLock(true);

	
	protected StubReference(ServiceID stubServiceID) {
		try {
			stateLock.writeLock().lock();

			this.stubServiceID = stubServiceID;
			debug("construc");
			
		} finally {
			stateLock.writeLock().unlock();
		}
	}
	
	protected void addProxy(Class<?> clazz, Object proxy) {
		try {
			stateLock.writeLock().lock();
			
			proxies.put(clazz, proxy);
			debug("addproxy->" + clazz);
		
		} finally {
			stateLock.writeLock().unlock();
		}
	}

	protected boolean isReleased() {
		try {
			stateLock.readLock().lock();
			return proxies == null;
			
		} finally {
			stateLock.readLock().unlock();
		}
	}
	
	public DeploymentID getStubDeploymentID() {
		try {
			stateLock.readLock().lock();
			return stubDeploymentID;
			
		} finally {
			stateLock.readLock().unlock();
		}
	}

	protected void setStubDeploymentID(DeploymentID stubDeploymentID) {
		try {
			stateLock.writeLock().lock();
			checkState();
			
			if (stubServiceID.equals(stubDeploymentID.getServiceID())) {
				this.stubDeploymentID = stubDeploymentID;
				
			} else {
				throw new CommuneRuntimeException("The deployment id '" + stubDeploymentID + 
						"' is invalid to the stub: " + stubServiceID);
			}
			
			debug("setStDID");
			
		} finally {
			stateLock.writeLock().unlock();
		}
	}
	
	protected void setDown() {
		try {
			stateLock.writeLock().lock();
			checkState();
			
			this.stubDeploymentID = null;
			
			debug("set_down");
			
		} finally {
			stateLock.writeLock().unlock();
		}
	}
	
	protected void invalidate() {
		try {
			stateLock.writeLock().lock();
			checkState();
			
			this.stubDeploymentID = null;
			this.proxies = null;
			
			debug("invalida");
			
		} finally {
			stateLock.writeLock().unlock();
		}
	}

	public Object getProxy(Class<?> clazz) {
		try {
			stateLock.readLock().lock();

			checkState();
			return proxies.get(clazz);
		} finally {
			stateLock.readLock().unlock();
		}
	}


	protected void checkState() {
		try {
			stateLock.readLock().lock();
			if (proxies == null) {
				throw new InvalidStubStateException("The stub is released: " + this.stubServiceID);
			}
			
		} finally {
			stateLock.readLock().unlock();
		}
	}

	public ServiceID getStubServiceID() {
		try {
			stateLock.readLock().lock();

			checkState();
			return stubServiceID;
		} finally {
			stateLock.readLock().unlock();
		}
	}
	
	public Collection<Class<?>> getProxies() {
		try {
			stateLock.readLock().lock();

			checkState();
			return proxies.keySet();
		} finally {
			stateLock.readLock().unlock();
		}
	}

	protected Set<Class<?>> getReferenceTypes() {
		try {
			stateLock.readLock().lock();

			checkState();
			return proxies.keySet();
		} finally {
			stateLock.readLock().unlock();
		}
	}
	
	@Override
	public String toString() {
		try {
			stateLock.readLock().lock();

			String id = (stubDeploymentID == null) ? stubServiceID.toString() : stubDeploymentID.toString();
			String state = (stubDeploymentID == null) ? "[down]" : "[up]";

			return state + " " + id;
			
		} finally {
			stateLock.readLock().unlock();
		}
	}
	
	private void debug(String method) {
		logger.trace("[" + method + "]: " + toString());
	}

	protected boolean containsProxy(Object stub) {
		try {
			stateLock.readLock().lock();
			
			Collection<Object> values = proxies.values();
			for (Object object : values) {
				if (object == stub) {
					return true;
				}
			}
			
			return false;
			
		} finally {
			stateLock.readLock().unlock();
		}
	}
}