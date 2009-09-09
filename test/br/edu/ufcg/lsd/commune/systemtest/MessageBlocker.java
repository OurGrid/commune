package br.edu.ufcg.lsd.commune.systemtest;

import br.edu.ufcg.lsd.commune.message.Message;
import br.edu.ufcg.lsd.commune.network.CommuneNetwork;
import br.edu.ufcg.lsd.commune.network.Protocol;
import br.edu.ufcg.lsd.commune.network.ProtocolException;

public class MessageBlocker extends Protocol {
	
	
	private BlockerConfiguration receiverBlocker;
	private BlockerConfiguration senderBlocker;
	

	public MessageBlocker(CommuneNetwork communeNetwork) {
		super(communeNetwork);
	}

	@Override
	protected void onReceive(Message message) throws ProtocolException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onSend(Message message) throws ProtocolException {
		// TODO Auto-generated method stub

	}

	public BlockerConfiguration getReceiverBlocker() {
		return receiverBlocker;
	}

	public void setReceiverBlocker(BlockerConfiguration receiverBlocker) {
		this.receiverBlocker = receiverBlocker;
	}

	public BlockerConfiguration getSenderBlocker() {
		return senderBlocker;
	}

	public void setSenderBlocker(BlockerConfiguration senderBlocker) {
		this.senderBlocker = senderBlocker;
	}
}