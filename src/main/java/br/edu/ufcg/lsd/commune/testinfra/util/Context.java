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
package br.edu.ufcg.lsd.commune.testinfra.util;

import java.io.File;

import br.edu.ufcg.lsd.commune.monitor.MonitorProperties;
import br.edu.ufcg.lsd.commune.network.certification.CertificationProperties;
import br.edu.ufcg.lsd.commune.network.certification.providers.FileCertificationDataProvider;
import br.edu.ufcg.lsd.commune.network.certification.providers.FileCertificationProperties;
import br.edu.ufcg.lsd.commune.network.signature.SignatureProperties;
import br.edu.ufcg.lsd.commune.network.xmpp.XMPPProperties;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferProperties;
import br.edu.ufcg.lsd.commune.processor.interest.InterestProperties;

public class Context {

	public static final String USER = "user";
	public static final String PASSWORD = "password";
	public static final String REAL_SERVER = "xmpp.ourgrid.org";
	public static final String PORT = "5222";
	public static final String A_MODULE_NAME = "aName";
	public static final String A_SERVICE_NAME = "aServiceName";
	public static final String OTHER_SERVICE_NAME = "otherServiceName";
	public static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCqMahIKKqrK0fYMmik63UdyQ4D3/6sc5EHQKRMH5htDb+5qLO2bCAvtVb07CFaXxbJv5IWiwT1iuH5ppgSrbY5vRSUmr/Aag1pXc748q8+cIcnVPZYRRGRm2qUMkDxjHMTc/J3qZuIsYiGgSvaGAbp5AaRr3+U+vqa5yrcB9ucbQIDAQAB";
	public static final String PRIVATE_KEY = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAKoxqEgoqqsrR9gyaKTrdR3JDgPf/qxzkQdApEwfmG0Nv7mos7ZsIC+1VvTsIVpfFsm/khaLBPWK4fmmmBKttjm9FJSav8BqDWldzvjyrz5whydU9lhFEZGbapQyQPGMcxNz8nepm4ixiIaBK9oYBunkBpGvf5T6+prnKtwH25xtAgMBAAECgYEAhhLQauf+axGRa3NqlGEYxfoZHFxdGCWCXqBBzeYmAeMO4odP66mKQYstkYxjSfoWzaAoybGCmpxqIG1o524JWnDoCVqKtjvHYQ7l9/RRgaRj+PvBxvc4AicD9c5dQ/op3P/svGHhygX3NW5QqSK0f5xFun+YzOLmQI0lyTCd8VECQQDirLmROd/KSK50cWtnhsxsBWGLsYd++2wjxUnEt1xg9lZzcpYEzcNkV5seAJSX/FzSNZav+AjJ4GCHFfxuYamLAkEAwDZWl2qfFF2guBokZCMKbxuUes+U9/6Tl3gVozaf3QOYW/xwTFUep+MDhJ5zju24cXNACNC7wXXD/Os1xGbg5wJAIjMtTFwPB9YvfCIgsl4EOcgWD52Zc+87QapiluuTZI3GPWTsfY0ODfWp0b0ErmnFF3I+ag5iRRM1fSw0CfNyQQJBALe4uJfOT/O1cpPGWRZl7+k3oXqpw6gva3+SRoxVAATPFOiHqAcuLTkn4gr3MVlim7PFB2XlrjFCkWzEngX2l+0CQQCnm+ZTtw58vUfpLXrz9/Uh+XveC8qO4i+ZaHfrSvOk+ZPWK31Z3LakzS3VTr9Ru+nIDwq7GO8TcBBWvtki7iar";
	public static final String A_METHOD = "myMethod";
	public static final String A_MONITOR = "monitor";
	
	public static TestContext createRealContext() {
		TestContext context = new TestContext();
		context.set(XMPPProperties.PROP_USERNAME, USER);
		context.set(XMPPProperties.PROP_PASSWORD, PASSWORD);
		context.set(XMPPProperties.PROP_XMPP_SERVERNAME, REAL_SERVER);
		context.set(XMPPProperties.PROP_XMPP_SERVERPORT, PORT);
		context.set(SignatureProperties.PROP_PUBLIC_KEY, PUBLIC_KEY);
		context.set(SignatureProperties.PROP_PRIVATE_KEY, PRIVATE_KEY);
		context.set(InterestProperties.PROP_WAN_DETECTION_TIME, "12");
		context.set(InterestProperties.PROP_WAN_HEARTBEAT_DELAY, "6");
		context.set(TransferProperties.PROP_FILE_TRANSFER_MAX_OUT, "600000");
		context.set(CertificationProperties.PROP_CERT_PROVIDER_CLASS, 
				FileCertificationDataProvider.class.getName());
		context.set( TransferProperties.PROP_FILE_TRANSFER_TIMEOUT, 
				TransferProperties.DEFAULT_FILE_TRANSFER_TIMEOUT );
		context.set( TransferProperties.PROP_FILE_TRANSFER_MAX_OUT, 
				TransferProperties.DEFAULT_FILE_TRANSFER_MAX_OUT );
		context.set( TransferProperties.PROP_FILE_TRANSFER_NOTIFY_PROGRESS, 
				TransferProperties.DEFAULT_FILE_TRANSFER_NOTIFY_PROGRESS );
		context.set(FileCertificationProperties.PROP_MYCERTIFICATE_FILEPATH, 
				"data" + File.separator + "certification" + File.separator + "testLSD.cer");
		context.set(MonitorProperties.PROP_COMMUNE_MONITOR, "no");
		
		return context;
	}
}
