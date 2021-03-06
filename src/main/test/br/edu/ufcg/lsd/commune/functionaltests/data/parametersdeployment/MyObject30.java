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
package br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment;

import java.util.Set;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.MonitoredBy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.testinfra.util.Context;

public class MyObject30 implements MyInterface30 {

	public void hereIsSet(@MonitoredBy(Context.A_SERVICE_NAME)Set<MyInterface1> stubs) {
		
	}
	
	@RecoveryNotification
	public void notifyMyParamRecovery(MyInterface1 myInterface1) {
		
	}
	
	@FailureNotification
	public void notifyMyParamFailure(MyInterface1 myInterface1) {
		
	}

}
