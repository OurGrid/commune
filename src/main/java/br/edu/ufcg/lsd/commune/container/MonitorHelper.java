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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import sun.security.provider.certpath.X509CertPath;
import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;

public class MonitorHelper {

	public static boolean isMonitor(Object monitor) {
		Class<?> monitorType = monitor.getClass();
		
		Map<Class<?>, Method> recoveryMethods = 
			getNotificationMethods(monitorType, "recovery", RecoveryNotification.class);
		
		Map<Class<?>, Method> failureMethods = 
			getNotificationMethods(monitorType, "failure", FailureNotification.class);
		
		return compareMethods(monitorType, recoveryMethods.keySet(), failureMethods.keySet());
	}

	private static Map<Class<?>, Method> getNotificationMethods(Class<?> monitorType, String name, 
			Class<? extends Annotation> annotationType) {
		Map<Class<?>, Method> notificationMethods = new HashMap<Class<?>, Method>(); 
		
		for (Method method : monitorType.getMethods()) {
			
			if (method.getAnnotation(annotationType) != null) {
				Class<?>[] parameterTypes = method.getParameterTypes();
				
				int numberOfParameters = parameterTypes.length;
				if (numberOfParameters == 0 || numberOfParameters > 3) {
					throw new InvalidMonitoringException("The " + name + " notification method '" + method + 
							"' is not valid");
				}
				
				if (parameterTypes.length == 2) {
					if (!parameterTypes[1].equals(DeploymentID.class)) {
						throw new InvalidMonitoringException("The " + name + " notification method '" + method + 
								"' is not valid");
					}
				}
				
				if (parameterTypes.length == 3) {
					if (!parameterTypes[2].equals(X509CertPath.class)) {
						throw new InvalidMonitoringException("The " + name + " notification method '" + method + 
								"' is not valid");
					}
				}
				
				Method sameTypeMethod = notificationMethods.get(parameterTypes[0]);
				
				if (sameTypeMethod == null) {
					notificationMethods.put(parameterTypes[0], method);
				
				} else {
					throw new InvalidMonitoringException("The " + name + " notification method '" + method + 
						"' has a duplicated " + name + " notification method '" + sameTypeMethod + "'");
				}
			}
		}

		return notificationMethods;
	}

	private static boolean compareMethods(Class<?> monitorType, Set<Class<?>> recoveryTypes, Set<Class<?>> failureTypes) {
		if (recoveryTypes.size() == 0 && failureTypes.size() == 0) {
			return false;
		}
		
		if (recoveryTypes.size() != failureTypes.size()) {
			throw new InvalidMonitoringException("The class " + monitorType + " is not valid monitor");
		}
		
		for (Class<?> recoveryType : recoveryTypes) {
			if (!failureTypes.contains(recoveryType)) {
				throw new InvalidMonitoringException("The class " + monitorType + " is not valid monitor for the " +
						"type " + recoveryType);
			}
		}

		for (Class<?> failureType : failureTypes) {
			if (!recoveryTypes.contains(failureType)) {
				throw new InvalidMonitoringException("The class " + monitorType + " is not valid monitor for the " +
						"type " + failureType);
			}
		}

		return true;
	}

}
