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
import org.eclipse.draw2d.TreeSearch;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.nebula.widgets.timeline.ITimed;
import org.eclipse.nebula.widgets.timeline.TimeBaseConverter;
import org.eclipse.nebula.widgets.timeline.Timing;
import org.eclipse.nebula.widgets.timeline.figures.IStyledFigure;
import org.eclipse.nebula.widgets.timeline.figures.RootFigure;
import org.eclipse.nebula.widgets.timeline.figures.detail.track.lane.EventFigure;
import org.eclipse.nebula.widgets.timeline.jface.ITimelineStyleProvider;

public class OverviewEventLayer extends FreeformLayer implements IStyledFigure {

	private static final int MINIMUM_WIDTH = 1;
	private int fEventHeight = 0;

	public OverviewEventLayer(ITimelineStyleProvider styleProvider) {
		updateStyle(styleProvider);

		setLayoutManager(new OverviewLayout());
	}

	public OverviewEventFigure addEvent(EventFigure eventFigure) {
		final OverviewEventFigure overviewEventFigure = new OverviewEventFigure(eventFigure);
		overviewEventFigure.setForegroundColor(eventFigure.getForegroundColor());

		add(overviewEventFigure, eventFigure);

		return overviewEventFigure;
	}

	@Override
	protected boolean useLocalCoordinates() {
		return true;
	}

	@Override
	public void updateStyle(ITimelineStyleProvider styleProvider) {
		fEventHeight = styleProvider.getOverviewLaneHeight();
	}

	@Override
	protected IFigure findDescendantAtExcluding(int x, int y, TreeSearch search) {
		// do not dig deeper in the figure hierarchy
		return null;
	}

	private class OverviewLayout extends XYLayout {

		@Override
		public Rectangle getConstraint(IFigure figure) {
			final TimeBaseConverter timeConverter = RootFigure.getTimeViewDetails(figure);

			final EventFigure eventFigure = (EventFigure) super.getConstraint(figure);
			final ITimed event = eventFigure.getEvent();

			final Timing screenCoordinates = timeConverter.toOverviewScreenCoordinates(event.getTiming());
			final Rectangle overviewEventArea = new PrecisionRectangle(screenCoordinates.left(),
					OverviewFigure.VERTICAL_INDENT + ((fEventHeight + OverviewFigure.Y_PADDING) * RootFigure.getLaneIndex(eventFigure)),
					screenCoordinates.getDuration(), fEventHeight);

			if (overviewEventArea.width() < MINIMUM_WIDTH)
				overviewEventArea.setWidth(MINIMUM_WIDTH);

			return overviewEventArea;
		}
	}
}
