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

import br.edu.ufcg.lsd.commune.identification.ContainerID;


public class IncomingTransferHandle extends TransferHandle {

	
	private static final long serialVersionUID = 1L;
	
	
	private ContainerID senderID;
	private Boolean readable;
	private Boolean writable;
	private Boolean executable;
	
	public IncomingTransferHandle(Long id, String logicalFileName, String description, 
			long fileSize, ContainerID senderID) {
		super(id, logicalFileName, fileSize, description);
		this.senderID = senderID;
		
	}
	
	public IncomingTransferHandle(String logicalFileName, String description, long fileSize, ContainerID senderID) {
		this(randomID(), logicalFileName, description, fileSize, senderID);
	}
	
	public IncomingTransferHandle() {
		this(randomID(), null, null, 0, null);
	}
	

	/**
	 * Originally without public key
	 * @return
	 */
	public ContainerID getSenderID() {
		return senderID;
	}

	public void setSenderID(ContainerID senderID) {
		this.senderID = senderID;
	}

	@Override
	public ContainerID getOppositeID() {
		return getSenderID();
	}

	public Boolean isReadable() {
		return readable;
	}

	public void setReadable(Boolean readable) {
		this.readable = readable;
	}

	public Boolean isWritable() {
		return writable;
	}

	public void setWritable(Boolean writable) {
		this.writable = writable;
	}

	public Boolean isExecutable() {
		return executable;
	}

	public void setExecutable(Boolean executable) {
		this.executable = executable;
	}
}