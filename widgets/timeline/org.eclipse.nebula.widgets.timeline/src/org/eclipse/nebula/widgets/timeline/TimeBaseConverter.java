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

package org.eclipse.nebula.widgets.timeline;

import org.eclipse.nebula.widgets.timeline.figures.RootFigure;

public class TimeBaseConverter {

	/** Scaling for detail area. A factor > 1 mean we are zooming in. */
	private double fScaleFactor = 1;

	/** Physical size of the available screen area to draw on. */
	private double fDetailScreenWidth = -1;

	/** Minimal area needed to contain all events. From start timestamp of first event until end timestamp of last event. */
	private Timing fRequiredEventArea = null;

	/** Offset in eventTime where to start drawing. */
	private double fOffset = 0;

	private final RootFigure fRootFigure;

	/** Physical size of the available screen area to draw on for the overview area. */
	private int fOverviewScreenWidth = -1;

	public TimeBaseConverter(RootFigure rootFigure) {
		fRootFigure = rootFigure;
	}

	/**
	 * Reset scale factor and screen offset to defaults.
	 */
	public void resetDisplaySettings() {
		fScaleFactor = 1;
		fOffset = getEventArea().left();

		fRootFigure.fireTimebaseChanged();
	}

	public Timing getVisibleEventArea() {
		final Timing visibleArea = new Timing(0, fDetailScreenWidth);
		visibleArea.scale(1 / fScaleFactor);
		visibleArea.translate(fOffset);

		return visibleArea;
	}

	public long screenOffsetToEventTime(int screenOffset) {
		double offset = screenOffset;
		offset /= getScaleFactor();
		return Math.round((offset + fOffset));
	}

	/**
	 * Total area to be displayed in eventTime. Unit is [ns] as we get them directly from the events.
	 *
	 * @return area containing all events in eventTime
	 */
	public Timing getEventArea() {
		return (fRequiredEventArea != null) ? fRequiredEventArea.copy() : new Timing(0);
	}

	public void resetEventArea() {
		fRequiredEventArea = null;
	}

	public void addEvent(ITimed event) {
		if (fRequiredEventArea == null) {
			fRequiredEventArea = event.getTiming().copy();

			// first event
			setOffset(event.getTiming().left());
			if (fDetailScreenWidth >= 0)
				setScaleFactor((fDetailScreenWidth / 3) / event.getTiming().getDuration(), false);

		} else
			fRequiredEventArea.union(event.getTiming());
	}

	/**
	 * Translate the offset by pixels on the screen of the detail area.
	 *
	 * @param screenPixels
	 *            pixels on the screen to translate
	 * @return <code>true</code> when offset got changed
	 */
	public boolean translateDetailAreaOffset(int screenPixels) {
		return translateEventTime(Math.round(screenPixels / getScaleFactor()));
	}

	/**
	 * Translate the event time. The time cannot be translated past the overall display area.
	 *
	 * @param eventTime
	 *            eventTime to change
	 * @return
	 */
	private boolean translateEventTime(long eventTime) {
		final Timing eventArea = getEventArea();
		if (eventTime < 0) {
			if (fOffset > eventArea.left())
				setOffset(fOffset + eventTime);

		} else if (eventTime > 0) {
			if (fOffset < ((eventArea.right()) - getVisibleEventArea().getDuration()))
				setOffset(fOffset + eventTime);

		} else
			return false;

		return true;
	}

	public void setScreenWidth(double screenWidth) {
		final boolean screenNeedsUpdate = fDetailScreenWidth < 0;
		fDetailScreenWidth = screenWidth;

		if (screenNeedsUpdate)
			fRootFigure.fireTimebaseChanged();
	}

	public void zoom(double factor, int zoomCenterX) {
		final long eventTimeAtZoomCenter = screenOffsetToEventTime(zoomCenterX);

		setScaleFactor(fScaleFactor * factor, true);
		final long newEventTimeAtZoomCenter = screenOffsetToEventTime(zoomCenterX);

		setOffset((fOffset - newEventTimeAtZoomCenter) + eventTimeAtZoomCenter);
	}

	private double getScaleFactor() {
		return fScaleFactor;
	}

