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
package br.edu.ufcg.lsd.commune.processor.objectdeployer;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.CommuneAddress;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.message.Message;
import br.edu.ufcg.lsd.commune.message.MessageParameter;
import br.edu.ufcg.lsd.commune.message.MessageUtil;
import br.edu.ufcg.lsd.commune.message.StubParameter;
import br.edu.ufcg.lsd.commune.network.DiscardMessageException;
import br.edu.ufcg.lsd.commune.processor.AbstractProcessor;
import br.edu.ufcg.lsd.commune.processor.filetransfer.IncomingTransfer;
import br.edu.ufcg.lsd.commune.processor.filetransfer.IncomingTransferHandle;

public class ServiceProcessor extends AbstractProcessor {


	private HashSet<ObjectReference> updatedObjects;
	private List<NotificationListener> notificationListeners = new ArrayList<NotificationListener>();

	
    public ServiceProcessor(Module module) {
        super(module);
    }

    
    public void addListener(NotificationListener listener) {
    	notificationListeners.add(listener);
    }
    
	public void processMessage(Message message) {

		//TODO validation
		
		msgLogger.debug(message.toString() + "\n{");	
		
		CommuneAddress destination = message.getDestination();
		String serviceName = destination.getServiceName();
		
		ObjectDeployment objectDeployment = this.getObjectDeployment(serviceName);
		
		if (objectDeployment == null) {
			msgLogger.fatal("Local service not found: " + serviceName, new Exception());
			return;
		}
		
		Class<?>[] parameterTypes = message.getParameterTypes();
		Object[] parameterValues = message.getParameterValues();
		List<MessageParameter> parameters = message.getParameters();
		
		getModule().setExecutionContext(objectDeployment, message.getSource(), 
				message.getSenderCertificatePath());

		try {
			updateReferences(objectDeployment, parameterTypes, parameterValues, parameters);
		} catch (Exception e) {
			msgLogger.error(e);
		}

		Object target = objectDeployment.getObject();
		String functionName = message.getFunctionName();

		Method method = null;
		
		try {
			Class<?> targetClass = target.getClass();
			method = targetClass.getMethod(functionName, parameterTypes);

			if (!isNotificationMethod(method)) {
				registerParameterInterests(objectDeployment, method, parameters);
			
			} 
			
			if(isFailureNotificationMethod(method)) {
				Object stub = parameterValues[0];
				fireNotifyFailure(getModule().getStubDeploymentID(stub).getServiceID());
				getModule().setStubDown(stub);
			}

			if(isRecoveryNotificationMethod(method)) {
				Object stub = parameterValues[0];
				fireNotifyRecovery(getModule().getStubDeploymentID(stub).getServiceID());
			}
			
			if (isIncomingTransferCompleted(method)) {
				IncomingTransferHandle handle = (IncomingTransferHandle) parameterValues[0];
				setFilePermissions(handle);
			}

			method.invoke(target, parameterValues);
			
			msgLogger.debug("\n}");
			
		} catch (DiscardMessageException e) {
			String msg = "Method '" + functionName + "' of object '" + destination + "' was discarted: ";
			msgLogger.debug(msg);
			
		} catch (InvocationTargetException e) {
			String msg = "Method '" + functionName + "' of object '" + destination + "' has thrown an exception: ";
			msgLogger.fatal(msg, e.getCause());

		} catch (Exception e) {
			msgLogger.error(e);
		}		
	}
	
	private boolean isIncomingTransferCompleted(Method method) {
		return method.getName().equals(IncomingTransfer.INCOMING_TRANSFER_COMPLETED);
	}

	private void setFilePermissions(IncomingTransferHandle incomingTransferHandle) {
		File localFile = incomingTransferHandle.getLocalFile();
		localFile.setReadable(incomingTransferHandle.isReadable());
		localFile.setWritable(incomingTransferHandle.isWritable());				
		localFile.setExecutable(incomingTransferHandle.isExecutable());		
	}

