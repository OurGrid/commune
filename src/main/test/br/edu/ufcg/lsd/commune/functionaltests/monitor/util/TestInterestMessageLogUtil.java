package br.edu.ufcg.lsd.commune.functionaltests.monitor.util;

import java.util.ArrayList;
import java.util.Collection;

import org.easymock.EasyMock;

import br.edu.ufcg.lsd.commune.functionaltests.monitor.MonitorAcceptanceUtil;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.matchers.MessageLogMatcher;
import br.edu.ufcg.lsd.commune.message.Message;
import br.edu.ufcg.lsd.commune.monitor.CommuneMonitor;
import br.edu.ufcg.lsd.commune.monitor.CommuneMonitorClient;
import br.edu.ufcg.lsd.commune.testinfra.util.TestableModule;

public class TestInterestMessageLogUtil extends MonitorAcceptanceUtil {
	public void getEmptyLog(TestableModule application) throws Exception {
		CommuneMonitor monitor = getMonitorController(application);
		
		CommuneMonitorClient client = EasyMock.createMock(CommuneMonitorClient.class);
		
		client.hereIsInterestMessagesLog(new ArrayList<Message>());
		
		EasyMock.replay(client);
		monitor.getInterestMessagesLog(client);
		EasyMock.verify(client);
	}
	
	public void getMessagesLog(TestableModule application, Collection<Message> messages) {
		CommuneMonitor monitor = getMonitorController(application);
		
		CommuneMonitorClient client = EasyMock.createMock(CommuneMonitorClient.class);
		
		client.hereIsInterestMessagesLog(MessageLogMatcher.eqMatcher(messages));
		
		EasyMock.replay(client);
		monitor.getInterestMessagesLog(client);
		EasyMock.verify(client);
	}
}