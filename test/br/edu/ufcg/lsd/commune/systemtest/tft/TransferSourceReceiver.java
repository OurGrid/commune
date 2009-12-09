package br.edu.ufcg.lsd.commune.systemtest.tft;


import java.io.File;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferProgress;

public class TransferSourceReceiver implements TransferSource {

	private ServiceManager serviceManager;
	private final int transfersToCreate;
	private File localFile;
	private final String[] destAdresses;
	
	public TransferSourceReceiver(int transfersToCreate, File fileToSend, String... destAdresses) {
		this.transfersToCreate = transfersToCreate;
		this.localFile = fileToSend;
		this.destAdresses = destAdresses;
	}

	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
		
		for (String destAdress : destAdresses) {
			serviceManager.registerInterest(FileTransferTestConstants.SOURCE_SERVICE, 
					destAdress, TransferDestination.class, 
					FileTransferTestConstants.DETECTION_TIME, 
					FileTransferTestConstants.HEARTBEAT_DELAY);
		}
	}
	
	@Override
	public void outgoingTransferCancelled(OutgoingTransferHandle handle,
			long amountWritten) {
		// TODO Auto-generated method stub

	}

	@Override
	public void outgoingTransferCompleted(OutgoingTransferHandle handle,
			long amountWritten) {
		// TODO Auto-generated method stub

	}

	@Override
	public void outgoingTransferFailed(OutgoingTransferHandle handle,
			Exception failCause, long amountWritten) {
		
		failCause.printStackTrace();

	}

	@Override
	public void transferRejected(OutgoingTransferHandle handle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateTransferProgress(TransferProgress transferProgress) {
		// TODO Auto-generated method stub

	}

	
	@RecoveryNotification
	public void recover(TransferDestination dest) {
		for (int i = 0; i < transfersToCreate; i++) {
			OutgoingTransferHandle oth = new OutgoingTransferHandle(
					localFile.getName(), localFile, "", 
					serviceManager.getStubDeploymentID(dest).getContainerID());
			
			serviceManager.startTransfer(oth, this);
		}
	}
	
	@FailureNotification
	public void fail(TransferDestination dest) {
		serviceManager.release(dest);
	}
}
