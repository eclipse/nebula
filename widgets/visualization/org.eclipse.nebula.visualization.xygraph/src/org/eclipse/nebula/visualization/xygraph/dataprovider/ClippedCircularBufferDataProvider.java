/*******************************************************************************
 * Copyright (c) 2013, 2017 Max Hohenegger and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Max Hohenegger - initial API and implementation - Bug 418168
 *******************************************************************************/
package org.eclipse.nebula.visualization.xygraph.dataprovider;

import org.eclipse.nebula.visualization.xygraph.dataprovider.CircularBufferDataProvider;
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

	@Override
	public Range getDataRange(final boolean positiveOnly, final boolean isXAxis) {
		Range range = null;
		if (getSize() > 0) { // does not handle NaNs
			int lowerBound = 0;
			if (getSize() > clippingWindow && clippingWindow > 0) {
				lowerBound = (getSize() - 1) - clippingWindow;
			}
			range = super.getDataRange(positiveOnly, isXAxis, lowerBound);
		}
		return range;
	}
}
