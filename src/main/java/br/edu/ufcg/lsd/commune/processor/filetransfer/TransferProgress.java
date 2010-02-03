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

import java.io.Serializable;

import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;

public class TransferProgress implements Serializable {

	private static final long serialVersionUID = 1L;

	
	private final TransferHandle handle;
	private final Status newStatus;
	private final long amountWritten;
	private final double progress;
	private final double transferRate;
	private final String fileName;
	private final long fileSize;
	private final boolean outgoing;


	public TransferProgress(TransferHandle handle, String fileName, long fileSize, Status newStatus, long amountWritten, 
			double progress, double transferRate, boolean outgoing) {

		this.handle = handle;
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.newStatus = newStatus;
		this.amountWritten = amountWritten;
		this.progress = progress;
		this.transferRate = transferRate;
		this.outgoing = outgoing;
	}


	public TransferHandle getHandle() {
		return this.handle;
	}

	public Status getNewStatus() {
		return this.newStatus;
	}

	public long getAmountWritten() {
		return this.amountWritten;
	}

	public double getProgress() {
		return this.progress;
	}

	public String getFileName() {
		return this.fileName;
	}

	public long getFileSize() {
		return this.fileSize;
	}

	public double getTransferRate() {
		return transferRate;
	}

	public boolean isOutgoing() {
		return this.outgoing;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((this.handle == null) ? 0 : this.handle.hashCode());
		long temp;
		temp = Double.doubleToLongBits( this.progress );
		result = PRIME * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals( Object obj ) {
		if ( this == obj )
			return true;
		if ( !(obj instanceof TransferProgress) )
			return false;
		final TransferProgress other = (TransferProgress) obj;
		if ( !this.handle.equals( other.handle ) )
			return false;
		if ( Double.doubleToLongBits( this.progress ) != Double.doubleToLongBits( other.progress ) )
			return false;
		return true;
	}
}