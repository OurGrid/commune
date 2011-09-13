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
package br.edu.ufcg.lsd.commune.processor.interest;

import java.lang.reflect.Method;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import sun.security.provider.certpath.X509CertPath;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.message.Message;

@SuppressWarnings("restriction")
public class Interest {

	private Monitor monitor;
	private ServiceID stubServiceID;
	private InterestRequirements reqs;

	private X509CertPath interestCertPath; 
	
	private ScheduledFuture<?> scheduledExecution;
	private volatile long lastUpdateStatusAvaliableArrival;
    private ReadWriteLock updateStatusLock = new ReentrantReadWriteLock(true);;

	public Interest(Monitor monitor, ServiceID stubServiceID, InterestRequirements reqs) {
		this.monitor = monitor;
		this.stubServiceID = stubServiceID;
		this.reqs = reqs;
	}
	
	
	public ServiceID getStubServiceID() {
		return stubServiceID;
	}

	protected Method getRecoveryNotificationMethod() {
		return monitor.getRecoveryNotification();
	}
	
	protected boolean hasRecoveryNotificationMethodDeploymentID() {
		Method recoveryNotificationMethod = monitor.getRecoveryNotification();
		return hasNotificationMethodDeploymentID(recoveryNotificationMethod);
	}

	protected boolean hasRecoveryNotificationMethodCertificate() {
		Method recoveryNotificationMethod = monitor.getRecoveryNotification();
		return hasNotificationMethodCertificate(recoveryNotificationMethod);
	}
	
	protected Method getFailureNotificationMethod() {
		return monitor.getFailureNotification();
	}

	protected boolean hasFailureNotificationMethodDeploymentID() {
		Method failureNotificationMethod = monitor.getFailureNotification();
		return hasNotificationMethodDeploymentID(failureNotificationMethod);
	}
	
	protected boolean hasFailureNotificationMethodCertificate() {
		Method failureNotificationMethod = monitor.getFailureNotification();
		return hasNotificationMethodCertificate(failureNotificationMethod);
	}

	private boolean hasNotificationMethodDeploymentID(Method notificationMethod) {
		Class<?>[] parameterTypes = notificationMethod.getParameterTypes();
		
		if (parameterTypes.length < 2) {
			return false;
		}
		
		return parameterTypes[1].equals(DeploymentID.class);
	}
	
	public boolean hasNotificationMethodCertificate(Method notificationMethod) {
		Class<?>[] parameterTypes = notificationMethod.getParameterTypes();
		
		if (parameterTypes.length < 3) {
			return false;
		}
		
		return parameterTypes[2].equals(X509CertPath.class);
	}
	
	public ObjectDeployment getInterested() {
		return monitor.getMonitorDeployment();
	}
	
	public void cancelScheduledExecution() {
		if ( scheduledExecution != null ) {
			scheduledExecution.cancel( false );
		}

		this.scheduledExecution = null;
	}

	public void setScheduledExecution(ScheduledFuture<?> future) {
		this.scheduledExecution = future;
	}
	
	public ScheduledFuture<?> getScheduledExecution() {
		return this.scheduledExecution;
	}


	public void setLastHeartbeat() {
		try {
			updateStatusLock.writeLock().lock();
			this.lastUpdateStatusAvaliableArrival = System.currentTimeMillis();
		} finally {
			updateStatusLock.writeLock().unlock();
		}
	}

	public boolean isTimedOut() {
		try {
			updateStatusLock.readLock().lock();
			return System.currentTimeMillis() - lastUpdateStatusAvaliableArrival > reqs.getDetectionTime();
			
		} finally {
			updateStatusLock.readLock().unlock();
		}
	}

	public long getHeartbeatDelay() {
		return reqs.getHeartbeatDelay();
	}
	
	public Message createIsItAliveMessage() {
		DeploymentID sourceID = monitor.getMonitorDeployment().getDeploymentID();
		String processorType = InterestProcessor.class.getName();
		return
			new Message(sourceID, stubServiceID, InterestProcessor.IS_IT_ALIVE_MESSAGE, processorType);
	}

	@Override
	public int hashCode() {
		int result = 1;
		final int prime = 31;
		result = prime * result + ((stubServiceID == null) ? 0 : stubServiceID.hashCode());
		result = prime * result + ((monitor == null) ? 0 : monitor.hashCode());
		return result;
	}

	@Override
	public boolean equals( Object obj ) {
		if (this == obj){
			return true;
		}
		
		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}
		
		final Interest other = (Interest) obj;

		if (monitor == null) {
			if (other.monitor != null) {
				return false;
			}
			
		} else if (!monitor.equals(other.monitor)) {
			return false;
		}
		
		if (stubServiceID == null) {
			if (other.stubServiceID != null) {
				return false;
			}
			
		} else if (!stubServiceID.equals(other.stubServiceID)) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		try {
			updateStatusLock.readLock().lock();
			return "Monitor: " + monitor +
			"\nStub: " + stubServiceID + 
			"\nRequirements: " + reqs + " / Last update status: " + lastUpdateStatusAvaliableArrival;
			
		} finally {
			updateStatusLock.readLock().unlock();
		}
	}


	public void setInterestCertPath(X509CertPath interestCertPath) {
		this.interestCertPath = interestCertPath;
	}


	public X509CertPath getInterestCertPath() {
		return interestCertPath;
	}


}