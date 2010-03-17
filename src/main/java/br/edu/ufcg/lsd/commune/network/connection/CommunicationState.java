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

import br.edu.ufcg.lsd.commune.network.DiscardMessageException;


public interface CommunicationState {
	
	void registerInterest(Communication connection);

	void release(Communication connection);
	
	void messageWithCallbackOkSessionOkSequence(Communication connection) throws DiscardMessageException;

	void heartbeatOkSessionZeroSequence(Communication connection) throws DiscardMessageException;

	void heartbeatOkSessionOkSequence(Communication connection) throws DiscardMessageException;

	void heartbeatOkSessionNonSequence(Communication connection) throws DiscardMessageException;

	void heartbeatNonSessionZeroSequence(Communication connection) throws DiscardMessageException;

	void heartbeatNonSessionOkSequence(Communication connection) throws DiscardMessageException;

	void heartbeatNonSessionNonSequence(Communication connection) throws DiscardMessageException;

	void updateStatusUp(Communication connection) throws DiscardMessageException;

	void updateStatusDown(Communication connection) throws DiscardMessageException;

	void updateStatusNonSession(Communication connection) throws DiscardMessageException;

	void messageNonSession(Communication connection) throws DiscardMessageException;

	void messageNonSequence(Communication connection) throws DiscardMessageException;

	void messageOkSessionOkSequence(Communication connection) throws DiscardMessageException;

	void timeout(Communication connection);

	void notifyFailure(Communication connection) throws DiscardMessageException;

	void notifyRecovery(Communication connection) throws DiscardMessageException;
}
