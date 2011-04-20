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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import br.edu.ufcg.lsd.commune.container.logging.LoggerConstants;

public class TimeStampClient{

	private static Socket requestSocket;
	private BufferedReader in;
	private String message = "";
	private Long timeStamp;

	private boolean isLocal = true;

	public TimeStampClient(){}

	public boolean isLocal(){
		return this.isLocal;
	}

	@SuppressWarnings("finally")
	public Long getTimeStamp(){

		timeStamp = System.currentTimeMillis();

		try {

			InetAddress addr = InetAddress.getByName(LoggerConstants.TIMESTAMP_SERVER);
			SocketAddress sockaddr = new InetSocketAddress(addr, LoggerConstants.TIMESTAMP_PORT);

			requestSocket = new Socket();
			requestSocket.connect(sockaddr, LoggerConstants.TIMEOUT);

			in = new BufferedReader(new InputStreamReader(requestSocket.getInputStream()));
			message = in.readLine();
			timeStamp = new Long(message);
			isLocal = false;

		} catch(UnknownHostException unknownHost) {
		} catch(SocketTimeoutException socketTimeout){
		} catch(IOException ioException) {
		} finally {

			try {
				in.close();
				requestSocket.close();
			} catch(IOException ioException){
			} finally {
				return timeStamp;
			}
		}
	}
}