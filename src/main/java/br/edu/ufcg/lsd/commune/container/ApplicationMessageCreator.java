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
package br.edu.ufcg.lsd.commune.container;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLoggerFactory;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.message.Message;
import br.edu.ufcg.lsd.commune.message.MessageUtil;
import br.edu.ufcg.lsd.commune.processor.objectdeployer.ServiceProcessor;

public class ApplicationMessageCreator implements InvocationHandler {

    private final Module myModule;
	private final ServiceID stubServiceID;
	private final boolean isLocal;
	
	protected static final transient CommuneLogger msgLogger = 
		CommuneLoggerFactory.getInstance().getMessagesLogger();

    public ApplicationMessageCreator(Module myModule, ServiceID stubServiceID) {
		this.myModule = myModule;
		this.stubServiceID = stubServiceID;
		this.isLocal = myModule.getContainerID().equals(stubServiceID.getContainerID());
    }
    
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		DeploymentID targetID = null;
		
		if (isLocal) {
			targetID = myModule.getObjectRepository().get(stubServiceID.getServiceName()).getDeploymentID();
		} else {
			targetID = myModule.getStubDeploymentID(stubServiceID);
		}
		
		if (targetID == null) {
			throw new InvalidStubStateException("The object stub is not recovered");
		}
		
        String methodName = method.getName();
        
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] parameterValues = args;
    
        DeploymentID sourceID = myModule.getExecutionContext().getRunningObject().getDeploymentID();

        try {
        	
        	Message message = 
        		new Message(sourceID, targetID, methodName, ServiceProcessor.class.getName());
        	
        	if (parameterValues != null) {
        		
        		for (int i = 0; i < parameterValues.length; i++) {
        			Object value = parameterValues[i];
        			if (MessageUtil.isRemote(value, parameterTypes[i])) {
        				
        				DeploymentID paramID = null;
        				
        				DeploymentID localID = myModule.getObjectRepository().getDeploymentID(value);
        				
        				if (localID == null) {
        					paramID = myModule.getStubDeploymentID(value);
        				} else {
        					paramID = localID;
        				}
        				
        				if (paramID == null) {
        					throw new InvalidStubStateException("The parameter stub is not recovered");
        				}
        				
        				message.addStubParameter(parameterTypes[i], paramID);
        				
        			} else {
        				message.addParameter(parameterTypes[i], value);
        			}
        		}
        	}
        	
        	this.myModule.sendMessage(message);
        	
        } catch (Exception e) {
        	msgLogger.error(e);
		}
        
        return null;
    }
}