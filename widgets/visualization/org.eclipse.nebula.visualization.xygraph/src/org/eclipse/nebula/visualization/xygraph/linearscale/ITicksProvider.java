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

import java.util.List;

/**
 * This interface if used to enable different method for generating tick marks
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
	 * @return the tick visibilities
	 */
	public List<Boolean> getVisibilities();

	/**
	 * @return the tick labels
	 */
	public List<String> getLabels();

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
	 * Update ticks.
	 *
	 * @param min
	 * @param max
	 * @param length
	 * @return null unless the provider has not had cause to change the range -
	 *         in which case, the new range is returned.
	 */
	public Range update(double min, double max, int length);

	/**
	 *
	 * @return maximum width in pixels of tick labels
	 */
	public int getMaxWidth();

	/**
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

	/**
	 * Shows the maximum value of the range as a label
	 *
	 * @return true (default) if shown
	 */
	public boolean isShowMaxLabel();

	/**
	 * sets the visibility of the maximum label
	 * 
	 * @param b
	 *            show maximal value as a label
	 */
	public void setShowMaxLabel(boolean showMaxLabel);

	/**
	 * Shows the minimum value of the range as a label
	 *
	 * @return true (default) if shown
	 */
	public boolean isShowMinLabel();

	/**
	 * Sets the visibility of the minimum label
	 *
	 * @param b
	 *            show minimum value as a label
	 */
	public void setShowMinLabel(boolean showMinLabel);

}