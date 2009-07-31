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

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

import br.edu.ufcg.lsd.commune.Application;
import br.edu.ufcg.lsd.commune.functionaltests.util.Context;
import br.edu.ufcg.lsd.commune.functionaltests.util.TestContext;
import br.edu.ufcg.lsd.commune.functionaltests.util.TestWithApplication;
import br.edu.ufcg.lsd.commune.network.certification.CertificationProperties;
import br.edu.ufcg.lsd.commune.network.certification.providers.FileCertificationDataProvider;
import br.edu.ufcg.lsd.commune.network.certification.providers.FileCertificationProperties;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.network.xmpp.XMPPProperties;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferProperties;

public class ContainerCreation extends TestWithApplication {

	
	private static final String WRONG_PASSWORD = "wrong";
	private static final String UNKNOWN_SERVER = "server";
	private static final String UNKNOWN_PORT = "52200";
	private static final String SECURE_PORT = "5223";
	private static final String UNKNOWN_SECURE_PORT = "52000";

	
	@Test(expected=IllegalArgumentException.class)
	public void validateConstructorAllNull() throws Exception {
		application = new Application(null, null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void validateConstructorNullContext() throws Exception {
		application = new Application(Context.A_CONTAINER_NAME, null);
	}

	@Test(expected=CommuneNetworkException.class)
	public void validateContextUnknownServer() throws Exception {
		TestContext context = createBasicContext();
		context.set(XMPPProperties.PROP_XMPP_SERVERNAME, UNKNOWN_SERVER);
		application = new Application(Context.A_CONTAINER_NAME, context);
	}

	private TestContext createBasicContext() {
		TestContext context = new TestContext();
		context.set(XMPPProperties.PROP_USERNAME, Context.USER);
		context.set(XMPPProperties.PROP_XMPP_SERVER_SECURE_PORT, SECURE_PORT);
		context.set(XMPPProperties.PROP_XMPP_SERVERPORT, Context.PORT);
		context.set(TransferProperties.PROP_FILE_TRANSFER_MAX_OUT, "100");
		context.set(CertificationProperties.PROP_CERT_PROVIDER_CLASS, 
				FileCertificationDataProvider.class.getName());
		context.set(FileCertificationProperties.PROP_MYCERTIFICATE_FILEPATH, 
				"data" + File.separator + "certification" + File.separator + "testLSD.cer");
		return context;
	}

	@Test(expected=CommuneNetworkException.class)
	public void validateContextUnknownPort() throws Exception {
		TestContext context = Context.createRealContext();
		context.set(XMPPProperties.PROP_XMPP_SERVERPORT, UNKNOWN_PORT);
		application = new Application(Context.A_CONTAINER_NAME, context);
	}

	@Ignore(value="Commune is not using secure port!!!")
	@Test(expected=CommuneNetworkException.class)
	public void validateContextUnknownSecurePort() throws Exception {
		TestContext context = Context.createRealContext();
		context.set(XMPPProperties.PROP_XMPP_SERVER_SECURE_PORT, UNKNOWN_SECURE_PORT);
		application = new Application(Context.A_CONTAINER_NAME, context);
	}

	@Test
	public void correctCreation() throws Exception {
		TestContext context = Context.createRealContext();
		application = new Application(Context.A_CONTAINER_NAME, context);
	}
	
	@Test(expected=CommuneNetworkException.class)
	public void validateContextWrongPassword() throws Exception {
		TestContext context = Context.createRealContext();
		context.set(XMPPProperties.PROP_PASSWORD, WRONG_PASSWORD);
		application = new Application(Context.A_CONTAINER_NAME, context);
	}
}