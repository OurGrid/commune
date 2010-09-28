package br.edu.ufcg.lsd.commune.functionaltests.monitor.util;


import org.easymock.EasyMock;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.control.ModuleControlClient;
import br.edu.ufcg.lsd.commune.container.control.ServerModuleManager;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.MonitorAcceptanceUtil;
import br.edu.ufcg.lsd.commune.functionaltests.monitor.matchers.ControlOperationResultMatcher;
import br.edu.ufcg.lsd.commune.testinfra.util.Context;
import br.edu.ufcg.lsd.commune.testinfra.util.TestContext;
import br.edu.ufcg.lsd.commune.testinfra.util.TestableModule;

public class TestDeployMonitorUtil extends MonitorAcceptanceUtil {
	public TestableModule startApplication(TestableModule application) throws Exception {
		ObjectDeployment control = application.getObject(Module.CONTROL_OBJECT_NAME);
		
		ModuleControlClient appClient = EasyMock.createMock(ModuleControlClient.class);
		appClient.operationSucceed(ControlOperationResultMatcher.noError());
		
		EasyMock.replay(appClient);
		
		((ServerModuleManager)control.getObject()).start(appClient);
		
		EasyMock.verify(appClient);
		
		return application;
	}
	
	public TestableModule createAndStartApplication(TestableModule application) throws Exception {
		TestContext context = createBasicContext();
		application = new TestableModule(Context.A_MODULE_NAME, context);
		
		ObjectDeployment control = application.getObject(Module.CONTROL_OBJECT_NAME);
		
		ModuleControlClient appClient = EasyMock.createMock(ModuleControlClient.class);
		appClient.operationSucceed(ControlOperationResultMatcher.noError());
		
		EasyMock.replay(appClient);
		
		((ServerModuleManager)control.getObject()).start(appClient);
		
		EasyMock.verify(appClient);
		
		return application;
	}
}