	/**
	 * Set the offset to a defined value. The offset will automatically be adjusted regarding the total visible area.
	 *
	 * @param eventTime
	 *            time to set offset to
	 */
	public void setOffset(double eventTime) {
		fOffset = eventTime;
		adjustInvalidOffset();

		fRootFigure.fireTimebaseChanged();
	}

	/**
	 * Set the scale factor to a defined value. The factor will automatically be adjusted regarding the total visible area.
	 *
	 * @param scaleFactor
	 *            factor to set
	 * @param adjust
	 *            <code>true</code> to automatically adjust scaling to reasonable bounds
	 */
	private void setScaleFactor(double scaleFactor, boolean adjust) {
		fScaleFactor = scaleFactor;

		if (adjust) {
			final double maxScaleFactor = 100;
			final double minScaleFactor = fDetailScreenWidth / getEventArea().getDuration();

			fScaleFactor = Math.max(minScaleFactor, fScaleFactor);
			fScaleFactor = Math.min(maxScaleFactor, fScaleFactor);
		}
	}

	private void adjustInvalidOffset() {
		if (fOffset > (getEventArea().right() - getVisibleEventArea().getDuration()))
			fOffset = getEventArea().right() - getVisibleEventArea().getDuration();

		if (fOffset < getEventArea().left())
			fOffset = getEventArea().left();
	}

	/**
	 * Reveal a given area on screen and center it. Zoom is adjusted in case the event cannot be displayed as a whole on screen.
	 *
	 * @param revealArea
	 *            area to reveal
	 */
	public void revealEvent(Timing revealArea) {
		if (getVisibleEventArea().getDuration() <= revealArea.getDuration())
			fScaleFactor = fDetailScreenWidth / (revealArea.getDuration() * 3.0d);

		setOffset(revealArea.getTimestamp() - ((getVisibleEventArea().getDuration() - revealArea.getDuration()) / 2));

		fRootFigure.fireTimebaseChanged();
	}

	/**
	 * Get the scale factor for the overview area.
	 *
	 * @return scale factor
	 */
	private double getOverviewScaleFactor() {
		return fOverviewScreenWidth / getEventArea().getDuration();
	}

	/**
	 * Translate the offset by pixels on the screen of the overview area.
	 *
	 * @param screenPixels
	 *            pixels on the screen to translate
	 * @return <code>true</code> when offset got changed
	 */
	public boolean translateOverviewAreaOffset(int screenPixels) {
		return translateEventTime(Math.round(screenPixels / getOverviewScaleFactor()));
	}

	/**
	 * Convert overview screen coordinates to eventTime.
	 *
	 * @param screenCoordinates
	 *            x offset on the overview figure
	 * @return coordinates in eventTime
	 */
	public Timing overviewCoordinatesToEventTime(Timing screenCoordinates) {
		final Timing eventTime = screenCoordinates.copy();
		eventTime.scale(1 / getOverviewScaleFactor());
		eventTime.translate(getEventArea().left());

		return eventTime;
	}

	/**
	 * Convert eventTime to overview screen coordinates.
	 *
	 * @param eventTime
	 *            eventTime to convert
	 * @return x dimension screen coordinates for given eventTime
	 */
	public Timing toOverviewScreenCoordinates(Timing eventTime) {
		final Timing coordinates = eventTime.copy();
		coordinates.translate(-getEventArea().left());
		coordinates.scale(getOverviewScaleFactor());

		return coordinates;
	}

	/**
	 * Convert eventTime to detail screen coordinates.
	 *
	 * @param eventTime
	 *            eventTime to convert
	 * @return x dimension screen coordinates for given eventTime
	 */
	public Timing toDetailCoordinates(Timing eventTime) {
		final Timing coordinates = eventTime.copy();
		coordinates.translate(-fOffset);
		coordinates.scale(getScaleFactor());

		return coordinates;
	}

	/**
	 * Set the width of the overview area.
	 *
	 * @param widthInPixels
	 *            overview area width in pixels
	 */
	public void setOverviewScreenWidth(int widthInPixels) {
		fOverviewScreenWidth = widthInPixels;
	}
}
