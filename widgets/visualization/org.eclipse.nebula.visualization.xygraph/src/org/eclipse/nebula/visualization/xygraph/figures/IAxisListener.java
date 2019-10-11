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
package org.eclipse.nebula.visualization.xygraph.figures;

import org.eclipse.nebula.visualization.xygraph.linearscale.Range;
import org.eclipse.swt.graphics.Color;

/**
 * A listener on the axis when axis was revalidated.
 * 
 * @author Xihui Chen
 *
 */
public interface IAxisListener {

	/**
	 * This event indicates a change in the axis' value range
	 */
	public void axisRangeChanged(Axis axis, Range old_range, Range new_range);

	/**
	 * This method will be notified by axis whenever the axis is revalidated.
	 */
	public void axisRevalidated(Axis axis);

	public void axisForegroundColorChanged(Axis axis, Color oldColor, Color newColor);

	public void axisTitleChanged(Axis axis, String oldTitle, String newTitle);

	public void axisAutoScaleChanged(Axis axis, boolean oldAutoScale, boolean newAutoScale);

	public void axisLogScaleChanged(Axis axis, boolean old, boolean logScale);

}
