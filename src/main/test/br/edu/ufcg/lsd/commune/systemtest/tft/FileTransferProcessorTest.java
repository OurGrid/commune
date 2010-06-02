package br.edu.ufcg.lsd.commune.systemtest.tft;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.edu.ufcg.lsd.commune.context.DefaultContextFactory;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.context.PropertiesParser;
import br.edu.ufcg.lsd.commune.network.certification.CertificationProperties;
import br.edu.ufcg.lsd.commune.network.certification.providers.FileCertificationDataProvider;
import br.edu.ufcg.lsd.commune.network.certification.providers.FileCertificationProperties;
import br.edu.ufcg.lsd.commune.network.xmpp.XMPPProperties;
import br.edu.ufcg.lsd.commune.systemtest.ConditionChecker;
import br.edu.ufcg.lsd.commune.systemtest.SystemTestModule;
import br.edu.ufcg.lsd.commune.systemtest.tft.conditions.TransferFinishedCondition;

public class FileTransferProcessorTest {

	
	private static final String RECEIVED_FILES_DIR = "tests/filetransfer/received";
	private static final int VERIFICATION_COUNT = 5000;
	private static final int VERIFICATION_DELAY = 10;
	
	
	private SystemTestModule source_module;
	private SystemTestModule dest_a_module;
	private SystemTestModule dest_b_module;
	
	
	@Before
	public void startModules() throws Exception {
		source_module = createModule(FileTransferTestConstants.SOURCE_CONTAINER, 
				FileTransferTestConstants.SOURCE_USER);
		dest_a_module = createModule(FileTransferTestConstants.DEST_CONTAINER, 
				FileTransferTestConstants.DEST_USER_A);
		dest_b_module = createModule(FileTransferTestConstants.DEST_CONTAINER, 
				FileTransferTestConstants.DEST_USER_B);
	}
	
	@After
	public void stopModules() throws Exception {
		
		if (source_module != null) {
			source_module.stop();
		}

		if (dest_a_module != null) {
			dest_a_module.stop();
		}
		
		if (dest_b_module != null) {
			dest_b_module.stop();
		}
		
		cleanReceivedFiles();
	}
	
	private void cleanReceivedFiles() {
		for (File file : new File(RECEIVED_FILES_DIR).listFiles()) {
			file.delete();
		}
		
	}

	@Test
	public void testZeroSizeFiles() throws Exception {
	
		int transfersToCreate = 100;
		File fileToSend = new File("tests/filetransfer/testzerosizefile.txt");
		
		TransferSourceReceiver transferSourceRec = new TransferSourceReceiver(transfersToCreate, fileToSend, 
				FileTransferTestConstants.DEST_A_ADDRESS);
		source_module.getContainer().deploy(FileTransferTestConstants.SOURCE_SERVICE, 
				transferSourceRec);
		
		TransferDestinationReceiver transferDestRec = new TransferDestinationReceiver();
		dest_a_module.getContainer().deploy(FileTransferTestConstants.DEST_SERVICE, 
				transferDestRec);
		
		ConditionChecker<List<TransferDestinationReceiver>> checker = createCondition(transfersToCreate, transferDestRec);
		
		Assert.assertTrue(checker.waitUntilCondition(VERIFICATION_DELAY, VERIFICATION_COUNT));
		
		for (File eachFile : new File(RECEIVED_FILES_DIR).listFiles()) {
			Assert.assertEquals(0, eachFile.length());
		}
	}
	
	@Test
	public void testSmallAscFiles() throws Exception {
	
		int transfersToCreate = 100;
		File fileToSend = new File("tests/filetransfer/testsmallfile-asc.txt");
		
		TransferSourceReceiver transferSourceRec = new TransferSourceReceiver(transfersToCreate, fileToSend, 
				FileTransferTestConstants.DEST_A_ADDRESS);
		source_module.getContainer().deploy(FileTransferTestConstants.SOURCE_SERVICE, 
				transferSourceRec);
		
		TransferDestinationReceiver transferDestRec = new TransferDestinationReceiver();
		dest_a_module.getContainer().deploy(FileTransferTestConstants.DEST_SERVICE, 
				transferDestRec);
		
		ConditionChecker<List<TransferDestinationReceiver>> checker = createCondition(transfersToCreate, transferDestRec);
		
		Assert.assertTrue(checker.waitUntilCondition((int)(VERIFICATION_DELAY * 1.5), VERIFICATION_COUNT));
		
		for (File eachFile : new File(RECEIVED_FILES_DIR).listFiles()) {
			Assert.assertEquals(fileToSend.length(), eachFile.length());
		}
	}

