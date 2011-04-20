package br.edu.ufcg.lsd.commune.testinfra.util;

import br.edu.ufcg.lsd.commune.identification.DeploymentID;

public class TestStub {
	
	private DeploymentID deploymentID;
	private Object object;

	
	public TestStub(DeploymentID deploymentID, Object object) {

		this.deploymentID = deploymentID;
		this.object = object;
	}

	public DeploymentID getDeploymentID() {
		return deploymentID;
	}
	
	public Object getObject() {
		return object;
	}
}
