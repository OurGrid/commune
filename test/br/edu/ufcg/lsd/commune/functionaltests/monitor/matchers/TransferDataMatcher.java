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
package br.edu.ufcg.lsd.commune.functionaltests.monitor.matchers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;

import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.monitor.data.TransferData;

public class TransferDataMatcher implements IArgumentMatcher {
	
	private Collection<TransferData> transferDatas;
	
	public TransferDataMatcher(Collection<TransferData> transferDatas) {
		this.transferDatas = transferDatas;
	}
	
	public TransferDataMatcher(ContainerID destinationID, DeploymentID listenerID, Status status, File file, 
			int queuePosition, boolean isIncoming) {
		Collection<TransferData> transferDatas = new ArrayList<TransferData>();
		transferDatas.add(new TransferData(destinationID, listenerID, status, file, 0, 0,
				queuePosition, true, isIncoming));
		
		this.transferDatas = transferDatas;
	}

	/* (non-Javadoc)
	 * @see org.easymock.IArgumentMatcher#appendTo(java.lang.StringBuffer)
	 */
	public void appendTo(StringBuffer arg0) {
		
	}

	/* (non-Javadoc)
	 * @see org.easymock.IArgumentMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object arg0) {
		
		if (arg0 == null || !(arg0 instanceof Collection)) {
			return false;
		}
		
		Collection<TransferData> collection = (Collection<TransferData>) arg0;
		
		if (this.transferDatas.size() != collection.size()) {
			return false;
		}

		Iterator<TransferData> transferDataIterator = this.transferDatas.iterator();
		Iterator<TransferData> collectionIterator = collection.iterator();
		
		while (transferDataIterator.hasNext() && collectionIterator.hasNext()) {
			TransferData transferData = transferDataIterator.next();
			TransferData other = collectionIterator.next();
			
			if (!compareTransferDatas(transferData, other)) {
				return false;
			}
			
		}
		
		return true;
	}
	
	private boolean equalsStatus(Status status, Status otherStatus) {
		return status == null ? otherStatus == null : status.equals(otherStatus);
	}
	
	private boolean compareTransferDatas(TransferData transferData, TransferData otherTransferData) {
		return transferData.getDestinationID().equals(otherTransferData.getDestinationID()) &&
			transferData.getFile().equals(otherTransferData.getFile()) && 
			transferData.getListenerID().equals(otherTransferData.getListenerID()) &&
			equalsStatus(transferData.getStatus(), otherTransferData.getStatus()) &&
			transferData.getQueuePosition() == otherTransferData.getQueuePosition();
	}

	public static Collection<TransferData> eqMatcher(ContainerID destinationID, DeploymentID listenerID, Status status, File file,
			int queuePosition, boolean isIncoming) {
		EasyMock.reportMatcher(new TransferDataMatcher(destinationID, listenerID, status, file, queuePosition, isIncoming));
		return null;
	}
	
	public static Collection<TransferData> eqMatcher(Collection<TransferData> transferDatas) {
		EasyMock.reportMatcher(new TransferDataMatcher(transferDatas));
		return null;
	}

}
