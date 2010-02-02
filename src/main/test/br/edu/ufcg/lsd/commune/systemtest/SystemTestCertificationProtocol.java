package br.edu.ufcg.lsd.commune.systemtest;

import sun.security.provider.certpath.X509CertPath;
import br.edu.ufcg.lsd.commune.message.Message;
import br.edu.ufcg.lsd.commune.network.CommuneNetwork;
import br.edu.ufcg.lsd.commune.network.DiscardMessageException;
import br.edu.ufcg.lsd.commune.network.ProtocolException;
import br.edu.ufcg.lsd.commune.network.certification.CertificationProtocol;

public class SystemTestCertificationProtocol extends CertificationProtocol {

	public SystemTestCertificationProtocol(CommuneNetwork communeNetwork, X509CertPath certPath) {
		super(communeNetwork, certPath);
	}
	
	@Override
	protected void onSend(Message message) throws ProtocolException {
		if (message == null) {
			throw new DiscardMessageException();
		}
		
		if (message.getSequence() == null || message.getSequence().equals(0L)) {
			message.setSenderCertificatePath(myCertificatePath);
		}
	}

}
