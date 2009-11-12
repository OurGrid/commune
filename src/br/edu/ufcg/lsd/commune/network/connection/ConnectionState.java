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


public interface ConnectionState {
	
	void registerInterest(Connection connection);

	void release(Connection connection);
	
	void messageWithCallbackOkSessionOkSequence(Connection connection) throws DiscardMessageException;

	void heartbeatOkSessionZeroSequence(Connection connection) throws DiscardMessageException;

	void heartbeatOkSessionOkSequence(Connection connection) throws DiscardMessageException;

	void heartbeatOkSessionNonSequence(Connection connection) throws DiscardMessageException;

	void heartbeatNonSessionZeroSequence(Connection connection) throws DiscardMessageException;

	void heartbeatNonSessionOkSequence(Connection connection) throws DiscardMessageException;

	void heartbeatNonSessionNonSequence(Connection connection) throws DiscardMessageException;

	void updateStatusUp(Connection connection) throws DiscardMessageException;

	void updateStatusDown(Connection connection) throws DiscardMessageException;

	void updateStatusNonSession(Connection connection) throws DiscardMessageException;

	void messageNonSession(Connection connection) throws DiscardMessageException;

	void messageNonSequence(Connection connection) throws DiscardMessageException;

	void messageOkSessionOkSequence(Connection connection) throws DiscardMessageException;

	void timeout(Connection connection);

	void notifyFailure(Connection connection) throws DiscardMessageException;

	void notifyRecovery(Connection connection) throws DiscardMessageException;
}
