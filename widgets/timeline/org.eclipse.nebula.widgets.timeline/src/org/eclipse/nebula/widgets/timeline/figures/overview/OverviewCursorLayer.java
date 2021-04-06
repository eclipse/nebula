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
import org.eclipse.nebula.widgets.timeline.ICursor;
import org.eclipse.nebula.widgets.timeline.ITimed;
import org.eclipse.nebula.widgets.timeline.TimeBaseConverter;
import org.eclipse.nebula.widgets.timeline.Timing;
import org.eclipse.nebula.widgets.timeline.figures.RootFigure;

public class OverviewCursorLayer extends FreeformLayer {

	public OverviewCursorLayer() {
		setLayoutManager(new OverviewLayout());
	}

	@Override
	protected boolean useLocalCoordinates() {
		return true;
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

			final ITimed cursor = (ICursor) super.getConstraint(figure);
			final Timing screenCoordinates = timeConverter.toOverviewScreenCoordinates(cursor.getTiming());

			return new PrecisionRectangle(screenCoordinates.left(), 0, 1, getBounds().height());
		}
	}
}
