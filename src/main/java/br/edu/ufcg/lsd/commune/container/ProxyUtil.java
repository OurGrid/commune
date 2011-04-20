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
package br.edu.ufcg.lsd.commune.container;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.message.MessageUtil;

public class ProxyUtil {

	/**
	 * The interfaces' methods must return void and not throw exceptions.
	 * All the methods parameters must be Serializable
	 */
	public static <T> void verifyInterfaceMethods(Class<T> interfaceType, List<Class<?>> verifiedClasses) {
		for ( Method method : interfaceType.getMethods() ) {
			
			if ( !method.getReturnType().isAssignableFrom( void.class ) ) {
				throw new InvalidDeploymentException( 
						"The method '" + method + 
						"' must not return a value" );
			}
			
			if ( method.getExceptionTypes().length > 0 ) {
				throw new InvalidDeploymentException( 
						"The method '" + method + 
						"' must not throw any exception" );
			}
			
			Class<?>[] parameters = method.getParameterTypes();
			Type[] types = method.getGenericParameterTypes();
			
			for (int i = 0; i < parameters.length; i++) {
				if ( !ProxyUtil.verifyRemoteParameter(parameters[i]) ) {
					ProxyUtil.verifySerialization(method, parameters[i], verifiedClasses, types[i]);
				}
			}
	
		}
	}

	/**
	 * @param type
	 * @return true if the type is a remote interface, false otherwise
	 * @throws InvalidDeploymentException If the type is a concrete remote object instead of a remote interface
	 */
	static boolean verifyRemoteParameter(Class<?> type) {
		
		if (MessageUtil.hasRemoteInterface(type)) {
	
			if (type.isInterface()) {
				return true;
			} 
			throw new InvalidDeploymentException( 
					"The type '" + type.getName() + "' is a remote object, " +
			"a remote interface should be used instead." );
	
		}
	
		return false;
		
	}

	static void verifySerialization(Method method, Class<?> type, List<Class<?>> verifiedClasses, Type gType) {
		
		if (!verifiedClasses.contains(type)) {
	    	
			if (MessageUtil.hasRemoteInterface(type)) {
				
				throw new InvalidDeploymentException( 
							"Error on deployment of method " + method + ": The type '" + type.getName() + "' is a remote type. ");
			
			} else if (type.isArray()) {
	    		
				Class<?> dataType = type.getComponentType();
				if ( !verifyRemoteParameter(dataType) ) {
					verifySerialization(method, dataType, verifiedClasses, dataType);
				}	
				
			} else if (Serializable.class.isAssignableFrom(type)) {
	    		verifiedClasses.add(type);
	    		ProxyUtil.verifyFieldsSerialization(method, type, verifiedClasses);
	    	} else if (type.isPrimitive()) {
	    		verifiedClasses.add(type);
	    	} else if (Map.class.isAssignableFrom(type) || List.class.isAssignableFrom(type) || 
	    			Set.class.isAssignableFrom(type)) {
	    		ProxyUtil.checkCollection(method, verifiedClasses, gType, type);
	    	} else {
				throw new InvalidDeploymentException( 
						"Error on deployment of method " + method + ": The type '" + type.getName() + "' is not Serializable" );
	    	}
			
		}
	}

	/**
	 * @param method 
	 * @param type
	 * @param verifiedClasses
	 * @param gType
	 * @param clazz 
	 */
	static void checkCollection(Method method, List<Class<?>> verifiedClasses,
			Type gType, Class<?> clazz) {
		
		if (gType instanceof Class<?>) {
			throw new InvalidDeploymentException( 
					"Error on deployment of method " + method + ": The collection '" + gType.toString() + "' has no generic declaration" );
		}
		
		Type[] actualTypeParams = ((ParameterizedType)gType).getActualTypeArguments();
		
		if (Map.class.isAssignableFrom(clazz)) {
			//In case of Map, we should not allow the key to be a remote object
			ProxyUtil.verifyGenericParameter(method, verifiedClasses, actualTypeParams[0], false);
			ProxyUtil.verifyGenericParameter(method, verifiedClasses, actualTypeParams[1], true);
		} else {
			ProxyUtil.verifyGenericParameter(method, verifiedClasses, actualTypeParams[0], true);
		}
		
	}

