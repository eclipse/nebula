/*******************************************************************************
 * Copyright (c) 2008, 2012 Stepan Rutz.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Stepan Rutz - initial implementation
 *    Hallvard Trætteberg - further cleanup and development
 *******************************************************************************/

package org.eclipse.nebula.widgets.geomap;

/**
 * Holds x and y coordinates of type double.
 */
public final class PointD {

	/**
	 * The x coordinate
	 */
	public double x;

	/**
	 * The y coordinate
	 */
	public double y;

	/**
	 * Initializes this PointD with the provided x- and y-coordinates
	 * 
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 */
	public PointD(double x, double y) {
		this.x = x;
		this.y = y;
	}

	@Override
	@SuppressWarnings("nls")
	public String toString() {
		return "PointD {" + x + ", " + y + "}";
	}
}
