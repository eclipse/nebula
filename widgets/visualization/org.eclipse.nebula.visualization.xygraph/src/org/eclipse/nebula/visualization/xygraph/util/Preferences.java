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
 * Access to preference settings.
 * 
 * See preferences.ini for details on the available settings
 * 
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Preferences {
	public static final String PROHIBIT_ADVANCED_GRAPHICS = "prohibit_advanced_graphics"; //$NON-NLS-1$
	// useAdvancedGraphics() is called from many drawing operations, so
	// only determine it once
	private static boolean use_advanced_graphics = true;

	public static boolean useAdvancedGraphics() {
		if (use_advanced_graphics) {
			String value = System.getProperty(PROHIBIT_ADVANCED_GRAPHICS); // $NON-NLS-1$
			if (value == null || !value.equals("true")) //$NON-NLS-1$
				return true;
			return false;
		}
		return false;
	}
}
