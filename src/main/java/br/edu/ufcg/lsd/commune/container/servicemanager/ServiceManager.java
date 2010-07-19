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
package br.edu.ufcg.lsd.commune.container.servicemanager;

import java.io.File;
import java.io.Serializable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import sun.security.provider.certpath.X509CertPath;
import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.InvalidMonitoringException;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.container.servicemanager.actions.RepeatedAction;
import br.edu.ufcg.lsd.commune.container.servicemanager.dao.ContainerDAO;
import br.edu.ufcg.lsd.commune.container.servicemanager.dao.DAO;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.CommuneAddress;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.processor.filetransfer.IncomingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferReceiver;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferSender;
import br.edu.ufcg.lsd.commune.processor.interest.InterestRequirements;

public class ServiceManager implements Serializable {

	
	private static final long serialVersionUID = 1L;

	
	private transient final Module application;
	private transient final ModuleContext containerContext;
	private transient final ObjectDeployment objDeployment;
	
	private FileTransferManager fileTransferManager;
	
	
	public ServiceManager(Module application, ModuleContext containerContext, ObjectDeployment deployment) {
		this.application = application;
		this.containerContext = containerContext;
		this.objDeployment = deployment;
		this.fileTransferManager = new FileTransferManager(application);
	}

	protected FileTransferManager getFileTransferManager() {
		if (application.getFileTransferManager() == null) {
			return fileTransferManager;
		}
		
		return application.getFileTransferManager();
	}
	
	public Module getApplication() {
		return application;
	}

	public ModuleContext getContainerContext() {
		return containerContext;
	}

	public void release(Object object) {
		
		if (object == null) {
			throw new CommuneRuntimeException("Can not release a null object");
		}
		
		this.application.releaseStub(object);
	}
	
	public void release(ServiceID serviceID) {
		this.application.release(serviceID);
	}

	public <T> T registerInterest(String monitorName, String monitorableAddress, Class<T> monitorableType, 
			int detectionTime, int heartbeatDelay) {
		
		InterestRequirements requirements = new InterestRequirements(detectionTime, heartbeatDelay);
		return registerInterest(monitorName, monitorableAddress, monitorableType, requirements);
	}

	public <T> T registerInterest(String monitorName, String monitorableAddress, Class<T> monitorableType, 
			String detectionTimeProperty, String heartbeatDelayProperty) {
		
		long detectionTime = 0;
		long heartbeatDelay = 0;
		
		try {
			detectionTime = containerContext.parseIntegerProperty(detectionTimeProperty);
			heartbeatDelay = containerContext.parseIntegerProperty(heartbeatDelayProperty);
			
		} catch (Exception e) {
			throw new CommuneRuntimeException(
					"Invalid interest configuration: detection time(" + detectionTimeProperty + "=" + 
					containerContext.getProperty(detectionTimeProperty) + 
					") heartbeat delay(" + heartbeatDelayProperty + "=" + 
					containerContext.getProperty(heartbeatDelayProperty));
		}
		
		InterestRequirements requirements = new InterestRequirements(detectionTime, heartbeatDelay);
		return registerInterest(monitorName, monitorableAddress, monitorableType, requirements);
	}

	public <T> T registerInterest(String monitorName, String monitorableAddress, Class<T> monitorableType) {
		InterestRequirements requirements = new InterestRequirements(containerContext);
		return registerInterest(monitorName, monitorableAddress, monitorableType, requirements);
	}

	private <T> T registerInterest(String monitorName,
			String monitorableAddress, Class<T> monitorableType,
			InterestRequirements requirements) {
		ServiceID serviceID = null;
		
		if (isID(monitorableAddress)) {
			serviceID = createServiceID(monitorableAddress);
			
		} else if (isInContext(monitorableAddress)) {
			serviceID = createServiceID(containerContext.getProperty(monitorableAddress));
			
		} else {
			throw new InvalidMonitoringException("The monitorable address '" + monitorableAddress + 
					"'is not a valid Service ID neither a valid Service ID property");
		}
		
		return 
			(T)this.application.registerInterest(monitorName, monitorableType, serviceID, requirements);
	}

	private boolean isID(String monitorableAddress) {
		return ServiceID.validate(monitorableAddress);
	}
	
	private boolean isInContext(String addressKey) {
		
		String value = containerContext.getProperty(addressKey);

		if (value == null) {
			return false;
		}
		
		return isID(value);
	}
	
	public ScheduledExecutorService getTimer(){
		return application.getTimer();
	}
	
	private ServiceID createServiceID(String monitorableAddress) {
		return ServiceID.parse(monitorableAddress);
	}
	
	/* Logger */
	public CommuneLogger getLog() {
		
		return application.getLogger(objDeployment.getObject().getClass());
		
	}
	
	/* Actions */
	public <T extends Serializable> Future<?> scheduleActionToRunOnce(String actionName, long delay, TimeUnit timeUnit, T handler) {
		
		return application.scheduleActionToRunOnce(actionName, delay, timeUnit, handler);
	}
	
	public <T extends Serializable> Future<?> scheduleActionWithFixedDelay(String actionName, long delay, TimeUnit timeUnit, T handler) {
		
		return application.scheduleActionWithFixedDelay(actionName, delay, delay, timeUnit, handler);
	}
	
