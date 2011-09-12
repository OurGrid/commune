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
import java.util.Map;

import org.jivesoftware.smack.packet.Message;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLoggerFactory;
import br.edu.ufcg.lsd.commune.network.Protocol;

public class FragmentationManager {

	public static final String FRAG_MESSAGE = "FRAG_MESSAGE";
	public static final String FRAG_NUM = "FRAG_NUM";
	public static final String FRAG_ID = "FRAG_ID";

	protected Protocol protocol;

	protected Map<String,FragmentedEvent> fragEvents;


	public static Message[ ] createFragMessages( br.edu.ufcg.lsd.commune.message.Message event, String from, String to,
			String thread, Message.Type type ) throws IOException {

		FragmentedEvent fe = new FragmentedEvent( event );

		int numOfFrags = fe.getTotalNumOfFrags();

		Message[ ] messages = new Message[ numOfFrags ];

		for ( int a = 0; a < numOfFrags; a++ ) {
			messages[a] = new Message();
			messages[a].setProperty( FRAG_MESSAGE, fe.getFragment( a ) );
			messages[a].setProperty( FRAG_NUM, numOfFrags );
			messages[a].setProperty( FRAG_ID, a );
			messages[a].setFrom( from );
			messages[a].setTo( to );
			messages[a].setThread( thread );
			messages[a].setType( type );
		}
		return messages;
	}


	public FragmentationManager( Protocol protocol ) {
		this.protocol = protocol;
		this.fragEvents = new HashMap<String,FragmentedEvent>();
	}

	public void receiveMessage( Message message ) {
		String to = message.getTo();
		int numOfFrags = (Integer) message.getProperty( FRAG_NUM );
		int fragID = (Integer) message.getProperty( FRAG_ID );
		byte[ ] frag = (byte[ ]) message.getProperty( FRAG_MESSAGE );

		if ( numOfFrags == 1 ) {
			FragmentedEvent fe = new FragmentedEvent( numOfFrags );
			try {
				fe.receiveFragment( frag, fragID );
				protocol.receiveMessage(fe.getMessage());
			} catch ( IOException e ) {
				CommuneLoggerFactory.getInstance().gimmeALogger(FragmentationManager.class).error("Fragmentation error");
			}
			
		} else {
			FragmentedEvent fe;

			if ( fragID == 0 ) {
				fragEvents.remove( to );
			}

			if ( fragEvents.containsKey( to ) ) {
				fe = fragEvents.get( to );
				
				try {
					fe.receiveFragment( frag, fragID );
				} catch ( IOException e ) {
					fragEvents.remove( to ); 
				}
				
				if ( fe.allFragmentsReceived() ) {
					try {
						protocol.receiveMessage(fe.getMessage());
					} catch ( IOException e ) {
						
					} finally {
						fragEvents.remove( to );
					}
				}
				
			} else {
				if ( fragID == 0 ) {
					fe = new FragmentedEvent( numOfFrags );

					try {
						fe.receiveFragment( frag, fragID );
						fragEvents.put( to, fe );
					} catch ( IOException ioe ) {
						fragEvents.remove( to );
					}
				}
			}
		}
	}

}