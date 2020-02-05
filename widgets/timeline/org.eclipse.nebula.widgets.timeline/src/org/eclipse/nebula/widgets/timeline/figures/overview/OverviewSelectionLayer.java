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

package org.eclipse.nebula.widgets.timeline.figures.overview;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.nebula.widgets.timeline.TimeBaseConverter;
import org.eclipse.nebula.widgets.timeline.Timing;
import org.eclipse.nebula.widgets.timeline.figures.RootFigure;
import org.eclipse.nebula.widgets.timeline.jface.ITimelineStyleProvider;
import org.eclipse.nebula.widgets.timeline.listeners.OverviewSelector;

public class OverviewSelectionLayer extends FreeformLayer {

	private static final int MINIMUM_WIDTH = 5;

	public OverviewSelectionLayer(ITimelineStyleProvider styleProvider) {
		setLayoutManager(new OverviewSelectionLayerLayout());

		add(new OverviewSelectionFigure(styleProvider));

		new OverviewSelector(this);
	}

	@Override
	protected boolean useLocalCoordinates() {
		return true;
	}

	@Override
	public boolean containsPoint(int x, int y) {
		return getBounds().contains(x, y);
	}

	private class OverviewSelectionLayerLayout extends XYLayout {
		@Override
		public Object getConstraint(IFigure figure) {
			final TimeBaseConverter timeConverter = RootFigure.getTimeViewDetails(figure);

			final Timing coordinates = timeConverter.toOverviewScreenCoordinates(timeConverter.getVisibleEventArea());
			final Rectangle bounds = new PrecisionRectangle(coordinates.left(), 0, coordinates.getDuration(), getBounds().height());
			if (bounds.width() < MINIMUM_WIDTH)
				bounds.setWidth(MINIMUM_WIDTH);

			return bounds;
		}
	}
}
