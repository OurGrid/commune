package br.edu.ufcg.lsd.commune.functionaltests.monitor.util;

import java.io.File;
import java.util.ArrayList;

import org.easymock.EasyMock;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.MonitorAcceptanceUtil;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.matchers.TransferDataMatcher;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.monitor.CommuneMonitor;
import br.edu.ufcg.lsd.commune.monitor.CommuneMonitorClient;
import br.edu.ufcg.lsd.commune.monitor.data.TransferData;

public class TestGetFileTransfersUtil extends MonitorAcceptanceUtil {
	
	public void getFileTransfers(Module application) {
		CommuneMonitor monitor = getMonitorController(application);
		
		CommuneMonitorClient client = EasyMock.createMock(CommuneMonitorClient.class);
		
		client.hereAreFileTransfers(new ArrayList<TransferData>());
		
		EasyMock.replay(client);
		monitor.getFileTransfers(client);
		EasyMock.verify(client);		
	}
	
	public void getFileTransfers(Module application, ContainerID destinationID, DeploymentID listenerID, Status status, File file,
			int queuePosition, boolean isIncoming) {
		CommuneMonitor monitor = getMonitorController(application);
		
		CommuneMonitorClient client = EasyMock.createMock(CommuneMonitorClient.class);
		
		client.hereAreFileTransfers(TransferDataMatcher.eqMatcher(destinationID, listenerID, status, file, queuePosition, isIncoming));
		
		EasyMock.replay(client);
		monitor.getFileTransfers(client);
		EasyMock.verify(client);		
	}
	
}
