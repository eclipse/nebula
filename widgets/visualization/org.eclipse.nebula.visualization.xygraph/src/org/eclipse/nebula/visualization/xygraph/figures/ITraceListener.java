/*******************************************************************************
 * Copyright (c) 2010, 2017 Oak Ridge National Laboratory and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.nebula.visualization.xygraph.figures;

import org.eclipse.nebula.visualization.xygraph.figures.Trace.PointStyle;
import org.eclipse.nebula.visualization.xygraph.figures.Trace.TraceType;
import org.eclipse.swt.graphics.Color;

/**
 * A listener on the trace when it is revalidated.
 *
 * @author Xihui Chen
 *
 */
public interface ITraceListener {

	/**
	 * Called when trace name has changed
	 *
	 * @param trace
	 * @param oldName
	 * @param newName
	 */
	void traceNameChanged(Trace trace, String oldName, String newName);

	/**
	 * Called when Y-Axis has changed
	 *
	 * @param trace
	 * @param oldName
	 * @param newName
	 */
	void traceYAxisChanged(Trace trace, Axis oldName, Axis newName);

	/**
	 * Called when trace type has changed
	 *
	 * @param trace
	 * @param old
	 * @param newTraceType
	 */
	void traceTypeChanged(Trace trace, TraceType old, TraceType newTraceType);

	/**
	 * Called when trace color has changed
	 *
	 * @param trace
	 * @param old
	 * @param newColor
	 */
	void traceColorChanged(Trace trace, Color old, Color newColor);

	/**
	 * Called when trace point style has changed
	 *
	 * @param trace
	 * @param old
	 * @param newStyle
	 */
	void pointStyleChanged(Trace trace, PointStyle old, PointStyle newStyle);

	/**
	 * Called when trace width has changed
	 *
	 * @param trace
	 * @param old
	 * @param newWidth
	 */
	void traceWidthChanged(Trace trace, int old, int newWidth);
}
