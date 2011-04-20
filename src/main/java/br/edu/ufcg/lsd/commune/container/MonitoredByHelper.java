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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.api.MonitoredBy;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.message.MessageUtil;
import br.edu.ufcg.lsd.commune.processor.interest.InterestManager;
import br.edu.ufcg.lsd.commune.processor.interest.InterestRequirements;

public class MonitoredByHelper {

	private final Module module;

	public MonitoredByHelper(Module module) {
		this.module = module;
	}

	public void process(ObjectDeployment deployment) {
		Object object = deployment.getObject();
        List<Method> monitoredMethods = getMonitoredMethods(object);
        if (monitoredMethods != null && !monitoredMethods.isEmpty()) {
        	
        	for (Method monitoredMethod : monitoredMethods) {
				
       			Annotation[][] methodAnnotations = monitoredMethod.getParameterAnnotations();
       			Class<?>[] parameterTypes = monitoredMethod.getParameterTypes();
       			Type[] genericParameterTypes = monitoredMethod.getGenericParameterTypes();
       			
       			int i = 0;
       			for (Class<?> parameterType : parameterTypes) {
       				Annotation[] parameterAnnotations = methodAnnotations[i];
       				
       				MonitoredBy annotation = 
       					(MonitoredBy)getMonitoredByAnnotation(parameterAnnotations, monitoredMethod);
       				
       				if (annotation != null) {
       					
       					if (MessageUtil.isCollection(parameterType)) {
       						parameterType = MessageUtil.getCollectionType(parameterType, genericParameterTypes[i]);
       					}
       					
       					InterestRequirements requirements = getRequirements(annotation);
       					addMonitoredMethod(deployment, monitoredMethod, i, parameterType, annotation, requirements);
       				}
       				
       				i++;
    			}
			}
        }
	}

	private InterestRequirements getRequirements(MonitoredBy annotation) {
		
		int realDetectionTime = 0;
		int realHeartbeatDelay = 0;
		
		String detectionTimeProperty = annotation.detectionTimeProperty();
		String heartBeatDelayProperty = annotation.heartBeatDelayProperty();
		
		ModuleContext context = module.getContext();
		
		try {
			if (!detectionTimeProperty.equals("")) {
				realDetectionTime = context.parseIntegerProperty(detectionTimeProperty);
			}
			if (!heartBeatDelayProperty.equals("")) {
				realHeartbeatDelay = context.parseIntegerProperty(heartBeatDelayProperty);
			}
			
		} catch (Exception e) {
			//TODO log
		}
		
		int detectionTime = annotation.detectionTime();
		if (detectionTime != 0) {
			realDetectionTime = detectionTime;
		} 

		int heartBeatDelay = annotation.heartBeatDelay();
		if (heartBeatDelay != 0) {
			realHeartbeatDelay = heartBeatDelay;
		} 

		if (realHeartbeatDelay != 0 && realDetectionTime != 0) {
			return new InterestRequirements(realDetectionTime, realHeartbeatDelay);
		
		} else {
			return new InterestRequirements(context);
		}
	}

	private List<Method> getMonitoredMethods(Object object) {
		List<Method> monitoredMethods = new ArrayList<Method>();
    	Class<?> type = object.getClass();
    	List<Method> allMethods = MessageUtil.getAllMethods(type);
    	
   		for (Method method : allMethods) {
   			Method remoteDeclaration = MessageUtil.getRemoteDeclaration(method, type);

   			if (remoteDeclaration != null) {
   			
	   			Annotation[][] methodAnnotations = method.getParameterAnnotations();
	   			Class<?>[] parameterTypes = method.getParameterTypes();
	   			Type[] parameterGenTypes = method.getGenericParameterTypes();
	
	   			int i = 0;
	   			for (Class<?> parameterType : parameterTypes) {
	   				Annotation[] parameterAnnotations = methodAnnotations[i];
	   				Type genericType = parameterGenTypes[i]; 
	   				verifyParameterType(monitoredMethods, method, parameterType, 
	   						parameterAnnotations, genericType);
	   				i++;
				}
   			}
		}
   		
		return monitoredMethods;
	}

	private void verifyParameterType(List<Method> monitoredMethods, Method method, Class<?> parameterType,
			Annotation[] parameterAnnotations, Type genericType) {
		
		Annotation parameterAnnotation = getMonitoredByAnnotation(parameterAnnotations, method);
		
		if (MessageUtil.hasRemoteInterface(parameterType) 
				|| isRemoteCollection(parameterType, genericType)) { //Must have @MonitoredBy
			if (parameterAnnotation == null) {
				throw new InvalidDeploymentException("The method ' " + method + "' has remote parameters " +
						"without @MonitoredBy annotation.");
			} else {
				if (!monitoredMethods.contains(method)) {
					monitoredMethods.add(method);
				}
			}
			
		} else { //Must not have @MonitoredBy
			if (parameterAnnotation != null) {
				throw new InvalidDeploymentException("The method ' " + method + "' has non remote " +
						"parameters with @MonitoredBy annotation.");
			}
		}
	}

	private boolean isRemoteCollection(Class<?> parameterType, Type gType) {
		
		if (MessageUtil.isCollection(parameterType)) {
			Type collectionType = MessageUtil.getCollectionType(parameterType, gType);
			if (collectionType instanceof Class && MessageUtil.hasRemoteInterface((Class<?>) collectionType)) {
				return true;
			}
		}
		
		return false;
	}

	private Annotation getMonitoredByAnnotation(Annotation[] parameterAnnotations, Method method) {
		Annotation found = null;
		
		for (int j = 0; j < parameterAnnotations.length; j++) {
			Annotation parameterAnnotation = parameterAnnotations[j];
			
			if (parameterAnnotation.annotationType().equals(MonitoredBy.class)) {
				
				if (found == null) {
					found = parameterAnnotation;
				} else {
					throw new InvalidDeploymentException("The method ' " + method + "' has parameters with " +
							"duplicated @MonitoredBy annotations.");
				}
			}
		}
		
		return found;
	}

	private void addMonitoredMethod(ObjectDeployment deployment, Method method, int parameterIndex, 
			Class<?> parameterType, MonitoredBy annotation, InterestRequirements requirements) {
		
		String monitorServiceName = annotation.value();
		
		InterestManager interestManager = this.module.getInterestManager();
		interestManager.addMonitoredParameter(deployment, method, parameterIndex, parameterType, monitorServiceName, 
				requirements);
	}
}