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
import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jivesoftware.smack.util.StringUtils;

public class DeploymentID implements Serializable, CommuneAddress {

	private static final long serialVersionUID = 1L;

	private final ServiceID serviceID;
	private final long deploymentNumber;


	public DeploymentID(String fullID) {
		this(
				new ServiceID(getLogin( fullID ), getServer( fullID ), getContainerName( fullID ), 
						getServiceName( fullID )), 
				getDeploymentNumber( fullID ));
	}

	public DeploymentID( ServiceID serviceID ) {
		this( serviceID, generateDeploymentNumber() );
	}

	public DeploymentID( ContainerID containerID, String serviceName ) {
		this( containerID, serviceName, generateDeploymentNumber() );
	}

	public DeploymentID( ContainerID containerID, String serviceName, long deploymentNumber ) {
		this( new ServiceID(containerID, serviceName), deploymentNumber );
	}
	
	public DeploymentID( ServiceID serviceID, long deploymentNumber ) {
		this.serviceID = serviceID;
		this.deploymentNumber = deploymentNumber;
	}

	//TODO Try to remove
	public DeploymentID( String containerLocation, String serviceName ) {
		this( containerLocation, serviceName, generateDeploymentNumber() );
	}

	//TODO Try to remove
	public DeploymentID( String containerLocation, String serviceName, long incarnationNumber ) {
		this( new ServiceID( getLogin( containerLocation ), getServer( containerLocation ), 
				getContainerName( containerLocation ), serviceName ), incarnationNumber );
	}
	
	
	private static long generateDeploymentNumber() {
		SecureRandom random = new SecureRandom();
		long toReturn = random.nextLong();
		return (toReturn >= 0 ? toReturn : -1 * toReturn);
	}

	@Override
	public boolean equals( Object obj ) {

		if ( this == obj )
			return true;
		if ( !(obj instanceof DeploymentID) )
			return false;
		final DeploymentID other = (DeploymentID) obj;

		return this.serviceID.equals( other.serviceID ) && this.deploymentNumber == other.deploymentNumber;
	}
	
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	public boolean isSameEntity( ServiceID entityID ) {
		return (this.serviceID.equals( entityID ));
	}
	
	public ServiceID getServiceID() {
		return serviceID;
	}

	public Long getDeploymentNumber() {
		return this.deploymentNumber;
	}


	public String getContainerLocation() {

		return serviceID.getContainerID().toString();
	}

	public String getServiceName() {
		return this.serviceID.getServiceName();
	}

	@Override
	public String toString() {
		return this.serviceID.toString() + "/" + this.deploymentNumber;
	}

	public String getPublicKey() {
		return serviceID.getPublicKey();
	}

	public void setPublicKey(String publicKey) {
		serviceID.getContainerID().setPublicKey(publicKey);
	}
	
	public static String getLogin( String fullID ) throws InvalidIdentificationException {

		if ( !validate( fullID ) ) {
			throw new InvalidIdentificationException( "Invalid identification: " + fullID );
		}

		return StringUtils.parseName( fullID );
	}

	public static String getServer( String fullID ) throws InvalidIdentificationException {

		if ( !validate( fullID ) ) {
			throw new InvalidIdentificationException( "Invalid identification: " + fullID );
		}

		return StringUtils.parseServer( fullID );
	}
	
	public static String getLoginAndServer( String fullID ) throws InvalidIdentificationException {
		return getLogin(fullID) + "@" + getServer(fullID);
	}

	public static String getContainerName( String fullID ) throws InvalidIdentificationException {

		if ( !validate( fullID ) ) {
			throw new InvalidIdentificationException( "Invalid identification: " + fullID );
		}

		String[ ] split = fullID.split( "/" );

		if ( split.length < 2 ) {
			throw new InvalidIdentificationException( "Cannot extract container name from id: " + fullID );
		}

		return split[1];
	}

	public static String getServiceName( String fullID ) throws InvalidIdentificationException {

		if ( !validate( fullID ) ) {
			throw new InvalidIdentificationException( "Invalid identification: " + fullID );
		}

		String[ ] split = fullID.split( "/" );

		if ( split.length < 3 ) {
			throw new InvalidIdentificationException( "Cannot extract service name from id: " + fullID );
		}

		return split[2];
	}
	
	public static long getDeploymentNumber( String fullID ) {

		if ( !validate( fullID ) ) {
			throw new InvalidIdentificationException( "Invalid identification: " + fullID );
		}

		String[ ] split = fullID.split( "/" );

		if ( split.length != 4 ) {
			throw new InvalidIdentificationException( "Cannot extract deployment number from id: " + fullID );
		}

		String deploymentNumber = split[3];

		try {
			return Long.parseLong( deploymentNumber );
		} catch ( Exception e ) {
			throw new InvalidIdentificationException( "Cannot extract deployment number from id: " + fullID );
		}
	}

	public static boolean validate( String fullID ) {
		
		if ( fullID != null ) {
			Pattern p = Pattern.compile( "[a-zA-Z_0-9-\\.]+@[a-zA-Z_0-9-\\.]+[/a-zA-Z_0-9-\\.]*" );
			Matcher m = p.matcher( fullID );
			return m.matches();
		}
		return false;
	}

	public String getContainerName() {
		return serviceID.getContainerName();
	}

	public String getServerName() {
		return serviceID.getServerName();
	}

	public String getUserName() {
		return serviceID.getUserName();
	}

	public ContainerID getContainerID() {
		return serviceID.getContainerID();
	}
}