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
package br.edu.ufcg.lsd.commune.functionaltests;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import br.edu.ufcg.lsd.commune.functionaltests.data.invokeservice.ExtendsSerializable;
import br.edu.ufcg.lsd.commune.functionaltests.data.invokeservice.MyInterface;
import br.edu.ufcg.lsd.commune.functionaltests.data.invokeservice.MyObject;
import br.edu.ufcg.lsd.commune.functionaltests.data.invokeservice.SerializableObject;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.message.Message;
import br.edu.ufcg.lsd.commune.testinfra.util.Context;
import br.edu.ufcg.lsd.commune.testinfra.util.TestWithTestableCommuneContainer;

public class InvokeService extends TestWithTestableCommuneContainer {


	private static final double DOUBLE_PARAMETER = 0.1;
	private static final int INT_PARAMETER = 1;
	private static final Integer INTEGER_PARAMETER = new Integer(2);
	private static final String STRING_PARAMETER = "abc";
	private static final String OTHER_STRING_PARAMETER = "def";
	private DeploymentID source;
	private DeploymentID target;
	private MyInterface mock;


	@Before
	public void init() throws Exception {
		application = createApplication();
		mock = EasyMock.createMock(MyInterface.class);
		MyObject object = new MyObject();
		object.setMock(mock);
		
		application.getContainer().deploy(Context.A_SERVICE_NAME, object);
		
		source = createOtherMessageSource();
		target = application.getDeploymentID(Context.A_SERVICE_NAME);
	}

	@Test
	public void invokeFunctionWithoutParameters() throws Exception {
		Message message = new Message(source, target, "withoutParameters");
		application.getContainer().deliverMessage(message);
		
		mock.withoutParameters();
		EasyMock.replay(mock);
		
		application.getContainer().getServiceConsumer().consumeMessage();
		EasyMock.verify(mock);
	}
	
	@Test
	public void invokeFunctionWithPrimitiveParameter() throws Exception {
		Message message = new Message(source, target, "withPrimitiveParameter");
		message.addParameter(int.class, INT_PARAMETER);
		application.getContainer().deliverMessage(message);
		
		mock.withPrimitiveParameter(INT_PARAMETER);
		EasyMock.replay(mock);
		
		application.getContainer().getServiceConsumer().consumeMessage();
		EasyMock.verify(mock);

	}

	@Test
	public void invokeFunctionWithWrongPrimitiveParameter() throws Exception {
		Message message = new Message(source, target, "withPrimitiveParameter");
		message.addParameter(int.class, STRING_PARAMETER);
		application.getContainer().deliverMessage(message);
		
		EasyMock.replay(mock);
		
		application.getContainer().getServiceConsumer().consumeMessage();
		EasyMock.verify(mock);

	}

	@Test
	public void invokeFunctionWithWrongPrimitiveParameterType() throws Exception {
		Message message = new Message(source, target, "withPrimitiveParameter");
		message.addParameter(String.class, INT_PARAMETER);
		application.getContainer().deliverMessage(message);
		
		EasyMock.replay(mock);
		
		application.getContainer().getServiceConsumer().consumeMessage();
		EasyMock.verify(mock);

	}

	@Test
	public void invokeFunctionWithPrimitiveParameters() throws Exception {
		Message message = new Message(source, target, "withPrimitiveParameters");
		message.addParameter(Integer.class, INT_PARAMETER);
		message.addParameter(String.class, STRING_PARAMETER);
		application.getContainer().deliverMessage(message);
		
		mock.withPrimitiveParameters(INT_PARAMETER, STRING_PARAMETER);
		EasyMock.replay(mock);
		
		application.getContainer().getServiceConsumer().consumeMessage();
		EasyMock.verify(mock);

	}

	@Test
	public void invokeFunctionOverloadString() throws Exception {
		Message message = new Message(source, target, "overload");
		message.addParameter(String.class, STRING_PARAMETER);
		application.getContainer().deliverMessage(message);
		
		mock.overload(STRING_PARAMETER);
		EasyMock.replay(mock);
		
		application.getContainer().getServiceConsumer().consumeMessage();
		EasyMock.verify(mock);
	}

	@Test
	public void invokeFunctionOverloadDouble() throws Exception {
		Message message = new Message(source, target, "overload");
		message.addParameter(double.class, DOUBLE_PARAMETER);
		application.getContainer().deliverMessage(message);
		
		mock.overload(DOUBLE_PARAMETER);
		EasyMock.replay(mock);
		
		application.getContainer().getServiceConsumer().consumeMessage();
		EasyMock.verify(mock);
	}
	