	@Test
	public void testSmallBinFiles() throws Exception {
	
		int transfersToCreate = 100;
		File fileToSend = new File("tests/filetransfer/testsmallfile-bin.zip");
		
		TransferSourceReceiver transferSourceRec = new TransferSourceReceiver(transfersToCreate, fileToSend, 
				FileTransferTestConstants.DEST_A_ADDRESS);
		source_module.getContainer().deploy(FileTransferTestConstants.SOURCE_SERVICE, 
				transferSourceRec);
		
		TransferDestinationReceiver transferDestRec = new TransferDestinationReceiver();
		dest_a_module.getContainer().deploy(FileTransferTestConstants.DEST_SERVICE, 
				transferDestRec);
		
		ConditionChecker<List<TransferDestinationReceiver>> checker = createCondition(transfersToCreate, transferDestRec);
		
		Assert.assertTrue(checker.waitUntilCondition((int)(VERIFICATION_DELAY * 1.5), VERIFICATION_COUNT));
		
		for (File eachFile : new File(RECEIVED_FILES_DIR).listFiles()) {
			Assert.assertEquals(fileToSend.length(), eachFile.length());
		}
	}
	
	@Test
	public void testLargeAscFiles() throws Exception {
		
		int transfersToCreate = 100;
		File fileToSend = new File("tests/filetransfer/testmedfile-asc.txt");
		
		TransferSourceReceiver transferSourceRec = new TransferSourceReceiver(transfersToCreate, fileToSend, 
				FileTransferTestConstants.DEST_A_ADDRESS);
		source_module.getContainer().deploy(FileTransferTestConstants.SOURCE_SERVICE, 
				transferSourceRec);
		
		TransferDestinationReceiver transferDestRec = new TransferDestinationReceiver();
		dest_a_module.getContainer().deploy(FileTransferTestConstants.DEST_SERVICE, 
				transferDestRec);
		
		ConditionChecker<List<TransferDestinationReceiver>> checker = createCondition(transfersToCreate, transferDestRec);
		
		Assert.assertTrue(checker.waitUntilCondition((int) (VERIFICATION_DELAY * 2.5), VERIFICATION_COUNT));
		
		for (File eachFile : new File(RECEIVED_FILES_DIR).listFiles()) {
			Assert.assertEquals(fileToSend.length(), eachFile.length());
		}
	}
	
	@Test
	public void testLargeBinFiles() throws Exception {
	
		int transfersToCreate = 100;
		File fileToSend = new File("tests/filetransfer/testmedfile-bin.mp3");
		
		TransferSourceReceiver transferSourceRec = new TransferSourceReceiver(transfersToCreate, fileToSend,
				FileTransferTestConstants.DEST_A_ADDRESS);
		source_module.getContainer().deploy(FileTransferTestConstants.SOURCE_SERVICE, 
				transferSourceRec);
		
		TransferDestinationReceiver transferDestRec = new TransferDestinationReceiver();
		dest_a_module.getContainer().deploy(FileTransferTestConstants.DEST_SERVICE, 
				transferDestRec);
		
		ConditionChecker<List<TransferDestinationReceiver>> checker = createCondition(transfersToCreate, transferDestRec);
		
		Assert.assertTrue(checker.waitUntilCondition(VERIFICATION_DELAY, VERIFICATION_COUNT));
		
		for (File eachFile : new File(RECEIVED_FILES_DIR).listFiles()) {
			Assert.assertEquals(fileToSend.length(), eachFile.length());
		}
	}
	
