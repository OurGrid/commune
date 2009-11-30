package br.edu.ufcg.lsd.commune.experiments.commune;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.experiments.Util;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class PeerImpl implements Peer {

	
	private ServiceManager serviceManager;
	private Map<Integer,Boolean> otherPeers = new HashMap<Integer,Boolean>();
	private Map<Integer,Long> upPeersCounter = new HashMap<Integer,Long>();
	private Map<Integer,Peer> upPeers = new HashMap<Integer,Peer>();
	private Lock peersLock = new ReentrantLock();
	
	private int counter = 0;
	private final Map<Integer, String> properties;
	private final Integer myNumber;
	
	
	public PeerImpl(Integer myNumber, Map<Integer, String> properties) {
		this.myNumber = myNumber;
		this.properties = properties;
	}


	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;

		for (Integer otherNumber : properties.keySet()) {
			if (!otherNumber.equals(myNumber) && !otherPeers.keySet().contains(otherNumber)) {
				String otherServer = properties.get(otherNumber);
				String otherAddress = 
					Peer.PEER_USERNAME + otherNumber + "@" + otherServer + "/" + Peer.PEER_CONTAINER + "/" + 
					Peer.PEER_SERVICE; 
				serviceManager.registerInterest(Peer.PEER_SERVICE, otherAddress, Peer.class, 60, 30);
				otherPeers.put(otherNumber, Boolean.FALSE);
			}
		}
		
		new Thread(createRunnable()).start();
	}

	private String getMyName() {
		return serviceManager.getApplication().getContainer().getContainerID().getUserName();
	}

	@RecoveryNotification
	public void otherPeerIsUp(Peer otherPeer, DeploymentID senderDID) {
		ServiceID otherServiceID = senderDID.getServiceID();
		
		Integer otherNumber = getPeerNumber(otherServiceID);
		
		try {
			peersLock.lock();
			
			upPeers.put(otherNumber, otherPeer);
			
			Boolean otherHasPing = otherPeers.get(otherNumber);
			if (otherHasPing) {
				otherPeer.pong();
			}

		} finally {
			peersLock.unlock();
		}
		Util.log(getMyName() + "->otherPeerIsUp(" + otherServiceID.getUserName() + ")");
	}


	private Integer getPeerNumber(ServiceID serviceID) {
		String otherNumberStr = serviceID.getUserName().replace(Peer.PEER_USERNAME, "");
		return new Integer(otherNumberStr);
	}
	
	@FailureNotification
	public void otherPeerIsDown(Peer peer, DeploymentID senderDID) {
		ServiceID serviceID = senderDID.getServiceID();
		String senderID = serviceID.toString();
		
		try {
			peersLock.lock();
			
			upPeers.remove(senderID);

		} finally {
			peersLock.unlock();
		}
		Util.log(getMyName() + "->otherPeerIsDown(" + serviceID.getUserName() + ")");
	}


	public void ping() {
		ServiceID serviceID = serviceManager.getSenderServiceID();
		Integer otherNumber = getPeerNumber(serviceID);

		try {
			peersLock.lock();
			
			Peer otherPeer = upPeers.get(otherNumber);
			
			if (otherNumber != null) {
				otherPeer.pong();
			}
			
			otherPeers.put(otherNumber, Boolean.TRUE);

		} finally {
			peersLock.unlock();
		}
		
		Util.log(getMyName() + "->ping()");
	}

	public void pong() {
		ServiceID otherServiceID = serviceManager.getSenderServiceID();
		Integer otherNumber = getPeerNumber(otherServiceID);

		try {
			peersLock.lock();
			
			int size = upPeers.size();
			
			Long begin = upPeersCounter.get(otherNumber);
			
			if (begin == null) {
				return;
			}
			
			Long finish = System.currentTimeMillis();
			upPeersCounter.put(otherNumber, null);

			Long elapsed = finish - begin;
			
			System.out.println(counter++ + ";" + size + ";" + elapsed);
			
		} finally {
			peersLock.unlock();
		}
		Util.log(getMyName() + "->pong()");
	}

	protected Runnable createRunnable() {
		return new Runnable() {
			public void run() {
				
				try {
					while(true) {
						choosePeerAndSendPing();
						sleep();
						Util.log(getMyName() + "->run()");
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		};
	}


	private void choosePeerAndSendPing() {
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

			Long counter = upPeersCounter.get(key);
			if (counter == null) {
				counter = System.currentTimeMillis();
			}
			upPeersCounter.put(key, counter);
			
			peer.ping();
			
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