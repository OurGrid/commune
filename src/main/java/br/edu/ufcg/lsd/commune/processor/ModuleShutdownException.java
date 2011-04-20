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
package br.edu.ufcg.lsd.commune.processor;


public class ModuleShutdownException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
    public ModuleShutdownException(){
        
    }

    public ModuleShutdownException(String message) {
        super(message);
    }

    public ModuleShutdownException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModuleShutdownException(Throwable cause) {
        super(cause);
    }
}