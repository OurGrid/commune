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
 * Class created to receive the information needed to describe the result of a
 * control operation.
 * 
 * @see ModuleManager
 * @see ModuleManagerClient
 */
public class ControlOperationResult implements Serializable {


	private static final long serialVersionUID = 1L;
	
	private Serializable result = null;
	private Exception errorCause = null;
	
	/**
	 * Constructor used in cases when an error occur.
	 * @param result The cause of the error that occurs during the execution
	 * @param errorCause The cause of the error that occurs during the execution
	 */
	public ControlOperationResult(Exception errorCause){
		this(null, errorCause);
	}
	
	/**
	 * Constructor used in where a return result is needed.
	 * @param result The cause of the error that occurs during the execution
	 */
	public ControlOperationResult(Serializable result) {
		this(result, null);
	}
	
	/**
	 * Constructor used in where a return result is needed and also an error ocurred.
	 * @param result The cause of the error that occurs during the execution
	 * @param errorCause The cause of the error that occurs during the execution
	 */
	public ControlOperationResult(Serializable result, Exception errorCause) {
		this.result = result;
		this.errorCause = errorCause;
	}
	
	/**
	 * Default Constructor
	 */
	public ControlOperationResult() {
		this(null, null);
	}

	/**
	 * @return If, during the execution of the operation, an error occurs, so
	 *         this method will return the <code>Throwable</code> that
	 *         represents the cause of the error.
	 */
	public Exception getErrorCause() {

		return errorCause;
	}
	
	/**
	 * Sets the cause of the error that occurs during the execution.
	 * 
	 * @param errorCause
	 */
	public void setErrorCause( Exception errorCause ) {

		this.errorCause = errorCause;
	}
	
	/**
	 * @return True in error ocurred during the execution of the operation,
	 *         false otherwise.
	 */
	public boolean hasAnErrorOcurred() {
		return this.errorCause != null;
	}
	
	/**
	 * Get's the result for this operation.
	 * 
	 * @return Result
	 */
	public Serializable getResult() {
		
		return result;
	}
	
	/**
	 * Set's the result for this operation.
	 * 
	 * @param result Result
	 */
	public void setResult( Serializable result ) {
		this.result = result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((errorCause == null) ? 0 : errorCause.hashCode());
		result = prime * result
				+ ((this.result == null) ? 0 : this.result.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ControlOperationResult other = (ControlOperationResult) obj;
		if (errorCause == null) {
			if (other.errorCause != null)
				return false;
		} else if (!errorCause.equals(other.errorCause))
			return false;
		if (result == null) {
			if (other.result != null)
				return false;
		} else if (!result.equals(other.result))
			return false;
		return true;
	}
}