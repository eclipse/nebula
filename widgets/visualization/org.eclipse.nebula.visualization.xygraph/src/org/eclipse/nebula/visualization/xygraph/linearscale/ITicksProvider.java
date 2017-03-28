/*******************************************************************************
 * Copyright (c) 2012, 2017 Diamond Light Source Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.nebula.visualization.xygraph.linearscale;

import java.util.List;

/**
 * 
 *
 */
public interface ITicksProvider {

	/**
	 * Gets the tick positions.
	 * 
	 * @return the tick positions
	 */
	public List<Integer> getPositions();

	/**
	 * @param index
	 * @return tick position
	 */
	public int getPosition(int index);

	/**
	 * @param index
	 * @return tick value
	 */
	public double getValue(int index);

	/**
	 * @param index
	 * @return tick label
	 */
	public String getLabel(int index);

	/**
	 * @param index
	 * @return label position
	 */
	public int getLabelPosition(int index);

	/**
	 * @param index
	 * @return true if tick is visible
	 */
	public boolean isVisible(int index);

	/**
	 * @return number of major ticks
	 */
	public int getMajorCount();

	/**
	 * @param index
	 * @return minor tick position
	 */
	public int getMinorPosition(int index);

	/**
	 * @return number of minor ticks
	 */
	public int getMinorCount();

	/**
	 * Update ticks
	 * 
	 * @param min
	 * @param max
	 * @param length
	 * @return new axis range
	 */
	public Range update(double min, double max, int length);

	/**
	 * 
	 * @return maximum width in pixels of tick labels
	 */
	public int getMaxWidth();

	/**
	 * 
	 * @return maximum height in pixels of tick labels
	 */
	public int getMaxHeight();

	/**
	 * @param min
	 * @param max
	 * @return default format pattern for labels
	 */
	public String getDefaultFormatPattern(double min, double max);

	/**
	 * @return margin in pixel between edge of client area and head of axis line
	 */
	public int getHeadMargin();

	/**
	 * @return margin in pixel between edge of client area and tail of axis line
	 */
	public int getTailMargin();
}