	private void fireNotifyFailure(ServiceID serviceID) throws DiscardMessageException {
		for (NotificationListener listener : notificationListeners) {
			listener.notifyFailure(serviceID);
		}
	}


	private void fireNotifyRecovery(ServiceID serviceID) throws DiscardMessageException {
		for (NotificationListener listener : notificationListeners) {
			listener.notifyRecovery(serviceID);
		}
	}


	private boolean isNotificationMethod(Method method) {
		return (method.getAnnotation(FailureNotification.class) != null) ||
			(method.getAnnotation(RecoveryNotification.class) != null);
	}

	private boolean isFailureNotificationMethod(Method method) {
		return (method.getAnnotation(FailureNotification.class) != null);
	}

	private boolean isRecoveryNotificationMethod(Method method) {
		return (method.getAnnotation(RecoveryNotification.class) != null);
	}

	public void registerParameterInterests(ObjectDeployment objectDeployment, Method method, List<MessageParameter> parameters) {

		int i = 0;
		for (MessageParameter messageParameter : parameters) {
			
			Object parameterValue = messageParameter.getValue();
			Class<?> parameterType = messageParameter.getType();
			
			if (parameterValue == null) {
				continue;
			}
			
			if (MessageUtil.isStubParameter(parameterValue)) {
				registerInterest(objectDeployment, method, i, parameterValue, parameterType);
				
			} else if (Collection.class.isAssignableFrom(parameterValue.getClass())) {
				
				Collection<?> stubParameterCollection = (Collection<?>) parameterValue;
				for (Object object : stubParameterCollection) {
					if (MessageUtil.isStubParameter(object)) {
						registerInterest(objectDeployment, method, i, object, parameterType);
					}
				}
			}
			i++;
		}
	}

	private void registerInterest(ObjectDeployment objectDeployment,
			Method method, int i, Object parameterValue, Class<?> parameterType) {
		StubParameter stubParameter = (StubParameter) parameterValue;
		
		getModule().registerParameterInterest(objectDeployment, method, i, parameterType, 
				stubParameter.getId().getServiceID());
	}
	
	private void updateReferences(ObjectDeployment objectDeployment, Class<?>[] parameterTypes, 
			Object[] parameterValues, List<MessageParameter> parameters) 
		throws InstantiationException, IllegalAccessException, IllegalArgumentException, ClassNotFoundException {

		this.updatedObjects = new HashSet<ObjectReference>();
		
		for (int i = 0; i < parameterValues.length; i++) {
			Object parameterValue = parameterValues[i];
			Class<?> parameterType = parameterTypes[i];

			if (parameterValue == null) {
				continue;
			}
			
			if (MessageUtil.isStubParameter(parameterValue)) { //Stub
				StubParameter stubParameter = (StubParameter) parameterValue;
				
				if (MessageUtil.hasRemoteInterface(parameterType)) {
					Object proxy = createStub(stubParameter, parameterType);
					parameterValues[i] = proxy;
					
				} else {
					//TODO LOG error
				}
				
			} else {
					
				Class<?> parameterValueClass = parameterValue.getClass();
				
				if (ServiceManager.class.isAssignableFrom(parameterValueClass)) {
					parameterValues[i] = objectDeployment.getServiceManager(); 
						
				} else if (List.class.isAssignableFrom(parameterValueClass)) {
					
					List<?> originalCollection = (List<?>) parameterValue;
					List<?> newCollection = 
						(List<?>) updateCollectionReferences(originalCollection, new ArrayList<Object>(), parameterType);
					parameterValues[i] = newCollection;
					parameterTypes[i] = List.class;
					
				} else if (Set.class.isAssignableFrom(parameterValueClass)) {
					
					Set<?> originalCollection = (Set<?>) parameterValue;
					Set<?> newCollection = 
						(Set<?>) updateCollectionReferences(originalCollection, new HashSet<Object>(), parameterType);
					parameterValues[i] = newCollection;
					parameterTypes[i] = Set.class;
					
				} else if (Object[].class.isAssignableFrom(parameterValueClass)) {
					updateArrayReferences((Object[]) parameterValue);
				}
				
			}
		}
	}

