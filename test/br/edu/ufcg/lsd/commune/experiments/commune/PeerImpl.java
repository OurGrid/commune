package br.edu.ufcg.lsd.commune.experiments.commune;

import static br.edu.ufcg.lsd.commune.experiments.commune.Registry.REGISTRY_CONTAINER;
import static br.edu.ufcg.lsd.commune.experiments.commune.Registry.REGISTRY_SERVERNAME;
import static br.edu.ufcg.lsd.commune.experiments.commune.Registry.REGISTRY_SERVICE;
import static br.edu.ufcg.lsd.commune.experiments.commune.Registry.REGISTRY_USERNAME;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.api.MonitoredBy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.experiments.Util;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class PeerImpl implements Peer {

	
	private ServiceManager serviceManager;
	private List<String> otherPeers = new ArrayList<String>();
	private Map<String,Long> upPeersCounter = new HashMap<String,Long>();
	private Map<String,Peer> upPeers = new HashMap<String,Peer>();
	private Lock peersLock = new ReentrantLock();
	
	private int counter = 0;
	
	
	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
		
		String monitorableAddress = 
			REGISTRY_USERNAME + "@" + REGISTRY_SERVERNAME + "/" + REGISTRY_CONTAINER + "/" + REGISTRY_SERVICE;
		serviceManager.registerInterest(Peer.PEER_SERVICE, monitorableAddress, Registry.class, 300, 150);
	}


	@RecoveryNotification
	public void registryIsUp(Registry registry) {
		registry.getPeerList(this);
		new Thread(createRunnable()).start();
		Util.log(getMyName() + "->registryIsUp");
	}


	private String getMyName() {
		return serviceManager.getApplication().getContainer().getContainerID().getUserName();
	}

	public void updateList(List<String> newPeers) {
		for (String newPeer : newPeers) {
			if (!otherPeers.contains(newPeer)) {
				
				if (newPeer.equals(serviceManager.getMyDeploymentID().getServiceID().toString())) {
					continue;
				}
				
				serviceManager.registerInterest(Peer.PEER_SERVICE, newPeer, Peer.class, 600, 300);
				otherPeers.add(newPeer);
			}
		}
		Util.log(getMyName() + "->updateList");
	}

	@FailureNotification
	public void registryIsDown(Registry registry) {}
	
	@RecoveryNotification
	public void otherPeerIsUp(Peer peer, DeploymentID senderDID) {
		ServiceID serviceID = senderDID.getServiceID();
		String senderID = serviceID.toString();
		
		try {
			peersLock.lock();
			
			upPeers.put(senderID, peer);
			//upPeersCounter.put(senderID, null);

		} finally {
			peersLock.unlock();
		}
		Util.log(getMyName() + "->otherPeerIsUp(" + serviceID.getUserName() + ")");
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


	public void ping(@MonitoredBy(Peer.PEER_SERVICE) Peer otherPeer) {
		ServiceID serviceID = serviceManager.getSenderServiceID();
		String senderID = serviceID.toString();

		try {
			peersLock.lock();
			
			upPeers.put(senderID, otherPeer);
			//upPeersCounter.put(senderID, null);

		} finally {
			peersLock.unlock();
		}
		
		otherPeer.pong();
		Util.log(getMyName() + "->ping()");
	}

	public void pong() {
		String senderID = serviceManager.getSenderServiceID().toString();

		try {
			peersLock.lock();
			
			int size = upPeers.size();
			
			Long begin = upPeersCounter.get(senderID);
			
			if (begin == null) {
				return;
			}
			
			Long finish = System.currentTimeMillis();
			upPeersCounter.put(senderID, null);

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
			
			List<String> keyList = new ArrayList<String>(upPeers.keySet());
			String key = keyList.get(i);
			Peer peer = upPeers.get(key);

			Long counter = upPeersCounter.get(key);
			if (counter == null) {
				counter = System.currentTimeMillis();
			}
			upPeersCounter.put(key, counter);
			
			peer.ping(this);
			
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