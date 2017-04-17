/*******************************************************************************
 * Copyright (c) 2010, 2017 Oak Ridge National Laboratory and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.nebula.visualization.xygraph.linearscale;

import java.util.ArrayList;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;

/**
 * Linear Scale tick labels.
 * 
 * @author Xihui Chen
 * @author Baha El-Kassaby, Peter Chang: Diamond Light Source contribution
 */
public class LinearScaleTickLabels extends Figure {

	protected static final String MINUS = "-";

	protected ITicksProvider ticks;

	protected IScaleProvider scale;

	/**
	 * Constructor.
	 *
	 * @param linearScale
	 *            the scale
	 */
	protected LinearScaleTickLabels(IScaleProvider linearScale) {

		this.scale = linearScale;
		createLinearScaleTicks();
		setFont(this.scale.getFont());
		setForegroundColor(this.scale.getForegroundColor());
	}

	/**
	 * Create the tick provider for the default axis implementation. To be
	 * overridden if another tick provider is needed.
	 *
	 */
	protected void createLinearScaleTicks() {
		ticks = new LinearScaleTicks(scale);
	}

	/**
	 *
	 * @return the ticks provider used
	 */
	public ITicksProvider getTicksProvider() {
		return ticks;
	}

	/**
	 * @return the gridStepInPixel
	 */
	public int getGridStepInPixel() {
		if (ticks instanceof LinearScaleTicks) {
			return ((LinearScaleTicks) ticks).getGridStepInPixels();
		}
		return -1;
	}

	/**
	 * Gets the tick label positions.
	 * 
	 * @return the tick label positions
	 */
	public ArrayList<Integer> getTickLabelPositions() {
		if (ticks != null) {
			return new ArrayList<Integer> (ticks.getPositions());
		}
		return null;
	}

	/**
	 * @return the tickVisibilities
	 */
	public ArrayList<Boolean> getTickVisibilities() {
		if (ticks != null)
			return new ArrayList<Boolean> (ticks.getVisibilities());
		return null;
	}

	/**
	 * Draw the X tick. To be overridden if needed.
	 *
	 * @param graphics
	 *            the graphics context
	 */
	protected void drawXTick(Graphics graphics) {
		// draw tick labels
		graphics.setFont(scale.getFont());
		for (int i = 0; i < ticks.getPositions().size(); i++) {
			if (ticks.isVisible(i) == true) {
				String text = ticks.getLabel(i);
				int fontWidth = FigureUtilities.getTextExtents(text, getFont()).width;
				int x = (int) Math.ceil(ticks.getLabelPosition(i) - fontWidth / 2.0);// +
																						// offset);
				graphics.drawText(text, x, 0);
			}
		}
	}

	/**
	 * Draw the Y tick. To be overridden if needed.
	 *
	 * @param graphics
	 *            the graphics context
	 */
	protected void drawYTick(Graphics graphics) {
		// draw tick labels
		graphics.setFont(scale.getFont());
		int fontHeight = ticks.getMaxHeight();
		for (int i = 0; i < ticks.getPositions().size(); i++) {
			if (ticks.getLabels().isEmpty()) {
				break;
			}

			if (ticks.isVisible(i)) {
				String label = ticks.getLabel(i);
				int x = 0;
				if (ticks.getLabel(0).startsWith(MINUS) && !label.startsWith(MINUS)) {
					x += FigureUtilities.getTextExtents(MINUS, getFont()).width;
				}
				int y = (int) Math.ceil(scale.getLength() - ticks.getPosition(i) - fontHeight / 2.0);
				graphics.drawText(label, x, y);
			}
		}
	}

	@Override
	protected void paintClientArea(Graphics graphics) {
		graphics.translate(bounds.x, bounds.y);
		graphics.setFont(getFont());
		if (scale.isHorizontal()) {
			drawXTick(graphics);
		} else {
			drawYTick(graphics);
		}

		super.paintClientArea(graphics);
	}

	/**
	 *
	 * @return True is min label is shown
	 */
	public boolean isShowMinLabel() {
		return ticks.isShowMinLabel();
	}

	/**
	 * sets whether min label is shown or not
	 *
	 * @param showMinLabel
	 */
	public void setShowMinLabel(boolean showMinLabel) {
		ticks.setShowMinLabel(showMinLabel);
	}

	/**
	 *
	 * @return True if max label is shown
	 */
	public boolean isShowMaxLabel() {
		return ticks.isShowMaxLabel();
	}

	/**
	 * set whether max label is shown or not
	 *
	 * @param showMaxLabel
	 */
	public void setShowMaxLabel(boolean showMaxLabel) {
		ticks.setShowMaxLabel(showMaxLabel);
	}

	/**
	 * @return the tickLabelMaxLength
	 */
	public int getTickLabelMaxLength() {
		return ticks.getMaxWidth();
	}

	/**
	 * @return the tickLabelMaxHeight
	 */
	public int getTickLabelMaxHeight() {
		return ticks.getMaxHeight();
	}

	/**
	 *
	 * @return the scale
	 */
	public IScaleProvider getScale() {
		return scale;
	}

	/**
	 * sets the type of scale
	 *
	 * @param scale
	 */
	public void setScale(IScaleProvider scale) {
		this.scale = scale;
	}

	/**
	 * Updates the tick labels.
	 *
	 * @param length
	 *            scale tick length (without margin)
	 */
	public Range update(int length) {
		final Range range = scale.getScaleRange();
		ticks.update(range.getLower(), range.getUpper(), length);
		return range;
	}
}