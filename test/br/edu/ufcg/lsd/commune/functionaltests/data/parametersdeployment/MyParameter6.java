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
package br.edu.ufcg.lsd.commune.functionaltests.data.parametersdeployment;

import java.io.Serializable;

public class MyParameter6 implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private MyParameter6_Inner myParameter6_Inner;

	private Object myObject;
	
	/**
	 * @return the object
	 */
	protected Object getMyObject() {
		return myObject;
	}

	/**
	 * @return the myParameter6_Inner
	 */
	protected MyParameter6_Inner getMyParameter6_Inner() {
		return myParameter6_Inner;
	}
	
	
}
