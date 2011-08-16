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

import static br.edu.ufcg.lsd.commune.processor.filetransfer.FileTransferConstants.*;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.message.Message;

public class IncomingTransfersManager implements FileTransferListener {

	
	private static final long serialVersionUID = 1L;

	
	private static transient org.apache.log4j.Logger LOG = 
		org.apache.log4j.Logger.getLogger(IncomingTransfersManager.class);

	
	private FileTransferManager manager;
	private Map<DeploymentID,TransferReceiver> receiverListeners;
	private Map<IncomingTransferHandle,FileTransferRequest> handlersRequestMap;

	private ConcurrentHashMap<TransferHandle,IncomingTransfer> transfers;
	private TransferStateMonitorThread transferStateMonitor;

	private final Module module;

	/**
	 * Lock used when making a change on both collections that store file
	 * transfers
	 */
	protected final ReentrantLock transfersLock;


	public IncomingTransfersManager(Module module, FileTransferManager manager, ReentrantLock transfersLock) {

		this.module = module;
		receiverListeners = new HashMap<DeploymentID,TransferReceiver>();
		handlersRequestMap = new HashMap<IncomingTransferHandle,FileTransferRequest>();
		transfers = new ConcurrentHashMap<TransferHandle,IncomingTransfer>();
		this.manager = manager;

		transferStateMonitor = new TransferStateMonitorThread();
		this.transfersLock = transfersLock;
	}


	public void start(XMPPConnection connection) {
		manager.addFileTransferListener(this);
		new Thread(transferStateMonitor).start();
	}

	public void addFileTransferInterested(TransferReceiver frl, DeploymentID receiverID) {
		receiverListeners.put(receiverID, frl );
	}

	public void removeFileTransferInterested(DeploymentID receiverID) {
		receiverListeners.remove(receiverID);
	}

	public void accept(IncomingTransferHandle handle, DeploymentID listenerID) {
		
		File destination = handle.getLocalFile(); 
		long inactivityTimeout = handle.getInactivityTimeout();
		boolean receiveProgressUpdates = handle.isReceiveProgressUpdate();

		LOG.debug( "Accepting transfer. Handle: " + handle + ", dest: " + destination.getAbsolutePath() );
		
		createSubFolders(destination);

		FileTransferRequest fileTransferRequest = handlersRequestMap.get( handle );
		final IncomingFileTransfer transfer = fileTransferRequest.accept();

		IncomingTransfer fileTransfer = new IncomingTransfer(module, listenerID, destination, transfer, handle,
			inactivityTimeout, fileTransferRequest.getFileSize(), receiveProgressUpdates);

		if ( receiveProgressUpdates ) {
			TransferProgress transferProgress = new TransferProgress( handle, destination.getName(),
				fileTransferRequest.getFileSize(), transfer.getStatus(), 0L, 0D, 0, false );
			Message message = 
				AbstractTransfer.createUpdateTransferProgressMessage(module.getContainerID(), listenerID, 
						transferProgress);
			module.sendMessage(message);
		}

		try {
			fileTransfer.start();
		} catch (IllegalArgumentException e) {
			LOG.error("Destination is not writable. Handle: " + handle + ", dest: " + destination.getAbsolutePath());
			reject(handle);
		}
		
		addTransfer(handle, fileTransfer);
	}


	private void addTransfer(IncomingTransferHandle handle,
			IncomingTransfer fileTransfer) {
		try {
			transfersLock.lock();
			transfers.put( handle, fileTransfer );
			
		} finally {
			transfersLock.unlock();
		}
	}

	private void createSubFolders(File destination) {
		
		if (destination == null) {
			return;
		}
		
		File parentFile = destination.getParentFile();
		
		if (parentFile != null && !parentFile.exists()) {
			parentFile.mkdirs();
		}
	}

	public void reject(TransferHandle handle) {
		FileTransferRequest request = handlersRequestMap.get(handle);
		rejectRequest(request);
	}

	private void rejectRequest(FileTransferRequest request) {
		LOG.debug("Rejecting transfer. " + " File name: " + request.getFileName() + ", description: "
				+ request.getDescription());
		request.reject();
	}

	public void cancelTransfer(TransferHandle handle) {
		try {
			transfersLock.lock();

			if (transfers.containsKey(handle)) {
				IncomingTransfer incomingTransfer = transfers.get(handle);
				incomingTransfer.cancel();
				remove(incomingTransfer);
			}

		} finally {
			transfersLock.unlock();
		}
	}
	
	public void fileTransferRequest(FileTransferRequest ftr) {
		Map<String, String> props = getPropsMap(ftr);
		
		if (props.size() < 5) {
			rejectRequest(ftr);
			
		} else {
			DeploymentID receiverID = new DeploymentID(props.get(DESTINATION_PROPERTY));
			TransferReceiver receiver = receiverListeners.get(receiverID);
			if (receiver != null) {
				String transferDescription = null;
				transferDescription = props.get(DESCRIPTION_PROPERTY);
				
				try {
					long id = Long.parseLong(props.get(HANDLE_PROPERTY));
					ContainerID senderID = ContainerID.parse(ftr.getRequestor());
					
					IncomingTransferHandle handle = 
						new IncomingTransferHandle(id, ftr.getFileName(), transferDescription, ftr.getFileSize(), senderID);
					
					handle.setReadable(Boolean.parseBoolean(props.get(READABLE_PROPERTY)));
					handle.setExecutable(Boolean.parseBoolean(props.get(EXECUTABLE_PROPERTY)));
					handle.setWritable(Boolean.parseBoolean(props.get(WRITABLE_PROPERTY)));
					
					handlersRequestMap.put(handle, ftr);
					
					Message message = new Message(module.getContainerID(), receiverID, "transferRequestReceived");
					message.addParameter(IncomingTransferHandle.class, handle);
					module.sendMessage(message);

				} catch (Exception e) {
					rejectRequest(ftr);					
				}
				
				
			} else {
				rejectRequest(ftr);
			}
		}
	}


	private Map<String, String> getPropsMap(FileTransferRequest ftr) {
		String[ ] msgArray = ftr.getDescription().split(PROPERTIES_SEPARATOR);
		Map<String, String> props = new TreeMap<String, String>(); 
		
		for (String msg : msgArray) {

			if (msg == null) {
				continue;
			}
			
			String[] data = msg.split(PROPERTY_SEPARATOR);
			
			if (data == null || data.length == 0 || data.length > 2) {
				continue;
			}
			
			if (data.length == 1) {
				props.put(data[0], null);
			} else {
				props.put(data[0], data[1]);
			}
		}
		return props;
	}


	/**
	 * Shutdowns this module removing this listener from the connection.
	 */
	public void shutdown() {

		manager.removeFileTransferListener( this );
		this.transferStateMonitor.shutdown();
	}

	private class TransferStateMonitorThread implements Runnable {

		private volatile boolean shutdown;


		public void run() {

			while ( !shutdown ) {
				try {
					transfersLock.lock();

					Iterator<Entry<TransferHandle,IncomingTransfer>> transfersIt = transfers.entrySet().iterator();

					while ( transfersIt.hasNext() ) {
						AbstractTransfer transfer = transfersIt.next().getValue();
						if ( transfer.updateStatus() ) {
							transfersIt.remove();
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

	public void remove(IncomingTransfer transfer) {
		try {
			transfersLock.lock();
			
			transfers.remove(transfer.getHandle());
		
		} finally {
			transfersLock.unlock();
		}
	}
	
	public Collection<IncomingTransfer> getTransfers() {
		return transfers.values();
	}
}
