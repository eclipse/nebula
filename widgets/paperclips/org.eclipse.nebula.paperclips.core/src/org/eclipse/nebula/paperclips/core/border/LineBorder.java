/*
 * Copyright (c) 2005 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */
package org.eclipse.nebula.paperclips.core.border;

import org.eclipse.nebula.paperclips.core.internal.util.ResourcePool;
import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;

/**
 * A border that draws a rectangle around a print.
 * 
 * @author Matthew Hall
 */
public class LineBorder implements Border {
	RGB rgb;
	int lineWidth = 1; // in points
	int gapSize = 5; // in points

	/**
	 * Constructs a LineBorder with a black border and 5-pt insets. (72 pts =
	 * 1")
	 */
	public LineBorder() {
		this(new RGB(0, 0, 0)); // black
	}

	/**
	 * Constructs a LineBorder with 5-pt insets. (72 pts = 1")
	 * 
	 * @param rgb
	 *            the color to use for the border.
	 */
	public LineBorder(RGB rgb) {
		setRGB(rgb);
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + gapSize;
		result = prime * result + lineWidth;
		result = prime * result + ((rgb == null) ? 0 : rgb.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LineBorder other = (LineBorder) obj;
		if (gapSize != other.gapSize)
			return false;
		if (lineWidth != other.lineWidth)
			return false;
		if (rgb == null) {
			if (other.rgb != null)
				return false;
		} else if (!rgb.equals(other.rgb))
			return false;
		return true;
	}

	/**
	 * Sets the border color to the argument.
	 * 
	 * @param rgb
	 *            the new border color.
	 */
	public void setRGB(RGB rgb) {
		this.rgb = new RGB(rgb.red, rgb.green, rgb.blue);
	}

	/**
	 * Returns the border color.
	 * 
	 * @return the border color.
	 */
	public RGB getRGB() {
		return new RGB(rgb.red, rgb.green, rgb.blue);
	}

	/**
	 * Sets the line width to the argument.
	 * 
	 * @param points
	 *            the line width, in points.
	 */
	public void setLineWidth(int points) {
		if (points < 1)
			points = 1;

		this.lineWidth = points;
	}

	/**
	 * Returns the line width of the border, expressed in points.
	 * 
	 * @return the line width of the border, expressed in points.
	 */
	public int getLineWidth() {
		return lineWidth;
	}

	/**
	 * Sets the size of the gap between the line border and the target print.
	 * 
	 * @param points
	 *            the gap size, expressed in points.
	 */
	public void setGapSize(int points) {
		if (points < 1)
			points = 1;

		this.gapSize = points;
	}

	/**
	 * Returns the size of the gap between the line border and the target print,
	 * expressed in points.
	 * 
	 * @return the gap size between the line border and the target print.
	 */
	public int getGapSize() {
		return Math.max(lineWidth, gapSize);
	}

	public BorderPainter createPainter(Device device, GC gc) {
		return new LineBorderPainter(this, device, gc);
	}
}

class LineBorderPainter extends AbstractBorderPainter {
	private final Device device;
	private final RGB rgb;
	private final Point lineWidth;
	private final Point borderWidth;

	LineBorderPainter(LineBorder border, Device device, GC gc) {
		Util.notNull(border, device, gc);
		this.rgb = border.rgb;
		this.device = device;

		int lineWidthPoints = border.getLineWidth();
		int borderWidthPoints = border.getGapSize();

		Point dpi = device.getDPI();
		lineWidth = new Point(Math.round(lineWidthPoints * dpi.x / 72f), Math
				.round(lineWidthPoints * dpi.y / 72f));
		borderWidth = new Point(Math.round(borderWidthPoints * dpi.x / 72f),
				Math.round(borderWidthPoints * dpi.y / 72f));
	}

	public int getLeft() {
		return borderWidth.x;
	}

	public int getRight() {
		return borderWidth.x;
	}

	public int getTop(boolean open) {
		return open ? 0 : borderWidth.y;
	}

	public int getBottom(boolean open) {
		return open ? 0 : borderWidth.y;
	}

	public void paint(GC gc, int x, int y, int width, int height,
			boolean topOpen, boolean bottomOpen) {
		Color oldColor = gc.getBackground();

		try {
			gc.setBackground(ResourcePool.forDevice(device).getColor(rgb));

			// Left & right
			gc.fillRectangle(x, y, lineWidth.x, height);
			gc.fillRectangle(x + width - lineWidth.x, y, lineWidth.x, height);

			// Top & bottom
			if (!topOpen)
				gc.fillRectangle(x, y, width, lineWidth.y);
			if (!bottomOpen)
				gc.fillRectangle(x, y + height - lineWidth.y, width,
						lineWidth.y);
		} finally {
			gc.setBackground(oldColor);
		}
	}

	public Point getOverlap() {
		return new Point(lineWidth.x, lineWidth.y);
	}

	public void dispose() {
	} // Shared resources -- nothing to dispose
}