	public <T extends Serializable> Future<?> scheduleActionToRunOnce(String actionName, long delay, TimeUnit timeUnit) {
		
		return this.scheduleActionToRunOnce(actionName, delay, timeUnit, null);
	}
	
	public <T extends Serializable> Future<?> scheduleActionWithFixedDelay(String actionName, long delay, TimeUnit timeUnit) {
		
		return this.scheduleActionWithFixedDelay(actionName, delay, delay, timeUnit, null);
	}
	
	public <T extends Serializable> Future<?> scheduleActionWithFixedDelay(String actionName, 
			long initialDelay, long delay, TimeUnit timeUnit) {
		
		return this.scheduleActionWithFixedDelay(actionName, initialDelay, delay, timeUnit, null);
	}
	
	public <T extends Serializable> Future<?> scheduleActionWithFixedDelay(String actionName, 
			long initialDelay, long delay, TimeUnit timeUnit, T handler) {
		
		return application.scheduleActionWithFixedDelay(actionName, initialDelay, delay, timeUnit, handler);
	}
	
	public void addActionForRepetition(String actionName, RepeatedAction action) {
		application.addActionForRepetition(actionName, action);
	}
	
	/* DAO */
	public <T extends DAO> T getDAO(Class<T> daoType) {
		
		return application.getDAO(daoType);
	}
	
	public <T extends DAO> T createDAO(Class<T> daoType) {
		
		return application.createDAO(daoType);
	}
	
	public ContainerDAO getContainerDAO() {
		
		return application.getContainerDAO();
	}
	
	
	/* ObjectDeployment */
	
	public ObjectDeployment getObjectDeployment(String serviceName) {
		return application.getObject(serviceName);
	}
	
	public DeploymentID getObjectDeploymentID(String serviceName){
		ObjectDeployment or = getObjectDeployment(serviceName);
		
		if(or == null)
			return null;

		return or.getDeploymentID();
	}
	
	public Object getLocalProxy(String serviceName){
		ObjectDeployment or = getObjectDeployment(serviceName);
		
		if(or == null)
			return null;

		return or.getProxy();
	}
	
	public void deploy(String serviceName, Object serviceObject) {
		application.deploy(serviceName, serviceObject);
	}
	
	public void undeploy(String serviceName) {
		application.undeploy(serviceName);
		//TODO remove deployed object, invalidate and remove stub. 
		//If is monitor and has interest, throw exception
	}
	
	public String getSenderPublicKey() {
		return application.getExecutionContext().getCurrentConsumer().getPublicKey();
	}
	
	public X509CertPath getSenderCertPath() {
		return application.getExecutionContext().getSenderCertPath();
	}
	
	public ContainerID getSenderContainerID() {
		CommuneAddress currentConsumer = application.getExecutionContext().getCurrentConsumer();
		return currentConsumer.getContainerID();
	}

	public ServiceID getSenderServiceID() {
		CommuneAddress currentConsumer = application.getExecutionContext().getCurrentConsumer();
		
		ContainerID containerID = getSenderContainerID();
		
		return new ServiceID(containerID, currentConsumer.getServiceName());
	}

	public boolean isThisMyPublicKey(String publicKey) {
		return application.getContainerID().getPublicKey().equals(publicKey);
	}

	public X509CertPath getMyCertPath() {
		return application.getMyCertPath();
	}
	
	public String getMyPublicKey() {
		return application.getContainerID().getPublicKey();
	}
	
	public DeploymentID getStubDeploymentID(Object object) {
		return application.getStubDeploymentID(object);
	}
	
	public DeploymentID getStubDeploymentID(ServiceID serviceID, Class<?> clazz) {
		Object stub = getStub(serviceID, clazz);
		if (stub == null) {
			return null;
		}
		return application.getStubDeploymentID(stub);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getStub(ServiceID serviceID, Class<T> clazz){
		return (T) application.getStub(serviceID, clazz);
	}

	public DeploymentID getLocalObjectID(Object object) {
		return application.getLocalObjectDeploymentID(object);
	}
	
	public DeploymentID getMyDeploymentID() {
		return this.objDeployment.getDeploymentID();
	}
	
	public void acceptTransfer(IncomingTransferHandle handle, TransferReceiver frl, File localFile) {
		this.getFileTransferManager().acceptTransfer(handle, frl, localFile);
	}

	public void rejectTransfer(IncomingTransferHandle handle) {
		this.getFileTransferManager().rejectTransfer(handle);
	}

	public void startTransfer(OutgoingTransferHandle handle, TransferSender fsl) {
		this.getFileTransferManager().startTransfer(handle, fsl);
	}

	public void cancelIncomingTransfer(IncomingTransferHandle handle) {
		this.getFileTransferManager().cancelIncomingTransfer(handle);
	}

	public void cancelOutgoingTransfer(OutgoingTransferHandle handle) {
		this.getFileTransferManager().cancelOutgoingTransfer(handle);
	}

	//TODO verify if this method is needed
	public <T> T createStub(DeploymentID deploymentID, Class<T> type) {
		return (T) this.application.createStub(deploymentID, type);
	}

}