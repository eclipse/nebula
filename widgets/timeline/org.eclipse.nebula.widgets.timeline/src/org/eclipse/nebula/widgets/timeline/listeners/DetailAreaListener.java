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

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.nebula.widgets.timeline.TimeBaseConverter;
import org.eclipse.nebula.widgets.timeline.figures.RootFigure;
import org.eclipse.nebula.widgets.timeline.figures.detail.track.lane.EventFigure;

/**
 * This listener has 3 tasks:
 * <ul>
 * <li>move x offset of detail area (mouse drag)</li>
 * <li>create cursor (mouse click in empty area)</li>
 * <li>select event (mouse click on event)</li>
 * </ul>
 */
public class DetailAreaListener extends MouseMotionListener.Stub implements MouseListener, MouseMotionListener {

	private Point fLocation = null;
	private final Figure fFigure;

	private boolean fDragged = false;

	public DetailAreaListener(Figure figure) {
		fFigure = figure;

		figure.addMouseListener(this);
		figure.addMouseMotionListener(this);
	}

	@Override
	public void mousePressed(MouseEvent me) {
		fDragged = false;
		fLocation = me.getLocation();

		me.consume();
	}

	@Override
	public void mouseReleased(MouseEvent me) {
		if (!fDragged) {
			final IFigure figureUnderCursor = fFigure.findFigureAt(me.x, me.y);

			if (figureUnderCursor instanceof EventFigure) {
				// selection
				RootFigure.getRootFigure(fFigure).setSelection((EventFigure) figureUnderCursor);
			} else {
				// create cursor
				final long eventTime = RootFigure.getTimeViewDetails(fFigure).screenOffsetToEventTime(me.x);
				RootFigure.getRootFigure(fFigure).createCursor(eventTime);
			}
		}

		if (fLocation != null) {
			fLocation = null;
			me.consume();
		}
	}

	@Override
	public void mouseDoubleClicked(MouseEvent me) {
		// nothing to do
	}

	@Override
	public void mouseDragged(MouseEvent me) {
		if (fLocation != null) {
			fDragged = true;

			final Point targetLocation = me.getLocation();

			final Dimension offset = fLocation.getDifference(targetLocation);
			if (offset.width() != 0) {
				final TimeBaseConverter timeDetails = RootFigure.getRootFigure(fFigure).getTimeViewDetails();
				if (timeDetails.translateDetailAreaOffset(offset.width()))
					fLocation = targetLocation;

				me.consume();
			}
		}
	}
}
