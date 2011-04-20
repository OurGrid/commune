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

import java.util.HashMap;
import java.util.Map;

import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.processor.interest.Interest;
import br.edu.ufcg.lsd.commune.processor.interest.InterestManager;
import br.edu.ufcg.lsd.commune.processor.interest.InterestProcessor;
import br.edu.ufcg.lsd.commune.processor.interest.Monitor;

public class TestableInterestManager extends InterestManager {

	
	private static Map<Interest, Runnable> runnables = new HashMap<Interest, Runnable>();
	
	
	public TestableInterestManager(InterestProcessor interestProcessor) {
		super(interestProcessor);
	}

	@Override
	protected Runnable createRunnable(Interest interest) {
		Runnable runnable = super.createRunnable(interest);
		runnables.put(interest, runnable);
		return runnable;
	}
	
	public static void runInterestExecution(Monitor monitor, ServiceID monitoredID) {
		Interest interest = new Interest(monitor, monitoredID, null);
		Runnable runnable = runnables.get(interest);
		runnable.run();
	}
	
	@Override
	protected void scheduleHBRequest(Interest interest, boolean replaced) {
		createRunnable(interest);
	}
	
	public static void clear() {
		runnables.clear();
	}
}