package br.edu.ufcg.lsd.commune.container.servicemanager;

import java.io.Serializable;

import br.edu.ufcg.lsd.commune.Application;
import br.edu.ufcg.lsd.commune.processor.filetransfer.IncomingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferReceiver;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferSender;

public class FileTransferManager implements Serializable {

	
	private static final long serialVersionUID = 1L;

	
	private transient final Application application;
	

	public FileTransferManager(Application application) {
		this.application = application;
	}
	
	
	public void acceptTransfer(IncomingTransferHandle handle, TransferReceiver frl) {
		this.application.getContainer().acceptTransfer(handle, frl);
	}

	public void rejectTransfer(IncomingTransferHandle handle) {
		this.application.getContainer().rejectTransfer(handle);
	}

	public void startTransfer(OutgoingTransferHandle handle, TransferSender fsl) {
		this.application.getContainer().startTransfer(handle, fsl);
	}

	public void cancelIncomingTransfer(IncomingTransferHandle handle) {
		this.application.getContainer().cancelIncomingTransfer(handle);
	}

	public void cancelOutgoingTransfer(OutgoingTransferHandle handle) {
		this.application.getContainer().cancelOutgoingTransfer(handle);
	}

}
