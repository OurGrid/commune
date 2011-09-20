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
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import sun.security.provider.certpath.X509CertPath;
import br.edu.ufcg.lsd.commune.container.ExecutionContext;
import br.edu.ufcg.lsd.commune.container.IMessageDeliverer;
import br.edu.ufcg.lsd.commune.container.IMessageSender;
import br.edu.ufcg.lsd.commune.container.InvalidDeploymentException;
import br.edu.ufcg.lsd.commune.container.InvokeOnDeployHelper;
import br.edu.ufcg.lsd.commune.container.MessageDeliverer;
import br.edu.ufcg.lsd.commune.container.MonitoredByHelper;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.ObjectRepository;
import br.edu.ufcg.lsd.commune.container.ProxyUtil;
import br.edu.ufcg.lsd.commune.container.ReceiverHelper;
import br.edu.ufcg.lsd.commune.container.StubReference;
import br.edu.ufcg.lsd.commune.container.StubRepository;
import br.edu.ufcg.lsd.commune.container.control.ModuleManager;
import br.edu.ufcg.lsd.commune.container.control.ServerModuleController;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLoggerFactory;
import br.edu.ufcg.lsd.commune.container.servicemanager.FileTransferManager;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.container.servicemanager.actions.RepeatedAction;
import br.edu.ufcg.lsd.commune.container.servicemanager.actions.RepetitionRunnable;
import br.edu.ufcg.lsd.commune.container.servicemanager.dao.ContainerDAO;
import br.edu.ufcg.lsd.commune.container.servicemanager.dao.DAO;
import br.edu.ufcg.lsd.commune.container.servicemanager.dao.DAOCache;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.CommuneAddress;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.InvalidIdentificationException;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.message.Message;
import br.edu.ufcg.lsd.commune.message.MessageUtil;
import br.edu.ufcg.lsd.commune.network.CommuneNetwork;
import br.edu.ufcg.lsd.commune.network.ConnectionListener;
import br.edu.ufcg.lsd.commune.network.NetworkBuilder;
import br.edu.ufcg.lsd.commune.network.ProtocolCreationListener;
import br.edu.ufcg.lsd.commune.network.certification.providers.CertificationDataProvider;
import br.edu.ufcg.lsd.commune.network.certification.providers.CertificationDataProviderFactory;
import br.edu.ufcg.lsd.commune.network.signature.SignatureProperties;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.network.xmpp.XMPPProperties;
import br.edu.ufcg.lsd.commune.processor.MessageProcessor;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;
import br.edu.ufcg.lsd.commune.processor.filetransfer.FileTransferProcessor;
import br.edu.ufcg.lsd.commune.processor.filetransfer.IncomingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferManager;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferReceiver;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferSender;
import br.edu.ufcg.lsd.commune.processor.interest.InterestManager;
import br.edu.ufcg.lsd.commune.processor.interest.InterestProcessor;
import br.edu.ufcg.lsd.commune.processor.interest.InterestRequirements;
import br.edu.ufcg.lsd.commune.processor.objectdeployer.ServiceProcessor;

public class Module {

	
	public static final String CONTROL_OBJECT_NAME = "CONTROL";

	
	private CommuneLogger logger;
	private ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
	private Map<String, ServiceManager> serviceManagers;
	private Map<String, RepeatedAction> scheduledActionsMap;
	private final DAOCache daoCache = new DAOCache();
	private ContainerDAO containerDAO;
	private X509CertPath myCertPath;
	private FileTransferManager fileTransferManager;

	private ContainerID containerID;
	private ModuleContext context;
	private boolean isShutdown = false;
	private boolean isStarted = false;

	//Processors 
	protected ServiceProcessor service;
	protected InterestProcessor interest;
	protected FileTransferProcessor fileTransfer;

	//Communication 
    protected CommuneNetwork communeNetwork;
    protected NetworkBuilder networkBuilder = createNetworkBuilder();
    protected IMessageDeliverer messageDeliverer;
    protected IMessageSender messageSender;
    
    //Objects
    protected ObjectRepository objectRepository;
    protected StubRepository stubRepository = new StubRepository(this);
    private ExecutionContext executionContext;
    private ReadWriteLock executionContextLock = new ReentrantReadWriteLock(true);

    //Helpers
    private InvokeOnDeployHelper invokeOnDeployHelper;
    private MonitoredByHelper monitoredByHelper;
    private ReceiverHelper receiverHelper;

    private ConnectionListener connectionListener;
	
    public Module(String containerName, ModuleContext context, ConnectionListener listener) 
		throws CommuneNetworkException, ProcessorStartException{
		
    	this.connectionListener = listener;
    	init(containerName, context);
		
	}
	
