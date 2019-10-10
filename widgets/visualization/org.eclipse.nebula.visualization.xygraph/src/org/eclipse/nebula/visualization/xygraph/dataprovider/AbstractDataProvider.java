/*******************************************************************************
 * Copyright (c) 2010, 2017 Oak Ridge National Laboratory and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.nebula.visualization.xygraph.dataprovider;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.visualization.xygraph.linearscale.Range;

/**
 * This gives the most common implementation of the {@link IDataProvider}
 * interface.
 * 
 * @author Xihui Chen
 * @author Kay Kasemir (synchronization)
 */
public abstract class AbstractDataProvider implements IDataProvider {

	protected boolean chronological = false;

	protected List<IDataProviderListener> listeners;

	protected Range xDataMinMax = null;
	protected Range yDataMinMax = null;

	/**
	 * @param trace
	 *            the trace which the data provider will provide data to.
	 * @param chronological
	 *            true if the data is sorted chronologically on xAxis, which
	 *            means the data is sorted on X Axis.
	 */
	public AbstractDataProvider(boolean chronological) {
		this.chronological = chronological;
		listeners = new ArrayList<IDataProviderListener>();
	}

	/**
	 * @return size
	 */
	public abstract int getSize();

	/**
	 * @param index
	 * @return the ISample element at the given index
	 */
	public abstract ISample getSample(int index);

	@Override
	public Range getXDataMinMax() {
		return getXDataMinMax(false);
	}

	@Override
	public Range getYDataMinMax() {
		return getYDataMinMax(false);
	}

	@Override
	synchronized public Range getXDataMinMax(boolean positiveOnly) {
		if (getSize() <= 0)
			return null;
		xDataMinMax = getDataRange(positiveOnly, true);
		return xDataMinMax;
	}

	@Override
	synchronized public Range getYDataMinMax(boolean positiveOnly) {
		if (getSize() <= 0)
			return null;
		yDataMinMax = getDataRange(positiveOnly, false);
		return yDataMinMax;
	}

	/**
	 * Returns an updated range whenever data changed, for the XAxis or YAxis,
	 * with positive data only or not.
	 *
	 * @param positiveOnly
	 *            if data is positive only (for log scale mode)
	 * @param isXAxis
	 * @param isXAxis
	 *            if true, then this will return the updated range for the
	 *            XAxis, YAxis otherwise
	 */
	public Range getDataRange(final boolean positiveOnly, final boolean isAxis) {
		return getDataRange(positiveOnly, isAxis, 0);
	}

	/**
	 * Returns an updated range whenever data changed, for the XAxis or YAxis,
	 * with positive data only or not.
	 *
	 * @param positiveOnly
	 *            if data is positive only (for log scale mode)
	 * @param isXAxis
	 *            if true, then this will return the updated range for the
	 *            XAxis, YAxis otherwise
	 * @param lowerBound
	 *            by default it should be 0
	 */
	public Range getDataRange(final boolean positiveOnly, final boolean isXAxis, final int lowerBound) {
		Range range = null;
		if (getSize() > 0) { // does not handle NaNs
			double min = Double.POSITIVE_INFINITY;
			double max = positiveOnly ? 0 : Double.NEGATIVE_INFINITY;

			for (int i = lowerBound; i < getSize(); i++) {
				ISample dp = getSample(i);
				double value = isXAxis ? dp.getXValue() - dp.getXMinusError() : dp.getYValue() - dp.getYMinusError();
				if ((!positiveOnly || value > 0) && min > value && (value != 0 || !positiveOnly)) {
					min = value;
				}
				value = isXAxis ? dp.getXValue() + dp.getXPlusError() : dp.getYValue() + dp.getYPlusError();
				if (max < value) {
					max = value;
				}
			}
			if (positiveOnly) {
				// check that the max is greater than its respective
				// minima.
				if (max < min) {
					max = min;
				}
			}
			range = new Range(min, max);
		}
		return range;
	}

	/**
	 * @param chronological
	 *            the chronological to set
	 */
	public void setChronological(boolean chronological) {
		this.chronological = chronological;
	}

	/**
	 * @return true if the order is chronological
	 */
	public boolean isChronological() {
		return chronological;
	}

	/**
	 * Add listener
	 *
	 * @param listener
	 */
	public void addDataProviderListener(final IDataProviderListener listener) {
		if (listeners.contains(listener))
			return;
		listeners.add(listener);
	}

	/**
	 * Remove listener
	 *
	 * @param listener
	 * @return true if this list contained the specified element
	 */
	public boolean removeDataProviderListener(final IDataProviderListener listener) {
		return listeners.remove(listener);
	}

	/**
	 * a data change has occured
	 */
	protected void fireDataChange() {
		for (IDataProviderListener listener : listeners) {
			listener.dataChanged(this);
		}
	}

}
