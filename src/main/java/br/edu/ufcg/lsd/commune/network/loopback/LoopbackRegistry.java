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
package br.edu.ufcg.lsd.commune.network.loopback;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.message.Message;
import br.edu.ufcg.lsd.commune.network.DiscardMessageException;

public class LoopbackRegistry {
	
	public static boolean ENABLE = true;
	
	private static transient final org.apache.log4j.Logger LOG = 
		org.apache.log4j.Logger.getLogger( LoopbackRegistry.class );

	private static Map<String, VirtualMachineLoopbackProtocol> objectMap = 
		new ConcurrentHashMap<String, VirtualMachineLoopbackProtocol>();

	public static void addModule( ContainerID containerID, VirtualMachineLoopbackProtocol sjh ) {
		objectMap.put(containerID.toString(), sjh);
	}

	public static void removeModule( ContainerID containerID ) {
		objectMap.remove(containerID.toString());
	}
	
	public static boolean isSingleJVMModule(String containerLocation) {
		return objectMap.keySet().contains(containerLocation);
	}

	public static VirtualMachineLoopbackProtocol getSingleJVMHandler( String objectLocation ) {
		return objectMap.get(objectLocation);
	}

	public static void sendMessage( Message message ) throws DiscardMessageException {
		
		if (!message.getSource().getContainerID().equals(
				message.getDestination().getContainerID()) && !ENABLE) {
			return;
		}
		
		synchronized (objectMap) {
			VirtualMachineLoopbackProtocol protocol = 
				objectMap.get(message.getDestination().getContainerID().toString());
			if (protocol != null) {
				try {
					Message clonedMesage = Cloner.clone( message );
					protocol.receiveMessage( clonedMesage );
				} catch ( CloneNotSupportedException e ) {
					LOG.error( e.getMessage() );
					throw new CommuneRuntimeException( e );
				}
				throw new DiscardMessageException();
			}
		}
	}
	
	public static boolean isInLoopback(ContainerID containerID) {
		synchronized (objectMap) {
			return objectMap.get(containerID.toString()) != null;
		}
	}
}