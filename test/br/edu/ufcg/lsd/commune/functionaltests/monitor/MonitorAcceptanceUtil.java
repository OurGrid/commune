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
package br.edu.ufcg.lsd.commune.functionaltests.monitor;

import java.util.ArrayList;
import java.util.List;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.functionaltests.util.Context;
import br.edu.ufcg.lsd.commune.functionaltests.util.TestContext;
import br.edu.ufcg.lsd.commune.monitor.CommuneMonitorController;
import br.edu.ufcg.lsd.commune.monitor.MonitorConstants;
import br.edu.ufcg.lsd.commune.monitor.MonitorProperties;

public class MonitorAcceptanceUtil {
	
	public TestContext createBasicContext() {
		TestContext context = Context.createRealContext();
		context.set(MonitorProperties.PROP_COMMUNE_MONITOR, "yes");
		
		return context;
	}
	
	public CommuneMonitorController getMonitorController(Module application) {
		ObjectDeployment objectDeployment = application.getObject(MonitorConstants.COMMUNE_MONITOR_CONTROLLER);
		return (CommuneMonitorController) objectDeployment.getObject();
	}
	
    public ObjectDeployment getMonitorControllerDeployment(Module application) {
    	return application.getObject(MonitorConstants.COMMUNE_MONITOR_CONTROLLER);
    }
    
    public ObjectDeployment getObjectDeployment(Module application, String serviceName) {
    	return application.getObject(serviceName);
    }
	
    public List<Object> createList(Object... objects) {
    	List<Object> list = new ArrayList<Object>();
    	for (int i = 0; i < objects.length; i++) {
    		list.add(objects[i]);
    	}
    	
    	return list;
    }
}
