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
package br.edu.ufcg.lsd.commune.processor.interest;


import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.InvalidMonitoringException;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.identification.CommuneAddress;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.message.Message;
import br.edu.ufcg.lsd.commune.processor.AbstractProcessor;

public class InterestProcessor extends AbstractProcessor {

	
	public static final String UPDATE_STATUS_MESSAGE = "updateStatus";
	public static final String IS_IT_ALIVE_MESSAGE = "isItAlive";

	
	private InterestManager interestManager;

	
    public InterestProcessor(Module module) {
        super(module);
        this.interestManager = createInterestManager();
    }


	protected InterestManager createInterestManager() {
		return new InterestManager(this);
	}
    

    @Override
	public void shutdown() {
        super.shutdown();
        this.interestManager.shutdown();
    }

	@Override
	public void processMessage(Message message) {

		//TODO validation
		
		msgLogger.trace("Before " + message.toString());

		
		if (IS_IT_ALIVE_MESSAGE.equals(message.getFunctionName())) {
			
			ServiceID targetID = (ServiceID) message.getDestination();
			DeploymentID interestedID = (DeploymentID) message.getSource();
			
			interestManager.isItAlive(targetID, interestedID);
			
		} else if (UPDATE_STATUS_MESSAGE.equals(message.getFunctionName())) {
			
			CommuneAddress targetID = (CommuneAddress) message.getSource();
			MonitorableStatus status = (MonitorableStatus) message.getParameters().get(0).getValue();
			
			interestManager.updateStatus(targetID, status, message.getSenderCertificatePath());
			
		} else {
			//TODO
		}
		
		msgLogger.trace("After " + message.toString());

	}

	public InterestManager getInterestManager() {
		return interestManager;
	}
	
	ObjectDeployment getMonitorDeployment(String monitorServiceName) {
		ObjectDeployment monitorDeployment = getObjectDeployment(monitorServiceName);

		if (monitorDeployment == null) {
			throw new InvalidMonitoringException("The monitor '" + monitorServiceName + "' is not deployed");
		}
		
		return monitorDeployment;
	}
	
	public String getThreadName() {
		return "IP";
	}

}