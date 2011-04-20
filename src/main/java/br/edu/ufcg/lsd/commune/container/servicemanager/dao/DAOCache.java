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
package br.edu.ufcg.lsd.commune.container.servicemanager.dao;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Map;

import br.edu.ufcg.lsd.commune.Module;


/**
 * This class creates and groups all DAO instances.
 */
public class DAOCache {
	
	private Map<Class<?>, DAO> daoInstances = new LinkedHashMap<Class<?>, DAO>();
	
	public <U extends DAO> U createDAO(Module application, Class<U> daoType) {
		
		U dao = null;
		try {
			Constructor<U> constructor = daoType.getConstructor(new Class[] {Module.class});
			dao = constructor.newInstance(new Object[] {application} );
		
		} catch (Exception e) { //Programming errors
			throw new RuntimeException(
					"The DAO " + daoType.getCanonicalName() + " hasn't a constructor with a module parameter", e);
		}
		
		daoInstances.put(daoType, dao);
		return dao;
	}
	
	public <T extends DAO> T getDAO(Class<T> daoType) {
		
		T dao = daoType.cast(daoInstances.get(daoType));
		
		if (dao == null) {
			throw new RuntimeException("DAO " + daoType.getCanonicalName() + " doesn't exist");
		}
		
		return dao;
	}

	public void reset() {
		daoInstances.clear();
	}
}
