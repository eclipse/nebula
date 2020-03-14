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

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.swt.graphics.Color;

public class LeftRightBorder extends LineBorder {

	public LeftRightBorder() {
		super();
	}

	public LeftRightBorder(Color color, int width, int style) {
		super(color, width, style);
	}

	public LeftRightBorder(Color color, int width) {
		super(color, width);
	}

	public LeftRightBorder(Color color) {
		super(color);
	}

	public LeftRightBorder(int width) {
		super(width);
	}

	@Override
	public void paint(IFigure figure, Graphics graphics, Insets insets) {
		tempRect.setBounds(getPaintRectangle(figure, insets));
		if ((getWidth() % 2) == 1) {
			tempRect.width--;
			tempRect.height--;
		}
		tempRect.shrink(getWidth() / 2, getWidth() / 2);
		graphics.setLineWidth(getWidth());
		graphics.setLineStyle(getStyle());
		if (getColor() != null)
			graphics.setForegroundColor(getColor());

		graphics.drawLine(tempRect.getTopLeft(), tempRect.getBottomLeft());
		graphics.drawLine(tempRect.getTopRight(), tempRect.getBottomRight());
	}
}
