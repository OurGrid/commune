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
package br.edu.ufcg.lsd.commune.container.control;

import java.io.Serializable;

/**
 * Description:This class generates data about the execution of a component
 */
public class TimeDataGenerator implements Serializable {

	/**
	 * Serial identification of the class. It need to be changed only if the
	 * class interface is changed.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The start time held by an instance of this type
	 */
	private long startTime = -1;

	/**
	 * The end time held by an instance of this type
	 */
	private long endTime = -1;

	/**
	 * A text defined by the user of this type that will appear in the
	 * <code>toString()</code> method.
	 */
	private String preText = "";

	
	/**
	 * Constructor method
	 * 
	 * @param text the name of the time collection
	 */
	public TimeDataGenerator( String text ) {
		preText = text;
	}

	public TimeDataGenerator() {
		this(null);
	}
	/**
	 * This method sets the initial part of the caption text of this object
	 * 
	 * @param newText the name of the time collection
	 */
	public void setPreText( String newText ) {
		preText = newText;
	}


	/**
	 * This method returns the initial part of caption text of this object
	 * 
	 * @return A string representing the initial part of caption text of this
	 *         object
	 */
	public String getPreText( ) {
		return preText;
	}


	/**
	 * This method sets the start time of an execution
	 */
	public void setStartTime( ) {
		startTime = System.currentTimeMillis();
	}


	/**
	 * This method sets the end time of an execution
	 */
	public void setEndTime( ) {
		endTime = System.currentTimeMillis();
	}

	/**
	 * Returns a string representation ot this object.
	 */
	public String toString( ) {
		if ( startTime == -1 ) {
			return preText + " unstarted!";
		} else if ( endTime == -1 ) {
			return preText + " unfinished!";
		}

		return preText + " " + (endTime - startTime) + " ms";
	}


	/**
	 * Gets the start time held by this type.
	 * 
	 * @return the start time held by this type.
	 */
	public long getStartTime( ) {
		return startTime;
	}


	/**
	 * Gets the end time held by this type.
	 * 
	 * @return the end time held by this type.
	 */
	public long getEndTime( ) {
		return endTime;
	}
	
	
	public long getElapsedTimeInSeconds() {

		return this.getElapsedTimeInMillis() / 1000;
	}


	public long getElapsedTimeInMillis() {

		if ( !isValid() )
			return 0L;
		return (endTime - startTime);

	}


	/**
	 * This method verifies if the StartTime and EndTime are not a valid time
	 * stamp. By an invalid timestamp we mean that the replica phase were not
	 * started or interrupted during its execution.
	 * 
	 * @return <b>true</b> if StarTime and EndTime timestamps were marked.
	 *         <b>false</b> otherwise.
	 */
	public boolean isValid() {

		return ((this.startTime != -1) && (this.endTime != -1));
	}

}
