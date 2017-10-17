/*******************************************************************************
 * Copyright (c) 2017 Diamond Light Source Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
