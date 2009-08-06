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
package br.edu.ufcg.lsd.commune.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class PropertiesParser implements ContextParser {

	private Map<Object, Object> properties;

	public PropertiesParser(Map<String, String> properties) {
		Map<Object, Object> context = new HashMap<Object, Object>();
		for (Entry<String, String> entry : properties.entrySet()) {
			context.put(entry.getKey(), entry.getValue());
		}
		this.properties = context;
	}
	
	public Map<Object, Object> parseContext() {
		return properties;
	}

}
