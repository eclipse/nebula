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
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.nebula.widgets.timeline.TimeBaseConverter;
import org.eclipse.nebula.widgets.timeline.Timing;
import org.eclipse.nebula.widgets.timeline.figures.RootFigure;

public class OverviewSelector extends MouseListener.Stub {

	private final Figure fFigure;

	public OverviewSelector(Figure figure) {
		fFigure = figure;

		figure.addMouseListener(this);
	}

	@Override
	public void mouseReleased(MouseEvent me) {
		if (me.button == 1) {
			System.out.println("Select: " + me.x);
			final TimeBaseConverter timeViewDetails = RootFigure.getTimeViewDetails(fFigure);
			final Timing eventTime = timeViewDetails.overviewCoordinatesToEventTime(new Timing(me.x - fFigure.getParent().getInsets().left, 0));

			timeViewDetails.revealEvent(eventTime);
		}
	}
}
