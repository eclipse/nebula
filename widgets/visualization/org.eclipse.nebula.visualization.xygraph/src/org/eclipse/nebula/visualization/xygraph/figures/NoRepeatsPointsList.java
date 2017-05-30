/*******************************************************************************
 * Copyright (c) 2017 Diamond Light Source.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * 		Matthew Gerring - initial API and implementation
 * 		Baha El-Kassaby - initial commit
 ******************************************************************************/
package org.eclipse.nebula.visualization.xygraph.figures;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;

/**
 * Class used to manage large amount of points in 1D plots
 *
 * @author Matthew Gerring
 * @author Baha El Kassaby
 *
 */
public class NoRepeatsPointsList extends PointList {

	private static final long serialVersionUID = 8769260981259832495L;

	/**
	 * Does not add the same point twice in a row.
	 *
	 * @param x
	 * @param y
	 */
	public void addPoint(int x, int y) {
		int size = size();
		if (size < 1) {
			super.addPoint(x, y);
			return;
		}
		final Point last = getPoint(size - 1);
		if (x == last.x && y == last.y) {
			return;
		}
		// The Trace class produces slightly wobbly data because of rounding
		// error.
		// We iron this out by ignoring values which are almost the same.
		if (size >= 3) {
			if (Math.abs(x - last.x) <= 1 && Math.abs(y - last.y) <= 1) {
				final Point lastb1 = getPoint(size - 2);
				if (x == lastb1.x && y == lastb1.y) {
					return;
				}
			}
		}
		super.addPoint(x, y);
	}
}
