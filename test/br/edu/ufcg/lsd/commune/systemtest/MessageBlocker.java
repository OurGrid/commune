package br.edu.ufcg.lsd.commune.systemtest;

import br.edu.ufcg.lsd.commune.message.Message;
import br.edu.ufcg.lsd.commune.network.CommuneNetwork;
import br.edu.ufcg.lsd.commune.network.DiscardMessageException;
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
		if (message == null) {
			throw new DiscardMessageException();
		}

		if (receiverBlocker.block(message)) {
			throw new DiscardMessageException();
		}
	}

	@Override
	protected void onSend(Message message) throws ProtocolException {
		if (message == null) {
			throw new DiscardMessageException();
		}

		if (senderBlocker.block(message)) {
			throw new DiscardMessageException();
		}
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