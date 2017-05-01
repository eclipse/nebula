/*******************************************************************************
 * Copyright (c) 2017 Diamond Light Source and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.nebula.visualization.xygraph.linearscale;

import java.util.List;

import org.eclipse.draw2d.Graphics;
import org.eclipse.nebula.visualization.xygraph.linearscale.AbstractScale.LabelSide;
import org.eclipse.nebula.visualization.xygraph.util.SWTConstants;

/**
 * Linear scale tick marks 2. Diamond Light Source implementation for drawing X
 * and Y tick marks.
 *
 * @author Baha El-Kassaby/Peter Chang - Diamond light Source contributions
 **/
public class LinearScaleTickMarks2 extends LinearScaleTickMarks {

	/**
	 * Constructor.
	 *
	 * @param chart
	 *            the chart
	 * @param style
	 *            the style
	 * @param scale
	 *            the scale
	 */
	public LinearScaleTickMarks2(LinearScale scale) {
		super(scale);
	}

	/**
	 * Draw the X tick marks.
	 *
	 * @param gc
	 *            the graphics context
	 * @param tickLabelPositions
	 *            the tick label positions
	 * @param tickLabelSide
	 *            the side of tick label relative to tick marks
	 * @param width
	 *            the width to draw tick marks
	 * @param height
	 *            the height to draw tick marks
	 */
	@Override
	protected void drawXTickMarks(Graphics gc, List<Integer> tickLabelPositions, LabelSide tickLabelSide, int width,
			int height) {
		// draw tick marks
		gc.setLineStyle(SWTConstants.LINE_SOLID);
		ITicksProvider ticks = getScaleProvider().getTicksProvider();
		int imax = ticks.getMajorCount();
		if (getScaleProvider().isLogScaleEnabled()) {
			int y;
			for (int i = 0; i < imax; i++) {
				int x = ticks.getPosition(i);
				int tickLength = ticks.isVisible(i) ? MAJOR_TICK_LENGTH : MINOR_TICK_LENGTH;
				y = tickLabelSide == LabelSide.Primary ? 0 : height - 1 - LINE_WIDTH - tickLength;

				// draw minor ticks for log scale
				if (ticks.isVisible(i) || getScaleProvider().isMinorTicksVisible())
					gc.drawLine(x, y, x, y + tickLength);
			}

			// draw minor ticks for log scale
			if (getScaleProvider().isMinorTicksVisible()) {
				final int start = getScaleProvider().getTicksProvider().getHeadMargin();
				y = tickLabelSide == LabelSide.Primary ? 0 : height - 1 - LINE_WIDTH - MINOR_TICK_LENGTH;
				int jmax = ticks.getMinorCount();
				for (int j = 0; j < jmax; j++) {
					int x = ticks.getMinorPosition(j);
					if (x >= start && x < width)
						gc.drawLine(x, y, x, y + MINOR_TICK_LENGTH);
				}
			}
		} else {
			int y = tickLabelSide == LabelSide.Primary ? 0 : height - 1 - LINE_WIDTH - MAJOR_TICK_LENGTH;
			for (int i = 0; i < imax; i++) {
				int x = ticks.getPosition(i);
				gc.drawLine(x, y, x, y + MAJOR_TICK_LENGTH);
			}

			// draw minor ticks for linear scale
			if (getScaleProvider().isMinorTicksVisible()) {
				final int start = getScaleProvider().getTicksProvider().getHeadMargin();
				if (tickLabelSide == LabelSide.Secondary) {
					y = height - 1 - LINE_WIDTH - MINOR_TICK_LENGTH;
				}
				int jmax = ticks.getMinorCount();
				for (int j = 0; j < jmax; j++) {
					int x = ticks.getMinorPosition(j);
					if (x >= start && x < width)
						gc.drawLine(x, y, x, y + MINOR_TICK_LENGTH);
				}
			}
		}

		// draw scale line
		if (getScaleProvider().isScaleLineVisible()) {
			if (tickLabelSide == LabelSide.Primary) {
				gc.drawLine(getScaleProvider().getMargin(), 0, width - getScaleProvider().getMargin(), 0);
			} else {
				gc.drawLine(getScaleProvider().getMargin(), height - 1, width - getScaleProvider().getMargin(), height - 1);
			}
		}
	}

