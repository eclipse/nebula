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

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.nebula.widgets.timeline.figures.IStyledFigure;
import org.eclipse.nebula.widgets.timeline.jface.ITimelineStyleProvider;
import org.eclipse.swt.SWT;

public class OverviewCursorFigure extends Shape implements IStyledFigure {

	public OverviewCursorFigure(ITimelineStyleProvider styleProvider) {
		updateStyle(styleProvider);
	}

	@Override
	protected void outlineShape(Graphics graphics) {
		final Rectangle bounds = getBounds();

		graphics.setLineStyle(SWT.LINE_DOT);
		graphics.drawLine(bounds.getTopLeft(), bounds.getBottomLeft());
	}

	@Override
	protected void fillShape(Graphics graphics) {
		// nothing to do
	}

	@Override
	public void updateStyle(ITimelineStyleProvider styleProvider) {
		setForegroundColor(styleProvider.getCursorColor());
	}
}
