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
package br.edu.ufcg.lsd.commune.monitor;

import java.util.Collection;
import java.util.Map;

import br.edu.ufcg.lsd.commune.api.Remote;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.message.Message;
import br.edu.ufcg.lsd.commune.monitor.data.StubData;
import br.edu.ufcg.lsd.commune.monitor.data.TransferData;

@Remote
public interface CommuneMonitorClient {
	
	public void hereAreDeployedObjects(Map<DeploymentID, Collection<Class<?>>> deployedObjects);
	
	public void hereAreStubs(Collection<StubData> stubDatas);
	
	public void hereAreFileTransfers(Collection<TransferData> transferDatas);
	
	public void hereIsServiceMessagesLog(Collection<Message> messagesLog);
	
	public void hereIsFileTransferMessagesLog(Collection<Message> messagesLog);
	
	public void hereIsInterestMessagesLog(Collection<Message> messagesLog);
}
