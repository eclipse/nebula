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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.TextFlow;

public class EventTooltip extends FlowPage {

	private static final int VERTICAL_INDENT = 2;
	private static final int HORIZONTAL_INDENT = 5;

	public EventTooltip(String text) {
		setForegroundColor(ColorConstants.tooltipForeground);
		setBackgroundColor(ColorConstants.tooltipBackground);
		setOpaque(true);

		setBorder(new MarginBorder(VERTICAL_INDENT, HORIZONTAL_INDENT, VERTICAL_INDENT, HORIZONTAL_INDENT));
		add(new TextFlow(text));
	}

	@Override
	public Dimension getPreferredSize(int w, int h) {
		Dimension dimension = super.getPreferredSize(-1, -1);
		if (dimension.width > 150)
			dimension = super.getPreferredSize(150, -1);

		return dimension;
	}
}