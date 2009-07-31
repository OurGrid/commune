package br.edu.ufcg.lsd.commune.functionaltests.monitor.util;


import org.easymock.EasyMock;

import br.edu.ufcg.lsd.commune.Application;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.control.ApplicationControlClient;
import br.edu.ufcg.lsd.commune.container.control.ApplicationServerManager;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.MonitorAcceptanceUtil;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.matchers.ControlOperationResultMatcher;
import br.edu.ufcg.lsd.commune.functionaltests.util.Context;
import br.edu.ufcg.lsd.commune.functionaltests.util.TestContext;
import br.edu.ufcg.lsd.commune.functionaltests.util.TestableApplication;

public class TestDeployMonitorUtil extends MonitorAcceptanceUtil {
	public TestableApplication startApplication(TestableApplication application) throws Exception {
		ObjectDeployment control = application.getObject(Application.CONTROL_OBJECT_NAME);
		
		ApplicationControlClient appClient = EasyMock.createMock(ApplicationControlClient.class);
		appClient.operationSucceed(ControlOperationResultMatcher.noError());
		
		EasyMock.replay(appClient);
		
		((ApplicationServerManager)control.getObject()).start(appClient);
		
		EasyMock.verify(appClient);
		
		return application;
	}
	
	public TestableApplication createAndStartApplication(TestableApplication application) throws Exception {
		TestContext context = createBasicContext();
		application = new TestableApplication(Context.A_CONTAINER_NAME, context);
		
		ObjectDeployment control = application.getObject(Application.CONTROL_OBJECT_NAME);
		
		ApplicationControlClient appClient = EasyMock.createMock(ApplicationControlClient.class);
		appClient.operationSucceed(ControlOperationResultMatcher.noError());
		
		EasyMock.replay(appClient);
		
		((ApplicationServerManager)control.getObject()).start(appClient);
		
		EasyMock.verify(appClient);
		
		return application;
	}
}
