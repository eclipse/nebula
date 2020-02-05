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

package org.eclipse.nebula.widgets.timeline.borders;

import org.eclipse.draw2d.AbstractLabeledBorder;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

public class TrackBorder extends AbstractLabeledBorder {

	private final Insets fTextPadding = new Insets(2, 50, 2, 0);
	private final Insets fLinePadding = new Insets(0, 8, 0, 8);

	public TrackBorder(String title) {
		super(title);

		setTextColor(ColorConstants.menuForegroundSelected);
	}

	@Override
	protected Insets calculateInsets(IFigure figure) {
		if (getLabel().isEmpty())
			return new Insets(0);
		else
			return new Insets(getTextExtents(figure).height + fTextPadding.getHeight(), 0, 0, 0);
	}

	@Override
	public void paint(IFigure figure, Graphics g, Insets insets) {
		if (!getLabel().isEmpty()) {
			final Dimension textExtents = getTextExtents(figure);

			final Rectangle area = figure.getBounds().getCopy();
			area.setWidth(figure.getParent().getBounds().width());
			area.setHeight(Math.min(area.height, textExtents.height + fTextPadding.getHeight()));
			area.shrink(insets);

			g.clipRect(area);

			int y = area.y + (area.height() / 2);
			g.setForegroundColor(ColorConstants.gray);
			g.drawLine(area.x + fLinePadding.left, y, (area.x + fTextPadding.left) - fLinePadding.left, y);
			g.drawLine(area.x + fTextPadding.left + textExtents.width + fLinePadding.left, y, (area.x + area.width()) - fLinePadding.right, y);

			y++;
			g.setForegroundColor(ColorConstants.lightGray);
			g.drawLine(area.x + fLinePadding.left, y, (area.x + fTextPadding.left) - fLinePadding.left, y);
			g.drawLine(area.x + fTextPadding.left + textExtents.width + fLinePadding.left, y, (area.x + area.width()) - fLinePadding.right, y);

			final int x = area.x + fTextPadding.left;
			y = area.y + fTextPadding.top;

			g.setFont(getFont(figure));
			g.setForegroundColor(getTextColor());
			g.drawString(getLabel(), x, y);
		}
	}
}
