/*******************************************************************************
 * Copyright (c) 2017 Diamond Light Source and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.nebula.visualization.xygraph.linearscale;

import org.eclipse.draw2d.Graphics;
import org.eclipse.nebula.visualization.xygraph.figures.DAxis;

/**
 * Linear Scale tick labels used for the new implementation of {@link Axis} :
 * {@link DAxis}.
 *
 * @author Baha El-Kassaby, Peter Chang: Diamond Light Source contribution
 */
public class LinearScaleTickLabels2 extends LinearScaleTickLabels {

	/**
	 * Constructor. (Takes a @link DAxis) as parameter.
	 *
	 * @param linearScale
	 *            the DAxis scale
	 */
	public LinearScaleTickLabels2(DAxis linearScale) {
		super(linearScale, new LinearScaleTicks2(linearScale));
		setTicksIndexBased(linearScale.isTicksIndexBased());
	}

	@Override
	protected void drawXTick(Graphics graphics) {
		// draw tick labels
		ITicksProvider ticks = getTicksProvider();
		final int imax = getTicksProvider().getMajorCount();
		for (int i = 0; i < imax; i++) {
			if (ticks.isVisible(i)) {
				graphics.drawText(ticks.getLabel(i), ticks.getLabelPosition(i), 0);
			}
		}
	}

	@Override
	protected void drawYTick(Graphics graphics) {
		// draw tick labels
		ITicksProvider ticks = getTicksProvider();
		final int imax = ticks.getMajorCount();
		if (imax < 1)
			return;
		final boolean hasNegative = ticks.getLabel(0).startsWith(MINUS);
		final int minus = getScale().getDimension(MINUS).width;
		for (int i = 0; i < imax; i++) {
			if (ticks.isVisible(i)) {
				String text = ticks.getLabel(i);
				int x = (hasNegative && !text.startsWith(MINUS)) ? minus : 0;
				graphics.drawText(text, x, ticks.getLabelPosition(i));
			}
		}
	}

	/**
	 * @param isTicksIndexBased
	 *            if true, make ticks based on axis dataset indexes
	 */
	public void setTicksIndexBased(boolean isTicksIndexBased) {
		((LinearScaleTicks2) getTicksProvider()).setTicksIndexBased(isTicksIndexBased);
	}
}
