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

import org.apache.log4j.xml.DOMConfigurator;

public class CommuneLoggerFactory {
	
	private static final String CONSOLE_CATEGORY = "CONSOLE";
	private static final String MESSAGES_CATEGORY = "MESSAGES";
	private static CommuneLoggerFactory instance = null;

	private CommuneLoggerFactory(){
		DOMConfigurator.configure(CommuneLoggerFactory.class.getResource("/log4j.cfg.xml"));
	}
	
	public static CommuneLoggerFactory getInstance(){
		if(instance == null){
			instance = new CommuneLoggerFactory();
		}
		return instance;
	}
	
	public CommuneLogger gimmeALogger(Class<?> clazz){
		return new CommuneLogger(clazz);
	}
	
	public CommuneLogger gimmeALogger(String category){
		return new CommuneLogger(category);
	}
	
	public CommuneLogger getConsoleLogger() {
		return new CommuneLogger(CONSOLE_CATEGORY);
	}
	
	public CommuneLogger getLogger(String category) {
		return new CommuneLogger(category);
	}
	
	public CommuneLogger getMessagesLogger() {
		return new CommuneLogger(MESSAGES_CATEGORY);
	}
}