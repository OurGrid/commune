package br.edu.ufcg.lsd.commune.network.xmpp;

import java.util.UUID;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class ConnectionCheckRunnable implements Runnable {

	private static transient final org.apache.log4j.Logger LOG = 
			org.apache.log4j.Logger.getLogger( ConnectionCheckRunnable.class );
	
	private final XMPPConnectionListener connectionListener;
	private final int sleepTime;
	private final XMPPConnection connection;

	public ConnectionCheckRunnable(XMPPConnection connection, 
			XMPPConnectionListener connectionListener, int sleepTime) {
		this.connection = connection;
		this.connectionListener = connectionListener;
		this.sleepTime = sleepTime;
	}
	
	public void run() {
		
		UUID connectionAttempt = UUID.randomUUID();
		
		while (!connection.isConnected()) {
			try {
				LOG.debug("Trying to connect to XMPP server : " + connection.getHost() + 
						", attempt: " + connectionAttempt);
				connection.connect();
			} catch (XMPPException e) {
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e1) {	}
			}
		}
		connectionListener.connectionCreated();
		
	}
	
	interface XMPPConnectionListener {

		public void connectionCreated();
		
	}
	
}
