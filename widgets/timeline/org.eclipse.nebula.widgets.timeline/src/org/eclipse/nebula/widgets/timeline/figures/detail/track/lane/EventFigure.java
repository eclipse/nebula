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

package org.eclipse.nebula.widgets.timeline.figures.detail.track.lane;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.OrderedLayout;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.TreeSearch;
import org.eclipse.nebula.widgets.timeline.ITimelineEvent;
import org.eclipse.nebula.widgets.timeline.figures.RootFigure;
import org.eclipse.nebula.widgets.timeline.layouts.CenterLayout;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public class EventFigure extends RoundedRectangle implements Comparable<EventFigure> {

	private Color fEventColor;
	private final Label fLabel;

	public EventFigure(ITimelineEvent event) {

		final ToolbarLayout layout = new ToolbarLayout(false);
		layout.setMinorAlignment(OrderedLayout.ALIGN_CENTER);
		layout.setStretchMinorAxis(true);
		setLayoutManager(layout);
		setLayoutManager(new CenterLayout());

		setLineWidth(2);

		fLabel = new Label(event.getTitle());
		fLabel.setForegroundColor(ColorConstants.black);
		add(fLabel);

		if (event.getMessage() != null)
			setToolTip(new EventTooltip(event.getMessage()));
	}

	public void setEventColor(Color color) {
		fEventColor = color;
		setBackgroundColor(color);

		final float[] hsb = color.getRGB().getHSB();
		setForegroundColor(RootFigure.getRootFigure(this).getStyleProvider().getColor(new RGB(hsb[0], Math.min(1, hsb[1] * 2f), hsb[2] * 0.8f)));

		setAlpha(150);
	}

	@Override
	public void revalidate() {
		final ITimelineEvent event = getEvent();
		if (event != null) {
			// event is null during construction. Label & tooltip updates are only needed later on changes

			fLabel.setText(event.getTitle());
			setToolTip((event.getMessage() == null) ? null : new EventTooltip(event.getMessage()));

			Color eventColor = getParent().getForegroundColor();
			if (event.getColorCode() != null)
				eventColor = RootFigure.getRootFigure(this).getStyleProvider().getColor(event.getRgb());

			setEventColor(eventColor);
		}

		super.revalidate();
	}

	/**
	 * Get the event color that was set on this event, even if the foreground/background colors got changed in the meantime.
	 *
	 * @return event color set on this event
	 */
	public Color getEventColor() {
		return fEventColor;
	}

	public ITimelineEvent getEvent() {
		if (getParent() != null)
			return (ITimelineEvent) getParent().getLayoutManager().getConstraint(this);

		return null;
	}

	@Override
	public int compareTo(EventFigure eventFigure) {
		final long difference = getEvent().getStartTimestamp() - eventFigure.getEvent().getStartTimestamp();
		if (difference < 0)
			return -1;
		else if (difference > 0)
			return 1;
		else
			return 0;
	}

	@Override
	protected IFigure findDescendantAtExcluding(int x, int y, TreeSearch search) {
		// do not dig deeper in the figure hierarchy
		return null;
	}
}