	private Object createStub(StubParameter stubParameter, Class<?> parameterType) {

		if (stubParameter.getId() != null) {
			return this.getModule().createStub(stubParameter.getId(), parameterType);
		} 
		
		throw new CommuneRuntimeException("Stub parameter with id null for " + parameterType);
	}

	@SuppressWarnings("unchecked")
	private Collection updateCollectionReferences(Collection originalCollection, Collection newCollection, 
			Class<?> parameterType) 
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, ClassNotFoundException {
		
		if (updatedObjects.contains(new ObjectReference(originalCollection))) {
			return originalCollection;
		}
		
		updatedObjects.add(new ObjectReference(originalCollection));
		
		for (Object object : originalCollection) {
			if (object == null) {
				newCollection.add(null);

			} else if (MessageUtil.isStubParameter(object)) {
				
				StubParameter stubParameter = (StubParameter) object;
				
				if (MessageUtil.hasRemoteInterface(parameterType)) {
					Object proxy = createStub(stubParameter, parameterType);
					newCollection.add(proxy);
					
				} else {
					//TODO LOG error
				}
				
			} else if (Serializable.class.isAssignableFrom(object.getClass())) {
				newCollection.add(object);
			}
			
//TODO		if (object instanceof EventProcessor) {
//				newCollection.add(updateRemoteReference(object));
//				
//			} else {
//				newCollection.add(object);
//				if (Collection.class.isAssignableFrom(object.getClass())) {
//					Collection originalCollection2 = (Collection) object;
//					Collection newCollection2 = originalCollection2.getClass()
//							.newInstance();
//					updateCollectionReferences(originalCollection2,
//							newCollection2);
//					originalCollection2.clear();
//					originalCollection2.addAll(newCollection2);
//				} else if (Object[].class.isAssignableFrom(object.getClass())) {
//					updateArrayEPReferences((Object[]) object);
//				} else {
//					// It is an object and may contain a field with EPs or Stubs
//					updateFieldReferences(object);
//				}
//
//			}
		}
		
		return newCollection;
	}

	private void updateArrayReferences(Object[] array)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, ClassNotFoundException {
		
		if (updatedObjects.contains(new ObjectReference(array))) {
			return;
		}
		
		updatedObjects.add(new ObjectReference(array));
		
		for (int i = 0; i < array.length; i++) {
			Object o = array[i];
			if (o == null) {
				continue;
			}
//TODO		if (o instanceof EventProcessor) {
//				array[i] = updateRemoteReference(o);
//			} else {
//				if (Collection.class.isAssignableFrom(o.getClass())) {
//					Collection originalCollection = (Collection) o;
//					Collection newCollection = (Collection) o.getClass()
//							.newInstance();
//					updateCollectionReferences(originalCollection,
//							newCollection);
//					originalCollection.clear();
//					originalCollection.addAll(newCollection);
//				} else if (Object[].class.isAssignableFrom(o.getClass())) {
//					updateArrayReferences((Object[]) o);
//				} else {
//					// It is an object and may contain a field with EPs or Stubs
//					updateFieldReferences(o);
//				}
//			}
		}
	}
	

	static class ObjectReference {
		
		private Object o;

		public ObjectReference(Object o) {
			this.o = o;
		}
		
		public Object get() {
			return this.o;
		}
		
		@Override
		public boolean equals(Object other) {
			if (other instanceof ObjectReference) {
				return ((ObjectReference) other).get() == this.o;
			}
			return false; 
		}
		
		@Override
		public int hashCode() {
			return this.o.hashCode();
		}	
	}
	
	public String getThreadName() {
		return "SP";
	}
}