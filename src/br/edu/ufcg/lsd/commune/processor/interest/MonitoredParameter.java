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

import java.lang.reflect.Method;

public class MonitoredParameter {

	
	private final Object deployedObject;
	private final Method method;
	private final int parameterIndex;
	private final Class<?> parameterType;
	private final InterestRequirements requirements;

	
	public MonitoredParameter(Object deployedObject, Method method, int parameterIndex, Class<?> parameterType, 
			InterestRequirements requirements) {
		this.deployedObject = deployedObject;
		this.method = method;
		this.parameterIndex = parameterIndex;
		this.parameterType = parameterType;
		this.requirements = requirements;
	}

	
	public Object getDeployedObject() {
		return deployedObject;
	}

	public Method getMethod() {
		return method;
	}

	public int getParameterIndex() {
		return parameterIndex;
	}

	public Class<?> getParameterType() {
		return parameterType;
	}
	
	public InterestRequirements getRequirements() {
		return requirements;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((deployedObject == null) ? 0 : deployedObject.hashCode());
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + parameterIndex;
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
		final MonitoredParameter other = (MonitoredParameter) obj;
		if (deployedObject == null) {
			if (other.deployedObject != null)
				return false;
		} else if (!deployedObject.equals(other.deployedObject))
			return false;
		if (method == null) {
			if (other.method != null)
				return false;
		} else if (!method.equals(other.method))
			return false;
		if (parameterIndex != other.parameterIndex)
			return false;
		return true;
	}


}