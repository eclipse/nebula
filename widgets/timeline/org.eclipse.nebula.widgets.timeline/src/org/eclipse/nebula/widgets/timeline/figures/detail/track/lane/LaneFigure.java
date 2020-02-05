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

import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.nebula.widgets.timeline.ITimelineEvent;
import org.eclipse.nebula.widgets.timeline.TimeBaseConverter;
import org.eclipse.nebula.widgets.timeline.Timing;
import org.eclipse.nebula.widgets.timeline.figures.IStyledFigure;
import org.eclipse.nebula.widgets.timeline.figures.RootFigure;
import org.eclipse.nebula.widgets.timeline.jface.ITimelineStyleProvider;

public class LaneFigure extends Figure implements IStyledFigure {

	private int fPreferredHeight;

	public LaneFigure(ITimelineStyleProvider styleProvider) {
		setLayoutManager(new LaneLayout());

		updateStyle(styleProvider);
	}

	@Override
	public void updateStyle(ITimelineStyleProvider styleProvider) {
		setForegroundColor(styleProvider.getLaneColor());
		fPreferredHeight = styleProvider.getLaneHeight();
	}

	@Override
	public Dimension getPreferredSize(int wHint, int hHint) {
		return new Dimension(wHint, fPreferredHeight);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void add(IFigure figure, Object constraint, int index) {
		super.add(figure, constraint, index);

		getChildren().sort((o1, o2) -> {
			return ((EventFigure) o1).compareTo((EventFigure) o2);
		});
	}

	public List<EventFigure> getEventFigures() {
		return ((List<?>) getChildren()).stream().filter(p -> p instanceof EventFigure).map(p -> (EventFigure) p).collect(Collectors.toList());
	}

	private class LaneLayout extends XYLayout {

		private Rectangle getConstraintAsRectangle(IFigure figure) {
			final ITimelineEvent event = (ITimelineEvent) getConstraint(figure);

			return new PrecisionRectangle(event.getStartTimestamp(), 0, event.getDuration(), 1);
		}

		@Override
		public void layout(IFigure parent) {
			final TimeBaseConverter timeViewDetails = RootFigure.getRootFigure(parent).getTimeViewDetails();

			for (final Object figure : getChildren()) {
				final ITimelineEvent event = (ITimelineEvent) getConstraint((IFigure) figure);

				final Timing screenCoordinates = timeViewDetails.toDetailCoordinates(event.getTiming());
				final Rectangle screenBounds = new PrecisionRectangle(screenCoordinates.getTimestamp(), getBounds().y(), screenCoordinates.getDuration(),
						getBounds().height());

				if (screenBounds.width() == 0)
					screenBounds.setWidth(1);

				((IFigure) figure).setBounds(screenBounds);
			}
		}

		/**
		 * This is a copy of the parent method. Only change is that we call getContraintAsRectangle() instead of directly accessing the constraints member.
		 */
		@Override
		protected Dimension calculatePreferredSize(IFigure f, int wHint, int hHint) {
			final Rectangle rect = new Rectangle();
			final ListIterator children = f.getChildren().listIterator();
			while (children.hasNext()) {
				final IFigure child = (IFigure) children.next();
				Rectangle r = getConstraintAsRectangle(child);
				if (r == null)
					continue;

				if ((r.width == -1) || (r.height == -1)) {
					final Dimension preferredSize = child.getPreferredSize(r.width, r.height);
					r = r.getCopy();
					if (r.width == -1)
						r.width = preferredSize.width;
					if (r.height == -1)
						r.height = preferredSize.height;
				}
				rect.union(r);
			}
			final Dimension d = rect.getSize();
			final Insets insets = f.getInsets();
			return new Dimension(d.width + insets.getWidth(), d.height + insets.getHeight()).union(getBorderPreferredSize(f));
		}
	}
}
