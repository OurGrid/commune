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
package br.edu.ufcg.lsd.commune.container.logging;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.RootLogger;


public class CommuneLogger{
	
	private Logger logger;
	private String userInfo;
	
	public CommuneLogger(Class<?> clazz) {
		logger = RootLogger.getLogger(clazz);
	}
	
	CommuneLogger(String category) {
		logger = RootLogger.getLogger(category);
	}	
	
	public CommuneLogger(String category, String userInfo) {
		logger = RootLogger.getLogger(category);
		this.userInfo = userInfo;
	}
	
	public String getUserInfo(){
		return userInfo;
	}

	public void enter(){
		logger.trace("Entering " + getMethodName());
	}
	
	public void leave(){
		logger.trace("Exiting " + getMethodName());
	}
	
	public void warnException(Exception e){
		logger.warn("Beware... ", e);
	}
	
	public void exception(Exception e){
		logger.error("An Error Ocurred: ", e);
	}
	
	public void info(Object msg){
		logger.info(addUserInfo(msg));
	}
	
	public void error(Object msg){
		logger.error(addUserInfo(msg));
	}

	public void error(Object msg, Throwable e){
		logger.error(addUserInfo(msg), e);
	}
	
	/**
	 * @return
	 */
	protected String getMethodName() {
		Thread currentThread = Thread.currentThread();
		StackTraceElement element = currentThread.getStackTrace()[3];
		return element.getMethodName();
	}


	/**
	 * @param message
	 * @see org.apache.log4j.Category#debug(java.lang.Object)
	 */
	public void debug(Object message) {
		logger.debug(addUserInfo(message));
	}


	/**
	 * @param message
	 * @param t
	 * @see org.apache.log4j.Category#fatal(java.lang.Object, java.lang.Throwable)
	 */
	public void fatal(Object message, Throwable t) {
		logger.fatal(addUserInfo(message), t);
	}


	/**
	 * @param message
	 * @see org.apache.log4j.Logger#trace(java.lang.Object)
	 */
	public void trace(Object message) {
		logger.trace(addUserInfo(message));
	}


	/**
	 * @param message
	 * @see org.apache.log4j.Category#warn(java.lang.Object)
	 */
	public void warn(Object message) {
		logger.warn(addUserInfo(message));
	}


	public void debug(String string, Exception e) {
		logger.debug(addUserInfo(string), e);
	}
	
	private Object addUserInfo(Object msg){
		return "[" + this.userInfo + "] " + msg;
	}
}
