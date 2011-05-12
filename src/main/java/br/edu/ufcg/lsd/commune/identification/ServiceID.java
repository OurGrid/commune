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
package br.edu.ufcg.lsd.commune.identification;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServiceID implements Serializable, CommuneAddress, Comparable<ServiceID> {

	private static final long serialVersionUID = 3L;

	private final ContainerID containerID;
	private String serviceName;

	public ServiceID( String userName, String serverName, String containerName, String serviceName ) {		
		this.containerID = new ContainerID(userName, serverName, containerName);
		this.serviceName = serviceName;
	}

	public ServiceID(ContainerID containerID, String serviceName) {
		this.containerID = containerID;
		this.serviceName = serviceName;
	}

	public String getServiceName() {

		return serviceName;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((containerID == null) ? 0 : containerID.hashCode());
		result = PRIME * result + ((serviceName == null) ? 0 : serviceName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ServiceID))
			return false;
		final ServiceID other = (ServiceID) obj;
		if (containerID == null) {
			if (other.containerID != null)
				return false;
		} else if (!containerID.equals(other.containerID))
			return false;
		if (serviceName == null) {
			if (other.serviceName != null)
				return false;
		} else if (!serviceName.equals(other.serviceName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return containerID.toString() + "/" + this.serviceName;
	}

	public ContainerID getContainerID() {
		return containerID;
	}

	public String getContainerName() {
		return containerID.getContainerName();
	}

	public String getServerName() {
		return containerID.getServerName();
	}

	public String getUserName() {
		return containerID.getUserName();
	}

	public Long getDeploymentNumber() {
		return null;
	}

	public String getPublicKey() {
		return containerID.getPublicKey();
	}
	
	public static boolean validate(String serviceID) {

		if ( serviceID != null ) {
			Pattern p = Pattern.compile("[a-zA-Z_0-9-\\.]+@[a-zA-Z_0-9-\\.]+/[/a-zA-Z_0-9-\\.]+/[/a-zA-Z_0-9-\\.]+");
			Matcher m = p.matcher( serviceID );
			return m.matches();
		}
		return false;
	}

	public static ServiceID parse(String serviceID) {
		
		if (!validate(serviceID)) {
			throw new InvalidIdentificationException("Invalid Commune Service ID: " + serviceID);
		}
		
		String[] split = serviceID.split(CommuneAddress.RESOURCE_SEPARATOR);
		
		ContainerID containerID = ContainerID.parse(split[0] + CommuneAddress.RESOURCE_SEPARATOR + split[1]);
		return new ServiceID(containerID, split[2]);
	}

	public int compareTo(ServiceID o) {
		return toString().compareTo(o.toString());
	}
}