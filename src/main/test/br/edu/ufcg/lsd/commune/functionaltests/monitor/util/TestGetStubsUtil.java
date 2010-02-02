package br.edu.ufcg.lsd.commune.functionaltests.monitor.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.easymock.EasyMock;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.MonitorAcceptanceUtil;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.matchers.StubDataMatcher;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.monitor.CommuneMonitor;
import br.edu.ufcg.lsd.commune.monitor.CommuneMonitorClient;
import br.edu.ufcg.lsd.commune.monitor.data.StubData;

public class TestGetStubsUtil extends MonitorAcceptanceUtil {
	
	public void getEmptyListOfStubs(Module application) {
		CommuneMonitor monitor = getMonitorController(application);
		
		CommuneMonitorClient client = EasyMock.createMock(CommuneMonitorClient.class);
		
		client.hereAreStubs(new ArrayList<StubData>());
		
		EasyMock.replay(client);
		monitor.getStubs(client);
		EasyMock.verify(client);
	}

	public void getStubs(Module application, DeploymentID monitorDID, DeploymentID stubDID, ServiceID stubSID,
			Class<?>... classes) {
		CommuneMonitor monitor = getMonitorController(application);
		
		CommuneMonitorClient client = EasyMock.createMock(CommuneMonitorClient.class);
		
		client.hereAreStubs(StubDataMatcher.eqMatcher(monitorDID, stubDID, stubSID, createClassList(classes)));
		
		EasyMock.replay(client);
		monitor.getStubs(client);
		EasyMock.verify(client);
	}
	
	public void getStubs(Module application, Collection<StubData> stubDatas) {
		CommuneMonitor monitor = getMonitorController(application);
		
		CommuneMonitorClient client = EasyMock.createMock(CommuneMonitorClient.class);
		
		client.hereAreStubs(StubDataMatcher.eqMatcher(stubDatas));
		
		EasyMock.replay(client);
		monitor.getStubs(client);
		EasyMock.verify(client);
	}
	
	
	public List<Class<?>> createClassList(Class<?>... classes) {
		List<Class<?>> list = new ArrayList<Class<?>>();
		
		for (int i = 0; i < classes.length; i++) {
			list.add(classes[i]);
		}
		
		return list;
	}
}
