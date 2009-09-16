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

import org.eclipse.swt.graphics.GC;

/**
 * Interface for drawing a GridLook.
 * 
 * @author Matthew Hall
 */
public interface GridLookPainter {
	/**
	 * Returns the grid margins used for the GridLook.
	 * 
	 * @return the grid margins used for the GridLook.
	 * @see GridMargins
	 */
	public GridMargins getMargins();

	/**
	 * Paints the grid look onto the GC.
	 * 
	 * @param gc
	 *            the graphics context to paint on.
	 * @param x
	 *            the x coordinate of the top-left of the grid.
	 * @param y
	 *            the y coordinate of the top-left of the grid.
	 * @param columns
	 *            the column widths. The left and right margins of each cell are
	 *            included in the column widths.
	 * @param headerRows
	 *            the header row heights.
	 * @param headerColSpans
	 *            a two-dimensional array of cell spans in the header. Each
	 *            element in the outer array is a header row. Each element of an
	 *            inner array is a cell, where the element value indicates how
	 *            many columns the cell spans.
	 * @param firstRowIndex
	 *            the zero-based index of the first row displayed on the page.
	 * @param topOpen
	 *            whether the top body row should be drawn with the top edge of
	 *            the cell border "open." An open top border is a visual
	 *            indication that the top row is being continued from the
	 *            previous page.
	 * @param bodyRows
	 *            the body row heights.
	 * @param bodyColSpans
	 *            a two-dimensional array of cell spans in the body. Each
	 *            element in the outer array is a body row. Each element of an
	 *            inner array is a cell, where the element value indicates how
	 *            many columns the cell spans.
	 * @param bottomOpen
	 *            whether the bottom body row should be drawn with the bottom
	 *            edge of the cell border "open." An open bottom border is a
	 *            visual indication that the bottom row will be continued on the
	 *            next page.
	 * @param footerRows
	 *            the footer row heights.
	 * @param footerColSpans
	 *            a two-dimensional array of cell spans in the footer. Each
	 *            element in the outer array is a footer row. Each element of an
	 *            inner array is a cell, where the element value indicates how
	 *            many columns the cell spans.
	 */
	public void paint(final GC gc, final int x, final int y,
			final int[] columns, final int[] headerRows,
			final int[][] headerColSpans, final int firstRowIndex,
			final boolean topOpen, final int[] bodyRows,
			final int[][] bodyColSpans, final boolean bottomOpen,
			final int[] footerRows, final int[][] footerColSpans);

	/**
	 * Disposes the system resources allocated by this GridLookPainter. The
	 * dispose method is <b>not</b> a permanent disposal of a GridLookPainter.
	 * It is intended to reclaim system resources, however future calls to
	 * paint(GC,int,int) may require that the resources be allocated again.
	 */
	public void dispose();
}
