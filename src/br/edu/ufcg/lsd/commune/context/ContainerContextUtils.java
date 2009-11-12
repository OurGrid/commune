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
package br.edu.ufcg.lsd.commune.context;

import java.io.File;

import br.edu.ufcg.lsd.commune.ModuleProperties;

/**
 *
 */
public class ContainerContextUtils {

	/**
	 * @param context
	 * @param filePath
	 * @return
	 */
	public static String normalizeFilePath(ModuleContext context, String filePath) {
		return normalizeFilePath(context.getProperty(ModuleProperties.PROP_CONFDIR), filePath);
	}
	
	/**
	 * @param confDir
	 * @param filePath
	 * @return
	 */
	public static String normalizeFilePath(String confDir, String filePath) {
		if (filePath == null) {
			return null;
		}
		File certFile = new File(filePath);
		if (!certFile.isAbsolute() && confDir != null) {
			return confDir + File.separator + filePath;
		}
		
		return filePath;
	}
	
}
