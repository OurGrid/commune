package br.edu.ufcg.lsd.commune.systemtest.tft;

import java.io.File;

import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.processor.filetransfer.IncomingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferProgress;

public class TransferDestinationReceiver implements TransferDestination {

	private ServiceManager serviceManager;
	
	public boolean transferCompleted = true;
	public int transfersFinished;
	public int transfersStarted;

	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}
	
	public void incomingTransferCompleted(IncomingTransferHandle handle,
			long amountWritten) {
		transfersFinished++;
	}

	public void incomingTransferFailed(IncomingTransferHandle handle,
			Exception failCause, long amountWritten) {
		this.transferCompleted &= false;
		failCause.printStackTrace();
	}

	public void transferRequestReceived(IncomingTransferHandle handle) {
		this.serviceManager.acceptTransfer(handle, this, 
				new File("tests/filetransfer/received/received." + 
						serviceManager.getMyDeploymentID().getContainerID().getUserName() + (transfersStarted++) + ".txt"));
	}

	public void updateTransferProgress(TransferProgress transferProgress) {

	}

}
