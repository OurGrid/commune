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

import org.jivesoftware.smack.util.StringUtils;

public class ContainerID implements Serializable, CommuneAddress {

	private String userName;
	private String serverName;
	private String containerName;
	private String publicKey;

	private static final long serialVersionUID = 1L;
	
	public ContainerID() {}
	
	public ContainerID(String userName, String serverName, String moduleName){
		this(userName, serverName, moduleName, null);
	}
	
	public ContainerID(String userName, String serverName, String containerName, String publicKey){
		if (containerName == null) {
			throw new NullPointerException("Container name cannot be null.");
		}
		
		assert userName != null : "User name cannot be null";
		assert serverName != null : "Server name cannot be null";
		

		this.userName = userName.toLowerCase();
		this.serverName = serverName.toLowerCase();
		this.containerName = containerName;
		this.publicKey = publicKey;
	}

	public String getContainerName() {
		return containerName;
	}

	public String getServerName() {
		return serverName;
	}

	public String getUserName() {
		return userName;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((containerName == null) ? 0 : containerName.hashCode());
		result = PRIME * result + ((serverName == null) ? 0 : serverName.hashCode());
		result = PRIME * result + ((userName == null) ? 0 : userName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ContainerID))
			return false;
		final ContainerID other = (ContainerID) obj;
		if (!(containerName == null ? other.containerName == null : containerName.equals(other.containerName)))
			return false;
		if (!(serverName == null ? other.serverName == null : serverName.equals(other.serverName)))
			return false;
		if (!(userName == null ? other.userName == null : userName.equals(other.userName)))
			return false;
		return true;
	}

	@Override
	public String toString() {
		if(userName == null || serverName == null){
			return null;
		}
		
		return userName + "@" + serverName + "/" + (this.containerName == null ? "" : this.containerName);
	}

	public Long getDeploymentNumber() {
		return null;
	}

	public String getServiceName() {
		return null;
	}

	public ContainerID getContainerID() {
		return this;
	}
	public static boolean validate(String containerID) {

		if ( containerID != null ) {
			Pattern p = Pattern.compile("[a-zA-Z_0-9-\\.]+@[a-zA-Z_0-9-\\.]+[/a-zA-Z_0-9-\\.]+");
			Matcher m = p.matcher( containerID );
			return m.matches();
		}
		return false;
	}

	public static ContainerID parse(String containerID) {
		if (!validate(containerID)) {
			throw new InvalidIdentificationException("Invalid Commune Container ID: " + containerID);
		}
		
		String login = StringUtils.parseName(containerID);
		String server = StringUtils.parseServer(containerID);
		String container = StringUtils.parseResource(containerID);
		
		return new ContainerID(login, server, container);
	}
	
	public String getUserAtServer() {
		return getUserName() + '@' + getServerName();
	}
}
