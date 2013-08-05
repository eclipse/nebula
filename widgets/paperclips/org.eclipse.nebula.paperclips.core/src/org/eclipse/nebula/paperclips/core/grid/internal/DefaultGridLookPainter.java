/*
 * Copyright (c) 2007 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */

package org.eclipse.nebula.paperclips.core.grid.internal;

import org.eclipse.nebula.paperclips.core.border.BorderPainter;
import org.eclipse.nebula.paperclips.core.grid.BasicGridLookPainter;
import org.eclipse.nebula.paperclips.core.grid.CellBackgroundProvider;
import org.eclipse.nebula.paperclips.core.grid.DefaultGridLook;
import org.eclipse.nebula.paperclips.core.grid.GridMargins;
import org.eclipse.nebula.paperclips.core.internal.util.ResourcePool;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

public class DefaultGridLookPainter extends BasicGridLookPainter {
	private final Rectangle cellPadding;

	private final BorderPainter border;

	private final CellBackgroundProvider headerBackground;
	private final CellBackgroundProvider bodyBackground;
	private final CellBackgroundProvider footerBackground;

	private final GridMargins margins;

	private final ResourcePool resources;

	public DefaultGridLookPainter(DefaultGridLook look, Device device, GC gc) {
		super(device);

		Point dpi = device.getDPI();

		this.border = look.getCellBorder().createPainter(device, gc);
		this.cellPadding = calculateCellPadding(look, dpi);
		this.margins = calculateGridMargins(look, dpi);

		this.bodyBackground = look.getBodyBackgroundProvider();
		this.headerBackground = look.getHeaderBackgroundProvider();
		this.footerBackground = look.getFooterBackgroundProvider();

		this.resources = ResourcePool.forDevice(device);
	}

	private Rectangle calculateCellPadding(DefaultGridLook look, Point dpi) {
		Rectangle cellPadding = new Rectangle(look.getCellPadding().x * dpi.x
				/ 72, look.getCellPadding().y * dpi.y / 72,
				look.getCellPadding().width * dpi.x / 72,
				look.getCellPadding().height * dpi.y / 72);
		return cellPadding;
	}

	private GridMargins calculateGridMargins(DefaultGridLook look, Point dpi) {
		final Point cellSpacing = new Point(
				border.getWidth()
						+ (look.getCellSpacing().x == DefaultGridLook.BORDER_OVERLAP ? -border.getOverlap().x
								: dpi.x * look.getCellSpacing().x / 72),
				border.getHeight(false, false)
						+ (look.getCellSpacing().y == DefaultGridLook.BORDER_OVERLAP ? -border
								.getOverlap().y : dpi.y
								* look.getCellSpacing().y / 72));

		final int headerClosedSpacing = border.getHeight(false, false)
				+ (look.getHeaderGap() == DefaultGridLook.BORDER_OVERLAP ? -border
						.getOverlap().y : dpi.y * look.getHeaderGap() / 72);
		final int headerOpenSpacing = border.getHeight(true, false)
				+ (look.getHeaderGap() == DefaultGridLook.BORDER_OVERLAP ? dpi.y / 72
						: dpi.y * look.getHeaderGap() / 72);
		final int footerClosedSpacing = border.getHeight(false, false)
				+ (look.getFooterGap() == DefaultGridLook.BORDER_OVERLAP ? -border
						.getOverlap().y : dpi.y * look.getFooterGap() / 72);
		final int footerOpenSpacing = border.getHeight(false, true)
				+ (look.getFooterGap() == DefaultGridLook.BORDER_OVERLAP ? dpi.y / 72
						: dpi.y * look.getFooterGap() / 72);

		return new DefaultGridMargins(border, cellSpacing, cellPadding,
				headerClosedSpacing, headerOpenSpacing, footerClosedSpacing,
				footerOpenSpacing);
	}

	public GridMargins getMargins() {
		return margins;
	}

	protected void paintHeaderCell(GC gc, Rectangle bounds, int row, int col,
			int colspan) {
		RGB background = headerBackground.getCellBackground(row, col, colspan);
		paintCell(gc, background, bounds, false, false);
	}

	protected void paintBodyCell(GC gc, Rectangle bounds, int row, int col,
			int colspan, boolean topOpen, boolean bottomOpen) {
		RGB background = bodyBackground.getCellBackground(row, col, colspan);
		paintCell(gc, background, bounds, topOpen, bottomOpen);
	}

	protected void paintFooterCell(GC gc, Rectangle bounds, int row, int col,
			int colspan) {
		RGB background = footerBackground.getCellBackground(row, col, colspan);
		paintCell(gc, background, bounds, false, false);
	}

	private void paintCell(GC gc, RGB background, Rectangle bounds,
			boolean topOpen, boolean bottomOpen) {
		// Compute effective cell rectangle
		int x = bounds.x - border.getLeft() - cellPadding.x;
		int y = bounds.y - border.getTop(topOpen)
				- (topOpen ? 0 : cellPadding.y);
		int width = bounds.width + border.getWidth() + cellPadding.x
				+ cellPadding.width;
		int height = bounds.height + border.getHeight(topOpen, bottomOpen)
				+ (bottomOpen ? 0 : cellPadding.y + cellPadding.height);

		// Paint background
		Color backgroundColor = resources.getColor(background);
		if (backgroundColor != null) {
			Color oldBackground = gc.getBackground();
			gc.setBackground(backgroundColor);
			gc.fillRectangle(x, y, width, height);
			gc.setBackground(oldBackground);
		}

		// Paint border
		border.paint(gc, x, y, width, height, topOpen, bottomOpen);
	}

	public void dispose() {
		border.dispose();
	}
}