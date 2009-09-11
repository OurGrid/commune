package br.edu.ufcg.lsd.commune.systemtest.tc1;


public interface TestConstants {
	
	String DEFAULT_SERVER_NAME = "xmpp.ourgrid.org";

	String A_CONTAINER = "A_CONTAINER";
	String A_SERVICE = "A_SERVICE";
	String A_USER = "system_tester_a";

	String B_CONTAINER = "B_CONTAINER";
	String B_SERVICE = "B_SERVICE";
	String B_USER = "system_tester_b";
	String B_ADDRESS = B_USER + "@" + DEFAULT_SERVER_NAME + "/" + B_CONTAINER + "/" + B_SERVICE;
}
