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

import java.io.Serializable;

import br.edu.ufcg.lsd.commune.context.ModuleContext;


public class InterestRequirements implements Serializable {

	private static final long serialVersionUID = 1L;

	private long detectionTime;
	private long heartbeatDelay;


	public InterestRequirements(ModuleContext context) {
		int defaultDetectionDelay = context.parseIntegerProperty(InterestProperties.PROP_WAN_DETECTION_TIME);
		int defaultHeartbeatDelay = context.parseIntegerProperty(InterestProperties.PROP_WAN_HEARTBEAT_DELAY);

		this.detectionTime = defaultDetectionDelay * 1000;
		this.heartbeatDelay = defaultHeartbeatDelay * 1000;
	}
//TODO validations != 0, detectionTime>heartbeatDelay
	public InterestRequirements(long detectionTime, long heartbeatDelay) {
		validateDetectionTime(detectionTime);
		validateHearbeatDelay(heartbeatDelay);
		
		this.detectionTime = detectionTime * 1000;
		this.heartbeatDelay = heartbeatDelay * 1000;
	}


	private void validateDetectionTime(long detectionTime) {
		if (detectionTime <= 0) {
			throw new IllegalArgumentException( "Invalid detection time: " + detectionTime );
		}
	}

	private void validateHearbeatDelay(long heartbeatDelay) {
		if (heartbeatDelay <= 0) {
			throw new IllegalArgumentException( "Invalid hartbeat delay: " + heartbeatDelay );
		}
	}

	public long getDetectionTime() {
		return this.detectionTime;
	}

	public void setDetectionTime( long detectionTime ) {
		validateDetectionTime(detectionTime);
		this.detectionTime = detectionTime;
	}

	public long getHeartbeatDelay() {
		return this.heartbeatDelay;
	}

	public void setHeartbeatDelay( long heartBeatDelay ) {
		validateHearbeatDelay(heartbeatDelay);
		this.heartbeatDelay = heartBeatDelay;
	}

	@Override
	public boolean equals( Object obj ) {

		if ( obj instanceof InterestRequirements ) {
			InterestRequirements qoSRequirements = (InterestRequirements) obj;
			return getDetectionTime() == qoSRequirements.getDetectionTime()
					&& getHeartbeatDelay() == qoSRequirements.getHeartbeatDelay();
		}
		return false;
	}


	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + (int) (getDetectionTime() ^ (getDetectionTime() >>> 32));
		result = PRIME * result + (int) (getHeartbeatDelay() ^ (getHeartbeatDelay() >>> 32));
		return result;
	}

	@Override
	public String toString() {
		return "DT: " + this.detectionTime + " HBD: " + this.heartbeatDelay;
	}
}