	/**
	 * Draw the Y tick marks.
	 *
	 * @param tickLabelPositions
	 *            the tick label positions
	 * @param tickLabelSide
	 *            the side of tick label relative to tick marks
	 * @param width
	 *            the width to draw tick marks
	 * @param height
	 *            the height to draw tick marks
	 * @param gc
	 *            the graphics context
	 */
	protected void drawYTickMarks(Graphics gc, List<Integer> tickLabelPositions, LabelSide tickLabelSide, int width,
			int height) {
		// draw tick marks
		gc.setLineStyle(SWTConstants.LINE_SOLID);
		ITicksProvider ticks = getScaleProvider().getTicksProvider();
		if (getScaleProvider().isLogScaleEnabled()) {
			drawMajorTicks(gc, ticks, tickLabelSide, width, height, true);
			if (getScaleProvider().isMinorTicksVisible()) {
				drawMinorTicks(gc, ticks, tickLabelSide, width, height);
			}
		} else {
			drawMajorTicks(gc, ticks, tickLabelSide, width, height, false);
			if (getScaleProvider().isMinorTicksVisible()) {
				drawMinorTicks(gc, ticks, tickLabelSide, width, height);
			}
		}
		// draw scale line
		if (getScaleProvider().isScaleLineVisible()) {
			if (tickLabelSide == LabelSide.Primary) {
				gc.drawLine(width - 1, getScaleProvider().getMargin(), width - 1, height - getScaleProvider().getMargin());
			} else {
				gc.drawLine(0, getScaleProvider().getMargin(), 0, height - getScaleProvider().getMargin());
			}
		}
	}

	/**
	 * Draw major ticks for linear scale if {@link isLogScaleEnabled} is false,
	 * otherwise draw major ticks for log scale.
	 *
	 * @param gc
	 * @param ticks
	 * @param tickLabelSide
	 * @param width
	 * @param height
	 * @param isLogScaleEnabled
	 */
	private void drawMajorTicks(Graphics gc, ITicksProvider ticks, LabelSide tickLabelSide, int width, int height,
			boolean isLogScaleEnabled) {
		int imax = ticks.getMajorCount();
		int x, y;
		if (isLogScaleEnabled) {
			for (int i = 0; i < imax; i++) {
				int tickLength = ticks.isVisible(i) ? MAJOR_TICK_LENGTH : MINOR_TICK_LENGTH;
				x = tickLabelSide == LabelSide.Primary ? width - 1 - LINE_WIDTH - tickLength : LINE_WIDTH;
				y = height - ticks.getPosition(i);
				if (ticks.isVisible(i) || getScaleProvider().isMinorTicksVisible())
					gc.drawLine(x, y, x + tickLength, y);
			}
		} else {
			x = tickLabelSide == LabelSide.Primary ? width - LINE_WIDTH - MAJOR_TICK_LENGTH : LINE_WIDTH;
			for (int i = 0; i < imax; i++) {
				y = height - ticks.getPosition(i);
				gc.drawLine(x, y, x + MAJOR_TICK_LENGTH, y);
			}
		}
	}

	/**
	 * Draw minor ticks for linear scale or log scale
	 *
	 * @param gc
	 * @param ticks
	 * @param tickLabelSide
	 * @param width
	 * @param height
	 */
	private void drawMinorTicks(Graphics gc, ITicksProvider ticks, LabelSide tickLabelSide, int width, int height) {
		final int end = height - getScaleProvider().getTicksProvider().getTailMargin();
		int x = tickLabelSide == LabelSide.Primary ? width - LINE_WIDTH - MINOR_TICK_LENGTH : LINE_WIDTH;
		int y = 0;
		final int jmax = ticks.getMinorCount();
		for (int j = 0; j < jmax; j++) {
			y = height - ticks.getMinorPosition(j);
			if (y >= 0 && y < end)
				gc.drawLine(x, y, x + MINOR_TICK_LENGTH, y);
		}
	}
}
