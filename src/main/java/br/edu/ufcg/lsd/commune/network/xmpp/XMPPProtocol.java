/*
 * Copyright (C) 2008 Universidade Federal de Campina Grande
 *  
 * This file is part of Commune. 
 *
 * Commune is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package br.edu.ufcg.lsd.commune.network.xmpp;

import java.io.IOException;
import java.util.HashMap;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLoggerFactory;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.InvalidIdentificationException;
import br.edu.ufcg.lsd.commune.network.CommuneNetwork;
import br.edu.ufcg.lsd.commune.network.ConnectionListener;
import br.edu.ufcg.lsd.commune.network.Protocol;

public class XMPPProtocol extends Protocol implements PacketListener{
	
	private static String prefix = StringUtils.randomString( 5 );
	private static long id = 0;
	private static Object packetLock = new Object();
	private static transient final org.apache.log4j.Logger LOG = 
		org.apache.log4j.Logger.getLogger( XMPPProtocol.class );

	private boolean wasShutdown;
	private XMPPConnection connection;
	protected ContainerID identification;
	private HashMap<String,String> chats;

	private FragmentationManager fm;
	private ModuleContext context;
	private final ConnectionListener connectionListener;


	public XMPPProtocol(CommuneNetwork communicationLayer, ContainerID identification, 
			ModuleContext context, ConnectionListener connectionListener) {
		super(communicationLayer);
		
		this.identification = identification;
		this.context = context;
		this.connectionListener = connectionListener;
		this.chats = new HashMap<String,String>();
		this.wasShutdown = false;
	}

	private static synchronized String nextID() {
		return prefix + Long.toString( id++ );
	}

	@Override
	public void start() throws CommuneNetworkException {
		if ( this.identification == null ) {
			throw new InvalidIdentificationException( 
					"The xmpp protocol could not be started. Identification is null");
		}

		final String login = identification.getUserName();
		final String serverName = identification.getServerName();
		final String resource = identification.getContainerName();

		final String password = context.getProperty(XMPPProperties.PROP_PASSWORD);
		int serverPort = context.parseIntegerProperty(XMPPProperties.PROP_XMPP_SERVERPORT);

		ConnectionConfiguration cc = new ConnectionConfiguration( serverName, serverPort );
		cc.setReconnectionAllowed(true);
		connection = new XMPPConnection( cc);
		new Thread(new ConnectionRunnable(new XMPPConnectionListener() {
			
			@Override
			public void connetionCreated() {
				
				try {
					createAccount( login, password );
					connection.login( login, password, resource );
				} catch ( XMPPException e ) {
					connectionListener.connectionFailed(new CommuneNetworkException( "Error logging in to XMPP server with user name: '" + login +
							"'. Check XMPP user name and password. " + e.getMessage() , e ));
					return;
				}

				fm = new FragmentationManager( XMPPProtocol.this );
				
				connection.addPacketListener( XMPPProtocol.this, new AFilter() );
				protocolStarted();
				
				if(connectionListener != null){
					connectionListener.connected();
				}
				
				connection.addConnectionListener(new org.jivesoftware.smack.ConnectionListener() {
				
				@Override
				public void reconnectionSuccessful() {
					if(connectionListener != null){
						connectionListener.reconnected();
					}
				}
				
				@Override
				public void reconnectionFailed(Exception arg0) {
					if(connectionListener != null){
						connectionListener.reconnectedFailed();
					}
					
				}
				
				@Override
				public void reconnectingIn(int arg0) {}
				
				@Override
				public void connectionClosedOnError(Exception arg0) {
					if(connectionListener != null){
						connectionListener.disconnected();
					}
				}
				
				@Override
				public void connectionClosed() {
					if(connectionListener != null){
						connectionListener.disconnected();
					}
				}
			});
			}
		})).start();

	}


	public class AFilter implements PacketFilter {

		public boolean accept(Packet arg0) {
			if (!(arg0 instanceof Message)) {
				return false;
			}
			Message m = (Message) arg0;
			if (m.getTo().equals(identification.toString())) {
				if (m.getProperty(FragmentationManager.FRAG_NUM) != null) {
					return true;
				}
				return false;
			}
			return false;
		}
		
	}


	private void createAccount( String login, String password ) {

		try {
			this.connection.getAccountManager().createAccount( login, password );
			
		} catch ( XMPPException ignored ) {} // The account already exists!
	}

	public void processPacket( Packet packet ) {

		if ( packet instanceof Message ) {

			Message message = (Message) packet;

			synchronized (XMPPProtocol.packetLock) {
				
				String chatDestination = message.getFrom();
				String chat = this.chats.get( chatDestination );
				if ( chat == null ) {
					chat = nextID();
					this.chats.put( chatDestination, chat );
				}
				this.fm.receiveMessage( message );
			}
		}
	}

	@Override
	public void shutdown() throws CommuneNetworkException {

		this.wasShutdown = true;
		this.connection.disconnect();
	}

	public XMPPConnection getConnection() {
		return this.connection;
	}

	@Override
	protected void onReceive( br.edu.ufcg.lsd.commune.message.Message message ) {
		CommuneLoggerFactory.getInstance().getMessagesLogger().debug("X >>> " + message);
	}
	
	public static void showMessageData(byte[] signature, br.edu.ufcg.lsd.commune.message.Message message) {
		if (message.getFunctionName().equals("hereIsWorker")) {
			System.out.println(message.toString());
			
			showByteArray(signature);
			
			System.out.println("sender public key: " + message.getSource().getPublicKey());
		}
	}

	public static void showByteArray(byte[] array) {
		for (byte b : array) {
			System.out.print(b + ",");
		}
		System.out.println();
	}

	@Override
	protected void onSend( br.edu.ufcg.lsd.commune.message.Message message ) {
		if ( this.wasShutdown ) {
			return;
		}

		String destinationModule = message.getDestination().getContainerID().toString();

		String chat = this.chats.get( destinationModule );
		if ( chat == null ) {
			chat = nextID();
			this.chats.put( destinationModule, chat );
		}

		CommuneLoggerFactory.getInstance().getMessagesLogger().debug(">>> X " + message);

		Message[] messages;
		
		try {
			messages = 
				FragmentationManager.createFragMessages(message, this.identification.toString(), 
						destinationModule, chat, Message.Type.chat);
		} catch (IOException e) {
			LOG.error( "Error on message fragmentation: " + e.getMessage() );
			throw new CommuneRuntimeException("Could not fragment message.", e);
		}
		
		for (int i = 0; i < messages.length; i++) {
			try {
				
				connection.sendPacket( messages[i] );
			} catch (IllegalStateException ise) {}
		}
		
		//TODO alternative solution to the openfire bug JM-835
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {}
	}
	
	private  class ConnectionRunnable implements Runnable {

		private static final long SLEEP_TIME = 60000;
		
		private final XMPPConnectionListener connectionListener;

		public ConnectionRunnable(XMPPConnectionListener connectionListener) {
			this.connectionListener = connectionListener;
			
		}
		
		@Override
		public void run() {
			
			while (!connection.isConnected()) {
				try {
					connection.connect();
				} catch (XMPPException e) {
					try {
						Thread.sleep(SLEEP_TIME);
					} catch (InterruptedException e1) {	}
				}
			}
			
			connectionListener.connetionCreated();
			
		}
	}
	
	private interface XMPPConnectionListener{
		
		public void connetionCreated();
	}

}