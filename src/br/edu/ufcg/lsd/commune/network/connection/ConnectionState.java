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


public interface ConnectionState {
	
	void registerInterest(Connection connection);

	void release(Connection connection);
	
	void messageWithCallbackOkSessionOkSequence(Connection connection);

	void heartbeatOkSessionZeroSequence(Connection connection);

	void heartbeatOkSessionOkSequence(Connection connection);

	void heartbeatOkSessionNonSequence(Connection connection);

	void heartbeatNonSessionZeroSequence(Connection connection);

	void heartbeatNonSessionOkSequence(Connection connection);

	void heartbeatNonSessionNonSequence(Connection connection);

	void updateStatusUp(Connection connection);

	void updateStatusDown(Connection connection);

	void updateStatusNonSession(Connection connection);

	void messageNonSession(Connection connection);

	void messageNonSequence(Connection connection);

	void messageOkSessionOkSequence(Connection connection);

	void timeout(Connection connection);

	void notifyFailure(Connection connection);

	void notifyRecovery(Connection connection);
}
