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

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;

public class Monitor {

	private final ObjectDeployment monitorDeployment;
	private final Method recoveryNotification;
	private final Method failureNotification;

	public Monitor(ObjectDeployment monitorDeployment, Method recoveryNotification, Method failureNotification) {
		this.monitorDeployment = monitorDeployment;
		this.recoveryNotification = recoveryNotification;
		this.failureNotification = failureNotification;
	}

	protected ObjectDeployment getMonitorDeployment() {
		return monitorDeployment;
	}

	protected Method getRecoveryNotification() {
		return recoveryNotification;
	}

	protected Method getFailureNotification() {
		return failureNotification;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((monitorDeployment == null) ? 0 : monitorDeployment.getDeploymentID().getServiceID().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Monitor other = (Monitor) obj;
		if (monitorDeployment == null) {
			if (other.monitorDeployment != null)
				return false;
		} else if (!monitorDeployment.getDeploymentID().getServiceID().equals(
				other.monitorDeployment.getDeploymentID().getServiceID()))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return this.monitorDeployment.getDeploymentID().toString();
	}
}
