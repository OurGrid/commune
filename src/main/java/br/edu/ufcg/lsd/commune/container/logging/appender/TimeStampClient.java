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
package br.edu.ufcg.lsd.commune.container.logging.appender;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import br.edu.ufcg.lsd.commune.container.logging.LoggerConstants;

public class TimeStampClient{

	private static Socket requestSocket;
 	private ObjectInputStream in;
 	private String message = "";
 	private Long timeStamp;
 	
 	public TimeStampClient(){}
	
	public Long getTimeStamp(){
		
		timeStamp = System.currentTimeMillis();
		
		try {

			requestSocket = new Socket(LoggerConstants.TIMESTAMP_SERVER, LoggerConstants.TIMESTAMP_PORT);
			in = new ObjectInputStream(requestSocket.getInputStream());
			message = (String)in.readObject();
			timeStamp = new Long(message);
		
		} catch(UnknownHostException unknownHost) {
			System.err.println("You are trying to connect to an unknown host!");
		} catch(IOException ioException) {
			ioException.printStackTrace();
		} catch (ClassNotFoundException classNotFoundException) {
			classNotFoundException.printStackTrace();
		} finally {
			
			try {
				in.close();
				requestSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			} 
		}
		return timeStamp;
	}
}