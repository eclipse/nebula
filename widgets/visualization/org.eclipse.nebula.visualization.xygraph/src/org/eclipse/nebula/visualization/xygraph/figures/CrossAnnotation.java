/*******************************************************************************
 * Copyright (c) 2013 MARINTEK
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Torkild U. Resheim - Initial implementation
 ******************************************************************************/
package org.eclipse.nebula.visualization.xygraph.figures;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.nebula.visualization.xygraph.dataprovider.ISample;
import org.eclipse.nebula.visualization.xygraph.figures.Annotation;
import org.eclipse.nebula.visualization.xygraph.figures.Axis;
import org.eclipse.nebula.visualization.xygraph.figures.Trace;
import org.eclipse.nebula.visualization.xygraph.util.Preferences;
import org.eclipse.swt.SWT;

/**
 * Variation of annotation that is represented as a cross with an arm length of
 * five pixels.
 *
 * @author Torkild U. Resheim
 */
public class CrossAnnotation extends Annotation {

	final static int CROSS_SIZE = 5;

	public CrossAnnotation(String name, Trace trace) {
		super(name, trace);
	}

	public CrossAnnotation(String name, Axis xAxis, Axis yAxis) {
		super(name, xAxis, yAxis);
	}

	@Override
	public void setCurrentSnappedSample(ISample currentSnappedSample, boolean keepLabelPosition) {
		this.currentSnappedSample = currentSnappedSample;
		repaint();
	}

	@Override
	protected void paintFigure(Graphics graphics) {
		if (trace != null && currentSnappedSample == null && !pointerDragged)
			updateToDefaultPosition();

		if (Preferences.useAdvancedGraphics())
			graphics.setAntialias(SWT.ON);

		graphics.setForegroundColor(getForegroundColor());
		xValue = currentSnappedSample.getXValue();
		yValue = currentSnappedSample.getYValue();
		int x = xAxis.getValuePosition(xValue, false);
		int y = yAxis.getValuePosition(yValue, false);
		Point p = new Point();
		p.setLocation(y, x);

		graphics.drawLine(x - CROSS_SIZE, y - CROSS_SIZE, x + CROSS_SIZE, y + CROSS_SIZE);
		graphics.drawLine(x - CROSS_SIZE, y + CROSS_SIZE, x + CROSS_SIZE, y - CROSS_SIZE);
		graphics.drawLine(x, y + CROSS_SIZE, x, y - CROSS_SIZE);
		graphics.drawLine(x - CROSS_SIZE, y, x + CROSS_SIZE, y);
	}
}