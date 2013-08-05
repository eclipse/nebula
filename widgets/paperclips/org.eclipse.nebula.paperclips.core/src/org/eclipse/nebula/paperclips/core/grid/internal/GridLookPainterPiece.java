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

import org.eclipse.nebula.paperclips.core.PrintPiece;
import org.eclipse.nebula.paperclips.core.grid.GridLookPainter;
import org.eclipse.nebula.paperclips.core.grid.GridMargins;
import org.eclipse.nebula.paperclips.core.internal.util.PaperClipsUtil;
import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

class GridLookPainterPiece implements PrintPiece {
	final GridLookPainter look;

	final int[] columns;
	final int[] headerRows;
	final int[][] headerColSpans;
	final int firstRowIndex;
	final boolean topOpen;
	final int[] bodyRows;
	final int[][] bodyColSpans;
	final boolean bottomOpen;
	final int[] footerRows;
	final int[][] footerColSpans;

	final Point size;

	GridLookPainterPiece(GridLookPainter look, int[] colSizes,
			int[] headerRows, int[][] headerColSpans, int firstRowIndex,
			boolean topOpen, int[] bodyRows, int[][] bodyColSpans,
			boolean bottomOpen, int[] footerRows, int[][] footerColSpans) {
		Util.notNull(look);

		this.look = look;
		this.columns = PaperClipsUtil.copy(colSizes);
		this.headerRows = PaperClipsUtil.copy(headerRows);
		this.headerColSpans = PaperClipsUtil.copy(headerColSpans);

		this.firstRowIndex = firstRowIndex;
		this.topOpen = topOpen;
		this.bodyRows = PaperClipsUtil.copy(bodyRows);
		this.bodyColSpans = PaperClipsUtil.copy(bodyColSpans);
		this.bottomOpen = bottomOpen;

		this.footerRows = PaperClipsUtil.copy(footerRows);
		this.footerColSpans = PaperClipsUtil.copy(footerColSpans);

		GridMargins margins = look.getMargins();

		Point size = calculateSize(margins, colSizes, headerRows, topOpen,
				bodyRows, bottomOpen, footerRows);
		this.size = size;
	}

	private static Point calculateSize(GridMargins margins, int[] columns,
			int[] headerRows, boolean topOpen, int[] bodyRows,
			boolean bottomOpen, int[] footerRows) {
		final boolean headerPresent = headerRows.length > 0;
		final boolean footerPresent = footerRows.length > 0;

		int width = calculateWidth(margins, columns);

		int height = calculateBodyHeight(margins, topOpen, bodyRows,
				bottomOpen, headerPresent, footerPresent);
		if (headerPresent)
			height += calculateHeaderHeight(margins, headerRows);
		if (footerPresent)
			height += calculateFooterHeight(margins, footerRows);

		return new Point(width, height);
	}

	private static int calculateWidth(GridMargins margins, int[] columns) {
		return margins.getLeft() + margins.getHorizontalSpacing()
				* (columns.length - 1) + margins.getRight()
				+ PaperClipsUtil.sum(columns);
	}

	private static int calculateBodyHeight(GridMargins margins,
			boolean topOpen, int[] bodyRows, boolean bottomOpen,
			final boolean headerPresent, final boolean footerPresent) {
		return margins.getBodyTop(headerPresent, topOpen)
				+ margins.getBodyVerticalSpacing() * (bodyRows.length - 1)
				+ margins.getBodyBottom(footerPresent, bottomOpen)
				+ PaperClipsUtil.sum(bodyRows);
	}

	private static int calculateHeaderHeight(GridMargins margins,
			int[] headerRows) {
		return margins.getHeaderTop() + margins.getHeaderVerticalSpacing()
				* (headerRows.length - 1) + PaperClipsUtil.sum(headerRows);
	}

	private static int calculateFooterHeight(GridMargins margins,
			int[] footerRows) {
		return margins.getFooterVerticalSpacing() * (footerRows.length - 1)
				+ margins.getFooterBottom() + PaperClipsUtil.sum(footerRows);
	}

	public void dispose() {
		look.dispose();
	}

	public Point getSize() {
		return new Point(size.x, size.y);
	}

	public void paint(GC gc, int x, int y) {
		look.paint(gc, x, y, columns, headerRows, headerColSpans,
				firstRowIndex, topOpen, bodyRows, bodyColSpans, bottomOpen,
				footerRows, footerColSpans);
	}
}
