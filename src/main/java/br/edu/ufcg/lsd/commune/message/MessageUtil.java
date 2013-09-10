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
package br.edu.ufcg.lsd.commune.message;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
import br.edu.ufcg.lsd.commune.api.Remote;

//TODO refactoring
public class MessageUtil {

	public static boolean isRemote(Object parameter, Class<?> clazz) {
		boolean hasRemoteInterface = hasRemoteInterface(clazz);
		
		if (parameter == null && !hasRemoteInterface) {
			return false;
		}
		
		return hasRemoteInterface;
	}
	
	public static boolean isCollection(Class<?> clazz) {
		return Map.class.isAssignableFrom(clazz) || List.class.isAssignableFrom(clazz) || 
    			Set.class.isAssignableFrom(clazz) || clazz.isArray();
	}
	
	public static Class<?> getCollectionType(Class<?> clazz, Type gType) {
		if (clazz.isArray()) {
			return clazz.getComponentType();
		}
		
		int valueTypeIndex = Map.class.isAssignableFrom(clazz) ? 1 : 0;
		
		if (gType instanceof Class) {
			return null;
		}
		
		Type typeArgument = ((ParameterizedType)gType).getActualTypeArguments()[valueTypeIndex];
		
		if (typeArgument instanceof Class) {
			return (Class<?>)typeArgument;
		}
		
		if (typeArgument instanceof WildcardType) {
			WildcardType wType = (WildcardType) typeArgument;
			return (Class<?>) wType.getUpperBounds()[0];
		}
		
		if (typeArgument instanceof ParameterizedTypeImpl) {
			ParameterizedTypeImpl wType = (ParameterizedTypeImpl) typeArgument;
			return getCollectionType(wType.getRawType(), wType);
		}
		
		return null;
	}
	
	public static boolean hasRemoteInterface(Class<?> clazz) {
		if (hasRemoteAnnotation(clazz)) {
			return true;
		}
		
		Class<?>[] interfaces = clazz.getInterfaces();
		
		for (int i = 0; i < interfaces.length; i++) {
			Class<?> superInterface = interfaces[i];
			if (hasRemoteAnnotation(superInterface) || hasRemoteInterface(superInterface)) {
				return true;
			} 
		}
		
		return false;
	}

	public static boolean hasRemoteAnnotation(Class<?> typeInterface) {
		return typeInterface.isInterface() && typeInterface.getAnnotation(Remote.class) != null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] asArray(List<T> list) {
		T[] asArray = (T[]) new Object[list.size()];
		int i = 0;
		for (T t : list) {
			asArray[i++] = t;
		}
		return asArray;
	}

	public static <T> Class<?>[] asClassArray(List<Class<?>> list) {
		Class<?>[] asArray = new Class[list.size()];
		int i = 0;
		for (Class<?> t : list) {
			asArray[i++] = t;
		}
		return asArray;
	}

	public static List<Class<?>> getAllInterfaces(Class<?> type) {
		List<Class<?>> allInterfaces = new ArrayList<Class<?>>(); 
		
		if (!type.isInterface()) {
			Class<?> superclass = type.getSuperclass();
			if (superclass != null) {
				List<Class<?>> superIfs = getAllInterfaces(superclass);
				for (Class<?> superIf : superIfs) {
					if (!allInterfaces.contains(superIf)) {
						allInterfaces.add(superIf);						
					}
				}
			}
		}
		
		Class<?>[] interfaces = type.getInterfaces();
		for (Class<?> interf : interfaces) {
			if (!allInterfaces.contains(interf)) {
				allInterfaces.add(interf);						
			}
			
			List<Class<?>> interfIfs = getAllInterfaces(interf);
			for (Class<?> interfIf : interfIfs) {
				if (!allInterfaces.contains(interfIf)) {
					allInterfaces.add(interfIf);						
				}
			}
		}
		
		return allInterfaces; 
	}
	
	public static List<Class<?>> getRemoteInterfaces(Class<?> type) {
		List<Class<?>> allInterfaces = getAllInterfaces(type);
		List<Class<?>> remoteInterfaces = new ArrayList<Class<?>>(); 
		
		for (Class<?> interf : allInterfaces) {
			if (hasRemoteAnnotation(interf) && !remoteInterfaces.contains(interf)) {
				remoteInterfaces.add(interf);
			}
		}
		
		return remoteInterfaces;
	}
	
	public static boolean isRemoteType(Class<?> type) {
		return !getRemoteInterfaces(type).isEmpty() || hasRemoteAnnotation(type);
	}

	public static List<Method> getAllMethods(Class<?> type) {
		List<Method> allMethods = new ArrayList<Method>();
		getAllMethods(type, allMethods);
		
		Class<?> superClass = type.getSuperclass();
		if (superClass != null) {
			getAllMethods(superClass, allMethods);
		}
		
		return allMethods;
	}

	private static void getAllMethods(Class<?> type, List<Method> allMethods) {
		for (Method method : type.getMethods()) {
			if (!allMethods.contains(method)) {
				allMethods.add(method);
			}
		}
	}

	public static boolean equals(Method a, Method b) {
		if (a.getName().equals(b.getName())) {
			if (!a.getReturnType().equals(b.getReturnType())) {
				return false;
			}
	
			Class<?>[] paramsA = a.getParameterTypes();
			Class<?>[] paramsB = b.getParameterTypes();
			if (paramsA.length == paramsB.length) {
				for (int i = 0; i < paramsA.length; i++) {
					if (paramsA[i] != paramsB[i]){
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	public static Method getRemoteDeclaration(Method methodToTest, Class<?> type) {
		List<Class<?>> remoteInterfaces = getRemoteInterfaces(type);
		
		for (Class<?> remoteInterface : remoteInterfaces) {
			Method[] methods = remoteInterface.getMethods();
			for (Method method : methods) {
				if (equals(method, methodToTest)) {
					return method;
				}
			}
		}
		return null;
	}

	public static boolean isStubParameter(Object parameterValue) {
		return parameterValue instanceof StubParameter;
	}
}
