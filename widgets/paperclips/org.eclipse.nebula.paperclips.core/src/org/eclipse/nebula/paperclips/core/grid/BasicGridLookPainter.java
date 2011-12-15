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
package org.eclipse.nebula.paperclips.core.grid;

import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

/**
 * A abstract GridLookPainter which simplifies implementation of custom
 * GridLooks.
 * <p>
 * Subclasses must have the following methods implemented:
 * <ul>
 * <li>getMargins() - these margins are referenced by GridPrint for determining
 * proper layout of the cells, as well as by the paint() method.
 * <li>paintHeaderCell() - will be called by the paint() method for each header
 * cell.
 * <li>paintBodyCell() - will be called by the paint() method for each body
 * cell.
 * <li>paintFooterCell() - will be called by the paint() method for each footer
 * cell.
 * <li>dispose() - must dispose any SWT resources created by the subclass.
 * </ul>
 * 
 * @author Matthew Hall
 */
public abstract class BasicGridLookPainter implements GridLookPainter {
	/**
	 * The printer device on which the look is being painted. This is the device
	 * that was passed as an argument to the constructor.
	 */
	protected final Device device;

	/**
	 * Constructs a BasicGridLook painter.
	 * 
	 * @param device
	 *            the printer device (may not be null). This argument will be
	 *            saved in the protected {@link #device} field.
	 */
	public BasicGridLookPainter(Device device) {
		Util.notNull(device);
		this.device = device;
	}

	public void paint(GC gc, int x, int y, int[] columns, int[] headerRows,
			int[][] headerColSpans, int firstRowIndex, boolean topOpen,
			int[] bodyRows, int[][] bodyColSpans, boolean bottomOpen,
			int[] footerRows, int[][] footerColSpans) {
		GridMargins margins = getMargins();

		final boolean headerPresent = headerRows.length > 0;
		final boolean footerPresent = footerRows.length > 0;

		x += margins.getLeft();

		if (headerPresent)
			y = paintHeader(gc, x, y, columns, headerRows, headerColSpans);

		y += margins.getBodyTop(headerPresent, topOpen);
		y = paintBody(gc, x, y, columns, bodyRows, bodyColSpans, firstRowIndex,
				topOpen, bottomOpen);
		y += margins.getBodyBottom(footerPresent, bottomOpen);

		if (footerPresent)
			paintFooter(gc, x, y, columns, footerRows, footerColSpans);
	}

	private int paintHeader(GC gc, int x, int y, int[] columns, int[] rows,
			int[][] colSpans) {
		GridMargins margins = getMargins();

		y += margins.getHeaderTop();

		for (int i = 0; i < rows.length; i++) {
			int h = rows[i];

			paintHeaderRow(gc, x, y, columns, h, i, colSpans[i]);

			y += h;
			if (i < rows.length - 1)
				y += margins.getHeaderVerticalSpacing();
		}

		return y;
	}

	private int paintBody(GC gc, int x, int y, int[] columns, int[] rows,
			int[][] colSpans, int firstRowIndex, boolean topOpen,
			boolean bottomOpen) {
		GridMargins margins = getMargins();

		for (int i = 0; i < rows.length; i++) {
			final int h = rows[i];

			paintBodyRow(gc, x, y, columns, h, colSpans[i], firstRowIndex + i,
					i == 0 && topOpen, i == rows.length - 1 && bottomOpen);

			y += h;
			if (i < rows.length - 1)
				y += margins.getBodyVerticalSpacing();
		}
		return y;
	}

	private void paintFooter(GC gc, final int x, int y, int[] columns,
			int[] rows, int[][] colSpans) {
		GridMargins margins = getMargins();

		for (int i = 0; i < rows.length; i++) {
			final int h = rows[i];

			paintFooterRow(gc, x, y, columns, h, i, colSpans[i]);

			y += h;
			y += margins.getFooterVerticalSpacing();
		}
	}

