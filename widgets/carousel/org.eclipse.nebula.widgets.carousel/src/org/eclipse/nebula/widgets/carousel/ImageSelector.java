/*******************************************************************************
 * Copyright (c) 2020 Laurent CARON.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API and
 * implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.carousel;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;

class ImageSelector extends Canvas {
	private static final int CIRCLE_DIAMETER = 20;
	private static final int ARROW_SIZE = 12;

	Color arrowColor, circleBackground, circleForeground, circleHoverColor;
	private final List<Rectangle> rects = new ArrayList<>();
	private int indexHover = -1;
	private Rectangle arrowLeftArea, arrowRightArea;
	private boolean hoverLeftArrow, hoverRightArrow;
	private final Carousel carousel;

	public ImageSelector(final Carousel parent, final int none) {
		super(parent, SWT.DOUBLE_BUFFERED);
		carousel = parent;

		addListener(SWT.Paint, e -> {
			final GC gc = e.gc;
			gc.setFont(getFont());
			gc.setAdvanced(true);
			gc.setTextAntialias(SWT.ON);
			gc.setAntialias(SWT.ON);
			final Color previousForeground = gc.getForeground();
			final Color previousBackground = gc.getBackground();

			drawContent(gc);

			gc.setBackground(previousBackground);
			gc.setForeground(previousForeground);
		});

		addListener(SWT.MouseExit, e -> {
			indexHover = -1;
			setCursor(getDisplay().getSystemCursor(SWT.CURSOR_ARROW));
		});

		addListener(SWT.MouseMove, e -> {
			Point pt = getDisplay().getCursorLocation();
			pt = getDisplay().map(null, this, pt.x, pt.y);

			hoverLeftArrow = arrowLeftArea.contains(pt);
			hoverRightArrow = arrowRightArea.contains(pt);

			Cursor expectedCursor = hoverLeftArrow || hoverRightArrow ? getDisplay().getSystemCursor(SWT.CURSOR_HAND) : getDisplay().getSystemCursor(SWT.CURSOR_ARROW);
			indexHover = -1;
			for (int i = 0; i < rects.size(); i++) {
				final Rectangle rect = rects.get(i);
				if (rect.contains(pt)) {
					expectedCursor = getDisplay().getSystemCursor(SWT.CURSOR_HAND);
					indexHover = i;
					break;
				}
			}

			if (!expectedCursor.equals(getCursor())) {
				setCursor(expectedCursor);
			}
			redraw();
		});

		addListener(SWT.MouseUp, e -> {
			final ImageContainer imageContainer = parent.imageContainer;
			if (hoverLeftArrow) {
				imageContainer.movePrevious();
			} else if (hoverRightArrow) {
				imageContainer.moveNext();
			} else if (indexHover != -1) {
				imageContainer.moveTo(indexHover);
			}
		});
	}

	private void drawContent(final GC gc) {
		final int x = drawCircles(gc);
		drawArrows(x, gc);
	}

	private int drawCircles(final GC gc) {
		int x = 0;
		final Rectangle clientArea = getClientArea();
		rects.clear();
		for (int i = 0; i < carousel.getImages().size(); i++) {
			final Rectangle rect = new Rectangle(x, (clientArea.height - CIRCLE_DIAMETER) / 2, CIRCLE_DIAMETER, CIRCLE_DIAMETER);
			rects.add(rect);
			if (i == carousel.getSelection()) {
				gc.setBackground(circleBackground);
				gc.fillOval(rect.x, rect.y, CIRCLE_DIAMETER, CIRCLE_DIAMETER);
			} else if (i == indexHover) {
				//
				gc.setForeground(circleHoverColor);
				gc.drawOval(rect.x, rect.y, CIRCLE_DIAMETER, CIRCLE_DIAMETER);
			} else {
				gc.setForeground(circleForeground);
				gc.drawOval(rect.x, rect.y, CIRCLE_DIAMETER, CIRCLE_DIAMETER);
			}
			x += CIRCLE_DIAMETER + 5;
		}
		return x + CIRCLE_DIAMETER;

	}

	private void drawArrows(int x, final GC gc) {
		gc.setForeground(arrowColor);

		final Rectangle clientArea = getClientArea();
		final int topY = (clientArea.height - ARROW_SIZE) / 2;
		final int width = (int) (ARROW_SIZE * .5f);

		gc.setLineWidth(hoverLeftArrow ? 3 : 1);

		gc.drawPolyline(new int[] { x + width, topY, //
				x, topY + ARROW_SIZE / 2, //
				x + width, topY + ARROW_SIZE });

		arrowLeftArea = new Rectangle(x, topY, ARROW_SIZE, ARROW_SIZE);

		x += ARROW_SIZE * 3;

		gc.setLineWidth(hoverRightArrow ? 3 : 1);
		gc.drawPolyline(new int[] { x, topY, //
				x + width, topY + ARROW_SIZE / 2, //
				x, topY + ARROW_SIZE });
		arrowRightArea = new Rectangle(x, topY, ARROW_SIZE, ARROW_SIZE);
	}

	@Override
	public Point computeSize(final int wHint, final int hHint, final boolean changed) {
		final Point superSize = super.computeSize(wHint, hHint, changed);
		final int width = 70 + carousel.getImages().size() * (CIRCLE_DIAMETER + 5);
		final int height = 40;
		return new Point(Math.max(superSize.x, width), Math.max(superSize.y, height));
	}

}
