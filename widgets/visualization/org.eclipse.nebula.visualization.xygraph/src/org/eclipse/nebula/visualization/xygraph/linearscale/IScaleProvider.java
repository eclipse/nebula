/*******************************************************************************
 * Copyright (c) 2012, 2017 Diamond Light Source Ltd.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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

	/**
	 * @return font
	 */
	public Font getFont();

	/**
	 * @return foreground color
	 */
	public Color getForegroundColor();

	/**
	 * @return True if log scale is enabled
	 */
	public boolean isLogScaleEnabled();

	/**
	 * @return scale range
	 */
	public Range getScaleRange();

	/**
	 * @return True if date is enabled
	 */
	public boolean isDateEnabled();

	/**
	 * Formats the given object as a DateFormat if Date is enabled or as a
	 * DecimalFormat. This is based on an internal format pattern given the
	 * object in parameter.
	 *
	 * @param obj
	 *            the object
	 * @return the formatted string
	 */
	public String format(Object obj);

	/**
	 * Formats the given object as a DateFormat if Date is enabled or as a
	 * DecimalFormat. This is based on an internal format pattern given the
	 * object in parameter. When formatting a date, if minOrMaxDate is true as
	 * well as autoFormat, then the SimpleDateFormat us used to format the
	 * object.
	 *
	 * @param obj
	 *            the object
	 * @param minOrMaxDate
	 *            true if it is the min or max date on the scale.
	 * @return the formatted string
	 */
	public String format(Object obj, boolean minOrMaxDate);

	/**
	 * @return True if auto format is on
	 */
	public boolean isAutoFormat();

	/**
	 * @param autoFormat
	 *            the autoFormat to set
	 */
	public void setAutoFormat(boolean autoFormat);

	/**
	 * @return format pattern string
	 */
	public String getFormatPattern();

	/**
	 * @return margin
	 */
	public int getMargin();

	/**
	 * @return True if scale is horizontal
	 */
	public boolean isHorizontal();

	/**
	 * @return major grid step
	 */
	public double getMajorGridStep();

	/**
	 * @return major tick mark step hint
	 */
	public int getMajorTickMarkStepHint();

	/**
	 * @return minor tick mark step hint
	 */
	public int getMinorTickMarkStepHint();

	/**
	 *
	 * @return time unit
	 */
	public int getTimeUnit();

	/**
	 * @return length
	 */
	public int getLength();

	/**
	 * @param obj
	 * @return dimension of object that has been formatted as a string in
	 *         current font
	 */
	public Dimension getDimension(Object obj);

	/**
	 * @return true if axis is a primary one (i.e. left for y and bottom for x)
	 */
	public boolean isPrimary();

	/**
	 * @return true if ticks at end of axis are shown
	 */
	public boolean hasTicksAtEnds();

	/**
	 * If the scale has labels, this will return the label for the tick value,
	 * otherwise returns the value given in parameter.
	 *
	 * @param value
	 * @return double value of label
	 */
	public double getLabel(double value);

	/**
	 * @return true if axis labels are customised
	 */
	public boolean isLabelCustomised();

	/**
	 * Gets the ticks provider
	 *
	 * @return tick provider
	 */
	public ITicksProvider getTicksProvider();

}