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
package br.edu.ufcg.lsd.commune.network.certification.providers;

import java.lang.reflect.Constructor;

import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.network.certification.CertificationProperties;

public class CertificationDataProviderFactory {

	/**
	 * Factory method for {@link CertificationDataProvider}
	 * Uses reflection methods for instantiating classes.
	 * @param className Full name for {@link CertificationDataProvider} implementation
	 * @param context Context for this container
	 * @return {@link CertificationDataProvider} created
	 */
	@SuppressWarnings("unchecked")
	public CertificationDataProvider createCertificationDataProvider(
			ModuleContext context) {
		
    	if (context == null) {
    		throw new IllegalArgumentException( "The container context is mandatory" );
    	}
		
		String providerClass = context.getProperty(
				CertificationProperties.PROP_CERT_PROVIDER_CLASS);
		
		if (providerClass == null) {
			//Using FileCertificationProvider as default 
			providerClass = FileCertificationDataProvider.class.getName();
		}
		
		try {
			
			Class<CertificationDataProvider> clazz = 
				(Class<CertificationDataProvider>) Class.forName(providerClass);
			
			Constructor<CertificationDataProvider> constructor = clazz.getConstructor(
					ModuleContext.class);
			
			CertificationDataProvider certificationDataProvider = constructor.newInstance(context);
			
			return certificationDataProvider;
			
		} catch (Throwable e) {
			throw new IllegalArgumentException(e);
		}
		
		
	}
	
}
