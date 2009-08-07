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

/**
 * 
 * This interface specifies the callback of the transfer file status. Classes that implement this
 * Interface will notified about the sending files events. 
 * 
 */
public interface TransferSender extends TransferProgressListener {

	/**
	 * This method informs the Sender that the file transfer was rejected. The handle contains the 
	 * transfer parameters. 
	 * @param handle The handle that contains the transfer parameters.
	 */
	public void transferRejected(OutgoingTransferHandle handle);

	/**
	 * This methods informs the Sender that the file transfer failed. The transfer parameters are specified by
	 * the handle. The failCause informs the failure cause. The total transfered data is given by the
	 * amountWritten parameter.
	 * @param handle Specifies the transfer parameters.
	 * @param failCause The failure cause.
	 * @param amountWritten The total transfered data until the transfer fail.
	 */
	public void outgoingTransferFailed(OutgoingTransferHandle handle, Exception failCause, long amountWritten);

	/**
	 * This method informs the Sender that the file transfer was canceled. The transfer parameters are specified
	 * by the handle. The total transfer data is given by the the amountWritten parameter.   
	 * @param handle Specifies the transfer parameters. 
	 * @param amountWritten The total transfered data until the transfer be canceled.
	 */
	public void outgoingTransferCancelled(OutgoingTransferHandle handle, long amountWritten);

	/**
	 * This method informs the Sender that the file transfer was completed. The transfer parameters are specified
	 * by the handle. The total transfer data is given by the the amountWritten parameter. 
	 * @param handle Specifies the transfer parameters. 
	 * @param amountWritten The total transfered data.
	 */
	public void outgoingTransferCompleted(OutgoingTransferHandle handle, long amountWritten);
}