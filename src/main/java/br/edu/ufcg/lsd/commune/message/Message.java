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
package br.edu.ufcg.lsd.commune.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sun.security.provider.certpath.X509CertPath;
import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.identification.CommuneAddress;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.InvalidIdentificationException;
import br.edu.ufcg.lsd.commune.processor.objectdeployer.ServiceProcessor;

public class Message implements Serializable {

	private static final long serialVersionUID = 1L;

	private CommuneAddress source;
	private CommuneAddress destination;
	private String functionName;

	private List<Class<?>> parameterTypes;
	private List<Object> parameterValues;
	private List<MessageParameter> parameters = new ArrayList<MessageParameter>();

	private String processorType;
	private byte[] signature;
	private Long session;
	private Long sequence;
	
	private X509CertPath senderCertificateChain;
	
	private MessageMetadata metadata;

	
	public Message(CommuneAddress source, CommuneAddress destination, String functionName, MessageMetadata metadata, 
			String processorType) {

		if (isNull(source) || isNull(source.getUserName()) || isNull(source.getServerName()) ||
				isNull(source.getContainerName())) {
			
			throw new InvalidIdentificationException(
					"source neither can be empty or null nor can have empty or null container fields: " + source);
		}
		
		if (source.getPublicKey() == null || "".equals(source.getPublicKey())) {
			throw new InvalidIdentificationException(
					"Every message source must have a public key set: " + source);
		}
		
		this.source = source;

		if (isNull(functionName)) {
			throw new CommuneRuntimeException("functionName cannot be empty or null.");
		}
		this.functionName = functionName;

		if (isNull(destination) || isNull(destination.getUserName()) || isNull(destination.getServerName()) ||
				isNull(destination.getContainerName())) {
			
			System.out.println(toString());
			
			throw new InvalidIdentificationException(
					"destination neither can be empty or null nor can have empty or null container fields: " + 
					destination);
		}
		this.destination = destination;

		this.parameterTypes = new ArrayList<Class<?>>();
		this.parameterValues = new ArrayList<Object>();
		this.metadata = (metadata == null ? new MessageMetadata() : metadata);
		this.processorType = processorType;
	}
	
	public Message(CommuneAddress source, CommuneAddress destination, String functionName, MessageMetadata metadata) {
		this(source, destination, functionName, metadata, ServiceProcessor.class.getName());
		
//		if (isNull(source.getServiceName()) || isNull(source.getDeploymentNumber())) {
//			throw new InvalidIdentificationException(
//					"source can not have empty or null fields: " + source);
//		}
//
//		if (isNull(destination.getServiceName()) || isNull(destination.getDeploymentNumber())) {
//			throw new InvalidIdentificationException(
//					"source can not have empty or null fields: " + source);
//		}
	}

	public Message(CommuneAddress source, CommuneAddress destination, String functionName, String processorType) {
		this(source, destination, functionName, new MessageMetadata(), processorType);
	}

	public Message(CommuneAddress source, CommuneAddress destination, String functionName) {
		this(source, destination, functionName, new MessageMetadata());
	}

	private static boolean isNull(Object object) {
		return object == null || object.toString() == null || object.toString().equals("");
	}
	
	public void addParameter(Class<?> clazz, Object value) {
		addMessageParameter(new MessageParameter(clazz, value));
	}
	
	public void addStubParameter(Class<?> clazz, DeploymentID deploymentID) {
		StubParameter stubParameter = new StubParameter(deploymentID);
		addMessageParameter(new MessageParameter(clazz, stubParameter));
	}
	
	public void addStubList(Class<?> clazz, List<DeploymentID> deploymentIDs) {
		List<StubParameter> list = new ArrayList<StubParameter>();
		convertToStubParameter(deploymentIDs, list);
		addMessageParameter(new MessageParameter(clazz, list));
	}

	public void addStubSet(Class<?> clazz, Set<DeploymentID> deploymentIDs) {
		Set<StubParameter> set = new HashSet<StubParameter>();
		convertToStubParameter(deploymentIDs, set);
		addMessageParameter(new MessageParameter(clazz, set));
	}
	
