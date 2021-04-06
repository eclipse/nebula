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

import org.eclipse.draw2d.LayeredPane;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.nebula.widgets.timeline.TimeBaseConverter;
import org.eclipse.nebula.widgets.timeline.figures.IStyledFigure;
import org.eclipse.nebula.widgets.timeline.figures.RootFigure;
import org.eclipse.nebula.widgets.timeline.jface.ITimelineStyleProvider;

public class OverviewFigure extends LayeredPane implements IStyledFigure {

	private static final int DEFAULT_HEIGHT = 80;

	public static final int VERTICAL_INDENT = 8;

	public static final int Y_PADDING = 3;

	private final int fPreferredHeight = DEFAULT_HEIGHT;

	private int fEventHeight = 0;

	private Insets fAdditionalInsets;

	public OverviewFigure(ITimelineStyleProvider styleProvider) {
		add(new OverviewEventLayer(styleProvider));
		add(new OverviewCursorLayer());
		add(new OverviewSelectionLayer(styleProvider));

		updateStyle(styleProvider);
	}

	@Override
	public void updateStyle(ITimelineStyleProvider styleProvider) {
		setVisible(styleProvider.showOverview());
		setBorder(styleProvider.getOverviewAreaBorder());

		fEventHeight = styleProvider.getOverviewLaneHeight();
		fAdditionalInsets = styleProvider.getOverviewInsets();
	}

	@Override
	public Insets getInsets() {
		final Insets insets = new Insets(super.getInsets());
		insets.add(fAdditionalInsets);

		return insets;
	}

	@Override
	protected void layout() {
		super.layout();

		final TimeBaseConverter timeConverter = RootFigure.getTimeViewDetails(this);
		timeConverter.setOverviewScreenWidth(getBounds().width() - getInsets().getWidth());
	}

	@Override
	public Dimension getPreferredSize(int wHint, int hHint) {
		final int laneCount = RootFigure.getLaneCount(this);
		final int requiredHeight = (laneCount * fEventHeight) + ((laneCount + 1) * Y_PADDING) + getInsets().getHeight();

		return new Dimension(wHint, Math.max(requiredHeight, fPreferredHeight));
	}
}
