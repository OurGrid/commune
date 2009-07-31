package br.edu.ufcg.lsd.commune.functionaltests.monitor.util;

import java.util.Collection;
import java.util.Map;

import org.easymock.EasyMock;

import br.edu.ufcg.lsd.commune.Application;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.MonitorAcceptanceUtil;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.matchers.DeployedObjectsMatcher;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.monitor.CommuneMonitor;
import br.edu.ufcg.lsd.commune.monitor.CommuneMonitorClient;

public class TestGetDeployedObjectsUtil extends MonitorAcceptanceUtil {
	
	public void getDeployedObjects(Application application, Map<DeploymentID, Collection<Class<?>>> deployedObjects) {
		CommuneMonitor monitor = getMonitorController(application);
		
		CommuneMonitorClient client = EasyMock.createMock(CommuneMonitorClient.class);
		
		client.hereAreDeployedObjects(DeployedObjectsMatcher.eqMatcher(deployedObjects));
		
		EasyMock.replay(client);
		monitor.getDeployedObjects(client);
		EasyMock.verify(client);
	}
}