	@Test
	public void invokeFunctionSerializable() throws Exception {
		Message message = new Message(source, target, "serializable");
		message.addParameter(ExtendsSerializable.class, new SerializableObject(STRING_PARAMETER));
		application.getContainer().deliverMessage(message);
		
		mock.serializable(new SerializableObject(STRING_PARAMETER));
		EasyMock.replay(mock);
		
		application.getContainer().getServiceConsumer().consumeMessage();
		EasyMock.verify(mock);
	}
	
	@Test
	public void invokeFunctionSerializableList() throws Exception {
		List<ExtendsSerializable> originalList = createSerializableList();
		Message message = new Message(source, target, "list");
		message.addParameter(List.class, originalList);
		application.getContainer().deliverMessage(message);
		
		List<ExtendsSerializable> expectedList = createSerializableList();
		mock.list(expectedList);
		EasyMock.replay(mock);
		
		application.getContainer().getServiceConsumer().consumeMessage();
		EasyMock.verify(mock);
	}

	private List<ExtendsSerializable> createSerializableList() {
		List<ExtendsSerializable> list = new ArrayList<ExtendsSerializable>();
		list.add(new SerializableObject(STRING_PARAMETER));
		list.add(new SerializableObject(OTHER_STRING_PARAMETER));
		return list;
	}

	@Test
	public void invokeFunctionSerializableSet() throws Exception {
		Set<ExtendsSerializable> originalSet = createSerializableSet();
		Message message = new Message(source, target, "set");
		message.addParameter(Set.class, originalSet);
		application.getContainer().deliverMessage(message);
		
		Set<ExtendsSerializable> expectedSet = createSerializableSet();
		mock.set(expectedSet);
		EasyMock.replay(mock);
		
		application.getContainer().getServiceConsumer().consumeMessage();
		EasyMock.verify(mock);
	}

	private Set<ExtendsSerializable> createSerializableSet() {
		Set<ExtendsSerializable> set = new HashSet<ExtendsSerializable>();
		set.add(new SerializableObject(STRING_PARAMETER));
		set.add(new SerializableObject(OTHER_STRING_PARAMETER));
		return set;
	}
	
	@Test
	public void invokeFunctionSerializableMap() throws Exception {
		Map<Serializable,ExtendsSerializable> originalMap = createSerializableMap();
		Message message = new Message(source, target, "map");
		message.addParameter(Map.class, originalMap);
		application.getContainer().deliverMessage(message);
		
		Map<Serializable,ExtendsSerializable> expectedMap = createSerializableMap();
		mock.map(expectedMap);
		EasyMock.replay(mock);
		
		application.getContainer().getServiceConsumer().consumeMessage();
		EasyMock.verify(mock);
	}

	private Map<Serializable,ExtendsSerializable> createSerializableMap() {
		Map<Serializable,ExtendsSerializable> map = new HashMap<Serializable, ExtendsSerializable>();
		map.put(new SerializableObject(OTHER_STRING_PARAMETER), new SerializableObject(STRING_PARAMETER));
		map.put(new SerializableObject(STRING_PARAMETER), new SerializableObject(OTHER_STRING_PARAMETER));
		return map;
	}
	
	@Test
	public void invokeFunctionSerializableMisc() throws Exception {
		Map<Serializable,ExtendsSerializable> originalMap = createSerializableMap();
		List<String> originalList = createSerializableListString();
		Message message = new Message(source, target, "misc");
		message.addParameter(int.class, INT_PARAMETER);
		message.addParameter(Map.class, originalMap);
		message.addParameter(List.class, originalList);
		message.addParameter(Integer.class, INTEGER_PARAMETER);
		application.getContainer().deliverMessage(message);
		
		Map<Serializable,ExtendsSerializable> expectedMap = createSerializableMap();
		List<String> expectedList = createSerializableListString();
		mock.misc(INT_PARAMETER, expectedMap, expectedList, INTEGER_PARAMETER);
		EasyMock.replay(mock);
		
		application.getContainer().getServiceConsumer().consumeMessage();
		EasyMock.verify(mock);
	}
	
	private List<String> createSerializableListString() {
		List<String> list = new ArrayList<String>();
		list.add(STRING_PARAMETER);
		list.add(OTHER_STRING_PARAMETER);
		return list;
	}

	//TODO Wrong generics, unserializables, etc
}