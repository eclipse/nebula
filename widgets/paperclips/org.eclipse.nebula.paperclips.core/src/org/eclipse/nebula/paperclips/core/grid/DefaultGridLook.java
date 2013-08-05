/*
 * Copyright (c) 2006 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */
package org.eclipse.nebula.paperclips.core.grid;

import org.eclipse.nebula.paperclips.core.border.Border;
import org.eclipse.nebula.paperclips.core.border.GapBorder;
import org.eclipse.nebula.paperclips.core.grid.internal.DefaultGridLookPainter;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

/**
 * A GridLook which draws a border around grid cells, with configurable
 * background colors for body, header, and footer cells.
 * 
 * @author Matthew Hall
 */
public class DefaultGridLook implements GridLook {
	/**
	 * Constant cell spacing value indicating that the borders of adjacent cells
	 * should overlap so the appear continuous.
	 */
	public static final int BORDER_OVERLAP = -1;

	Point cellSpacing = new Point(BORDER_OVERLAP, BORDER_OVERLAP);
	Rectangle cellPadding = new Rectangle(0, 0, 0, 0);
	int headerGap = BORDER_OVERLAP;
	int footerGap = BORDER_OVERLAP;

	Border cellBorder = new GapBorder();

	DefaultCellBackgroundProvider defaultBodyBackgroundProvider;
	DefaultCellBackgroundProvider defaultHeaderBackgroundProvider;
	DefaultCellBackgroundProvider defaultFooterBackgroundProvider;

	CellBackgroundProvider bodyBackgroundProvider;
	CellBackgroundProvider headerBackgroundProvider;
	CellBackgroundProvider footerBackgroundProvider;

	/**
	 * Constructs a DefaultGridLook with no border, no cell spacing, and no
	 * background colors.
	 */
	public DefaultGridLook() {
		this.bodyBackgroundProvider = defaultBodyBackgroundProvider = new DefaultCellBackgroundProvider();
		this.headerBackgroundProvider = defaultHeaderBackgroundProvider = new DefaultCellBackgroundProvider(
				bodyBackgroundProvider);
		this.footerBackgroundProvider = defaultFooterBackgroundProvider = new DefaultCellBackgroundProvider(
				bodyBackgroundProvider);
	}

	/**
	 * Constructs a DefaultGridLook with the given cell spacing, and no border
	 * or background colors.
	 * 
	 * @param horizontalSpacing
	 *            the horizontal cell spacing.
	 * @param verticalSpacing
	 *            the vertical cell spacing.
	 */
	public DefaultGridLook(int horizontalSpacing, int verticalSpacing) {
		this();
		setCellSpacing(horizontalSpacing, verticalSpacing);
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((bodyBackgroundProvider == null) ? 0
						: bodyBackgroundProvider.hashCode());
		result = prime * result
				+ ((cellBorder == null) ? 0 : cellBorder.hashCode());
		result = prime * result
				+ ((cellPadding == null) ? 0 : cellPadding.hashCode());
		result = prime * result
				+ ((cellSpacing == null) ? 0 : cellSpacing.hashCode());
		result = prime
				* result
				+ ((footerBackgroundProvider == null) ? 0
						: footerBackgroundProvider.hashCode());
		result = prime * result + footerGap;
		result = prime
				* result
				+ ((headerBackgroundProvider == null) ? 0
						: headerBackgroundProvider.hashCode());
		result = prime * result + headerGap;
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultGridLook other = (DefaultGridLook) obj;
		if (bodyBackgroundProvider == null) {
			if (other.bodyBackgroundProvider != null)
				return false;
		} else if (!bodyBackgroundProvider.equals(other.bodyBackgroundProvider))
			return false;
		if (cellBorder == null) {
			if (other.cellBorder != null)
				return false;
		} else if (!cellBorder.equals(other.cellBorder))
			return false;
		if (cellPadding == null) {
			if (other.cellPadding != null)
				return false;
		} else if (!cellPadding.equals(other.cellPadding))
			return false;
		if (cellSpacing == null) {
			if (other.cellSpacing != null)
				return false;
		} else if (!cellSpacing.equals(other.cellSpacing))
			return false;
		if (footerBackgroundProvider == null) {
			if (other.footerBackgroundProvider != null)
				return false;
		} else if (!footerBackgroundProvider
				.equals(other.footerBackgroundProvider))
			return false;
		if (footerGap != other.footerGap)
			return false;
		if (headerBackgroundProvider == null) {
			if (other.headerBackgroundProvider != null)
				return false;
		} else if (!headerBackgroundProvider
				.equals(other.headerBackgroundProvider))
			return false;
		if (headerGap != other.headerGap)
			return false;
		return true;
	}

