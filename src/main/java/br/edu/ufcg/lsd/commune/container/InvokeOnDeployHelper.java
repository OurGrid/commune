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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.message.MessageUtil;

public class InvokeOnDeployHelper {

	private final Module module;

	public InvokeOnDeployHelper(Module module) {
		this.module = module;
	}

	public void process(ObjectDeployment deployment) {
		DeploymentID deploymentID = deployment.getDeploymentID();
		Object object = deployment.getObject();
        Method invokeOnDeploy = getInvokeOnDeploy(object);
        if (invokeOnDeploy != null) {
        	
        	Object[] parameters = injectParameters(invokeOnDeploy, deployment);
        	
        	module.setExecutionContext(deployment, deploymentID, module.getMyCertPath());
        	invokeOnDeploy(invokeOnDeploy, deployment.getObject(), parameters);
        }
	}

	private Method getInvokeOnDeploy(Object object) {
    	Method invokeOnDeploy = null;
    	Class<?> type = object.getClass();
    	
    	List<Method> allMethods = MessageUtil.getAllMethods(type);
    	
   		for (Method method : allMethods) {
			if (method.getAnnotation(InvokeOnDeploy.class) != null) { //Found!

//TODO remove				Method remoteDeclaration = MessageUtil.getRemoteDeclaration(method, type); 
//				if (remoteDeclaration != null) {
					if (invokeOnDeploy == null) {
//						invokeOnDeploy = remoteDeclaration;
						invokeOnDeploy = method;
					} else {
						throw new InvalidDeploymentException("The class ' " + type.getName() + 
								"'must have only one @InvokeOnDeploy method");
					}
//				} else {
//					throw new InvalidDeploymentException("The method " + method + "is not remote");
//				}
			}
		}
   		
		return invokeOnDeploy;
	}

	private Object[] injectParameters(Method invokeOnDeploy, ObjectDeployment deployment) {
    	Class<?>[] parameterTypes = invokeOnDeploy.getParameterTypes();
    	Object[] result = new Object[parameterTypes.length]; 
    	
    	int i = 0;
    	for (Class<?> parameterType : parameterTypes) {
    		
    		if (parameterType.equals(DeploymentID.class)) {
    			result[i++] = deployment.getDeploymentID();
    			
    		} else if (parameterType.equals(ServiceManager.class)) {
    			result[i++] = deployment.getServiceManager();
    		
    		} else {
    			throw new InvalidDeploymentException("The parameter '" + parameterType.getName() + 
    					"' is invalid in the invoke on deploy method: " + invokeOnDeploy);
    		}
		}
    	
		return result;
	}

	private void invokeOnDeploy(Method invokeOnDeploy, Object stub, Object[] parameters) {
		try {
			invokeOnDeploy.invoke(stub, parameters);
		} catch (IllegalArgumentException e) {}
		catch (IllegalAccessException e) {} 
		catch (InvocationTargetException e) {}
		//TODO log
	}


}
