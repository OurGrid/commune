package br.edu.ufcg.lsd.commune.systemtest.fd;

import org.junit.Assert;
import org.junit.Test;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.control.ModuleManager;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.processor.interest.InterestRequirements;
import br.edu.ufcg.lsd.commune.systemtest.Condition;
import br.edu.ufcg.lsd.commune.systemtest.ConditionChecker;
import br.edu.ufcg.lsd.commune.systemtest.SystemTestModule;

public class FailureDetectorTest {

	private static final String MANAGER_OBSERVER_NAME = "ManagerObserver";

	@Test
	public void test1() throws Exception {
		
		ServiceID moduleObjID = new ServiceID(
				new ContainerID("commune_test_user", "xmpp.ourgrid.org", "MODULE_A"), 
				Module.CONTROL_OBJECT_NAME);
		
		SystemTestModule moduleClient = createClientModule(moduleObjID);
		
		ConditionChecker<ManagerObserverReceiver> condition = createCondition(moduleClient);
		Assert.assertTrue(condition.waitUntilCondition(5000, 20));
		
		moduleClient.stop();
		
		moduleClient = createClientModule(moduleObjID);
		condition = createCondition(moduleClient);
		Assert.assertTrue(condition.waitUntilCondition(5000, 20));
		
		moduleClient.stop();
		
		moduleClient = createClientModule(moduleObjID);
		condition = createCondition(moduleClient);
		Assert.assertTrue(condition.waitUntilCondition(5000, 20));
		
		moduleClient.stop();
	}

	private ConditionChecker<ManagerObserverReceiver> createCondition(
			SystemTestModule moduleClient) {
		ManagerObserverReceiver observer = (ManagerObserverReceiver) moduleClient.getObject(MANAGER_OBSERVER_NAME).getObject();
		
		ConditionChecker<ManagerObserverReceiver> condition = new ConditionChecker<ManagerObserverReceiver>(observer, 
				new Condition<ManagerObserverReceiver>() {

					public boolean test(ManagerObserverReceiver t) {
						return t.hasRecovered();
					}
		});
		return condition;
	}
	
	public SystemTestModule createClientModule(ServiceID moduleManagerID) throws Exception {
		
		SystemTestModule moduleClient = FailureDetectorTestInitializer.createModule("MODULE_A_CLIENT", "commune_test_user");
		
		String managerObserverName = MANAGER_OBSERVER_NAME;
		ManagerObserverReceiver observer = new ManagerObserverReceiver();
		
		moduleClient.getContainer().deploy(managerObserverName, observer);
		
		InterestRequirements requirements = new InterestRequirements(4, 2);
		moduleClient.getContainer().registerInterest(managerObserverName, 
				ModuleManager.class, moduleManagerID, requirements);
		
		return moduleClient;
	}
	

}