	/**
	 * Returns the cell border. Default is an empty border with no margins.
	 * 
	 * @return the cell border.
	 */
	public Border getCellBorder() {
		return cellBorder;
	}

	/**
	 * Sets the cell border.
	 * 
	 * @param border
	 *            the cell border.
	 */
	public void setCellBorder(Border border) {
		this.cellBorder = border;
	}

	/**
	 * Returns the border spacing, in points, between adjacent grid cells.
	 * Default is (x=BORDER_OVERLAP, y=BORDER_OVERLAP).
	 * 
	 * @return the border spacing, in points, between adjacent grid cells.
	 */
	public Point getCellSpacing() {
		return new Point(cellSpacing.x, cellSpacing.y);
	}

	/**
	 * Sets the border spacing, in points, between adjacent grid cells. A value
	 * of {@link #BORDER_OVERLAP} causes the borders to overlap, making the
	 * border appear continuous throughout the grid. A value of 0 or more causes
	 * the cell borders to be spaced that many points apart. 72 points = 1".
	 * 
	 * @param cellSpacing
	 *            a point whose x and y elements indicate the horizontal and
	 *            vertical spacing between grid cells.
	 */
	public void setCellSpacing(Point cellSpacing) {
		setCellSpacing(cellSpacing.x, cellSpacing.y);
	}

	/**
	 * Sets the border spacing, in points, between adjacent grid cells. A value
	 * of {@link #BORDER_OVERLAP} causes the borders to overlap, making the
	 * border appear continuous throughout the grid. A value of 0 or more causes
	 * the cell borders to be spaced that many points apart. 72 points = 1".
	 * 
	 * @param horizontal
	 *            the horizontal cell spacing.
	 * @param vertical
	 *            the vertical cell spacing.
	 */
	public void setCellSpacing(int horizontal, int vertical) {
		if (horizontal == BORDER_OVERLAP || horizontal >= 0)
			this.cellSpacing.x = horizontal;
		if (vertical == BORDER_OVERLAP || vertical >= 0)
			this.cellSpacing.y = vertical;
	}

	/**
	 * Returns a rectangle whose public fields denote the left (x), top (y),
	 * right (width) and bottom (height) cell padding, expressed in points. 72
	 * points = 1" = 2.54cm.
	 * 
	 * @return a rectangle whose public fields denote the cell padding at each
	 *         edge.
	 */
	public Rectangle getCellPadding() {
		return new Rectangle(cellPadding.x, cellPadding.y, cellPadding.width,
				cellPadding.height);
	}

	/**
	 * Sets the cell padding to the values in the public fields of the argument.
	 * 
	 * @param cellPadding
	 *            the new cell padding.
	 */
	public void setCellPadding(Rectangle cellPadding) {
		setCellPadding(cellPadding.x, cellPadding.y, cellPadding.width,
				cellPadding.height);
	}

	/**
	 * Sets the cell padding to the given horizontal and vertical values. This
	 * is equivalent to calling setCellPadding(horizontalPadding,
	 * verticalPadding, horizontalPadding, verticalPadding).
	 * 
	 * @param horizontalPadding
	 *            the amount of padding to add to the left and right of each
	 *            cell, in points.
	 * @param verticalPadding
	 *            the amount padding to add to the top and bottom each cell, in
	 *            points.
	 */
	public void setCellPadding(int horizontalPadding, int verticalPadding) {
		setCellPadding(horizontalPadding, verticalPadding, horizontalPadding,
				verticalPadding);
	}

	/**
	 * Sets the cell padding to the specified values.
	 * 
	 * @param left
	 *            the left cell padding, in points.
	 * @param top
	 *            the top cell padding, in points.
	 * @param right
	 *            the right cell padding, in points.
	 * @param bottom
	 *            the bottom cell padding, in points.
	 */
	public void setCellPadding(int left, int top, int right, int bottom) {
		cellPadding.x = left;
		cellPadding.y = top;
		cellPadding.width = right;
		cellPadding.height = bottom;
	}

	/**
	 * Returns the header background color. If null, the body background color
	 * is used. Default is null.
	 * 
	 * @return the header background color.
	 */
	public RGB getHeaderBackground() {
		return defaultHeaderBackgroundProvider.getBackground();
	}

	/**
	 * Sets the header background color. Calls to this method override any
	 * previous calls to setHeaderBackgroundProvider(...).
	 * 
	 * @param headerBackground
	 *            the new background color. If null, the body background color
	 *            will be used.
	 */
	public void setHeaderBackground(RGB headerBackground) {
		defaultHeaderBackgroundProvider.setBackground(headerBackground);
		this.headerBackgroundProvider = defaultHeaderBackgroundProvider;
	}

	/**
	 * Returns the header background color provider.
	 * 
	 * @return the header background color provider.
	 */
	public CellBackgroundProvider getHeaderBackgroundProvider() {
		return headerBackgroundProvider;
	}

