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

package org.eclipse.nebula.widgets.timeline.listeners;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.nebula.widgets.timeline.ICursor;
import org.eclipse.nebula.widgets.timeline.TimeBaseConverter;
import org.eclipse.nebula.widgets.timeline.figures.RootFigure;
import org.eclipse.nebula.widgets.timeline.figures.detail.cursor.CursorFigure;
import org.eclipse.nebula.widgets.timeline.figures.detail.cursor.CursorTimingsLayer;
import org.eclipse.nebula.widgets.timeline.figures.detail.track.TracksLayer;
import org.eclipse.nebula.widgets.timeline.figures.detail.track.lane.EventFigure;
import org.eclipse.nebula.widgets.timeline.figures.detail.track.lane.LaneFigure;
import org.eclipse.nebula.widgets.timeline.figures.overview.OverviewCursorLayer;
import org.eclipse.nebula.widgets.timeline.jface.ITimelineStyleProvider;

public class CursorMover extends MouseMotionListener.Stub implements MouseListener, MouseMotionListener {

	private static final int SNAP_TO_FIGURE_OFFSET = 10;

	private final CursorFigure fFigure;

	private Point fLocation = null;

	public CursorMover(CursorFigure figure) {
		fFigure = figure;

		figure.addMouseListener(this);
		figure.addMouseMotionListener(this);
	}

	@Override
	public void mousePressed(MouseEvent me) {
		fLocation = me.getLocation();
		me.consume();
	}

	@Override
	public void mouseReleased(MouseEvent me) {
		if (me.button == 3) {
			hideCursorTimings();
			final ICursor cursor = (ICursor) fFigure.getParent().getLayoutManager().getConstraint(fFigure);
			RootFigure.getRootFigure(fFigure).deleteCursor(cursor);
		}

		if (fLocation != null) {
			fLocation = null;
			me.consume();
		}
	}

	@Override
	public void mouseDragged(MouseEvent me) {
		if (fLocation != null) {
			final Point targetLocation = me.getLocation();

			Long targetEventTime = snapToEvent(targetLocation);

			if (targetEventTime == null) {
				final Dimension offset = targetLocation.getDifference(fLocation);
				if (offset.width() != 0) {
					final TimeBaseConverter timeDetails = RootFigure.getRootFigure(fFigure).getTimeViewDetails();
					targetEventTime = timeDetails.screenOffsetToEventTime(targetLocation.x());
				}

				fLocation = targetLocation;
			}

			if (targetEventTime != null) {
				final ICursor cursor = (ICursor) fFigure.getParent().getLayoutManager().getConstraint(fFigure);
				cursor.setTimestamp(targetEventTime);

				fFigure.getParent().revalidate();

				RootFigure.getFigure(fFigure, CursorTimingsLayer.class).revalidate();
				RootFigure.getFigure(fFigure, OverviewCursorLayer.class).revalidate();
			}
		}

		me.consume();
	}

	/**
	 * Try to snap to an event for the current lane the cursor is in.
	 *
	 * @param mouseCursorLocation
	 *
	 * @return timestamp in eventTime to set cursor to or <code>null</code>
	 */
	private Long snapToEvent(Point mouseCursorLocation) {
		IFigure figureUnderCursor = RootFigure.getFigure(fFigure, TracksLayer.class).findFigureAt(mouseCursorLocation);
		if (figureUnderCursor instanceof LaneFigure) {
			for (int offset = 0; offset <= SNAP_TO_FIGURE_OFFSET; offset += 2) {
				IFigure figure = figureUnderCursor.findFigureAt(mouseCursorLocation.x() - offset, mouseCursorLocation.y());
				if (figure instanceof EventFigure) {
					figureUnderCursor = figure;
					break;
				}

				figure = figureUnderCursor.findFigureAt(mouseCursorLocation.x() + offset, mouseCursorLocation.y());
				if (figure instanceof EventFigure) {
					figureUnderCursor = figure;
					break;
				}
			}
		}

		if (figureUnderCursor instanceof EventFigure) {
			final Rectangle figureBounds = figureUnderCursor.getBounds();
			final int diffToStart = Math.abs(figureBounds.x() - mouseCursorLocation.x());
			final int diffToEnd = Math.abs((figureBounds.x() + figureBounds.width()) - mouseCursorLocation.x());

			if (Math.min(diffToStart, diffToEnd) <= SNAP_TO_FIGURE_OFFSET) {
				if (diffToStart <= diffToEnd) {
					// snap to start of figure
					return ((EventFigure) figureUnderCursor).getEvent().getStartTimestamp();
				} else {
					// snap to end of figure
					return ((EventFigure) figureUnderCursor).getEvent().getEndTimestamp();
				}
			}
		}

		return null;
	}

	@Override
	public void mouseEntered(MouseEvent me) {
		final ITimelineStyleProvider styleProvider = RootFigure.getRootFigure(fFigure).getStyleProvider();
		fFigure.setForegroundColor(styleProvider.getSelectedCursorColor());
		fFigure.setBackgroundColor(styleProvider.getSelectedCursorColor());

		final CursorTimingsLayer cursorTimingsLayer = RootFigure.getFigure(fFigure, CursorTimingsLayer.class);
		cursorTimingsLayer.showTimingsFor(fFigure, me);
	}

	@Override
	public void mouseExited(MouseEvent me) {
		final ITimelineStyleProvider styleProvider = RootFigure.getRootFigure(fFigure).getStyleProvider();
		fFigure.setForegroundColor(styleProvider.getCursorColor());
		fFigure.setBackgroundColor(styleProvider.getCursorColor());

		hideCursorTimings();
	}

	@Override
	public void mouseMoved(MouseEvent me) {
		final CursorTimingsLayer cursorTimingsLayer = RootFigure.getFigure(fFigure, CursorTimingsLayer.class);
		cursorTimingsLayer.moveTimingsTo(me.getLocation());
	}

	@Override
	public void mouseDoubleClicked(MouseEvent me) {
		// nothing to do
	}

	private void hideCursorTimings() {
		final CursorTimingsLayer cursorTimingsLayer = RootFigure.getFigure(fFigure, CursorTimingsLayer.class);
		cursorTimingsLayer.hideTimings();
	}
}
