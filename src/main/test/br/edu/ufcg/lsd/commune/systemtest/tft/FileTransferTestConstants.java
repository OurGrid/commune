package br.edu.ufcg.lsd.commune.systemtest.tft;

import br.edu.ufcg.lsd.commune.processor.interest.InterestProcessor;


public interface FileTransferTestConstants {
	
	String DEFAULT_SERVER_NAME = "xmpp.ourgrid.org";

	int DETECTION_TIME = 3;
	int HEARTBEAT_DELAY = 1;

	String HEARTBEAT_FUNCTION_NAME = InterestProcessor.IS_IT_ALIVE_MESSAGE;
	String UPDATTE_STATUS_FUNCTION_NAME = InterestProcessor.UPDATE_STATUS_MESSAGE;

	String SOURCE_CONTAINER = "SOURCE_CONTAINER";
	String SOURCE_SERVICE = "SOURCE_SERVICE";
	String SOURCE_USER = "system_tester_source_a";
	String SOURCE_ADDRESS = SOURCE_USER + "@" + DEFAULT_SERVER_NAME + "/" + SOURCE_CONTAINER + "/" + SOURCE_SERVICE;

	String DEST_CONTAINER = "SOURCE_CONTAINER";
	String DEST_SERVICE = "SOURCE_SERVICE";
	String DEST_USER_A = "system_tester_dest_a";
	String DEST_A_ADDRESS = DEST_USER_A + "@" + DEFAULT_SERVER_NAME + "/" + DEST_CONTAINER + "/" + DEST_SERVICE;

	String DEST_USER_B = "system_tester_dest_b";
	String DEST_B_ADDRESS = DEST_USER_B + "@" + DEFAULT_SERVER_NAME + "/" + DEST_CONTAINER + "/" + DEST_SERVICE;
}
