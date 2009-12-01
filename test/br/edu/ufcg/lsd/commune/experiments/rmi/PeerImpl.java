package br.edu.ufcg.lsd.commune.experiments.rmi;

import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import br.edu.ufcg.lsd.commune.experiments.Util;

public class PeerImpl implements Peer, Serializable {

	
	private static final long serialVersionUID = 1L;
	
	
	private final Integer myNumber;
	private final Map<Integer, String> properties;
	private Map<Integer,Peer> upPeers = new HashMap<Integer,Peer>();
	private Lock peersLock = new ReentrantLock();

	
	public PeerImpl(Integer myNumber, Map<Integer, String> properties) {
		this.myNumber = myNumber;
		this.properties = properties;
	}


	public void init() {
		new Thread(createRunnable()).start();

		Set<Integer> peersNumbers = properties.keySet();
		for (Integer otherNumber : peersNumbers) {
			
			String otherIP = properties.get(otherNumber);
			
			String otherAddress = Peer.PEER_IP_PREFIX + otherIP + Peer.PEER_SERVICE_PREFIX + otherNumber;
			
			Peer otherPeer = null;
			while (otherPeer == null) {
				
				try {
					otherPeer = (Peer) Naming.lookup(otherAddress);

					try {
						peersLock.lock();
						
						upPeers.put(otherNumber, otherPeer);
					} finally {
						peersLock.unlock();
					}
						
				} catch (Exception e) {}
			}
		}
	}
	
	protected Runnable createRunnable() {
		return new Runnable() {
			public void run() {
				try {
					sendPings();
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		};
	}

	private String getMyName() {
		return Peer.PEER_SERVICE_PREFIX + myNumber;
	}

	public String ping() throws RemoteException{
		Util.log(getMyName() + "->ping()");
		return "pong";
	}

	public void sendPings() {
		try {
			int i = 0;
			
			while(true) {
				
				choosePeerAndSendPing(i++);
				
				sleep();
				Util.log(getMyName() + "->sendPing()");
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private void choosePeerAndSendPing(int counter) {
		try {
			peersLock.lock();
			int size = upPeers.size();
			
			if (size == 0) {
				return;
			}
			
			int i = (int) (Math.random() * size);
			
			List<Integer> keyList = new ArrayList<Integer>(upPeers.keySet());
			Integer key = keyList.get(i);
			Peer peer = upPeers.get(key);

			long begin = System.currentTimeMillis();
			peer.ping();
			long end = System.currentTimeMillis();

			System.out.println(counter + ";" + size + ";" + (end - begin));
			
		} catch (RemoteException e) {
		} finally {
			peersLock.unlock();
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