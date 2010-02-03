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
package br.edu.ufcg.lsd.commune.functionaltests.data.invokewithremoteparameters;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.MonitoredBy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;


public class Object1 implements Interface1 {

	
	public static final String MY_SERVICE_NAME = "object1";
	

	private Object1 mock;
	

	public void setMock(Object1 mock) {
		this.mock = mock;
	}

	public void invoke(
			@MonitoredBy(MY_SERVICE_NAME)Stub stub1) {
		mock.invoke(stub1);
	}
	
	@RecoveryNotification
	public void stubIsUp(Stub stub1) {
		mock.stubIsUp(stub1);
	}

	@FailureNotification
	public void stubIsDown(Stub stub1) {
		mock.stubIsDown(stub1);
	}
}