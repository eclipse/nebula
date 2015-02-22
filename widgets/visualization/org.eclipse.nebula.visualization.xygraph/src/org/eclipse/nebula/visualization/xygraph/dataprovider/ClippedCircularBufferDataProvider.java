/*******************************************************************************
 * Copyright (c) 2013 Max Hohenegger.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Max Hohenegger - initial API and implementation - Bug 418168
 *******************************************************************************/
package org.eclipse.nebula.visualization.xygraph.dataprovider;

import org.eclipse.nebula.visualization.xygraph.dataprovider.CircularBufferDataProvider;
import org.eclipse.nebula.visualization.xygraph.dataprovider.ISample;
import org.eclipse.nebula.visualization.xygraph.linearscale.Range;

/**
 * {@link CircularBufferDataProvider} with a limited window on the trace data
 * for computing data ranges.
 */
public class ClippedCircularBufferDataProvider extends CircularBufferDataProvider {
	private int clippingWindow;

	public ClippedCircularBufferDataProvider(boolean chronological, int bufferSize, int clippingWindow) {
		super(chronological);
		setBufferSize(bufferSize);
		setClippingWindow(clippingWindow);
	}

	public void setClippingWindow(int clippingWindow) {
		assert clippingWindow > 0;
		assert clippingWindow <= getSize();
		this.clippingWindow = clippingWindow;
	}

	public int getClippingWindow() {
		return clippingWindow;
	}

	/**
	 * Ranges are computed from local min/max values from within the given
	 * window.
	 */
	@Override
	protected void updateDataRange() {
		if (!dataRangedirty)
			return;
		dataRangedirty = false;
		if (getSize() > 0) {
			int lowerBound = 0;
			if (getSize() > clippingWindow) {
				lowerBound = (getSize() - 1) - clippingWindow;
			}

			double xMin = getSample(lowerBound).getXValue();
			double yMin = getSample(lowerBound).getYValue();
			double xMax = xMin;
			double yMax = yMin;

			for (int i = lowerBound + 1; i < getSize(); i++) {
				ISample dp = getSample(i);

				if (xMin > dp.getXValue() - dp.getXMinusError())
					xMin = dp.getXValue() - dp.getXMinusError();
				if (xMax < dp.getXValue() + dp.getXPlusError())
					xMax = dp.getXValue() + dp.getXPlusError();

				if (yMin > dp.getYValue() - dp.getYMinusError())
					yMin = dp.getYValue() - dp.getYMinusError();
				if (yMax < dp.getYValue() + dp.getYPlusError())
					yMax = dp.getYValue() + dp.getYPlusError();
			}

			xDataMinMax = new Range(xMin, xMax);
			yDataMinMax = new Range(yMin, yMax);
		} else {
			xDataMinMax = null;
			yDataMinMax = null;
		}
	}

}
