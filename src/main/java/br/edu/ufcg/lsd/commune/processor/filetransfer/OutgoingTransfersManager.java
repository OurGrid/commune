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
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.message.Message;

public class OutgoingTransfersManager {

	private static final Logger LOG = Logger.getLogger( OutgoingTransfersManager.class );

	/** Smack File Transfer Manager. */
	private final FileTransferManager manager;

	/** Listeners on this manager. DeploymentID is the key, the value is a stub. */
	private final ConcurrentHashMap<TransferHandle,OutgoingTransfer> transfers;

	private final LinkedList<OutgoingTransfer> queuedTransfers;

	private final TransferStateMonitorThread transferStateMonitor;

	private final int maxOut;

	/**
	 * Lock used when making a change on both collections that store file
	 * transfers
	 */
	protected final ReentrantLock transfersLock;


	/**
	 * Default constructor. Initialize fields and sets the default detection
	 * time of an outgoing file transfer.
	 * 
	 * @param maxOut maximum number of simultaneous outgoing file transfers
	 */
	public OutgoingTransfersManager( FileTransferManager manager, int maxOut, int responseTimeout, ReentrantLock transfersLock) {

		this.maxOut = maxOut;
		transfers = new ConcurrentHashMap<TransferHandle,OutgoingTransfer>();
		this.transfersLock = transfersLock;
		queuedTransfers = new LinkedList<OutgoingTransfer>();
		// Setting negotiation timeout
		OutgoingFileTransfer.setResponseTimeout( responseTimeout );
		this.manager = manager;
		transferStateMonitor = new TransferStateMonitorThread();
	}


	public void startTransfer(OutgoingTransferHandle handle, DeploymentID listenerID, Module module) {

		File file = handle.getLocalFile();
		DeploymentID destination = handle.getDestinationID();
		long inactivityTimeout = handle.getInactivityTimeout();
		boolean receiveProgressUpdates = handle.isReceiveProgressUpdate();
		String transferDescription = handle.getDescription();
		
		final OutgoingFileTransfer transfer = manager.createOutgoingFileTransfer( destination.getContainerID().toString() );
 
		OutgoingTransfer fileTransfer = new OutgoingTransfer(module, listenerID, handle, transfer, file, 
				destination.toString(), inactivityTimeout, receiveProgressUpdates, transferDescription);

		if ( receiveProgressUpdates ) {
			TransferProgress transferProgress = 
				new TransferProgress(handle, file.getName(), file.length(), transfer.getStatus(), 0L, 0D, 0, true);
			Message message = 
				AbstractTransfer.createUpdateTransferProgressMessage(module.getContainerID(), listenerID, 
						transferProgress);
			module.sendMessage(message);
		}

		try {
			transfersLock.lock();
			if ( transfers.size() < maxOut ) {
				activateTransfer( fileTransfer );
			} else {
				LOG.debug( "Queueing outgoing transfer of file " + file.getName() + ", handle: " + handle );
				queuedTransfers.offer( fileTransfer );
			}
		} finally {
			transfersLock.unlock();
		}
	}


	private boolean activateTransfer( OutgoingTransfer fileTransfer ) {

		LOG.debug( "Activating outgoing transfer of file " + fileTransfer.getFile().getName() + ", handle: "
				+ fileTransfer.getHandle() );

		boolean started = fileTransfer.start();
		
		if ( started ) {
			/*
			 * if the file transfer can be successfully started, put it in the
			 * map so that its progress can be monitored
			 */
			transfers.put( fileTransfer.getHandle(), fileTransfer );
		} else {
			remove(fileTransfer);
			fileTransfer.cancel();
			fileTransfer.createOutgoingTransferFailed((OutgoingTransferHandle)fileTransfer.getHandle(), 
					new Exception("Can not start outgoing file transfer: " + fileTransfer.getHandle()));
		}
		
		return started;
	}


	public void cancelTransfer( TransferHandle handle ) {

		try {
			transfersLock.lock();
			if ( transfers.containsKey( handle ) ) {
				OutgoingTransfer outgoingTransfer = transfers.get( handle );
				remove(outgoingTransfer);
				outgoingTransfer.cancel();
				activateOtherTransfer();
				
			} else {
				
				for (OutgoingTransfer outgoingTransfer : queuedTransfers) {
					
					if (outgoingTransfer.equals(handle)) {
						queuedTransfers.remove( outgoingTransfer );
						break;
					}
				}
			}
		} finally {
			transfersLock.unlock();
		}
	}


	public void start() {

		new Thread( transferStateMonitor ).start();
	}


	public void shutdown() {

		transferStateMonitor.shutdown();
	}

	/**
	 * 
	 * @return false if the transfer failed, true if the transfer was activated
	 * or there was no transfer to activate.
	 */
	private boolean activateOtherTransfer() {
		OutgoingTransfer pendingTransfer = queuedTransfers.poll();
		if ( pendingTransfer != null ) {
			return activateTransfer( pendingTransfer );
		}
		
		return true;
	}

	private class TransferStateMonitorThread implements Runnable {

		private volatile boolean shutdown;


		public void run() {

			while ( !shutdown ) {

				LinkedHashMap<TransferHandle,OutgoingTransfer> transfersClone;

				try {
					transfersLock.lock();
					transfersClone = new LinkedHashMap<TransferHandle,OutgoingTransfer>( transfers );
	
					Iterator<Entry<TransferHandle,OutgoingTransfer>> transfersIt = transfersClone.entrySet()
						.iterator();
	
					int activations = 0;
					while ( transfersIt.hasNext() ) {
						Entry<TransferHandle,OutgoingTransfer> entry = transfersIt.next();
						Transfer transfer = entry.getValue();
						if ( transfer.updateStatus() ) {
							transfers.remove( entry.getKey() );
							activations++;
						}
					}
					
					while (activations > 0) {
						if (activateOtherTransfer()) {
							activations--;
						}
					}
					
					
				} finally {
					transfersLock.unlock();
				}

				synchronized ( this ) {
					try {
						this.wait( 5000 );
					} catch ( InterruptedException e ) {
						//TODO log
					}
				}
			}
		}


		public void shutdown() {

			this.shutdown = true;
			synchronized ( this ) {
				this.notify();
			}
		}
	}

	protected void remove(OutgoingTransfer outgoingTransfer) {
		transfers.remove(outgoingTransfer.getHandle());
	}
	
	public Collection<OutgoingTransfer> getTransfers() {
		return transfers.values();
	}

	public List<OutgoingTransfer> getQueuedTransfers() {
		return queuedTransfers;
	}
	
	public OutgoingTransfer getTransfer(TransferHandle handle) {
		return transfers.get(handle);
	}
}
