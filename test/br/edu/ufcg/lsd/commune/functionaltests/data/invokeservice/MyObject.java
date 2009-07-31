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
package br.edu.ufcg.lsd.commune.functionaltests.data.invokeservice;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MyObject implements MyInterface {
	
	
	private MyInterface mock;
	

	public void setMock(MyInterface mock) {
		this.mock = mock;
	}
	

	public void list(List<ExtendsSerializable> l) {
		mock.list(l);
	}

	public void map(Map<Serializable, ExtendsSerializable> m) {
		mock.map(m);
	}

	public void misc(int i, Map<Serializable, ExtendsSerializable> m, List<String> l, Integer in) {
		mock.misc(i, m, l, in);
	}

	public void overload(String s) {
		mock.overload(s);
	}

	public void overload(double d) {
		mock.overload(d);
	}

	public void serializable(ExtendsSerializable es) {
		mock.serializable(es);
	}

	public void set(Set<ExtendsSerializable> s) {
		mock.set(s);
	}

	public void withPrimitiveParameter(int i) {
		mock.withPrimitiveParameter(i);
	}

	public void withPrimitiveParameters(Integer i, String s) {
		mock.withPrimitiveParameters(i, s);
	}

	public void withoutParameters() {
		mock.withoutParameters();
	}

}