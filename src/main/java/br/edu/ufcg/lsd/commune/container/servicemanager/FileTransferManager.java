package br.edu.ufcg.lsd.commune.container.servicemanager;

import java.io.File;
import java.io.Serializable;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.processor.filetransfer.IncomingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferReceiver;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferSender;

public class FileTransferManager implements Serializable {

	
	private static final long serialVersionUID = 1L;

	
	private transient final Module application;
	

	public FileTransferManager(Module application) {
		this.application = application;
	}
	
	
	public void acceptTransfer(IncomingTransferHandle handle, TransferReceiver frl, File localFile) {
		handle.setLocalFile(localFile);
		this.application.acceptTransfer(handle, frl);
	}

	public void rejectTransfer(IncomingTransferHandle handle) {
		this.application.rejectTransfer(handle);
	}

	public void startTransfer(OutgoingTransferHandle handle, TransferSender fsl) {
		this.application.startTransfer(handle, fsl);
	}

	public void cancelIncomingTransfer(IncomingTransferHandle handle) {
		this.application.cancelIncomingTransfer(handle);
	}

	public void cancelOutgoingTransfer(OutgoingTransferHandle handle) {
		this.application.cancelOutgoingTransfer(handle);
	}

}