	/**
	 * @param method 
	 * @param verifiedClasses
	 * @param typeVar
	 * @param acceptRemoteType if true, it throws a {@link InvalidDeploymentException}
	 * if typeVar is a remote type.
	 */
	static void verifyGenericParameter(Method method, List<Class<?>> verifiedClasses,
			Type typeVar, boolean acceptRemoteType) {
		
		if (typeVar instanceof WildcardType) {
			WildcardType wType = (WildcardType) typeVar;
			Type[] upperBounds = wType.getUpperBounds();
			
			if (upperBounds.length > 1) {
				throw new InvalidDeploymentException( 
						"Error on deployment of method " + method + ": The type '" + typeVar.toString() + "' has a generic declaration with more than one upper bound." );
			}
			
			Type upperType = upperBounds[0];
			
			if (!(upperType instanceof Class<?>)) {
				throw new InvalidDeploymentException( 
						"Error on deployment of method " + method + ": The type '" + upperType.toString() + "' must be a single class." );
			}
			
			typeVar = upperType;
		} 
		
		
		if ( !verifyRemoteParameter((Class<?>)typeVar) )  {
			verifySerialization(method, (Class<?>)typeVar, verifiedClasses, typeVar);
		} else if (!acceptRemoteType) {
			throw new InvalidDeploymentException( 
					"Error on deployment of method " + method + ": The type '" + typeVar.toString() + "' must not be a remote type." );
		}
		
	}

	static void verifyFieldsSerialization(Method method, Class<?> type, List<Class<?>> verifiedClasses) {
		Field[] members = type.getDeclaredFields();
		for (Field member : members) {
			if ( !Modifier.isStatic(member.getModifiers()) && 
					!Modifier.isTransient(member.getModifiers()) ) {
				if (MessageUtil.isCollection(member.getType())) {
					Class<?> collectionType = MessageUtil.getCollectionType(member.getType(), member.getGenericType());
					if (collectionType != null) {
						verifySerialization(method, collectionType, verifiedClasses, collectionType);
					}
				} else {
					verifySerialization(method, member.getType(), verifiedClasses, member.getGenericType());
				}
			}
		}
	}

	public static Object createProxy(Module sourceModule, ServiceID stubSID, ClassLoader classloader, 
			Class<?>... interfaceTypes) {
		
	
		List<Class<?>> verifiedClasses = new ArrayList<Class<?>>(); //Serialization
		
		for (int i = 0; i < interfaceTypes.length; i++) {
			Class<?> interfaceType = interfaceTypes[i];
			
			//The types must be Java Interfaces, and annotated with @Remote or have a annotated super interface
			if (interfaceType.isInterface() && MessageUtil.hasRemoteInterface(interfaceType)) {
				verifyInterfaceMethods(interfaceType, verifiedClasses);
			} else {
				throw new IllegalArgumentException( interfaceType.getName() + " is not a remote interface." );
			}
		}
		
	            
	    ApplicationMessageCreator invocationHandler = new ApplicationMessageCreator(sourceModule, stubSID);
	
	    Class<?> proxyClass = Proxy.getProxyClass(classloader, interfaceTypes);
	    Object proxy = null;
	    
	    try {
	        Constructor<?> constructor = proxyClass.getConstructor( new Class[]{InvocationHandler.class} );
	        proxy = constructor.newInstance( new Object[]{invocationHandler});
			
	    } catch (Exception e) {
	    	throw new CommuneRuntimeException("Error while creating stub for " + stubSID, e);
	    }
		return proxy;
	}

}
