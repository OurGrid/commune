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
package br.edu.ufcg.lsd.commune.container.servicemanager.dao;

import sun.security.provider.certpath.X509CertPath;
import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.control.TimeDataGenerator;

/**
 * Store Component status
 */
public class ContainerDAO extends DAO {

	protected transient TimeDataGenerator timeDataGenerator;
	private X509CertPath myCertPath;
	
	public ContainerDAO(Module application) {
		super(application);
		
		this.timeDataGenerator = new TimeDataGenerator();
		this.timeDataGenerator.setStartTime();
	}

	// Component variables
	private boolean started = false;
	private boolean stopped = false;
	
	/* Peer state machine */
	/**
	 * Set the Component as Started
	 */
	public void startContainer() {
		this.started = true;
	}

	/**
	 * Set the Component as stopped
	 */
	public void stopContainer() {
		stopped = true;
		started = false;
	}
	
	/**
	 * Verifies if the Component is stopped
	 * @return
	 */
	public boolean isStopped() {
		return this.stopped;
	}

	public boolean isStarted() {
		return this.started;
	}
	
	public long getUpTime() {
		timeDataGenerator.setEndTime();
		return timeDataGenerator.getElapsedTimeInSeconds();
	}

	/**
	 * @return the myCertPath
	 */
	public X509CertPath getMyCertPath() {
		return myCertPath;
	}

	/**
	 * @param myCertPath the myCertPath to set
	 */
	public void setMyCertPath(X509CertPath myCertPath) {
		this.myCertPath = myCertPath;
	}



}