	public void addStubMap(Class<?> clazz, Map<Serializable, DeploymentID> deploymentIDs) {
		Map<Serializable, StubParameter> map = new HashMap<Serializable, StubParameter>();
		
		for (Serializable key : deploymentIDs.keySet()) {
			DeploymentID value = deploymentIDs.get(key);
			map.put(key, new StubParameter(value));
		}
		
		addMessageParameter(new MessageParameter(clazz, map));
	}

	private void convertToStubParameter(Collection<DeploymentID> deploymentIDs, Collection<StubParameter> col) {
		for (DeploymentID deploymentID : deploymentIDs) {
			col.add(new StubParameter(deploymentID));
		}
	}

	private void addMessageParameter(MessageParameter parameter) {
		parameters.add(parameter);
		parameterTypes.add(parameter.getType());
		parameterValues.add(parameter.getValue());
	}
	
	public CommuneAddress getSource() {
		return this.source;
	}

	public CommuneAddress getDestination() {
		return this.destination;
	}

	public String getFunctionName() {
		return this.functionName;
	}

	public Class<?>[] getParameterTypes() {
		return MessageUtil.asClassArray(this.parameterTypes);
	}

	public Object[] getParameterValues() {
		return MessageUtil.asArray(this.parameterValues);
	}

	public MessageMetadata getMetadata() {
		return this.metadata;
	}

	public String getProcessorType() {
		return this.processorType;
	}
	
	public byte[] getSignature() {
		return signature;
	}

	public void setSignature(byte[] signature) {
		this.signature = signature;
	}
	
	/**
	 * @param senderCertificate the senderCertificate to set
	 */
	public void setSenderCertificatePath(X509CertPath senderCertificateChain) {
		this.senderCertificateChain = senderCertificateChain;
	}

	/**
	 * @return the senderCertificate
	 */
	public X509CertPath getSenderCertificatePath() {
		return senderCertificateChain;
	}

	@Override
	public boolean equals(Object obj) {

		if ((obj != null) && (obj instanceof Message)) {
			Message message = (Message) obj;
			if (this.source.equals(message.getSource()) && this.destination.equals(message.getDestination()) 
					&& this.functionName.equals(message.getFunctionName())) {
				
				if (this.parameterTypes.size() == message.getParameterTypes().length) {
					for (int i = 0; i < this.parameterTypes.size(); i++) {
						if (!this.parameterTypes.get(i).getName().equals(message.getParameterTypes()[i].getName())) {
							return false;
						}
					}
					return equalsMetadata(message) && this.session == message.session 
							&& this.sequence == message.sequence;
				}
			}
		}
		return false;
	}

	private boolean equalsMetadata(Message message) {
		return (metadata == null ? message.metadata == null : metadata.equals(message.metadata));
	}
	
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((source == null) ? 0 : source.hashCode());
		result = PRIME * result + ((destination == null) ? 0 : destination.hashCode());
		result = PRIME * result + ((metadata == null) ? 0 : metadata.hashCode());
		result = PRIME * result + ((functionName == null) ? 0 : functionName.hashCode());
		result = PRIME * result + parameterTypes.hashCode();
		result = PRIME * result + ((processorType == null) ? 0 : processorType.hashCode());
		result += session - sequence;
		return result;
	}
	

	public String toString() {
		
		String fromTo = source.getUserName() + "->" + destination.getUserName();
		String conn = ":" + session + "," + sequence;
		String params = toString(parameterValues);
		String call = ":" + functionName + "(" + params + ")";
		
		return fromTo + conn + call;
	}

	private String toString(List<Object> parameterValues) {
		String result = "";
		
		if (parameterValues == null) {
			return "";
		}
		
		for (Object object : parameterValues) {
			
			if (object == null) {
				result += "null,";
				
			} else {
				result += object.toString() + ",";
			}
		}
		
		if (result.length() > 0) {
			result = result.substring(0, result.length() - 1);
		}
		
		return result;
	}

	public Long getSession() {
		return session;
	}

	public void setSession(Long session) {
		this.session = session;
	}

	public Long getSequence() {
		return sequence;
	}

	public void setSequence(Long sequence) {
		this.sequence = sequence;
	}
	
	public List<MessageParameter> getParameters() {
		return parameters;
	}
}