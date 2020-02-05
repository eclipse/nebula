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

package org.eclipse.nebula.widgets.timeline.figures.detail.cursor;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.nebula.widgets.timeline.ICursor;
import org.eclipse.nebula.widgets.timeline.TimeBaseConverter;
import org.eclipse.nebula.widgets.timeline.Timing;
import org.eclipse.nebula.widgets.timeline.figures.RootFigure;

public class CursorLayer extends FreeformLayer {

	public CursorLayer() {
		setLayoutManager(new CursorLayout());
	}

	@Override
	protected boolean useLocalCoordinates() {
		return true;
	}

	private class CursorLayout extends XYLayout {

		@Override
		public void layout(IFigure parent) {
			final TimeBaseConverter timeViewDetails = RootFigure.getTimeViewDetails(parent);

			for (final Object child : getChildren()) {
				final ICursor cursor = (ICursor) getConstraint((IFigure) child);
				final Dimension preferredSize = ((IFigure) child).getPreferredSize();

				final Timing screenCoordinates = timeViewDetails.toDetailCoordinates(cursor.getTiming());
				final Rectangle screenBounds = new PrecisionRectangle(screenCoordinates.getTimestamp(), 0, preferredSize.width(), getBounds().height());

				screenBounds.translate(-preferredSize.width() / 2, 0);

				((IFigure) child).setBounds(screenBounds);
			}
		}
	}
}
