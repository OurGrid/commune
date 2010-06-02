/*
 * Copyright (C) 2008 Universidade Federal de Campina Grande
 *  
 * This file is part of Commune. 
 *
 * Commune is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package br.edu.ufcg.lsd.commune.functionaltests;

import org.junit.Test;

import br.edu.ufcg.lsd.commune.container.InvalidDeploymentException;
import br.edu.ufcg.lsd.commune.container.InvalidMonitoringException;
import br.edu.ufcg.lsd.commune.functionaltests.data.remoteparameters.MyObject1_1;
import br.edu.ufcg.lsd.commune.functionaltests.data.remoteparameters.MyObject1_2;
import br.edu.ufcg.lsd.commune.functionaltests.data.remoteparameters.MyObject1_3;
import br.edu.ufcg.lsd.commune.functionaltests.data.remoteparameters.MyObject1_4;
import br.edu.ufcg.lsd.commune.functionaltests.data.remoteparameters.MyObject1_5a;
import br.edu.ufcg.lsd.commune.functionaltests.data.remoteparameters.MyObject1_5b;
import br.edu.ufcg.lsd.commune.functionaltests.data.remoteparameters.MyObject1_5c;
import br.edu.ufcg.lsd.commune.functionaltests.data.remoteparameters.MyObject1_5d;
import br.edu.ufcg.lsd.commune.functionaltests.data.remoteparameters.MyObject1_5e;
import br.edu.ufcg.lsd.commune.functionaltests.data.remoteparameters.MyObject1_5f;
import br.edu.ufcg.lsd.commune.functionaltests.data.remoteparameters.MyObject1_6;
import br.edu.ufcg.lsd.commune.functionaltests.data.remoteparameters.MyObject1_7;
import br.edu.ufcg.lsd.commune.functionaltests.data.remoteparameters.MyObject1_8a;
import br.edu.ufcg.lsd.commune.functionaltests.data.remoteparameters.MyObject1_8b;
import br.edu.ufcg.lsd.commune.functionaltests.data.remoteparameters.MyObject1_8c;
import br.edu.ufcg.lsd.commune.functionaltests.data.remoteparameters.MyObject1_9a;
import br.edu.ufcg.lsd.commune.functionaltests.data.remoteparameters.MyObject4;
import br.edu.ufcg.lsd.commune.functionaltests.data.remoteparameters.MyObject5;
import br.edu.ufcg.lsd.commune.functionaltests.data.remoteparameters.MyObject6;
import br.edu.ufcg.lsd.commune.functionaltests.data.remoteparameters.OtherObject1_3;
import br.edu.ufcg.lsd.commune.functionaltests.data.remoteparameters.OtherObject1_4;
import br.edu.ufcg.lsd.commune.functionaltests.data.remoteparameters.OtherObject1_5a;
import br.edu.ufcg.lsd.commune.functionaltests.data.remoteparameters.OtherObject1_5b;
import br.edu.ufcg.lsd.commune.functionaltests.data.remoteparameters.OtherObject1_5c;
import br.edu.ufcg.lsd.commune.functionaltests.data.remoteparameters.OtherObject1_5d;
import br.edu.ufcg.lsd.commune.functionaltests.data.remoteparameters.OtherObject1_5e;
import br.edu.ufcg.lsd.commune.functionaltests.data.remoteparameters.OtherObject1_5f;
import br.edu.ufcg.lsd.commune.functionaltests.data.remoteparameters.OtherObject1_6;
import br.edu.ufcg.lsd.commune.functionaltests.data.remoteparameters.OtherObject1_7;
import br.edu.ufcg.lsd.commune.functionaltests.data.remoteparameters.OtherObject1_8a;
import br.edu.ufcg.lsd.commune.functionaltests.data.remoteparameters.OtherObject1_8b;
import br.edu.ufcg.lsd.commune.functionaltests.data.remoteparameters.OtherObject1_8c;
import br.edu.ufcg.lsd.commune.functionaltests.data.remoteparameters.OtherObject6;
import br.edu.ufcg.lsd.commune.functionaltests.data.remoteparameters.OtherObject7;
import br.edu.ufcg.lsd.commune.testinfra.util.Context;
import br.edu.ufcg.lsd.commune.testinfra.util.TestWithTestableCommuneContainer;

public class RemoteParameters extends TestWithTestableCommuneContainer {

	@Test(expected=InvalidDeploymentException.class)
	public void remoteParameterWithoutMonitor() throws Exception {
		application = createApplication();
		application.getContainer().deploy(Context.A_SERVICE_NAME, new MyObject1_1());
	}
	
	@Test(expected=InvalidMonitoringException.class)
	public void remoteParameterWithUnknownMonitor() throws Exception {
		application = createApplication();
		application.getContainer().deploy(Context.A_SERVICE_NAME, new MyObject1_2());
	}

	@Test(expected=InvalidMonitoringException.class)
	public void selfMonitorWithoutNotificationMethods() throws Exception {
		application = createApplication();
		application.getContainer().deploy(Context.A_SERVICE_NAME, new MyObject1_3());
	}
	
	@Test(expected=InvalidMonitoringException.class)
	public void otherMonitorWithoutNotificationMethods() throws Exception {
		application = createApplication();
		application.getContainer().deploy(Context.OTHER_SERVICE_NAME, new OtherObject1_3());
		application.getContainer().deploy(Context.A_SERVICE_NAME, new MyObject1_9a());
	}

	@Test
	public void selfMonitor() throws Exception {
		application = createApplication();
		application.getContainer().deploy(Context.A_SERVICE_NAME, new MyObject1_4());
	}

	@Test
	public void otherMonitor() throws Exception {
		application = createApplication();
		application.getContainer().deploy(Context.OTHER_SERVICE_NAME, new OtherObject1_4());
		application.getContainer().deploy(Context.A_SERVICE_NAME, new MyObject1_9a());
	}

	@Test(expected=InvalidMonitoringException.class)
	public void notificationMethodsWithoutParameters() throws Exception {
		application = createApplication();
		application.getContainer().deploy(Context.A_SERVICE_NAME, new MyObject1_5a());
	}

	@Test(expected=InvalidMonitoringException.class)
	public void otherNotificationMethodsWithoutParameters() throws Exception {
		application = createApplication();
		application.getContainer().deploy(Context.OTHER_SERVICE_NAME, new OtherObject1_5a());
		application.getContainer().deploy(Context.A_SERVICE_NAME, new MyObject1_9a());
	}

	@Test(expected=InvalidMonitoringException.class)
	public void failureNotificationMethodWithoutParameters() throws Exception {
		application = createApplication();
		application.getContainer().deploy(Context.A_SERVICE_NAME, new MyObject1_5b());
	}

	@Test(expected=InvalidMonitoringException.class)
	public void otherFailureNotificationMethodWithoutParameters() throws Exception {
		application = createApplication();
		application.getContainer().deploy(Context.OTHER_SERVICE_NAME, new OtherObject1_5b());
		application.getContainer().deploy(Context.A_SERVICE_NAME, new MyObject1_9a());
	}

	@Test(expected=InvalidMonitoringException.class)
	public void recoveryNotificationMethodWithoutParameters() throws Exception {
		application = createApplication();
		application.getContainer().deploy(Context.A_SERVICE_NAME, new MyObject1_5c());
	}

	@Test(expected=InvalidMonitoringException.class)
	public void otherRecoveryNotificationMethodWithoutParameters() throws Exception {
		application = createApplication();
		application.getContainer().deploy(Context.OTHER_SERVICE_NAME, new OtherObject1_5c());
		application.getContainer().deploy(Context.A_SERVICE_NAME, new MyObject1_9a());
	}

	@Test(expected=InvalidMonitoringException.class)
	public void notificationMethodsWithSuperParameter1() throws Exception {
		application = createApplication();
		application.getContainer().deploy(Context.A_SERVICE_NAME, new MyObject1_5d());
	}

	@Test(expected=InvalidMonitoringException.class)
	public void otherNotificationMethodsWithSuperParameter1() throws Exception {
		application = createApplication();
		application.getContainer().deploy(Context.OTHER_SERVICE_NAME, new OtherObject1_5d());
		application.getContainer().deploy(Context.A_SERVICE_NAME, new MyObject1_9a());
	}

	@Test(expected=InvalidMonitoringException.class)
	public void notificationMethodsWithSubParameter() throws Exception {
		application = createApplication();
		application.getContainer().deploy(Context.A_SERVICE_NAME, new MyObject1_5e());
	}

	@Test(expected=InvalidMonitoringException.class)
	public void otherNotificationMethodsWithSubParameter() throws Exception {
		application = createApplication();
		application.getContainer().deploy(Context.OTHER_SERVICE_NAME, new OtherObject1_5e());
		application.getContainer().deploy(Context.A_SERVICE_NAME, new MyObject1_9a());
	}

	@Test(expected=InvalidMonitoringException.class)
	public void notificationMethodsWithSuperParameter2() throws Exception {
		application = createApplication();
		application.getContainer().deploy(Context.A_SERVICE_NAME, new MyObject1_5f());
	}

	@Test(expected=InvalidMonitoringException.class)
	public void otherNotificationMethodsWithSuperParameter2() throws Exception {
		application = createApplication();
		application.getContainer().deploy(Context.OTHER_SERVICE_NAME, new OtherObject1_5f());
		application.getContainer().deploy(Context.A_SERVICE_NAME, new MyObject1_9a());
	}
	
	@Test(expected=InvalidMonitoringException.class)
	public void selfMonitorWithoutFailureNotification() throws Exception {
		application = createApplication();
		application.getContainer().deploy(Context.A_SERVICE_NAME, new MyObject1_6());
	}

	@Test(expected=InvalidMonitoringException.class)
	public void otherMonitorWithoutFailureNotification() throws Exception {
		application = createApplication();
		application.getContainer().deploy(Context.OTHER_SERVICE_NAME, new OtherObject1_6());
		application.getContainer().deploy(Context.A_SERVICE_NAME, new MyObject1_9a());
	}

	@Test(expected=InvalidMonitoringException.class)
	public void selfMonitorWithoutRecoveryNotification() throws Exception {
		application = createApplication();
		application.getContainer().deploy(Context.A_SERVICE_NAME, new MyObject1_7());
	}

	@Test(expected=InvalidMonitoringException.class)
	public void otherMonitorWithoutRecoveryNotification() throws Exception {
		application = createApplication();
		application.getContainer().deploy(Context.OTHER_SERVICE_NAME, new OtherObject1_7());
		application.getContainer().deploy(Context.A_SERVICE_NAME, new MyObject1_9a());
	}

	@Test(expected=InvalidMonitoringException.class)
	public void notificationMethodsWithWrongParameters() throws Exception {
		application = createApplication();
		application.getContainer().deploy(Context.A_SERVICE_NAME, new MyObject1_8a());
	}

	@Test(expected=InvalidMonitoringException.class)
	public void otherNotificationMethodsWithWrongParameters() throws Exception {
		application = createApplication();
		application.getContainer().deploy(Context.OTHER_SERVICE_NAME, new OtherObject1_8a());
		application.getContainer().deploy(Context.A_SERVICE_NAME, new MyObject1_9a());
	}

	@Test(expected=InvalidMonitoringException.class)
	public void notificationMethodsWithWrongFailureParameter() throws Exception {
		application = createApplication();
		application.getContainer().deploy(Context.A_SERVICE_NAME, new MyObject1_8b());
	}

	@Test(expected=InvalidMonitoringException.class)
	public void otherNotificationMethodsWithWrongFailureParameter() throws Exception {
		application = createApplication();
		application.getContainer().deploy(Context.OTHER_SERVICE_NAME, new OtherObject1_8b());
		application.getContainer().deploy(Context.A_SERVICE_NAME, new MyObject1_9a());
	}

	@Test(expected=InvalidMonitoringException.class)
	public void notificationMethodsWithWrongRecoveryParameter() throws Exception {
		application = createApplication();
		application.getContainer().deploy(Context.A_SERVICE_NAME, new MyObject1_8c());
	}
	
	@Test(expected=InvalidMonitoringException.class)
	public void otherNotificationMethodsWithWrongRecoveryParameter() throws Exception {
		application = createApplication();
		application.getContainer().deploy(Context.OTHER_SERVICE_NAME, new OtherObject1_8c());
		application.getContainer().deploy(Context.A_SERVICE_NAME, new MyObject1_9a());
	}
	
	@Test
	public void duplicateTheUseOfSameMonitor() throws Exception {
		application = createApplication();
		application.getContainer().deploy(Context.OTHER_SERVICE_NAME, new OtherObject1_4());
		application.getContainer().deploy(Context.A_SERVICE_NAME, new MyObject4());
		application.getContainer().deploy(Context.A_SERVICE_NAME + "x", new MyObject5());
	}

	@Test(expected=InvalidMonitoringException.class)
	public void duplicateTheUseOfSameMonitorWithDifferentTypes1() throws Exception {
		application = createApplication();
		application.getContainer().deploy(Context.OTHER_SERVICE_NAME, new OtherObject1_4());
		application.getContainer().deploy(Context.A_SERVICE_NAME, new MyObject5());
		application.getContainer().deploy(Context.A_SERVICE_NAME + "x", new MyObject6());
	}

	@Test
	public void duplicateTheUseOfSameMonitorWithDifferentTypes2() throws Exception {
		application = createApplication();
		application.getContainer().deploy(Context.OTHER_SERVICE_NAME, new OtherObject6());
		application.getContainer().deploy(Context.A_SERVICE_NAME, new MyObject5());
		application.getContainer().deploy(Context.A_SERVICE_NAME + "x", new MyObject6());
	}

	@Test(expected=InvalidMonitoringException.class)
	public void duplicatedAnnotation() throws Exception {
		application = createApplication();
		application.getContainer().deploy(Context.OTHER_SERVICE_NAME, new OtherObject7());
	}

}