	/**
	 * Sets the header background color provider. Calls to this method override
	 * any previous calls to setHeaderBackground(RGB). Setting this property to
	 * null restores the default background provider.
	 * 
	 * @param headerBackgroundProvider
	 *            the new background color provider.
	 */
	public void setHeaderBackgroundProvider(
			CellBackgroundProvider headerBackgroundProvider) {
		this.headerBackgroundProvider = headerBackgroundProvider == null ? defaultHeaderBackgroundProvider
				: headerBackgroundProvider;
	}

	/**
	 * Returns the vertical gap between the header and body cells. Default is
	 * BORDER_OVERLAP.
	 * 
	 * @return the vertical gap between the header and body cells.
	 */
	public int getHeaderGap() {
		return headerGap;
	}

	/**
	 * Sets the vertical gap between the header and body cells. A value of
	 * {@link #BORDER_OVERLAP} causes the borders to overlap, making the border
	 * appear continuous in the transition from the header cells to the body
	 * cells.
	 * 
	 * @param headerGap
	 *            the new header gap.
	 */
	public void setHeaderGap(int headerGap) {
		this.headerGap = headerGap;
	}

	/**
	 * Returns the body background color. Default is null (no background color).
	 * 
	 * @return the body background color.
	 */
	public RGB getBodyBackground() {
		return defaultBodyBackgroundProvider.getBackground();
	}

	/**
	 * Sets the body background color. Calls to this method override any
	 * previous calls to setBodyBackgroundProvider(...).
	 * 
	 * @param bodyBackground
	 *            the new background color.
	 */
	public void setBodyBackground(RGB bodyBackground) {
		defaultBodyBackgroundProvider.setBackground(bodyBackground);
		this.bodyBackgroundProvider = defaultBodyBackgroundProvider;
	}

	/**
	 * Returns the body background color provider.
	 * 
	 * @return the body background color provider.
	 */
	public CellBackgroundProvider getBodyBackgroundProvider() {
		return bodyBackgroundProvider;
	}

	/**
	 * Sets the body background color provider. Calls to this method override
	 * any previous calls to setBodyBackground(RGB). Setting this property to
	 * null restores the default background provider.
	 * 
	 * @param bodyBackgroundProvider
	 *            the new background color provider.
	 */
	public void setBodyBackgroundProvider(
			CellBackgroundProvider bodyBackgroundProvider) {
		this.bodyBackgroundProvider = bodyBackgroundProvider == null ? defaultBodyBackgroundProvider
				: bodyBackgroundProvider;
	}

	/**
	 * Returns the vertical gap between the body and footer cells. Default is
	 * BORDER_OVERLAP.
	 * 
	 * @return the vertical gap between the header and body cells.
	 */
	public int getFooterGap() {
		return footerGap;
	}

	/**
	 * Sets the vertical gap between the header and body cells. A value of
	 * {@link #BORDER_OVERLAP} causes the borders to overlap, making the border
	 * appear continuous in the transition from the body cells to the footer
	 * cells.
	 * 
	 * @param footerGap
	 */
	public void setFooterGap(int footerGap) {
		this.footerGap = footerGap;
	}

	/**
	 * Returns the footer background color. If null, the body background color
	 * is used. Default is null.
	 * 
	 * @return the footer background color.
	 */
	public RGB getFooterBackground() {
		return defaultFooterBackgroundProvider.getBackground();
	}

	/**
	 * Sets the footer background color. Calls to this method override any
	 * previous calls to setFooterBackgroundProvider(...).
	 * 
	 * @param footerBackground
	 *            the new background color. If null, the body background color
	 *            will be used.
	 */
	public void setFooterBackground(RGB footerBackground) {
		defaultFooterBackgroundProvider.setBackground(footerBackground);
		this.footerBackgroundProvider = defaultFooterBackgroundProvider;
	}

	/**
	 * Returns the footer background color provider.
	 * 
	 * @return the footer background color provider.
	 */
	public CellBackgroundProvider getFooterBackgroundProvider() {
		return footerBackgroundProvider;
	}

	/**
	 * Sets the footer background color provider. Calls to this method override
	 * any previous calls to setFooterBackground(RGB). Setting this property to
	 * null restores the default background provider.
	 * 
	 * @param footerBackgroundProvider
	 *            the new background color provider.
	 */
	public void setFooterBackgroundProvider(
			CellBackgroundProvider footerBackgroundProvider) {
		this.footerBackgroundProvider = footerBackgroundProvider == null ? defaultFooterBackgroundProvider
				: footerBackgroundProvider;
	}

	public GridLookPainter getPainter(Device device, GC gc) {
		return new DefaultGridLookPainter(this, device, gc);
	}
}
