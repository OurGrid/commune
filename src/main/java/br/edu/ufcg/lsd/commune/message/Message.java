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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sun.security.provider.certpath.X509CertPath;
import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.identification.CommuneAddress;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.InvalidIdentificationException;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.processor.interest.InterestProcessor;
import br.edu.ufcg.lsd.commune.processor.objectdeployer.ServiceProcessor;

@SuppressWarnings("restriction")
public class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5841681088042142970L;
	
	private CommuneAddress source;
	private CommuneAddress destination;
	private String functionName;

	private List<MessageParameter> parameters = new ArrayList<MessageParameter>();

	private String processorType;
	private byte[] signature;
	private Long session;
	private Long sequence;
	
	private X509CertPath senderCertificateChain;
	
	public Message(CommuneAddress source, CommuneAddress destination, String functionName, MessageMetadata metadata, 
			String processorType) {

		if (isNull(source) || isNull(source.getUserName()) || isNull(source.getServerName()) ||
				isNull(source.getContainerName())) {
			
			throw new InvalidIdentificationException(
					"source neither can be empty or null nor can have empty or null container fields: " + source);
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
		
		Class<?>[] types = new Class<?>[parameters.size()];
		for (int i = 0; i < types.length; i++) {
			types[i] = parameters.get(i).getType();
		}
		
		return types;
	}

	public Object[] getParameterValues() {
		
		Object[] values = new Object[parameters.size()];
		for (int i = 0; i < values.length; i++) {
			values[i] = parameters.get(i).getValue();
		}
		
		return values;
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
				
				if (this.parameters.size() == message.getParameterTypes().length) {
					for (int i = 0; i < this.parameters.size(); i++) {
						if (!this.parameters.get(i).getType().getName().equals(message.getParameterTypes()[i].getName())) {
							return false;
						}
					}
					return this.session == message.session 
							&& this.sequence == message.sequence;
				}
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((source == null) ? 0 : source.hashCode());
		result = PRIME * result + ((destination == null) ? 0 : destination.hashCode());
		result = PRIME * result + ((functionName == null) ? 0 : functionName.hashCode());
		result = PRIME * result + getParameterTypes().hashCode();
		result = PRIME * result + ((processorType == null) ? 0 : processorType.hashCode());
		result += session - sequence;
		return result;
	}
	

	public String toString() {
		
		String fromTo = source.getUserName() + "->" + destination.getUserName();
		String conn = ":" + session + "," + sequence;
		String params = toString(Arrays.asList(getParameterValues()));
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
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		ContainerID source = new ContainerID("sourceUser", "SourceServer", "MODULE");
		source.setPublicKey("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC7qdm+PmfEQ0Of28aYHZpnphtRTBs+hrP1j813GpgE659DWdNGe/WZjFStFFt/rk8j5hg2tms6flI4iFq3txytcJzSokB0yD491+VFYZv7C9QjzjLaALJHf5bLzcICxEDXhHLbllKTV2Nlfb2pr5wRD3Aypgu45k2gq05tcwdyGwIDAQAB");
		DeploymentID sourceDID = new DeploymentID(new ServiceID(source, "SERVICE"), 109321023710293L); 
		
		System.out.println(sourceDID);
		
		ContainerID target = new ContainerID("sourceUser", "SourceServer", "MODULE");
		target.setPublicKey("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC7qdm+PmfEQ0Of28aYHZpnphtRTBs+hrP1j813GpgE659DWdNGe/WZjFStFFt/rk8j5hg2tms6flI4iFq3txytcJzSokB0yD491+VFYZv7C9QjzjLaALJHf5bLzcICxEDXhHLbllKTV2Nlfb2pr5wRD3Aypgu45k2gq05tcwdyGwIDAQAB");
		DeploymentID targetDID = new DeploymentID(new ServiceID(target, "SERVICE"), 109321023710293L); 
		
//		Message message = new Message(source, target, "method");
		
		String processorType = InterestProcessor.class.getName();
		Message m = new Message(sourceDID, targetDID, InterestProcessor.IS_IT_ALIVE_MESSAGE, processorType);
		m.setSession(0L);
		m.setSequence(0L);
		m.addParameter(int.class, 1234);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(m);
		System.out.println(baos.toByteArray().length);
		byte[] bytes = JsonMessageUtil.toBytes(m);
		System.out.println(bytes.length);
		
		Message parse = JsonMessageUtil.parse(bytes);
		
	}
}