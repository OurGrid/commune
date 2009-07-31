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
package br.edu.ufcg.lsd.commune.functionaltests.util;

import java.util.HashMap;

import br.edu.ufcg.lsd.commune.container.ContainerContext;

public class TestContext extends ContainerContext {

	public TestContext() {
		super(new HashMap<Object,Object>());
	}
	
	public TestContext(ContainerContext context) {
		super(context.getProperties());
	}

	public void set(String key, String value) {
		currentProperties.put(key, value);
	}
}
