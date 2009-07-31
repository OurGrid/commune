package br.edu.ufcg.lsd.commune.functionaltests.monitor.util;

import java.util.ArrayList;
import java.util.Collection;

import org.easymock.EasyMock;

import br.edu.ufcg.lsd.commune.functionaltests.monitor.MonitorAcceptanceUtil;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.matchers.MessageLogMatcher;
import br.edu.ufcg.lsd.commune.functionaltests.util.TestableApplication;
import br.edu.ufcg.lsd.commune.message.Message;
import br.edu.ufcg.lsd.commune.monitor.CommuneMonitor;
import br.edu.ufcg.lsd.commune.monitor.CommuneMonitorClient;

public class TestServiceMessageLogUtil extends MonitorAcceptanceUtil {
	public void getEmptyLog(TestableApplication application) throws Exception {
		CommuneMonitor monitor = getMonitorController(application);
		
		CommuneMonitorClient client = EasyMock.createMock(CommuneMonitorClient.class);
		
		client.hereIsServiceMessagesLog(new ArrayList<Message>());
		
		EasyMock.replay(client);
		monitor.getServiceMessagesLog(client);
		EasyMock.verify(client);
	}
	
	public void getMessagesLog(TestableApplication application, Collection<Message> messages) {
		CommuneMonitor monitor = getMonitorController(application);
		
		CommuneMonitorClient client = EasyMock.createMock(CommuneMonitorClient.class);
		
		client.hereIsServiceMessagesLog(MessageLogMatcher.eqMatcher(messages));
		
		EasyMock.replay(client);
		monitor.getServiceMessagesLog(client);
		EasyMock.verify(client);
	}
}