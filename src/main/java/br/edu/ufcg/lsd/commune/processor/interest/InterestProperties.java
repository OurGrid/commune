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
package br.edu.ufcg.lsd.commune.processor.interest;

public interface InterestProperties {

	/**
	 * The time between heartbeat requests used by the failure detector. It should be
	 * provided in seconds
	 */
	public static final String PROP_WAN_HEARTBEAT_DELAY = "commune.fd.wan.heartbeatdelay";
	
	/**
	 * The detection time that will be used by the failure detector to detect a
	 * failure or recovery of a component. It should be provided in seconds
	 */
	public static final String PROP_WAN_DETECTION_TIME = "commune.fd.wan.detectiontime";

	public static final String PROP_LAN_HEARTBEAT_DELAY = "commune.fd.lan.heartbeatdelay";
	public static final String PROP_LAN_DETECTION_TIME = "commune.fd.lan.detectiontime";
	
	public static final String PROP_LOCAL_HEARTBEAT_DELAY = "commune.fd.localhost.heartbeatdelay";
	public static final String PROP_LOCAL_DETECTION_TIME = "commune.fd.localhost.detectiontime";

}
