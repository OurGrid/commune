/*
 * Copyright (C) 2009 Universidade Federal de Campina Grande
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
package br.edu.ufcg.lsd.commune.network.connection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.edu.ufcg.lsd.commune.container.StubReference;
import br.edu.ufcg.lsd.commune.message.Message;

public class Communication {

	private Long messageNonSequenceTime;
	private Long incomingSession;
	private Long incomingSequence;
	private Long outgoingSession;
	private Long outgoingSequence;
	private CommunicationState state;
	private StubReference stubReference;
	private List<Message> pendingIncomingMessages = Collections.synchronizedList(new ArrayList<Message>());
	

	public StubReference getStubReference() {
		return stubReference;
	}

	public Long getOutgoingSession() {
		return outgoingSession;
	}

	public void setOutgoingSession(Long outgoingSession) {
		this.outgoingSession = outgoingSession;
	}

	public Long getOutgoingSequence() {
		return outgoingSequence;
	}

	public void setOutgoingSequence(Long outgoingSequence) {
		this.outgoingSequence = outgoingSequence;
	}

	public Long getIncomingSession() {
		return incomingSession;
	}

	public void setIncomingSession(Long incomingSession) {
		this.incomingSession = incomingSession;
	}

	public Long getIncomingSequence() {
		return incomingSequence;
	}

	public void setIncomingSequence(Long incomingSequence) {
		this.incomingSequence = incomingSequence;
	}

	public void setStubReference(StubReference stubReference) {
		this.stubReference = stubReference;
	}
	
	
	public void incIncomingSequenceNumber() {
		this.incomingSequence++;
	}
	
	public void decIncomingSequenceNumber() {
		this.incomingSequence--;
	}

	public void incOutgoingSequenceNumber() {
		this.outgoingSequence++;
	}

	public CommunicationState getState() {
		return state;
	}

	public void setState(CommunicationState state) {
		this.state = state;
	}
	
	public void invalidate() {
		incomingSession = null;
		incomingSequence = null;
		outgoingSession = null;
		outgoingSequence = null;
	}
	
	public void invalidateIncoming() {
		incomingSession = null;
		incomingSequence = null;
	}
	
	public void invalidateOutgoing() {
		outgoingSession = null;
		outgoingSequence = null;
	}
	
	public boolean isValidOutgoing() {
		return outgoingSession != null;
	}

	public boolean isValidIncoming() {
		return incomingSession != null;
	}

	public String toString() {
		String stub = "";
		if (stubReference != null) {
			stub = stubReference.toString();
		}
		
		return stub + ":" + state +":>" + incomingSession + "," + incomingSequence + "< <" + outgoingSession + "," + 
			outgoingSequence + ">";
	}

	public void setMessageNonSequenceTime(Long messageNonSequenceTime) {
		this.messageNonSequenceTime = messageNonSequenceTime;
	}

	public Long getMessageNonSequenceTime() {
		return messageNonSequenceTime;
	}
	
	public void addPendingMessage(Message message) {
		getPendingIncomingMessages().add(message);
		
		//since there is a buffer for the pending messages, the best thing to do is keep the new messages in order
		//insertion sort: will sort almost in linear time, since all previous messages were already sorted
		for (int i = 1; i < getPendingIncomingMessages().size(); i++) {
			Message temp = getPendingIncomingMessages().get(i);
			int j = i-1;
			
			while (temp.getSequence() < getPendingIncomingMessages().get(j).getSequence() && j >= 0) {
				getPendingIncomingMessages().set(j + 1, getPendingIncomingMessages().get(j));
				j--;
			}
			
			getPendingIncomingMessages().set(j+1, temp);
		}
	}

	public List<Message> getPendingIncomingMessages() {
		return pendingIncomingMessages;
	}
	
	public void clearPendingIncomingMessages() {
		this.pendingIncomingMessages.clear();
	}
	
	public void resetTimeOut() {
		setMessageNonSequenceTime(null);
	}
}