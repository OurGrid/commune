package br.edu.ufcg.lsd.commune.experiments.rmi;

import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.RemoteException;

import br.edu.ufcg.lsd.commune.experiments.Util;

public class PeerImpl implements Peer, Serializable {

	
	private static final long serialVersionUID = 1L;

	
	private String name;
	private Peer otherPeer;
	private final String otherPeerName;
	

	public PeerImpl(String name, String otherPeerName) throws RemoteException {
		this.name = name;
		this.otherPeerName = otherPeerName;
	}
	
	
	public void init() throws RemoteException {
		try {
//TODO			otherPeer = (Peer) Naming.lookup(Registry.REGISTRY_SERVERNAME + otherPeerName);
		} catch (Exception e) {}
		new Thread(createRunnable()).start();
	}
	
	protected Runnable createRunnable() {
		return new Runnable() {
			public void run() {
				
				try {
					while(true) {
						sendPings();
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		};
	}

	private String getMyName() {
		return name;
	}

	public String ping(String text) throws RemoteException{
		Util.log(getMyName() + "->ping()");
		return "Response to " + text;
	}

	public void sendPings() {
		try {
			int i = 0;
			
			while(true) {
				
				long begin = System.currentTimeMillis();
				otherPeer.ping(getMyName());
				long end = System.currentTimeMillis();
				
				System.out.println(i + ";" + (end - begin));
				i++;
				
				sleep();
				Util.log(getMyName() + "->sendPings()");
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}


	private void sleep() {
		try {
			Thread.sleep((60 * 1000) / Peer.MESSAGES_PER_MINUTE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}