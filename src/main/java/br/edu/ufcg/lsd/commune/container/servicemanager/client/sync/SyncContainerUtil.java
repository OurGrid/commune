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
package br.edu.ufcg.lsd.commune.container.servicemanager.client.sync;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class SyncContainerUtil {

	public static final long POLLING_TIMEOUT = 30L;

	/**
	 * Polls the blocking queue, waiting up to the POLLING_TIMEOUT (in seconds) if necessary 
	 * for an element to become available. 
	 * @param <T>
	 * @param queue
	 * @param clazz
	 * @throws RuntimeException if interrupted while waiting. 
	 * @return The removed object from the blocking queue, null in case of timeout
	 */
	public static <T> T waitForResponseObject(BlockingQueue<Object> queue, Class<T> clazz) {
		return waitForResponseObject(queue, clazz, POLLING_TIMEOUT);
	}
	
	/**
	 * Polls the blocking queue, waiting up to the timeout if necessary 
	 * for an element to become available. 
	 * @param <T>
	 * @param queue
	 * @param clazz
	 * @param timeout
	 * @throws RuntimeException if interrupted while waiting. 
	 * @return The removed object from the blocking queue, null in case of timeout
	 */
	@SuppressWarnings("unchecked")
	public static <T> T waitForResponseObject(BlockingQueue<Object> queue, Class<T> clazz, long timeout) {
		T takingResponse = null;
		try {
			takingResponse = (T) queue.poll(timeout, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		
		return takingResponse;
	}
	
	public static Object waitForeverForResponseObject(BlockingQueue<Object> queue) {
		Object takingResponse = null;
		try {
			takingResponse = queue.take();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		
		return takingResponse;
	}
	
	/**
	 * Polls the blocking queue, waiting as long as necessary for an element to become available. 
	 * @param <T>
	 * @param queue
	 * @param clazz
	 * @return The removed object from the blocking queue
	 */
	@SuppressWarnings("unchecked")
	public static <T> T busyWaitForResponseObject(BlockingQueue<Object> queue, Class<T> clazz) {
		T takingResponse = null;
		try {
			takingResponse = (T) queue.take();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		
		return takingResponse;
	}
	
	public static void putResponseObject(BlockingQueue<Object> queue, Object object) {
		try {
			queue.put(object);
		} catch (InterruptedException e) {
		}
	}
	
}
