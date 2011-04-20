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

import java.util.Collection;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;

import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class StubCollectionMatcher implements IArgumentMatcher {

	
	private TestableModule container;
	private final ServiceID[] stubIDs;
	
	
	StubCollectionMatcher(TestableModule module, ServiceID[] stubIDs) {
		this.container = module;
		this.stubIDs = stubIDs;
	}


	public boolean matches(Object obj) {
		Collection<?> col = (Collection<?>) obj;
		
		int i = 0;
		for (Object object : col) {
			ServiceID objID = container.getStubServiceID(object);
			
			if (objID == null || !objID.equals(stubIDs[i++])) {
				return false;
			}
		}
		
		return true;
	}


	public static <T> T eqCollectionRef(TestableModule module, ServiceID... stubIDs) {
		StubCollectionMatcher messageMatcher = new StubCollectionMatcher(module, stubIDs);
		EasyMock.reportMatcher(messageMatcher);
		return null;
	}

	public void appendTo(StringBuffer arg0) {}
}