	public Module(String containerName, ModuleContext context) 
			throws CommuneNetworkException, ProcessorStartException {
		
		init(containerName, context);
	}


	private void init(String containerName, ModuleContext context)
			throws ProcessorStartException, CommuneNetworkException {
		/*Load my certificate*/
		this.myCertPath = this.loadCertificate(context);
		
		if (containerName == null) {
    		throw new IllegalArgumentException( "The container name is mandatory" );
    	}

    	if (context == null) {
    		throw new IllegalArgumentException( "The container context is mandatory" );
    	}

    	validateContainerName(containerName);
    	
    	this.invokeOnDeployHelper = new InvokeOnDeployHelper(this);
    	this.monitoredByHelper = new MonitoredByHelper(this);
    	this.receiverHelper = new ReceiverHelper(this);
    	
    	this.context = context;
    	this.containerID = createContainerID(containerName);
    	this.objectRepository = new ObjectRepository(this);

    	moduleCreated();
    	
    	this.communeNetwork = networkBuilder.build(this);
        
    	this.service = createServiceProcessor();
        this.interest = createInterestProcessor();
        this.fileTransfer = createFileTransferProcessor();
        Map<String,MessageProcessor> processors = new HashMap<String,MessageProcessor>();
        processors.put(ServiceProcessor.class.getName(), service);
        processors.put(InterestProcessor.class.getName(), interest);
        processors.put(FileTransferProcessor.class.getName(), fileTransfer);
        messageDeliverer = createMessageDeliverer(processors);
        
        this.messageSender = this.communeNetwork;
        
        networkBuilder.configure(this);
		
        initComponents();
        
		/* Service managers */
		this.serviceManagers = new HashMap<String, ServiceManager>();
		
		/* Actions map */
		this.scheduledActionsMap = new HashMap<String, RepeatedAction>();
		
		/* DAO */
		this.containerDAO = createDAO(ContainerDAO.class);
	}

	protected void moduleCreated() {
		// TODO Auto-generated method stub
		
	}

	protected void setConnectionListener(ConnectionListener connectionListener) {
		this.connectionListener = connectionListener;
	}

	private void createServices() {

		/* ApplicationManager */
		this.createAndDeployApplicationManager();
	}

	private X509CertPath loadCertificate(ModuleContext context) {
		
		CertificationDataProvider certificationDataProvider = 
			new CertificationDataProviderFactory().createCertificationDataProvider(context);
		
		return certificationDataProvider.getMyCertificatePath();
	}
	
	public NetworkBuilder createNetworkBuilder() {
        return new NetworkBuilder();
    }


	private void createAndDeployApplicationManager() {
		deploy(CONTROL_OBJECT_NAME, createApplicationManager());
	}
	
	protected ModuleManager createApplicationManager() {
		return new ServerModuleController();
	}
	
	protected ModuleManager getApplicationManager() {
		return (ModuleManager) getObjectRepository().get(CONTROL_OBJECT_NAME).getObject();
	}
	
	/**
	 * @return the myCertPath
	 */
	public X509CertPath getMyCertPath() {
		return myCertPath;
	}

	public void stop() throws CommuneNetworkException {
		shutdown();
	}
	
	/**
	 * @param clazz 
	 * @return the logger
	 */
	public CommuneLogger getLogger(Class<?> clazz) {
		
		String containerId = containerID.toString();
		
		if (logger == null) {
			return CommuneLoggerFactory.getInstance().gimmeALogger(clazz, containerId);
		}
		return logger;
	}
	
