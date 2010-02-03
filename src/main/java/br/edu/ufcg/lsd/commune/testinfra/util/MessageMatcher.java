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
package br.edu.ufcg.lsd.commune.testinfra.util;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.CommuneAddress;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.message.Message;
import br.edu.ufcg.lsd.commune.message.MessageParameter;
import br.edu.ufcg.lsd.commune.message.StubParameter;
import br.edu.ufcg.lsd.commune.processor.interest.InterestProcessor;
import br.edu.ufcg.lsd.commune.processor.objectdeployer.ServiceProcessor;

public class MessageMatcher implements IArgumentMatcher {

	private CommuneAddress sourceAddress;
	private CommuneAddress destinationAddress;
	private String functionName;
	private boolean verifyDeploymentNumber;
	private String processorType;
	private List<MessageParameter> parameters;

	
	public MessageMatcher(CommuneAddress sourceAddress, CommuneAddress destinationAddress, String functionName, 
			boolean verifyDeploymentNumber, String processorType, List<MessageParameter> parameters) {
		this.sourceAddress = sourceAddress;
		this.destinationAddress = destinationAddress;
		this.functionName = functionName;
		this.verifyDeploymentNumber = verifyDeploymentNumber;
		this.processorType = processorType;
		this.parameters = parameters;
	}
	

	public boolean matches(Object obj) {
		
		if (obj instanceof Message){
			Message message = (Message)obj;
			
			return verifyAddresses(message.getSource(), this.sourceAddress) &&
				verifyAddresses(message.getDestination(), this.destinationAddress) &&
				verifyString(message.getFunctionName(), this.functionName) &&
				verifyString(message.getProcessorType(), this.processorType) &&
				verifyParameters(message.getParameters(), this.parameters);
			
		} else {
			return false;
		}
	}

	private boolean verifyAddresses(CommuneAddress otherAddress, CommuneAddress myAddress) {
		if (myAddress == null) {
			return true;
		}
		
		if (myAddress instanceof DeploymentID) {
			DeploymentID myDeploymentID = (DeploymentID) myAddress;
			
			if (otherAddress instanceof DeploymentID) {
				DeploymentID otherDeploymentID = (DeploymentID) otherAddress;
				return compareDeploymentIDs(otherDeploymentID, myDeploymentID);
				
			} else {
				return false;
			}
			
		} else {
			return myAddress.equals(otherAddress);
		}
	}

	private boolean verifyString(String otherString, String myString) {
		if (myString == null) {
			return true;
		}
		
		return myString.equals(otherString);
	}
	
	private boolean verifyParameters(List<MessageParameter> otherParameters, List<MessageParameter> myParameters) {
		if (myParameters == null) {
			return true;
		}
		
		if (otherParameters == null) {
			return false;
		}
		
		if (myParameters.size() != otherParameters.size()) {
			return false;
		}
		
		int i = 0;
		for (MessageParameter myParameter : myParameters) {
			
			if (!verifyParameter(otherParameters.get(i++), myParameter)) {
				return false;
			}
		}
		
		return true;
	}
	
	private boolean verifyParameter(MessageParameter otherParameter, MessageParameter myParameter) {
		return nullSafe(otherParameter, myParameter) &&
			nullSafeEquals(otherParameter.getType(), myParameter.getType()) &&
			nullSafeEquals(otherParameter.getValue(), myParameter.getValue());
	}


	private boolean nullSafeEquals(Object other, Object my) {
		if (other == null) {
			return my == null;
			
		} else {
			if (my == null) {
				return false;
			} else {
				
				if (other instanceof DeploymentID && my instanceof DeploymentID) {
					DeploymentID otherID = (DeploymentID) other;
					DeploymentID myID = (DeploymentID) my;
					return compareDeploymentIDs(otherID, myID);
					
				} else if (other instanceof StubParameter && my instanceof StubParameter) {
					StubParameter otherSP = (StubParameter) other;
					StubParameter mySP = (StubParameter) my;
					return compareDeploymentIDs(otherSP.getId(), mySP.getId());
					
				} else if (other instanceof ServiceManager && my instanceof ServiceManager) {
					return true;
				}
				
				return other.equals(my);
			}
		}
	}

	private boolean compareDeploymentIDs(DeploymentID otherID, DeploymentID myID) {
		
		if (verifyDeploymentNumber) {
			return otherID.equals(myID);
		}
		
		return nullSafe(otherID, myID)&& otherID.getServiceID().equals(myID.getServiceID());
	}


	private boolean nullSafe(Object other, Object my) {
		if (other == null) {
			return my == null;
			
		} else {
			if (my == null) {
				return false;
			} else {
				return true;
			}
		}
	}


	public void appendTo(StringBuffer arg0) {
	}

	public static Message eqServiceMessage(CommuneAddress sourceAddress, CommuneAddress destinationAddress, 
			String functionName, MessageParameter... parameters) {
		return reportMatcher(sourceAddress, destinationAddress, functionName, convertToList(parameters), 
				ServiceProcessor.class.getName());
	}

	public static Message eqServiceMessage(CommuneAddress sourceAddress, CommuneAddress destinationAddress, 
			String functionName, List<MessageParameter> parameters) {
		return reportMatcher(sourceAddress, destinationAddress, functionName, parameters, ServiceProcessor.class.getName());
	}
	
	public static Message eqServiceMessage(CommuneAddress sourceAddress, CommuneAddress destinationAddress, 
			String functionName) {
		return reportMatcher(sourceAddress, destinationAddress, functionName, null, ServiceProcessor.class.getName());
	}

	public static Message eqInterestMessage(CommuneAddress sourceAddress, CommuneAddress destinationAddress, 
			String functionName, MessageParameter... parameters) {
		return reportMatcher(sourceAddress, destinationAddress, functionName, convertToList(parameters), 
				InterestProcessor.class.getName());
	}

	private static Message reportMatcher(CommuneAddress sourceAddress, CommuneAddress destinationAddress, 
			String functionName, List<MessageParameter> parameters, String processorType) {
		
		MessageMatcher messageMatcher = 
			new MessageMatcher(sourceAddress, destinationAddress, functionName, false, 
					processorType, parameters);
		EasyMock.reportMatcher(messageMatcher);
		return null;
	}
	
	private static List<MessageParameter> convertToList(MessageParameter[] parameters) {
		List<MessageParameter> list = new ArrayList<MessageParameter>();
		
		for (MessageParameter messageParameter : parameters) {
			list.add(messageParameter);
		}
		
		return list;
	}
}