package br.edu.ufcg.lsd.commune.experiments.rmi;

import java.rmi.Naming;


public class PeerMain {
	
	
	public static final String REGISTER = "-r";
	public static final String INIT = "-i";


	public static void main(String[] args) throws Exception {
		
		if (REGISTER.equals(args[0])) {
			
			if (Peer.PEER_1.equals(args[1])) {
				PeerImpl peer1 = new PeerImpl(Peer.PEER_1, Peer.PEER_2);
				Naming.rebind(Peer.PEER1_SERVERNAME + Peer.PEER_1, peer1);
				
			} else if (Peer.PEER_2.equals(args[1])) {
				PeerImpl peer2 = new PeerImpl(Peer.PEER_2, Peer.PEER_1);
				Naming.rebind(Peer.PEER2_SERVERNAME + Peer.PEER_2, peer2);
			} 
			
		} else if (INIT.equals(args[0])) {

			if (Peer.PEER_1.equals(args[1])) {
				Peer peer = (Peer) Naming.lookup(Peer.PEER1_SERVERNAME + Peer.PEER_1);
				peer.init();
				
			} else if (Peer.PEER_2.equals(args[1])) {
				Peer peer = (Peer) Naming.lookup(Peer.PEER2_SERVERNAME + Peer.PEER_2);
				peer.init();
			} 
		}
	}
}
