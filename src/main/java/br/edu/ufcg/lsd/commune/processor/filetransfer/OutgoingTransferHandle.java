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
package br.edu.ufcg.lsd.commune.processor.filetransfer;

import java.io.File;

import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;


public class OutgoingTransferHandle extends TransferHandle {
	
	private static final long serialVersionUID = 1L;

	private final DeploymentID destinationID;

	public OutgoingTransferHandle(Long id, String logicalFileName, File localFile, 
			String description, DeploymentID destinationID) {
		super(id, logicalFileName, localFile.length(), description);
		this.destinationID = destinationID;
		this.setLocalFile(localFile);
	}

	public OutgoingTransferHandle(Long id, String logicalFileName, long fileLength, 
			String description, DeploymentID destinationID) {
		super(id, logicalFileName, fileLength, description);
		this.destinationID = destinationID;
	}
	
	public OutgoingTransferHandle(String logicalFileName, File localFile, 
			String description, DeploymentID destinationID) {
		this(randomID(), logicalFileName, localFile, description, destinationID);
	}

	public DeploymentID getDestinationID() {
		return destinationID;
	}

	@Override
	public ContainerID getOppositeID() {
		return getDestinationID().getContainerID();
	}
}