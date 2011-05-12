/*
 * Copyright (C) 20011 Universidade Federal de Campina Grande
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
package br.edu.ufcg.lsd.commune.network;


/**
 * Stops the chain flow. 
 * 
 * For some reason the application received an out of order message. It should
 * wait for the the correct sequence message for some time.
 * 
 */
public class MessageNonSequenceException extends DiscardMessageException {

	private static final long serialVersionUID = 1L;
	
	public MessageNonSequenceException() {}
	
	public MessageNonSequenceException(String msg) {
		super(msg);
	}

}
