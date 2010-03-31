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

import br.edu.ufcg.lsd.commune.container.StubReference;

public class Communication {

	private Long incomingSession;
	private Long incomingSequence;
	private Long outgoingSession;
	private Long outgoingSequence;
	private CommunicationState state;
	private StubReference stubReference;
	

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
}