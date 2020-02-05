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

package org.eclipse.nebula.widgets.timeline.jface;

import java.util.concurrent.TimeUnit;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.nebula.widgets.timeline.figures.detail.track.lane.EventFigure;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public interface ITimelineStyleProvider {

	/**
	 * Called when the composite gets disposed. Clean up colors and fonts.
	 */
	default void dispose() {
	};

	/**
	 * Get diagram background color.
	 *
	 * @return background color
	 */
	Color getBackgroundColor();

	/**
	 * Get the border for the detail area.
	 *
	 * @return detail area border
	 */
	Border getDetailAreaBorder();

	/**
	 * Get the border for the overview area.
	 *
	 * @return overview area border
	 */
	Border getOverviewAreaBorder();

	/**
	 * Display the time axis below the detail area.
	 *
	 * @return <code>true</code> to display the time axis, <code>false</code> to hide
	 */
	boolean showTimeAxis();

	/**
	 * Display the grid in the detail area.
	 *
	 * @return <code>true</code> to display the grid, <code>false</code> to hide
	 */
	boolean showGrid();

	/**
	 * Display the overview area.
	 *
	 * @return <code>true</code> to display the overview area, <code>false</code> to hide
	 */
	boolean showOverview();

	/**
	 * Get the color of the grid.
	 *
	 * @return grid color
	 */
	Color getGridColor();

	/**
	 * Get the line style for the grid.
	 *
	 * @return grid line style
	 */
	int getGridLineStyle();

	/**
	 * Get the border for the overview selection figure.
	 *
	 * @return overview selection figure border
	 */
	Border getOverviewSelectionBorder();

	/**
	 * Get the background color for the overview selection.
	 *
	 * @return figure background color
	 */
	Color getOverviewSelectionBackgroundColor();

	/**
	 * Get the alpha value for the overview selection figure.
	 *
	 * @return alpha value
	 */
	int getOverviewSelectionBackgroundAlpha();

	/**
	 * Get the border for a track.
	 *
	 * @param title
	 *            track title
	 *
	 * @return track border
	 */
	Border getTrackBorder(String title);

	/**
	 * Get the color for a non-selected cursor.
	 *
	 * @return cursor color
	 */
	Color getCursorColor();

	/**
	 * Get the color for a selected cursor.
	 *
	 * @return selected cursor color
	 */
	Color getSelectedCursorColor();

	/**
	 * Get the color for the next lane.
	 *
	 * @return lane color
	 */
	Color getLaneColor();

	/**
	 * Get height for a given lane.
	 *
	 * @return height in pixels
	 */
	int getLaneHeight();

	/**
	 * Get height for a given lane in overview.
	 *
	 * @return height in pixels
	 */
	int getOverviewLaneHeight();

	/**
	 * Apply styling to select an event figure.
	 *
	 * @param eventFigure
	 *            figure to style
	 */
	void selectEvent(EventFigure eventFigure);

	/**
	 * Revert styling of an event figure selection.
	 *
	 * @param eventFigure
	 *            figure to revert style
	 */
	void unselectEvent(EventFigure eventFigure);

	/**
	 * Get the multiplier for the zoom factor to be applied on a zoomIn/zoomOut operation.
	 *
	 * @return zoom factor multiplier
	 */
	double getZoomFactor();

	/**
	 * Get a color for the given RGB value. The style provider should take care to buffer colors and to dispose them when the widget gets disposed.
	 *
	 * @param rgb
	 *            RGB value to get color for
	 * @return color
	 */
	Color getColor(RGB rgb);

	/**
	 * Get the insets for the overview area. These insets will be applied within the border acquired from {@link #getOverviewAreaBorder()}. The top inset will
	 * also be used for lane spacing.
	 *
	 * @return insets to be used for the overview area
	 */
	Insets getOverviewInsets();

	/**
	 * Get a human readable timestamp for a given time. The provided label will be used for axis labels and cursor popups.
	 *
	 * @param timestamp
	 *            timestamp in eventTime
	 * @param unit
	 *            time unit to be used
	 * @return time value
	 */
	String getTimeLabel(double timestamp, TimeUnit unit);
}
