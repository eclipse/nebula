/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.nebula.visualization.xygraph.util;

/**
 * Implementation loader for RAP/RCP single sourcing.
 * 
 * @author Xihui Chen
 *
 */
public class ImplementationLoader {

	public static Object newInstance(Class<?> type) {
		String name = type.getName();
		Object result = null;
		try {
			// if Interface name ends with a digit
			if (name.matches("^.+?\\d$")) {
				// remove all trailing digits
				name = name.replaceAll("\\d*$", "");
			}
			result = type.getClassLoader().loadClass(name + "Impl").newInstance(); //$NON-NLS-1$
		} catch (Exception e) {

		}
		return result;
	}

}
