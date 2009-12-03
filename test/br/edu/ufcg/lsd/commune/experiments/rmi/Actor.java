package br.edu.ufcg.lsd.commune.experiments.rmi;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import br.edu.ufcg.lsd.commune.experiments.Util;

public class Actor {

	
	private final Map<Integer, String> properties;
	private Map<Integer,Reactor> upReactors = new HashMap<Integer,Reactor>();
	private Lock reactorsLock = new ReentrantLock();

	
	public Actor(Map<Integer, String> properties) {
		this.properties = properties;
	}


	public void init() {
		new Thread(createRunnable()).start();

		Set<Integer> reactorsNumbers = properties.keySet();
		
		List<Integer> list = new ArrayList<Integer>(reactorsNumbers);
		Collections.sort(list);
		
		for (Integer reactorNumber : list) {
			
			String reactorIP = properties.get(reactorNumber);
			
			String reactorAddress = ReactorMain.getReactorUrl(reactorIP, reactorNumber);
			
			Reactor reactor = null;
			while (reactor == null) {
				
				try {
					
					reactor = (Reactor) Naming.lookup(reactorAddress);

					try {
						reactorsLock.lock();
						
						upReactors.put(reactorNumber, reactor);
					} finally {
						reactorsLock.unlock();
					}
						
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				Util.sleep(30 * 1000);
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

	public void sendPings() {
		try {
			int i = 0;
			
			while(true) {
				
				choosePeerAndSendPing(i++);
				
				Util.sleep(1000 / Reactor.MESSAGES_PER_SECOND);
				Util.log("choosePeerAndSendPing()");
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private void choosePeerAndSendPing(int counter) {
		try {
			reactorsLock.lock();
			int size = upReactors.size();
			
			if (size == 0) {
				return;
			}
			
			int i = (int) (Math.random() * size);
			
			List<Integer> keyList = new ArrayList<Integer>(upReactors.keySet());
			Integer key = keyList.get(i);
			Reactor peer = upReactors.get(key);

			long begin = System.nanoTime();
			peer.ping();
			long end = System.nanoTime();

			System.out.println(counter + ";" + size + ";" + (end - begin));
			
		} catch (RemoteException e) {
			e.printStackTrace();
			
		} finally {
			reactorsLock.unlock();
		}
	}
}