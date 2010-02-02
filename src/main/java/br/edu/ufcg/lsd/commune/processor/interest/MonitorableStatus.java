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
package br.edu.ufcg.lsd.commune.processor.interest;

/**
 * This enumeration represents the status of a monitorable, which are:
 * <br>
 * <ol>
 * <li>AVAILABLE: if the monitored entity is up and running. :-) </li>
 * <li>UNAVAILABLE: If the monitored entity is down (has been failed) :-( </li>
 * <ol>
 * 
 * @since November 10, 2005.
 */
public enum MonitorableStatus {

        /* Teh status of a monitorable entity can be one of the followinf options */
        AVAILABLE, UNAVAILABLE;
        
}
