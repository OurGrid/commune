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
package br.edu.ufcg.lsd.commune.test;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.security.auth.x500.X500Principal;

import org.easymock.classextension.EasyMock;

import sun.security.provider.certpath.X509CertPath;
import br.edu.ufcg.lsd.commune.Application;
import br.edu.ufcg.lsd.commune.container.ContainerContext;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.identification.CommuneAddress;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.network.signature.SignatureProperties;

public class AcceptanceTestUtil {

	public static String getModuleLocation(String user, String server, String moduleName) {
		ContainerID accessPointID = new ContainerID(user, server, moduleName);
		return accessPointID.toString();
	}


	/* Bind object */
	
	public static boolean isBound(ServiceID serviceID, Class<?> type) {
		Object testObject = TestObjectsRegistry.getTestObject(serviceID);
		
		if (testObject == null) {
			return false;
		}
		
		return type.isInstance(testObject);
	}
	
	public static DeploymentID getBoundObjectID(ServiceID serviceID){
		return TestObjectsRegistry.getTestDeploymentID(serviceID);
	}

	public static Object getBoundObject(DeploymentID deploymentID){
		return TestObjectsRegistry.getTestObject(deploymentID);
	}
	
	public static DeploymentID publishTestObject(Application application, String user, String server, String moduleName, String objName, 
			Object obj, Class<?> stubClass) {
		
		ContainerID apID = new ContainerID(user, server, moduleName);
    	return publishTestObject(application, apID, objName, obj, stubClass);
	}

	public static DeploymentID publishTestObject(Application application, String user, String server, String moduleName, String publicKey, 
			String objName, Object obj, Class<?> stubClass) {
		
		ContainerID apID = new ContainerID(user, server, moduleName, publicKey);
    	return publishTestObject(application, apID, objName, obj, stubClass);
	}
	
	public static void publishTestObject(Application application, DeploymentID deploymentID, Object obj, Class<?> stubClass) {
        publishTestObject(application, deploymentID, obj, stubClass, true);
	}

	public static void publishTestObject(Application application, DeploymentID deploymentID, 
			Object obj, Class<?> stubClass, boolean stubUp) {
        TestObjectsRegistry.publish(obj, deploymentID);
        application.createTestStub(obj, stubClass, deploymentID, stubUp);
	}	
	
	private static DeploymentID publishTestObject(Application application, ContainerID apID, String objName, Object obj, Class<?> stubClass) {
		ServiceID entityID = new ServiceID(apID, objName);
    	DeploymentID objectID = new DeploymentID(entityID);

    	publishTestObject(application, objectID, obj, stubClass);
    	
		return objectID;
	}

	public static void notifyRecovery(Application app, DeploymentID targetID) {
		app.getContainer().getInterestManager().getInterest(
				targetID.getServiceID()).setLastHeartbeat();
		app.getContainer().setStubDeploymentID(targetID);
	}
	

	/* Interest and notification */
	
	public static boolean isInterested(Application application, ServiceID monitorableID, DeploymentID interestedID) {
		return application.getContainer().getInterestManager().isInterested(interestedID, monitorableID);
	}
	
	public static X509CertPath getCertificateMock(String domainName) {
		
		X509CertPath path = EasyMock.createMock(X509CertPath.class);
		X509Certificate x509Cert = EasyMock.createNiceMock(X509Certificate.class);
		X500Principal principal = new X500Principal(domainName);
		
		List<X509Certificate> certs = new ArrayList<X509Certificate>();
		certs.add(x509Cert);
		
		EasyMock.expect(path.getCertificates()).andReturn(certs).anyTimes();
		EasyMock.expect(x509Cert.getSubjectX500Principal()).andReturn(principal).anyTimes();
		
		EasyMock.replay(path);
		EasyMock.replay(x509Cert);
		
		return path;
	}

	public static X509CertPath getCertificateMock(CommuneAddress address) {
		
		return getCertificateMock(getCertificateDN(address));
		
	}
	
	public static String getCertificateDN(CommuneAddress address) {
		return "CN=" + address.getUserName() + ",OU=" + address.getServerName();
	}
	
	/* Key pair */
	
	/**
	 * Get the actual configuration public key
	 * @return
	 */
	public static String getPublicKeyFromConfiguration(ContainerContext context){
		return context.getProperty(SignatureProperties.PROP_PUBLIC_KEY);
	 }
	
	public static void setExecutionContext(Application application, ObjectDeployment runningObject, 
			CommuneAddress currentConsumer) {
		application.getContainer().setExecutionContext(runningObject, currentConsumer, application.getMyCertPath());
	}

	public static void setExecutionContext(Application application, ObjectDeployment runningObject, 
			CommuneAddress currentConsumer, X509CertPath senderCertPath) {
		application.getContainer().setExecutionContext(runningObject, currentConsumer, senderCertPath);
	}
	
	public static void setExecutionContext(Application application, ObjectDeployment runningObject, 
			String publicKey) {
		DeploymentID fakeID = new DeploymentID("a@b/c","d");
		fakeID.setPublicKey(publicKey);
		application.getContainer().setExecutionContext(runningObject, fakeID, application.getMyCertPath());
	}
	
	/* Other */
	public static <U> List<U> createList(U... items) {
		List<U> result = new LinkedList<U>();
		
		for (U item : items) {
			result.add(item);
		}

		return result;
	}
	
	public static <U> Map<U,U> createMap(U... items) {
        Map<U,U> result = new HashMap<U,U>();
        
        for(U item: items){
            result.put(item, item);
        }

        return result;
    }


	@SuppressWarnings("unchecked")
	public static <T> T getStub(Application application, DeploymentID rwpID, Class<T> clazz) {
		return (T) application.getContainer().getStubRepository().getStub(rwpID.getServiceID()).getProxy(clazz);
		
	}
	
}