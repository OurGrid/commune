package br.edu.ufcg.lsd.commune.functionaltests.monitor;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)  
@SuiteClasses({
	TestDeployMonitor.class,
	TestGetDeployedObjects.class,
	TestGetStubs.class,
	TestGetFileTransfers.class,
	TestFileTransferMessagesLog.class,
	TestServiceProcessorMessagesLog.class,
	TestInterestProcessorMessagesLog.class,
	TestAllMessagesLog.class
})

public class AllMonitorTests {

}
