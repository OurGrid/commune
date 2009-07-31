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
package br.edu.ufcg.lsd.commune;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import sun.security.provider.certpath.X509CertPath;
import br.edu.ufcg.lsd.commune.container.Container;
import br.edu.ufcg.lsd.commune.container.ContainerContext;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.control.ApplicationManager;
import br.edu.ufcg.lsd.commune.container.control.ApplicationServerController;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLoggerFactory;
import br.edu.ufcg.lsd.commune.container.servicemanager.FileTransferManager;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.container.servicemanager.actions.RepeatedAction;
import br.edu.ufcg.lsd.commune.container.servicemanager.actions.RepetitionRunnable;
import br.edu.ufcg.lsd.commune.container.servicemanager.dao.ContainerDAO;
import br.edu.ufcg.lsd.commune.container.servicemanager.dao.DAO;
import br.edu.ufcg.lsd.commune.container.servicemanager.dao.DAOCache;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.network.certification.providers.CertificationDataProvider;
import br.edu.ufcg.lsd.commune.network.certification.providers.CertificationDataProviderFactory;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;

public class Application {

	protected Container container;
	private CommuneLogger logger;
	private ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
	
	public static final String CONTROL_OBJECT_NAME = "CONTROL";
	
	private final Map<String, ServiceManager> serviceManagers;
	
	private final Map<String, RepeatedAction> scheduledActionsMap;
	
	private final DAOCache daoCache = new DAOCache();

	private final ContainerDAO containerDAO;
	
	private final X509CertPath myCertPath;
	
	private FileTransferManager fileTransferManager;
	
	public Application(String containerName, ContainerContext context) 
			throws CommuneNetworkException, ProcessorStartException {
		
		/*Load my certificate*/
		this.myCertPath = this.loadCertificate(context);
		
		/* Main container */		
		this.container = createContainer(containerName, context);
		this.container.start();
		
		/* Service managers */
		this.serviceManagers = new HashMap<String, ServiceManager>();
		
		/* Actions map */
		this.scheduledActionsMap = new HashMap<String, RepeatedAction>();
		
		/* DAO */
		this.containerDAO = createDAO(ContainerDAO.class);
		
		/* ApplicationManager */
		this.createAndDeployApplicationManager();
	}

	private X509CertPath loadCertificate(ContainerContext context) {
		
		CertificationDataProvider certificationDataProvider = 
			new CertificationDataProviderFactory().createCertificationDataProvider(context);
		
		return certificationDataProvider.getMyCertificatePath();
		
	}

	private void createAndDeployApplicationManager() {
		getContainer().deploy(CONTROL_OBJECT_NAME, createApplicationManager());
	}
	
	protected ApplicationManager createApplicationManager() {
		return new ApplicationServerController();
	}
	
	protected ApplicationManager getApplicationManager() {
		return (ApplicationManager) getContainer().getObjectRepository().get(CONTROL_OBJECT_NAME).getObject();
	}
	
	protected Container createContainer(String containerName, ContainerContext context) {
		return new Container(this, containerName, context);
	}

	/**
	 * @return the myCertPath
	 */
	public X509CertPath getMyCertPath() {
		return myCertPath;
	}

	public void stop() throws CommuneNetworkException {
		container.shutdown();
	}
	
	public Container getContainer(){
		return this.container;
	}
	
	/**
	 * @param clazz 
	 * @return the logger
	 */
	public CommuneLogger getLogger(Class<?> clazz) {
		if (logger == null) {
			return CommuneLoggerFactory.getInstance().gimmeALogger(clazz);
		}
		return logger;
	}
	
	public CommuneLogger getLogger() {
		if (logger == null) {
			return null;
		}
		return logger;
	}
	
	/**
	 * @param logger the logger to set
	 */
	public void setLogger(CommuneLogger logger) {
		this.logger = logger;
	}

	/* Actions */
	
	public <T extends Serializable> Future<?> scheduleActionWithFixedDelay(String actionName, 
			long initialDelay, long delay, TimeUnit timeUnit, T handler) {
		ApplicationManager manager = 
			(ApplicationManager) container.getObjectRepository().get(Application.CONTROL_OBJECT_NAME).getProxy();
		RepetitionRunnable runnable = new RepetitionRunnable(container, manager, actionName, handler);
		return getTimer().scheduleWithFixedDelay(runnable, initialDelay, delay, timeUnit);
	}
	
	public <T extends Serializable> Future<?> scheduleActionToRunOnce(String actionName, long delay, TimeUnit timeUnit, T handler) {
		ApplicationManager manager = 
			(ApplicationManager) container.getObjectRepository().get(Application.CONTROL_OBJECT_NAME).getProxy();
		
		RepetitionRunnable runnable = new RepetitionRunnable(container, manager, actionName, handler);
		return getTimer().schedule(runnable, delay, timeUnit);
	}
	
	public void addActionForRepetition(String actionName, RepeatedAction action) {
		scheduledActionsMap.put(actionName, action);
	}
	
	public RepeatedAction getScheduledAction(String actionName) {
		return scheduledActionsMap.get(actionName);
	}
	
	public ServiceManager createServiceManager(ObjectDeployment deployment) {
		ServiceManager serviceManager = new ServiceManager(this, container.getContext(), deployment);
		deployment.setServiceManager(serviceManager);
		this.serviceManagers.put(deployment.getDeploymentID().getServiceName(), serviceManager);
		
		return serviceManager;
	}

	/* Timer */
	
	/**
	 * @return the timer
	 */
	public ScheduledExecutorService getTimer() {
		return timer;
	}

	/**
	 * @param timer the timer to set
	 */
	public void setTimer(ScheduledExecutorService timer) {
		this.timer = timer;
	}
	
	/* DAO */
	public <U extends DAO> U createDAO(Class<U> daoType) {
		return daoCache.createDAO(this, daoType);
	}
	
	public <U extends DAO> U getDAO(Class<U> daoType) {
		return daoCache.getDAO(daoType);
	}
	
	public void resetDAOs() {
		daoCache.reset();
	}

	/**
	 * @return the containerDAO
	 */
	public ContainerDAO getContainerDAO() {
		return containerDAO;
	}
	
	/* ObjectDeployment */
	
	public ObjectDeployment getObject(String serviceName) {
		return container.getObjectRepository().get(serviceName);
	}

	public void createTestStub(Object stub, Class<?> stubClass, DeploymentID deploymentID, boolean setUp) {
		container.getStubRepository().createTestStub(stub, stubClass, deploymentID, setUp);
	}
	
	public FileTransferManager getFileTransferManager() {
		return fileTransferManager;
	}
	
	public void setFileTransferManager(FileTransferManager fileTransferManager) {
		this.fileTransferManager = fileTransferManager;
	}

	public void removeServiceManager(ObjectDeployment deployment) {
		if (deployment == null) {
			return;
		}
		
		DeploymentID deploymentID = deployment.getDeploymentID();
		if (deploymentID != null) {
			this.serviceManagers.remove(deploymentID.getServiceName());
		}
	}
}