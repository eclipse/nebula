/*******************************************************************************
 * Copyright (c) 2012, 2017 Diamond Light Source Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.nebula.visualization.xygraph.linearscale;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

/**
 * Provides a scale for drawing tick marks.
 * 
 * This allows the scale to draw tick marks different to the range which the
 * scale is operating over.
 * 
 * @author Matthew Gerring
 *
 */
public interface IScaleProvider {

	public Font getFont();

	public Color getForegroundColor();

	public boolean isLogScaleEnabled();

	public Range getScaleRange();

	public boolean isDateEnabled();

	public String format(Object date);

	public boolean isAutoFormat();

	public void setAutoFormat(boolean autoFormat);

	public int getMargin();

	public boolean isHorizontal();

	public double getMajorGridStep();

	public int getMajorTickMarkStepHint();

	public int getMinorTickMarkStepHint();

	public int getTimeUnit();

	public int getLength();

	/**
	 * @param obj
	 * @return dimension of object that has been formatted as a string in
	 *         current font
	 */
	public Dimension calculateDimension(Object obj);

	/**
	 * @return true if axis is a primary one (i.e. left for y and bottom for x)
	 */
	public boolean isPrimary();

	/**
	 * @return true if scale line starts and ends with ticks
	 */
	public boolean hasTicksAtEnds();

	/**
	 * If the scale has labels, this will return the label for the tick value
	 * 
	 * @param value
	 * @return
	 */
	public double getLabel(double value);

	/**
	 * @return true if ticks need to based on axis dataset indexes
	 */
	public boolean isTicksIndexBased();

	/**
	 * @return true if axis labels are customised
	 */
	public boolean areLabelCustomised();
}
