package br.edu.ufcg.lsd.commune.functionaltests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

 @RunWith(value=Suite.class)
 @SuiteClasses(value={ModuleCreation.class, FailureNotification.class, InvokeIsItAlive.class,
		 InvokeOnDeploy.class, InvokeService.class, InvokeWithRemoteParameters.class, ObjectDeployment.class,
		 ParametersDeployment.class, RecoveryNotification.class, RegisterInterest.class, RemoteCollectionParameters.class,
		 RemoteParameters.class
  }
 )
 public class AllTests {}
