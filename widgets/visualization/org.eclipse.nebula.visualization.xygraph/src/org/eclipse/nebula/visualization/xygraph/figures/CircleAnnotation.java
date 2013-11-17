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
import org.eclipse.nebula.visualization.xygraph.dataprovider.ISample;
import org.eclipse.nebula.visualization.xygraph.figures.Annotation;
import org.eclipse.nebula.visualization.xygraph.figures.Trace;
import org.eclipse.nebula.visualization.xygraph.util.Preferences;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

/**
 * Variation of annotation that is represented as a circle with a diameter of
 * eight pixels.
 *
 * @author Torkild U. Resheim
 */
public class CircleAnnotation extends Annotation {

	private final static int CIRCLE_DIAMETER = 8;

	public CircleAnnotation(String name, Trace trace) {
		super(name, trace);
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

		graphics.setForegroundColor(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		xValue = currentSnappedSample.getXValue();
		yValue = currentSnappedSample.getYValue();
		int x = xAxis.getValuePosition(xValue, false);
		int y = yAxis.getValuePosition(yValue, false);
		graphics.drawOval(x - (CIRCLE_DIAMETER / 2), y - (CIRCLE_DIAMETER / 2), CIRCLE_DIAMETER, CIRCLE_DIAMETER);
	}
}