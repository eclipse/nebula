/*******************************************************************************
 * Copyright (c) 2016 Bernhard Wedl and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Bernhard Wedl - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.visualization.xygraph.styleprovider;

import org.eclipse.nebula.visualization.xygraph.dataprovider.ISample;
import org.eclipse.nebula.visualization.xygraph.figures.Trace;
import org.eclipse.nebula.visualization.xygraph.figures.Trace.PointStyle;
import org.eclipse.swt.graphics.Color;

/**
 * Evaluates a sample and provides corresponding style information.
 */
public interface IPointStyleProvider {

	/**
	 * Get the marker color for the given sample
	 * 
	 * @param sample
	 *            sample to be styled
	 * @param trace
	 *            used to return the default properties
	 * @return marker point color
	 */
	public Color getPointColor(ISample sample, Trace trace);

	/**
	 * Get the marker style for the given sample
	 * 
	 * @param sample
	 *            sample to be styled
	 * @param trace
	 *            used to return the default properties
	 * @return marker point style
	 */
	public PointStyle getPointStyle(ISample sample, Trace trace);

	/**
	 * Get the marker size for the given sample
	 * 
	 * @param sample
	 *            sample to be styled
	 * @param trace
	 *            used to return the default properties
	 * @return marker point size
	 */
	public int getPointSize(ISample sample, Trace trace);

}
