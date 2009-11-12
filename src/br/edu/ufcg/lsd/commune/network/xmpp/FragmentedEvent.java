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
package br.edu.ufcg.lsd.commune.network.xmpp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import br.edu.ufcg.lsd.commune.message.Message;

public class FragmentedEvent {

	public static final int MESSAGE_SIZE = 128 * 1024;

	private int numOfFragments;
	private byte[][] fragments;
	private int receivedFragments = 0;

	public FragmentedEvent(int numOfFragments) {
		this.numOfFragments = numOfFragments;
		this.fragments = new byte[numOfFragments][];
	}

	public FragmentedEvent(Message event) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(event);
		byte[] bs = baos.toByteArray();

		numOfFragments = (bs.length / MESSAGE_SIZE) + 1;

		fragments = new byte[numOfFragments][];

		int index = 0;

		for (index = 0; index < numOfFragments - 1; index++) {
			fragments[index] = new byte[MESSAGE_SIZE];
			System.arraycopy(bs, index * MESSAGE_SIZE, fragments[index], 0,
					MESSAGE_SIZE);
		}

		fragments[index] = new byte[bs.length - (index * MESSAGE_SIZE)];
		
		System.arraycopy(bs, index * MESSAGE_SIZE, fragments[index], 0,
				bs.length - (index * MESSAGE_SIZE));

	}

	public void receiveFragment(byte[] frag, int fragNumber) throws IOException {
		if (receivedFragments != fragNumber) {
			throw new IOException("Received unexpected fragment.");
		}
		receivedFragments++;
		fragments[fragNumber] = frag;
	}

	public byte[] getFragment(int fragNumber) {
		return fragments[fragNumber];
	}

	public int getTotalNumOfFrags() {
		return fragments.length;
	}
	
	public int getReceivedNumOfFrags() {
		return receivedFragments;
	}
	
	public boolean allFragmentsReceived() {
		return getReceivedNumOfFrags() == getTotalNumOfFrags();
	}

	public Message getMessage() throws IOException {
		if (receivedFragments < numOfFragments)
			throw new IOException("Not all fragments received.");

		byte[] bs = new byte[((fragments.length - 1) * MESSAGE_SIZE)
				+ fragments[fragments.length - 1].length];

		int index = 0;

		for (index = 0; index < numOfFragments - 1; index++) {
			System.arraycopy(fragments[index], 0, bs, index * MESSAGE_SIZE,
					MESSAGE_SIZE);
		}

		System.arraycopy(fragments[index], 0, bs, index * MESSAGE_SIZE,
				bs.length - (index * MESSAGE_SIZE));
		
		ByteArrayInputStream bais = new ByteArrayInputStream(bs);
		ObjectInputStream ois = new ObjectInputStream(bais);

		try {
			return (Message) ois.readObject();
		} catch (ClassNotFoundException e) {
			throw new IOException(e.getMessage());
		}
	}
}