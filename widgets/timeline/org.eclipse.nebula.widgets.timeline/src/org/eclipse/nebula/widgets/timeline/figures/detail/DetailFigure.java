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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.Figure;
import org.eclipse.nebula.widgets.timeline.TimeBaseConverter;
import org.eclipse.nebula.widgets.timeline.Timing;
import org.eclipse.nebula.widgets.timeline.figures.RootFigure;
import org.eclipse.nebula.widgets.timeline.figures.detail.track.TracksFigure;
import org.eclipse.nebula.widgets.timeline.figures.detail.track.TracksLayer;
import org.eclipse.nebula.widgets.timeline.jface.ITimelineStyleProvider;
import org.eclipse.nebula.widgets.timeline.listeners.DetailAreaListener;

public class DetailFigure extends Figure {

	private static final int MIN_STEP_SIZE = 120;

	private static final List<Integer> STEP_SIZE_CANDIDATES = Arrays.asList(1, 2, 5, 10, 20, 25, 50, 100);

	public DetailFigure(ITimelineStyleProvider styleProvider) {
		final BorderLayout layout = new BorderLayout();
		layout.setHorizontalSpacing(0);
		layout.setVerticalSpacing(0);
		setLayoutManager(layout);

		add(new TracksFigure(styleProvider), BorderLayout.CENTER);
		add(new TimeAxisFigure(styleProvider), BorderLayout.BOTTOM);

		new DetailAreaListener(this);
	}

	/**
	 * Return a map of marker positions to be drawn on the detail area.
	 *
	 * @return map (timeValue, pixelOffset)
	 */
	public Map<Double, Integer> getMarkerPositions() {
		final TimeBaseConverter timeViewDetails = RootFigure.getTimeViewDetails(this);

		final Map<Double, Integer> markerPositions = new HashMap<>();
		for (final Double eventTime : getEventTimeMarkerPositions())
			markerPositions.put(eventTime, (int) Math.round(timeViewDetails.toDetailCoordinates(new Timing(eventTime)).getTimestamp()));

		return markerPositions;
	}

	/**
	 * Get timestamps in eventTime for markers that are visible on screen.
	 *
	 * @return list of timestamps (in eventTime) to draw markers for
	 */
	private List<Double> getEventTimeMarkerPositions() {
		final List<Double> positions = new ArrayList<>();

		final TimeBaseConverter timeViewDetails = RootFigure.getTimeViewDetails(this);
		final Timing visibleEventArea = timeViewDetails.getVisibleEventArea();

		final int stepSize = getStepSize();
		final long startValue = (long) ((Math.floor((visibleEventArea.left()) / stepSize) + 1) * stepSize);

		for (long pos = startValue; pos < visibleEventArea.right(); pos += stepSize)
			positions.add((double) pos);

		return positions;
	}

	/**
	 * Calculate the step size in eventTime. Therefore the screen with is divided in segments >= MIN_STEP_SIZE pixels. Then we try to find the closest candidate
	 * of STEP_SIZE_CANDIDATES * n that fits to the calculated screen interval.
	 *
	 * @return step size in eventTime
	 */
	private int getStepSize() {
		final TimeBaseConverter timeViewDetails = RootFigure.getTimeViewDetails(this);

		final double steps = RootFigure.getFigure(this, TracksLayer.class).getBounds().width() / MIN_STEP_SIZE;
		double stepSizeInEventTime = timeViewDetails.getVisibleEventArea().getDuration() / steps;
		int factor = 1;
		while (stepSizeInEventTime >= 100) {
			stepSizeInEventTime /= 10;
			factor *= 10;
		}

		final long preliminarySize = Math.round(stepSizeInEventTime);
		final int niceStepSize = STEP_SIZE_CANDIDATES.stream().filter(c -> c >= preliminarySize).findFirst().get();

		return niceStepSize * factor;
	}
}
