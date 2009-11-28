package br.edu.ufcg.lsd.commune.experiments.rmi.point2point;

import java.io.Serializable;
import java.rmi.RemoteException;

import br.edu.ufcg.lsd.commune.experiments.Util;

public class RegistryImpl implements Registry, Serializable {

	
	private static final long serialVersionUID = 1L;
	
	
	private static Peer peer1;
	private static Peer peer2;
	

	public Peer getOtherPeer(String myName) throws RemoteException {
		Util.log("registry->getOtherPeer(" + myName + ")");
		if (Peer.PEER_1.equals(myName)) {
			return peer2;
		} else {
			return peer1;
		}
	}

	public void join(String myName, Peer myRef) throws RemoteException {
		Util.log("registry->join(" + myName + ")");
		if (Peer.PEER_1.equals(myName)) {
			peer1 = myRef;
		} else {
			peer2 = myRef;
		}
	}
}