	private void paintHeaderRow(GC gc, int x, int y, int[] columns,
			final int h, int rowIndex, int[] colSpans) {
		GridMargins margins = getMargins();

		int col = 0;
		for (int i = 0; i < colSpans.length; i++) {
			final int colSpan = colSpans[i];
			final int w = sum(columns, col, colSpan) + (colSpan - 1)
					* margins.getHorizontalSpacing();

			paintHeaderCell(gc, new Rectangle(x, y, w, h), rowIndex, col,
					colSpan);

			col += colSpan;
			x += w + margins.getHorizontalSpacing();
		}
	}

	private void paintBodyRow(GC gc, int x, int y, int[] columns, final int h,
			int[] colSpans, int rowIndex, final boolean topOpen,
			final boolean bottomOpen) {
		GridMargins margins = getMargins();

		int col = 0;
		for (int i = 0; i < colSpans.length; i++) {
			final int colSpan = colSpans[i];
			final int w = sum(columns, col, colSpan) + (colSpan - 1)
					* margins.getHorizontalSpacing();

			paintBodyCell(gc, new Rectangle(x, y, w, h), rowIndex, col,
					colSpan, topOpen, bottomOpen);

			col += colSpan;
			x += w + margins.getHorizontalSpacing();
		}
	}

	private void paintFooterRow(GC gc, int x, int y, int[] columns,
			final int h, int rowIndex, int[] colSpans) {
		GridMargins margins = getMargins();

		int col = 0;
		for (int i = 0; i < colSpans.length; i++) {
			final int colSpan = colSpans[i];
			final int w = sum(columns, col, colSpan) + (colSpan - 1)
					* margins.getHorizontalSpacing();

			paintFooterCell(gc, new Rectangle(x, y, w, h), rowIndex, col,
					colSpan);

			col += colSpan;
			x += w + margins.getHorizontalSpacing();
		}
	}

	private int sum(int[] elements, int start, int length) {
		int sum = 0;
		for (int j = 0; j < length; j++)
			sum += elements[start + j];
		return sum;
	}

	/**
	 * Paint the decorations for the described header cell.
	 * 
	 * @param gc
	 *            the graphics context to use for painting.
	 * @param bounds
	 *            the bounds of the cell, excluding margins.
	 * @param row
	 *            the row offset of the cell within the header.
	 * @param col
	 *            the column offset of the cell within the header.
	 * @param colspan
	 *            the number of columns that this cell spans.
	 */
	protected abstract void paintHeaderCell(GC gc, Rectangle bounds, int row,
			int col, int colspan);

	/**
	 * Paint the decorations for the described body cell.
	 * 
	 * @param gc
	 *            the graphics context to use for painting.
	 * @param bounds
	 *            the bounds of the cell, excluding margins.
	 * @param row
	 *            the row offset of the cell within the header.
	 * @param col
	 *            the column offset of the cell within the header.
	 * @param colspan
	 *            the number of columns that this cell spans.
	 * @param topOpen
	 *            whether the cell should be drawn with the top edge of the cell
	 *            border "open." An open top border is a visual cue that the
	 *            cell is being continued from the previous page.
	 * @param bottomOpen
	 *            whether the cell should be drawn with the bottom edge of the
	 *            cell border "open." An open bottom border is a visual cue that
	 *            the cell will be continued on the next page.
	 */
	protected abstract void paintBodyCell(GC gc, Rectangle bounds, int row,
			int col, int colspan, boolean topOpen, boolean bottomOpen);

	/**
	 * Paint the decorations for the described footer cell.
	 * 
	 * @param gc
	 *            the graphics context to use for painting.
	 * @param bounds
	 *            the bounds of the cell, excluding margins.
	 * @param row
	 *            the row offset of the cell within the header.
	 * @param col
	 *            the column offset of the cell within the header.
	 * @param colspan
	 *            the number of columns that this cell spans.
	 */
	protected abstract void paintFooterCell(GC gc, Rectangle bounds, int row,
			int col, int colspan);
}