	/**
	 * @param category 
	 * @param userInfo
	 * @return the logger
	 */
	public CommuneLogger getLogger(String category) {
		
		String containerId = containerID.toString();
		
		if (logger == null) {
			return CommuneLoggerFactory.getInstance().gimmeALogger(category, containerId);
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
		ModuleManager manager = 
			(ModuleManager) getObjectRepository().get(Module.CONTROL_OBJECT_NAME).getProxy();
		RepetitionRunnable runnable = new RepetitionRunnable(this, manager, actionName, handler);
		return getTimer().scheduleWithFixedDelay(runnable, initialDelay, delay, timeUnit);
	}
	
	public <T extends Serializable> Future<?> scheduleActionToRunOnce(String actionName, long delay, TimeUnit timeUnit, T handler) {
		ModuleManager manager = 
			(ModuleManager) getObjectRepository().get(Module.CONTROL_OBJECT_NAME).getProxy();
		
		RepetitionRunnable runnable = new RepetitionRunnable(this, manager, actionName, handler);
		return getTimer().schedule(runnable, delay, timeUnit);
	}
	
	public void addActionForRepetition(String actionName, RepeatedAction action) {
		scheduledActionsMap.put(actionName, action);
	}
	
	public RepeatedAction getScheduledAction(String actionName) {
		return scheduledActionsMap.get(actionName);
	}
	
	public ServiceManager createServiceManager(ObjectDeployment deployment) {
		ServiceManager serviceManager = new ServiceManager(this, getContext(), deployment);
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
		return getObjectRepository().get(serviceName);
	}

	public void createTestStub(Object stub, Class<?> stubClass, DeploymentID deploymentID, boolean setUp) {
		getStubRepository().createTestStub(stub, stubClass, deploymentID, setUp);
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
	
	protected InterestProcessor createInterestProcessor() {
		return new InterestProcessor(this);
	}

	protected ServiceProcessor createServiceProcessor() {
		return new ServiceProcessor(this);
	}

	protected FileTransferProcessor createFileTransferProcessor() {
		return new FileTransferProcessor(this);
	}

	protected IMessageDeliverer createMessageDeliverer(Map<String, MessageProcessor> processors) {
		return new MessageDeliverer(processors);
	}
    
    private ContainerID createContainerID(String containerName) {
    	String userName = context.getProperty(XMPPProperties.PROP_USERNAME);
		String serverName = context.getProperty(XMPPProperties.PROP_XMPP_SERVERNAME);
    	String publicKey = context.getProperty(SignatureProperties.PROP_PUBLIC_KEY);
    	return new ContainerID(userName, serverName, containerName, publicKey);
    }

    public void deploy(String serviceName, Object object, long deploymentNumber) throws InvalidDeploymentException {
    	validateControl(serviceName, object);
    	verifyBeforeDeploy(serviceName, object);        
        
    	try {
            DeploymentID objectID = new DeploymentID(getContainerID(), serviceName, deploymentNumber);
            this.deploy(objectID, object);
        } catch (InvalidIdentificationException exception) {
        	throw new InvalidDeploymentException("Unexpected error. Invalid identification: Container: " + 
            		this.containerID.toString() + "\t service name: " + serviceName + "\t deployment number: " + 
            		deploymentNumber, 
            		exception);
        }
    }

    public void deploy(String serviceName, Object object) throws InvalidDeploymentException {
    	validateControl(serviceName, object);
    	verifyBeforeDeploy(serviceName, object);        
        
    	try {
            DeploymentID objectID = new DeploymentID(getContainerID(), serviceName);
            this.deploy(objectID, object);
        } catch (InvalidIdentificationException exception) {
        	throw new InvalidDeploymentException("Unexpected error. Invalid identification: Container: " + 
            		this.containerID.toString() + "\t service name: " + serviceName, 
            		exception);
        }
    }
    
    protected void connectionCreated(){
    	
    }
    
	private void validateControl(String controlService, Object controlObject) {
		if (controlService == null) {
    		throw new IllegalArgumentException( "The control service name is mandatory" );
    	}

    	if (controlObject == null) {
    		throw new IllegalArgumentException( "The control service object is mandatory" );
    	}
    	
	}

    private void verifyBeforeDeploy(String serviceName, Object object) {
    	verifyStarted();
    	verifyShutdown();

    	if (serviceName == null || "".equals(serviceName.trim())) {
        	throw new InvalidDeploymentException("Service name cannot be null or empty.");
        }
        
        if (this.objectRepository.get( serviceName ) != null ) {
        	throw new InvalidDeploymentException("There is another object already bound with this same service id: " + serviceName );
        }
        
        DeploymentID deploymentID = this.objectRepository.getDeploymentID(object);
        if (deploymentID != null) {
        	throw new InvalidDeploymentException( "This object already bound with another name" );
        }
    }

    private void deploy(DeploymentID deploymentID, Object object) {
    	ObjectDeployment deployment = new ObjectDeployment(this, deploymentID, object);
    	deploymentID.setPublicKey(getContainerID().getPublicKey());

        this.objectRepository.addObject(deployment);
        
        List<Class<?>> remoteInterfaces = MessageUtil.getRemoteInterfaces(object.getClass());
        
        if (!remoteInterfaces.isEmpty()) {
        	
        	synchronized (this) {
        		
        		Class<?>[] types = new Class<?>[remoteInterfaces.size()];
        		
        		for (int i = 0; i < remoteInterfaces.size(); i++) {
        			types[i] = remoteInterfaces.get(i);
        		}
        		
        		Object proxy = 
        			ProxyUtil.createProxy(this, deploymentID.getServiceID(), types[0].getClassLoader(), types); 
        		
        		deployment.setProxy(proxy);
			}
        }
        
        createServiceManager(deployment);

        try {
        	invokeOnDeployHelper.process(deployment);
        	monitoredByHelper.process(deployment);
        	receiverHelper.process(deployment);
        	
        } catch (CommuneRuntimeException exception) {
        	this.objectRepository.removeObject(deployment);
        	throw exception;
        }
        
    }

	public synchronized void undeploy(String serviceName) {
    	verifyBeforeUndeploy(serviceName);
    	
    	ObjectDeployment deployment = this.objectRepository.get(serviceName);
    	removeServiceManager(deployment);
    	this.objectRepository.removeObject(serviceName);
    }

    private void verifyBeforeUndeploy(String serviceName) {
    	verifyStarted();
    	verifyShutdown();

    	if (serviceName == null || "".equals(serviceName.trim())) {
        	throw new InvalidDeploymentException("Service name cannot be null or empty.");
        }
        
        if (this.objectRepository.get( serviceName ) == null ) {
        	//throw new InvalidDeploymentException("There is not object bound with this service id: " + serviceName );
        }
    }

	public DeploymentID isBound( ServiceID identification ) {		
    	verifyShutdown();
    	ObjectDeployment od = objectRepository.get(identification.getServiceName());
		if (od != null) {
			return od.getDeploymentID();
		}
		return null;
	}


    public void initComponents() throws ProcessorStartException, CommuneNetworkException {
    	verifyShutdown();
    	this.communeNetwork.addProtocolChainStartedListener(new ProtocolCreationListener() {
			
			public void started() {
		        service.start();
		        interest.start();        
		        fileTransfer.start();
		        isStarted = true;
		        createServices();
		        connectionCreated();
				
			}
		});
    	this.communeNetwork.start();
    }

    public void shutdown() throws CommuneNetworkException {
//    	verifyStarted();
    	//verifyShutdown();
    	if(!isStarted){
    		return;
    	}
    	
    	isShutdown = true;
    	this.communeNetwork.shutdown();
    	this.fileTransfer.shutdown();
    	this.interest.shutdown();
    	this.service.shutdown();
    	
    	this.objectRepository.removeAll();
    	this.stubRepository.removeAll();
    }
    
    public ServiceProcessor getServiceProcessor() {
    	return service;
    }
    
    public MessageProcessor getFileTransferProcessor() {
    	return fileTransfer;
    }
    
    public MessageProcessor getInterestProcessor() {
    	return interest;
    }

    private void validateContainerName(String containerName) throws IllegalArgumentException {
    	if (containerName == null) {
    		throw new NullPointerException("Container name cannot be null.");
    	}
    	if ("".equals(containerName.trim()) || (containerName.indexOf('/') != -1)) {
    		throw new IllegalArgumentException("Invalid container name: " + containerName);
    	}
    }

    public ContainerID getContainerID() {
        return containerID;
    }

    private void verifyShutdown() {
    	if (isShutdown) {
    		throw new IllegalStateException("Module is shutdown.");
    	}
    }
    
    private void verifyStarted() {
    	if (!isStarted) {
    		throw new IllegalStateException("Module is not started.");
    	}
    }

	public ModuleContext getContext() {
		return this.context;
	}

	public ObjectRepository getObjectRepository() {
		return this.objectRepository;
	}
	
	public void deliverMessage(Message message) {
		messageDeliverer.deliverMessage(message);
	}

	public void sendMessage(Message message) {
		this.messageSender.sendMessage(message);
	}
	
	public IMessageSender getMessageSender() {
		return this.messageSender;
	}

	public ExecutionContext getExecutionContext() {
		try {
			executionContextLock.readLock().lock();
			
			return executionContext;
			
		} finally {
			executionContextLock.readLock().unlock();
		}
	}

	public void setExecutionContext(ObjectDeployment runningObject, 
			CommuneAddress currentConsumer, X509CertPath consumerCertPath) {
		try {
			executionContextLock.writeLock().lock();
			
			this.executionContext = new ExecutionContext(runningObject, 
					currentConsumer, consumerCertPath);
			
		} finally {
			executionContextLock.writeLock().unlock();
		}
	}

	public InterestManager getInterestManager() {
		return this.interest.getInterestManager();
	}
	
	public <T> T registerInterest(String monitorName, Class<T> monitorableType, ServiceID serviceID, 
			InterestRequirements requirements) {
		
		T stub = createStub(serviceID, monitorableType);
		
		getInterestManager().registerInterest(monitorName, monitorableType, serviceID, requirements);
		
		return stub;
	}

	public void registerParameterInterest(ObjectDeployment objectDeployment, Method method, int parameterIndex, 
			Class<?> parameterType, ServiceID stubServiceID) {
		getInterestManager().registerParameterInterest(objectDeployment, method, parameterIndex, parameterType, 
				stubServiceID);
	}
	
	public DeploymentID getLocalObjectDeploymentID(Object object) {
		return this.objectRepository.getDeploymentID(object);
	}

	
	//Transfer
	
	public TransferManager getTransferManager() {
		return this.fileTransfer.getTransferManager();
	}
	
	private Message createFileTransferMessage(String functionName) {
		return new Message(getContainerID(), getContainerID(), functionName, FileTransferProcessor.class.getName());
	}
	
	public void acceptTransfer(IncomingTransferHandle handle, TransferReceiver frl) {
		DeploymentID deploymentID = this.objectRepository.getDeploymentID(frl);

		Message message = createFileTransferMessage(FileTransferProcessor.ACCEPT_TRANSFER);
		message.addParameter(IncomingTransferHandle.class, handle);
		message.addStubParameter(TransferReceiver.class, deploymentID);
		
		sendMessage(message);
	}

	public void rejectTransfer(IncomingTransferHandle handle) {
		Message message = createFileTransferMessage(FileTransferProcessor.REJECT_TRANSFER);
		message.addParameter(IncomingTransferHandle.class, handle);

		sendMessage(message);
	}

	public void startTransfer(OutgoingTransferHandle handle, TransferSender fsl) {
		
		DeploymentID deploymentID = this.objectRepository.getDeploymentID(fsl);
		
		Message message = createFileTransferMessage(FileTransferProcessor.START_TRANSFER);
		message.addParameter(OutgoingTransferHandle.class, handle);
		message.addStubParameter(TransferSender.class, deploymentID);

		sendMessage(message);
	}

	public void cancelIncomingTransfer(IncomingTransferHandle handle) {
		Message message = createFileTransferMessage(FileTransferProcessor.CANCEL_INCOMING_TRANSFER);
		message.addParameter(IncomingTransferHandle.class, handle);

		sendMessage(message);
	}

	public void cancelOutgoingTransfer(OutgoingTransferHandle handle) {
		Message message = createFileTransferMessage(FileTransferProcessor.CANCEL_OUTGOING_TRANSFER);
		message.addParameter(OutgoingTransferHandle.class, handle);

		sendMessage(message);
	}
	
	
	//Stubs
	
	@SuppressWarnings("unchecked")
	public <T> T createStub(ServiceID identification, Class<T> interfaceType) throws IllegalArgumentException {
		return (T) this.stubRepository.createStub(identification, interfaceType).getProxy(interfaceType);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T createStub(DeploymentID deploymentID, Class<T> interfaceType) {
		return (T) this.stubRepository.createStub(deploymentID, interfaceType).getProxy(interfaceType);
	}
	
	public DeploymentID getStubDeploymentID(Object object) {
		return this.stubRepository.getStubDeploymentID(object);
	}

	public DeploymentID getStubDeploymentID(ServiceID serviceID) {
		return this.stubRepository.getStubDeploymentID(serviceID);
	}

	public ServiceID getStubServiceID(Object object) {
		return this.stubRepository.getStubServiceID(object);
	}

	public synchronized void releaseStub(Object stub) {
		getInterestManager().removeInterest(stub);
		this.stubRepository.removeStub(stub);
	}

	public void release(ServiceID serviceID) {
		getInterestManager().removeInterest(serviceID);
		getStubRepository().removeStub(serviceID);
	}
	
	public boolean isStubUp(ServiceID stubServiceID) {
		return this.stubRepository.isStubUp(stubServiceID);
	}

	public void setStubDown(Object stub) {
		this.stubRepository.setStubDown(stub);
	}

	public void setStubDeploymentID(DeploymentID targetDeploymentID) {
		this.stubRepository.setStubDeploymentID(targetDeploymentID);
	}
	
	public StubRepository getStubRepository() {
		return stubRepository;
	}

	public Object getStub(ServiceID serviceID, Class<?> clazz) {
		StubReference stubReference = stubRepository.getStub(serviceID);
		if(stubReference != null){
			return stubReference.getProxy(clazz);
		}
		return null;
	}
	
	public boolean isLocal(CommuneAddress address) {
		return containerID.equals(address.getContainerID());
	}


	public ConnectionListener getConnectionListener() {
		return connectionListener;
	}

}