	@Test
	public void testSmallAscFilesTwoDest() throws Exception {
	
		int transfersToCreate = 100;
		File fileToSend = new File("tests/filetransfer/testsmallfile-asc.txt");
		
		TransferSourceReceiver transferSourceRec = new TransferSourceReceiver(transfersToCreate, fileToSend, 
				FileTransferTestConstants.DEST_A_ADDRESS, FileTransferTestConstants.DEST_B_ADDRESS);
		source_module.getContainer().deploy(FileTransferTestConstants.SOURCE_SERVICE, 
				transferSourceRec);
		
		TransferDestinationReceiver transferDestRecA = new TransferDestinationReceiver();
		dest_a_module.getContainer().deploy(FileTransferTestConstants.DEST_SERVICE, 
				transferDestRecA);
		
		TransferDestinationReceiver transferDestRecB = new TransferDestinationReceiver();
		dest_b_module.getContainer().deploy(FileTransferTestConstants.DEST_SERVICE, 
				transferDestRecB);
		
		ConditionChecker<List<TransferDestinationReceiver>> checker = createCondition(transfersToCreate, 
				transferDestRecA, transferDestRecB);
		
		Assert.assertTrue(checker.waitUntilCondition((int) (VERIFICATION_DELAY*2.5), VERIFICATION_COUNT));
		
		for (File eachFile : new File(RECEIVED_FILES_DIR).listFiles()) {
			Assert.assertEquals(fileToSend.length(), eachFile.length());
		}
	}
	
	@Test
	public void testSmallBinFilesTwoDest() throws Exception {
	
		int transfersToCreate = 100;
		File fileToSend = new File("tests/filetransfer/testsmallfile-bin.zip");
		
		TransferSourceReceiver transferSourceRec = new TransferSourceReceiver(transfersToCreate, fileToSend, 
				FileTransferTestConstants.DEST_A_ADDRESS, FileTransferTestConstants.DEST_B_ADDRESS);
		source_module.getContainer().deploy(FileTransferTestConstants.SOURCE_SERVICE, 
				transferSourceRec);
		
		TransferDestinationReceiver transferDestRecA = new TransferDestinationReceiver();
		dest_a_module.getContainer().deploy(FileTransferTestConstants.DEST_SERVICE, 
				transferDestRecA);
		
		TransferDestinationReceiver transferDestRecB = new TransferDestinationReceiver();
		dest_b_module.getContainer().deploy(FileTransferTestConstants.DEST_SERVICE, 
				transferDestRecB);
		
		ConditionChecker<List<TransferDestinationReceiver>> checker = createCondition(transfersToCreate, 
				transferDestRecA, transferDestRecB);
		
		Assert.assertTrue(checker.waitUntilCondition((int) (VERIFICATION_DELAY*2.5), VERIFICATION_COUNT));
		
		for (File eachFile : new File(RECEIVED_FILES_DIR).listFiles()) {
			Assert.assertEquals(fileToSend.length(), eachFile.length());
		}
	}
	
	private ConditionChecker<List<TransferDestinationReceiver>> createCondition(
			int transfersToCreate, TransferDestinationReceiver... transferDestRec) {
		TransferFinishedCondition transferReqRecCondition = new TransferFinishedCondition(transfersToCreate);
		
		ConditionChecker<List<TransferDestinationReceiver>> checker = 
			new ConditionChecker<List<TransferDestinationReceiver>>(Arrays.asList(transferDestRec), transferReqRecCondition);
		
		return checker;
	}
	
	private SystemTestModule createModule(String container, String user) throws Exception {
		return new SystemTestModule(container, createModuleContext(user));
	}
	
	private ModuleContext createModuleContext(String username) {
		Map<String, String> properties = new HashMap<String,String>();
		properties.put(XMPPProperties.PROP_USERNAME, username);
		properties.put(CertificationProperties.PROP_CERT_PROVIDER_CLASS, 
				FileCertificationDataProvider.class.getName());
		properties.put(FileCertificationProperties.PROP_MYCERTIFICATE_FILEPATH, 
				"data" + File.separator + "certification" + File.separator + "testLSD.cer");
		
		DefaultContextFactory factory = new DefaultContextFactory(new PropertiesParser(properties));
		return factory.createContext();
	}
}
