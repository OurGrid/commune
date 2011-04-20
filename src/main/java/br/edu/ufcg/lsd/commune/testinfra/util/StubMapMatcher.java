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
package br.edu.ufcg.lsd.commune.testinfra.util;

import java.io.Serializable;
import java.util.Map;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;

import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class StubMapMatcher implements IArgumentMatcher {

	
	private TestableModule container;
	private final Map<Serializable,ServiceID> mapIDs;
	
	
	StubMapMatcher(TestableModule module, Map<Serializable,ServiceID> mapIDs) {
		this.container = module;
		this.mapIDs = mapIDs;
	}


	@SuppressWarnings("unchecked")
	public boolean matches(Object obj) {
		Map<Serializable,?> objMap = (Map<Serializable,?>) obj;
		
		for (Serializable object : objMap.keySet()) {
			
			Object objValue = objMap.get(object);
			ServiceID mapID = mapIDs.get(object);
			
			ServiceID objID = container.getStubServiceID(objValue);
			
			if (objID == null || !objID.equals(mapID)) {
				return false;
			}
		}
		
		return true;
	}


	public static <T> T eqMapRef(TestableModule module, Class<T> clazz, Map<Serializable,ServiceID> mapIDs) {
		StubMapMatcher messageMatcher = new StubMapMatcher(module, mapIDs);
		EasyMock.reportMatcher(messageMatcher);
		return null;
	}

	public void appendTo(StringBuffer arg0) {}
}