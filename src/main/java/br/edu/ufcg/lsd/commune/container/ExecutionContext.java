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
package br.edu.ufcg.lsd.commune.container;

import sun.security.provider.certpath.X509CertPath;
import br.edu.ufcg.lsd.commune.identification.CommuneAddress;

public class ExecutionContext {

    private final ObjectDeployment runningObject; 
    private final CommuneAddress currentConsumer;
    private final X509CertPath consumerCertPath;

    public ExecutionContext(ObjectDeployment runningObject, 
    		CommuneAddress currentConsumer, X509CertPath comsumerCertPath) {
		this.runningObject = runningObject;
		this.currentConsumer = currentConsumer;
		this.consumerCertPath = comsumerCertPath;
	}

	public ObjectDeployment getRunningObject() {
		return runningObject;
	}
	
    public CommuneAddress getCurrentConsumer() {
		return currentConsumer;
	}

	/**
	 * @return the senderCertificateChain
	 */
	public X509CertPath getSenderCertPath() {
		return consumerCertPath;
	}
	
}