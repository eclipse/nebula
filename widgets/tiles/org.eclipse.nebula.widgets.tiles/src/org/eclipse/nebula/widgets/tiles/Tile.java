/*****************************************************************************
 * Copyright (c) 2014, 2021 Fabian Prasser, Laurent Caron
 *
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Fabian Prasser - Initial API and implementation
 * Laurent Caron <laurent dot caron at gmail dot com> - Integration into the Nebula Project
 *****************************************************************************/
package org.eclipse.nebula.widgets.tiles;

import org.eclipse.swt.graphics.Color;

/**
 * A rendered tile
 *
 * @author Fabian Prasser
 *
 * @param <T>
 */
class Tile<T> {

	/** Element*/
	protected final T      item;

	/** Style*/
	protected final String label;
	/** Style*/
	protected final int    lineWidth;
	/** Style*/
	protected final int    lineStyle;
	/** Style*/
	protected final Color  foregroundColor;
	/** Style*/
	protected final Color  backgroundColor;
	/** Style*/
	protected final Color  lineColor;

	/** Location*/
	protected final int    x;
	/** Location*/
	protected final int    y;
	/** Location*/
	protected final int    width;
	/** Location*/
	protected final int    height;

	/**
	 * Creates a new instance
	 * @param element
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param label
	 * @param lineWidth
	 * @param lineStyle
	 * @param lineColor
	 * @param foregroundColor
	 * @param backgroundColor
	 */
	Tile(final T element, final int x, final int y, final int width, final int height, final String label, final int lineWidth,
			final int lineStyle, final Color lineColor, final Color foregroundColor,
			final Color backgroundColor) {

		this.item = element;

		this.label = label;
		this.lineWidth = lineWidth;
		this.lineStyle = lineStyle;
		this.lineColor = lineColor;
		this.foregroundColor = foregroundColor;
		this.backgroundColor = backgroundColor;

		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Tile [x=" + x + ", y=" + y + ", width=" + width
				+ ", height=" + height + "]";
	}
}
