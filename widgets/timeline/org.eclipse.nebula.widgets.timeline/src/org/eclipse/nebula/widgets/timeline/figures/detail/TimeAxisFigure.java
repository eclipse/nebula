/*******************************************************************************
 * Copyright (c) 2020 Christian Pontesegger and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Christian Pontesegger - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.timeline.figures.detail;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.nebula.widgets.timeline.figures.IStyledFigure;
import org.eclipse.nebula.widgets.timeline.figures.RootFigure;
import org.eclipse.nebula.widgets.timeline.figures.detail.track.TracksFigure;
import org.eclipse.nebula.widgets.timeline.jface.ITimelineStyleProvider;
import org.eclipse.swt.SWT;

public class TimeAxisFigure extends Figure implements IStyledFigure {

	public TimeAxisFigure(ITimelineStyleProvider styleProvider) {

		setOpaque(false);
		updateStyle(styleProvider);
	}

	@Override
	public void updateStyle(ITimelineStyleProvider styleProvider) {
		setVisible(styleProvider.showTimeAxis());
	}

	@Override
	public Dimension getPreferredSize(int wHint, int hHint) {
		return new Dimension(wHint, 20);
	}

	@Override
	protected void paintClientArea(Graphics graphics) {
		paintMarkers(graphics);

		super.paintClientArea(graphics);
	}

	private DetailFigure getDetailFigure() {
		return (DetailFigure) getParent();
	}

	private void paintMarkers(Graphics graphics) {

		final Rectangle bounds = getBounds();
		graphics.setForegroundColor(ColorConstants.darkGray);
		graphics.setLineStyle(SWT.LINE_SOLID);

		final Insets insets = RootFigure.getFigure(this, TracksFigure.class).getInsets();
		final ITimelineStyleProvider styleProvider = RootFigure.getRootFigure(this).getStyleProvider();

		final Map<Double, Integer> markerPositions = getDetailFigure().getMarkerPositions();
		for (final Entry<Double, Integer> entry : markerPositions.entrySet()) {
			final String label = styleProvider.getTimeLabel(entry.getKey(), TimeUnit.NANOSECONDS);
			final int textWidth = FigureUtilities.getTextWidth(label, graphics.getFont());

			graphics.drawLine(entry.getValue() + bounds.x() + insets.left, bounds.y, entry.getValue() + bounds.x() + insets.left, bounds.y + 5);
			graphics.drawText(label, (entry.getValue() + bounds.x() + insets.left) - (textWidth / 2), bounds.y + 5);
		}
	}
}
