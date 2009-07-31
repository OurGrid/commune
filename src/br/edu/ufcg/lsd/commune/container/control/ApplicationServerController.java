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
package br.edu.ufcg.lsd.commune.container.control;

import br.edu.ufcg.lsd.commune.Application;
import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.MonitoredBy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.servicemanager.dao.ContainerDAO;
import br.edu.ufcg.lsd.commune.monitor.CommuneMonitorController;
import br.edu.ufcg.lsd.commune.monitor.MonitorConstants;
import br.edu.ufcg.lsd.commune.monitor.MonitorProperties;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;

public class ApplicationServerController extends ApplicationController implements ApplicationServerManager {
	
	public void start(@MonitoredBy(Application.CONTROL_OBJECT_NAME) ApplicationControlClient client) {
		
		if (validateStartSenderPublicKey(client, getServiceManager().getSenderPublicKey()) && canComponentBeStarted(client)) {

			ControlOperationResult result = null;
			
			try {
				if (getServiceManager().getContainerContext().isEnabled(MonitorProperties.PROP_COMMUNE_MONITOR)) {
					getServiceManager().deploy(MonitorConstants.COMMUNE_MONITOR_CONTROLLER, new CommuneMonitorController());
				}
				
				createDAOs();
				createMonitors();
				createServices();

				startComponent();
				
				getServiceManager().getContainerDAO().startContainer();

				result = new ControlOperationResult();
				client.operationSucceed(result);
				
			} catch (Exception e) {
				
				result =  new ControlOperationResult(new ComponentNotStartedException(getComponentName(), e));
				try {
					client.operationSucceed(result);
					killContainer(false, result);
					
					try {
						getServiceManager().getApplication().stop();
					} catch (CommuneNetworkException e1) {
						e1.printStackTrace();
					}
					
					callExit(false, result);
				} catch (CommuneNetworkException e1) {
					result = new ControlOperationResult(result, e1);
				}
				
			}
		
		}
	}

	protected boolean canComponentBeStarted(ApplicationControlClient client) {
		ContainerDAO dao = getServiceManager().getContainerDAO();
		
		ControlOperationResult result = null;
		
		if(dao.isStopped()) {
			result = new ControlOperationResult(new ComponentStoppedException(getComponentName() + " is stopped"));
		}
		else if (dao.isStarted()) {
			ComponentAlreadyStartedException cae = new ComponentAlreadyStartedException();
			result = new ControlOperationResult(cae);
		}
		
		if (result == null) {
			return true;
		}
		
		client.operationSucceed(result);
		return false;
	}
	
	protected void createDAOs() {}
	
	protected void createServices() {}
	
	protected void createMonitors() {}

	protected void startComponent() throws Exception {}
		
	public String getComponentName() {
		return "Component";
	}
	
	public void stop(boolean callExit, boolean force, 
			@MonitoredBy(Application.CONTROL_OBJECT_NAME) ApplicationControlClient client) {
		
		if (validateStopSenderPublicKey(client, getServiceManager().getSenderPublicKey()) && canComponentBeUsed(client)){
			ControlOperationResult result = new ControlOperationResult();
			
			try {
				killContainer(callExit, result);
				getServiceManager().getTimer().shutdownNow();
			} catch (Exception errorCause) {
				result = new ControlOperationResult(new Exception("Unable to stop " + getComponentName(), errorCause));
			}

			client.operationSucceed(result);
			
			try {
				getServiceManager().getApplication().stop();
			} catch (CommuneNetworkException e) {
				e.printStackTrace();
			}
			callExit(callExit, result);
		}
	}

	protected boolean validateStartSenderPublicKey(ApplicationControlClient client, String senderPublicKey) {
		return true;
	}
	
	protected boolean validateStopSenderPublicKey(ApplicationControlClient client, String senderPublicKey) {
		return true;
	}

	/**
	 * @param callExit
	 * @param result
	 * @throws CommuneNetworkException 
	 * @throws HandlerShutdownException
	 */
	private void killContainer(boolean callExit, ControlOperationResult result) throws CommuneNetworkException  {
		getServiceManager().getContainerDAO().stopContainer();
	}

	protected boolean canComponentBeUsed(ApplicationControlClient client) {
		ContainerDAO dao = getServiceManager().getContainerDAO();
		
		ControlOperationResult result = null;

		if(dao.isStopped()){
			result = new ControlOperationResult(new ComponentStoppedException(getComponentName() + " is stopped"));
		}
		else if (!dao.isStarted()) {
			result = new ControlOperationResult(new ComponentNotStartedException(getComponentName()));
		}

		if (result == null) {
			return true;
		}
		
		client.operationSucceed(result);
		return false;
	}
	
	protected boolean canStatusBeUsed() {
		ContainerDAO dao = getServiceManager().getContainerDAO();
		return dao.isStarted() && !dao.isStopped();
	}

	private void callExit(boolean callExit, ControlOperationResult result) {
		if (callExit) {
			if (result.hasAnErrorOcurred()) {
				System.exit(ErrorCode.FAIL);
			} else {
				System.exit(ErrorCode.OK);
			}
		}

	}

	public void getConfiguration(@MonitoredBy(Application.CONTROL_OBJECT_NAME) ApplicationStatusProviderClient client) {
		
		client.hereIsConfiguration( getServiceManager().getContainerContext().getProperties() );
		
	}

	public void getUpTime(@MonitoredBy(Application.CONTROL_OBJECT_NAME) ApplicationStatusProviderClient client) {
		
		client.hereIsUpTime(getServiceManager().getContainerDAO().getUpTime());
		
	}
	
	@RecoveryNotification
	public void controlClientIsUp(ApplicationControlClient client) {
		
	}
	
	@FailureNotification
	public void controlClientIsDown(ApplicationControlClient client) {
		
	}
	
	@RecoveryNotification
	public void statusProviderClientIsUp(ApplicationStatusProviderClient statusProviderClient) {
		
	}
	
	@FailureNotification
	public void statusProviderClientIsDown(ApplicationStatusProviderClient statusProviderClient) {
		
	}
	
}
