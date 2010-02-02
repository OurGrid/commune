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
package br.edu.ufcg.lsd.commune.functionaltests.monitor.matchers;

import java.util.Collection;
import java.util.Iterator;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;

import br.edu.ufcg.lsd.commune.message.Message;

public class MessageLogMatcher implements IArgumentMatcher {
	
	private Collection<Message> messages;
	
	public MessageLogMatcher(Collection<Message> messages) {
		this.messages = messages;
	}
	
	/* (non-Javadoc)
	 * @see org.easymock.IArgumentMatcher#appendTo(java.lang.StringBuffer)
	 */
	public void appendTo(StringBuffer arg0) {
		
	}

	/* (non-Javadoc)
	 * @see org.easymock.IArgumentMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object arg0) {
		
		if (arg0 == null || !(arg0 instanceof Collection)) {
			return false;
		}
		
		Collection<Message> other = (Collection<Message>) arg0;
		
		if (this.messages.size() != other.size()) {
			return false;
		}
		
		Iterator<Message> messageIterator = messages.iterator();
		Iterator<Message> otherIterator = other.iterator();
		
		while (messageIterator.hasNext() && otherIterator.hasNext()) {
			Message message = messageIterator.next();
			Message otherMessage = otherIterator.next();
			
			if (couldParametersBeCompared(message, otherMessage)) {
				for (int i = 0; i < message.getParameterTypes().length; i++) {
					if (!message.getParameterTypes()[i].getName().equals(otherMessage.getParameterTypes()[i].getName())) {
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	private boolean couldParametersBeCompared(Message message, Message otherMessage) {
		return message.getSource().getContainerID().equals(otherMessage.getSource().getContainerID()) && message.getDestination().getContainerID().
			   equals(otherMessage.getDestination().getContainerID()) && message.getFunctionName().equals(otherMessage.getFunctionName()) && (message.getSession() ==
			   otherMessage.getSession()) && (message.getSequence() == otherMessage.getSequence());
	}

	public static Collection<Message> eqMatcher(Collection<Message> messages) {
		EasyMock.reportMatcher(new MessageLogMatcher(messages));
		return null;
	}

}

