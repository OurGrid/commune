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

public interface XMPPProperties {

	/**
	 * The XMPP Server hostname. This is a mandatory property, i.e., it has no default value.
	 */
	public static final String PROP_XMPP_SERVERNAME = "commune.xmpp.servername";

	/** The XMPP Server port */
	public static final String PROP_XMPP_SERVERPORT = "commune.xmpp.serverport";

	/** The XMPP Server secure port */
	public static final String PROP_XMPP_SERVER_SECURE_PORT = "commune.xmpp.serversecureport";

	/** The user name. */
	public static final String PROP_USERNAME = "commune.xmpp.username";

	/**
	 * The user password. It is also a mandatory property.
	 */
	public static final String PROP_PASSWORD = "commune.xmpp.password";

	/**
	 * Enables resource checking before creating connection.
	 */
	public static final String PROP_CHECK_RESOURCE = "commune.xmpp.checkresource";

}
