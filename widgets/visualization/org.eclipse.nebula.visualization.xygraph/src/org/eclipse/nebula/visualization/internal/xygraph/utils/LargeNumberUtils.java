/*******************************************************************************
 * Copyright (c) 2017 Diamond Light Source Ltd.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.nebula.visualization.internal.xygraph.utils;

public class LargeNumberUtils {

	/**
	 * Ensure value is finite
	 * @param value
	 * @return value between +/- {@link Double#MAX_VALUE}
	 */
	public static double requireFinite(double value) {
		return Double.isFinite(value) ? value : (value > 0 ? Double.MAX_VALUE : -Double.MAX_VALUE);
	}

	/**
	 * Get magnitude of largest value
	 * @param a
	 * @param b
	 * @return maximum magnitude
	 */
	public static double maxMagnitude(double a, double b) {
		return Math.max(Math.abs(a), Math.abs(b));
	}
}
