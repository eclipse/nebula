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

package org.eclipse.nebula.widgets.timeline.figures.detail.track;

import java.util.Map;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.nebula.widgets.timeline.figures.IStyledFigure;
import org.eclipse.nebula.widgets.timeline.figures.RootFigure;
import org.eclipse.nebula.widgets.timeline.figures.detail.DetailFigure;
import org.eclipse.nebula.widgets.timeline.jface.ITimelineStyleProvider;

public class GridLayer extends FreeformLayer implements IStyledFigure {

	public GridLayer(ITimelineStyleProvider styleProvider) {
		updateStyle(styleProvider);
	}

	@Override
	protected boolean useLocalCoordinates() {
		return super.useLocalCoordinates();
	}

	@Override
	protected void paintClientArea(Graphics graphics) {
		paintGrid(graphics);

		super.paintClientArea(graphics);
	}

	private void paintGrid(Graphics graphics) {
		final ITimelineStyleProvider styleProvider = RootFigure.getRootFigure(this).getStyleProvider();
		graphics.setLineStyle(styleProvider.getGridLineStyle());

		final Rectangle bounds = getBounds();
		final Map<Double, Integer> markerPositions = RootFigure.getFigure(this, DetailFigure.class).getMarkerPositions();
		for (final int position : markerPositions.values())
			graphics.drawLine(position + bounds.x(), bounds.y, position + bounds.x(), bounds.y + bounds.height);
	}

	@Override
	public void updateStyle(ITimelineStyleProvider styleProvider) {
		setForegroundColor(styleProvider.getGridColor());
		setVisible(styleProvider.showGrid());
	}
}
