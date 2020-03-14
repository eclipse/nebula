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

package org.eclipse.nebula.widgets.timeline.figures.detail.track;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.nebula.widgets.timeline.figures.IStyledFigure;
import org.eclipse.nebula.widgets.timeline.jface.ITimelineStyleProvider;

public class TrackFigure extends Figure implements IStyledFigure {

	private String fTitle;

	public TrackFigure(String title, ITimelineStyleProvider styleProvider) {

		fTitle = title;
		final ToolbarLayout layout = new ToolbarLayout(false);
		layout.setStretchMinorAxis(true);
		layout.setSpacing(5);
		setLayoutManager(layout);

		updateStyle(styleProvider);
	}

	@Override
	public void updateStyle(ITimelineStyleProvider styleProvider) {
		setBorder(styleProvider.getTrackBorder(fTitle));
	}

	/**
	 * Set the title of this track.
	 *
	 * @param title
	 *            track title
	 */
	public void setTitle(String title) {
		fTitle = title;
	}
}
