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
package br.edu.ufcg.lsd.commune.functionaltests.data.remotecollectionparameters;

import java.util.List;
import java.util.Map;
import java.util.Set;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.MonitoredBy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;

public class RemoteObject1 implements RemoteInterface1 {
	
	
	public static final String MY_SERVICE_NAME = "remoteobject1";

	
	private RemoteObject1 mock;

	
	public void list(
			@MonitoredBy(MY_SERVICE_NAME) List<RemoteParameter> parameter) {
		mock.list(parameter);
	}

	public void map(
			@MonitoredBy(MY_SERVICE_NAME) Map<String, RemoteParameter> parameter) {
		mock.map(parameter);
	}

	public void set(
			@MonitoredBy(MY_SERVICE_NAME) Set<RemoteParameter> parameter) {
		mock.set(parameter);
	}

	@RecoveryNotification
	public void parameterIsUp(RemoteParameter remote) {
		mock.parameterIsUp(remote);
	}
	
	@FailureNotification
	public void parameterIsDown(RemoteParameter remote) {
		mock.parameterIsDown(remote);
	}

	public void setMock(RemoteObject1 mock) {
		this.mock = mock;
	}
}
