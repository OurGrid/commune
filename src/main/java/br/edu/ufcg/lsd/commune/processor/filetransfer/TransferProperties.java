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
package br.edu.ufcg.lsd.commune.processor.filetransfer;

public interface TransferProperties {

	public static final String PROP_FILE_TRANSFER_TIMEOUT = "commune.filetransfer.timeout";
	public static final String PROP_FILE_TRANSFER_MAX_OUT = "commune.filetransfer.max.simultaneous";
	
	public static final String PROP_FILE_TRANSFER_NOTIFY_PROGRESS = 
		"commune.filetransfer.notify.progress";
	
	public static final String DEFAULT_FILE_TRANSFER_TIMEOUT = "120";
	public static final String DEFAULT_FILE_TRANSFER_MAX_OUT = "10";
	public static final String DEFAULT_FILE_TRANSFER_NOTIFY_PROGRESS = "no";

}
