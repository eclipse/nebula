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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.nebula.paperclips.core.CompositeEntry;
import org.eclipse.nebula.paperclips.core.CompositePiece;
import org.eclipse.nebula.paperclips.core.PaperClips;
import org.eclipse.nebula.paperclips.core.PrintIterator;
import org.eclipse.nebula.paperclips.core.PrintPiece;
import org.eclipse.nebula.paperclips.core.grid.GridCell;
import org.eclipse.nebula.paperclips.core.grid.GridColumn;
import org.eclipse.nebula.paperclips.core.grid.GridLookPainter;
import org.eclipse.nebula.paperclips.core.grid.GridMargins;
import org.eclipse.nebula.paperclips.core.grid.GridPrint;
import org.eclipse.nebula.paperclips.core.internal.util.PaperClipsUtil;
import org.eclipse.nebula.paperclips.core.internal.util.PrintSizeStrategy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

public class GridIterator implements PrintIterator {
	final Device device;
	final Point dpi;

	final GridColumn[] columns;
	final int[][] columnGroups;

	final GridLookPainter look;

	final GridCellIterator[][] header;
	final GridCellIterator[][] body;
	final GridCellIterator[][] footer;

	final boolean cellClippingEnabled;

	final int[] minimumColSizes; // PIXELS
	final int[] preferredColSizes; // PIXELS

	final Point minimumSize; // PIXELS
	final Point preferredSize; // PIXELS

	// This is the cursor!
	private int row;

	// Determines whether top edge of cell border is drawn open or closed for
	// current row.
	private boolean rowStarted;

	public GridIterator(GridPrint grid, Device device, GC gc) {
		this.device = device;
		this.dpi = device.getDPI();
		this.columns = new GridColumn[grid.getColumns().length];
		System.arraycopy(grid.getColumns(), 0, this.columns, 0,
				grid.getColumns().length);
		this.columnGroups = grid.getColumnGroups();

		this.header = createGridCellIterators(grid.getHeaderCells(), device, gc);
		this.body = createGridCellIterators(grid.getBodyCells(), device, gc);
		this.footer = createGridCellIterators(grid.getFooterCells(), device, gc);

		this.cellClippingEnabled = grid.isCellClippingEnabled();

		this.look = grid.getLook().getPainter(device, gc);

		this.minimumColSizes = computeColumnSizes(PrintSizeStrategy.MINIMUM);
		this.preferredColSizes = computeColumnSizes(PrintSizeStrategy.PREFERRED);

		this.minimumSize = computeSize(PrintSizeStrategy.MINIMUM,
				minimumColSizes);
		this.preferredSize = computeSize(PrintSizeStrategy.PREFERRED,
				preferredColSizes);

		row = 0;
		rowStarted = false;
	}

	private static GridCellIterator[][] createGridCellIterators(
			GridCell[][] gridCells, Device device, GC gc) {
		GridCellIterator[][] result = new GridCellIterator[gridCells.length][];
		for (int rowIndex = 0; rowIndex < result.length; rowIndex++)
			result[rowIndex] = createRowCellIterators(gridCells[rowIndex],
					device, gc);
		return result;
	}

	private static GridCellIterator[] createRowCellIterators(
			GridCell[] rowCells, Device device, GC gc) {
		GridCellIterator[] result = new GridCellIterator[rowCells.length];
		for (int cellIndex = 0; cellIndex < rowCells.length; cellIndex++)
			result[cellIndex] = ((GridCellImpl) rowCells[cellIndex]).iterator(
					device, gc);
		return result;
	}

	/** Copy constructor (used by copy() only) */
	private GridIterator(GridIterator that) {
		this.device = that.device;
		this.dpi = that.dpi;

		this.columns = that.columns;
		this.columnGroups = that.columnGroups;

		this.header = that.header; // never directly modified, clone not
		// necessary
		this.body = cloneRows(that.body, that.row); // Only need to deep copy
		// the unconsumed rows.
		this.footer = that.footer; // never directly modified, clone not
		// necessary

		this.cellClippingEnabled = that.cellClippingEnabled;

		this.look = that.look;

		this.minimumColSizes = that.minimumColSizes;
		this.preferredColSizes = that.preferredColSizes;

		this.minimumSize = that.minimumSize;
		this.preferredSize = that.preferredSize;

		this.row = that.row;
		this.rowStarted = that.rowStarted;
	}

	private static GridCellIterator[][] cloneRows(GridCellIterator[][] rows,
			int firstRow) {
		GridCellIterator[][] result = (GridCellIterator[][]) rows.clone();
		// Cloning the outer array is all that's necessary. The inner arrays
		// (rows) are cloned every time a row
		// is laid out, so all we have to do is make sure different
		// GridIterators have distinct outer arrays.
		// for ( int i = firstRow; i < result.length; i++ )
		// result[i] = cloneRow( result[i] );
		return result;
	}

	/**
	 * Compute the size of a column, respecting the constraints of the
	 * GridColumn.
	 */
	private int computeCellWidth(GridCellIterator entry, GridColumn col,
			PrintSizeStrategy strategy) {
		if (col.size == SWT.DEFAULT)
			return strategy.computeSize(entry.getTarget()).x;
		if (col.size == GridPrint.PREFERRED)
			return entry.getTarget().preferredSize().x;
		return Math.round(col.size * device.getDPI().x / 72f);
	}

	private static boolean isExplicitSize(GridColumn col) {
		return col.size > 0;
	}

	private void applyColumnGrouping(int[] columnSizes) {
		for (int groupIndex = 0; groupIndex < columnGroups.length; groupIndex++) {
			int[] group = columnGroups[groupIndex];

			// find max column width in group
			int maxSize = 0;
			for (int columnInGroupIndex = 0; columnInGroupIndex < group.length; columnInGroupIndex++) {
				int col = group[columnInGroupIndex];
				maxSize = Math.max(maxSize, columnSizes[col]);
			}

			// grow all columns to max column width
			for (int columnInGroupIndex = 0; columnInGroupIndex < group.length; columnInGroupIndex++) {
				int col = group[columnInGroupIndex];
				columnSizes[col] = maxSize;
			}
		}
	}

	private boolean isColumnGrouped(int col) {
		for (int groupIndex = 0; groupIndex < columnGroups.length; groupIndex++) {
			int[] group = columnGroups[groupIndex];
			for (int columnInGroupIndex = 0; columnInGroupIndex < group.length; columnInGroupIndex++) {
				int groupedColumn = group[columnInGroupIndex];
				if (groupedColumn == col)
					return true;
			}
		}

		return false;
	}

	private int[] computeColumnSizes(PrintSizeStrategy strategy) {
		final int[] result = new int[columns.length];
		final GridCellIterator[][] rows = aggregateHeaderBodyAndFooterCells();

		calculateExplicitlySizedColumnWidths(result);

		calculateColumnWidthsForCellsSpanningOneColumn(result, rows, strategy);

		applyColumnGrouping(result);

		calculateColumnWidthsForCellsSpanningMultipleColumns(result, rows,
				strategy);

		applyColumnGrouping(result);

		return result;
	}

	private GridCellIterator[][] aggregateHeaderBodyAndFooterCells() {
		GridCellIterator[][] rows = new GridCellIterator[body.length
				+ header.length + footer.length][];

		int offset = 0;

		System.arraycopy(body, 0, rows, offset, body.length);
		offset += body.length;

		System.arraycopy(header, 0, rows, offset, header.length);
		offset += header.length;

		System.arraycopy(footer, 0, rows, offset, footer.length);

		return rows;
	}

	private void calculateColumnWidthsForCellsSpanningMultipleColumns(
			final int[] colSizes, final GridCellIterator[][] rows,
			final PrintSizeStrategy strategy) {
		int horizontalSpacing = look.getMargins().getHorizontalSpacing();

		for (int rowIndex = 0; rowIndex < rows.length; rowIndex++) {
			GridCellIterator[] row = rows[rowIndex];
			int columnIndex = 0;
			for (int cellIndex = 0; cellIndex < row.length; cellIndex++) {
				GridCellIterator entry = row[cellIndex];
				int colspan = entry.getColspan();
				if (colspan > 1) {
					int currentWidth = PaperClipsUtil.sum(colSizes,
							columnIndex, colspan);

					// Subtract column spacing so the weighted distribution of
					// extra width stays proportional.
					int minimumWidth = strategy.computeSize(entry.getTarget()).x
							- horizontalSpacing * (colspan - 1);

					if (currentWidth < minimumWidth) {
						int extraWidth = minimumWidth - currentWidth;

						int[] indices = getExpandableColumnIndices(columnIndex,
								colspan);
						int totalWidth = PaperClipsUtil.sumByIndex(colSizes,
								indices);

						if (totalWidth == 0)
							resizeColumnsEqually(colSizes, extraWidth, indices);
						else
							resizeColumnsProportionateToCurrentSizes(colSizes,
									indices, extraWidth, totalWidth);
					}
				}
				columnIndex += colspan;
			}
		}
	}

	private void resizeColumnsProportionateToCurrentSizes(final int[] colSizes,
			final int[] columnIndices, int adjustment, int totalWidth) {
		for (int i = 0; i < columnIndices.length && totalWidth != 0
				&& adjustment != 0; i++) {
			int columnIndex = columnIndices[i];

			int addedWidth = (int) ((long) adjustment * colSizes[columnIndex] / totalWidth);

			// Adjust extraWidth and totalCurrentWidth for future iterations.
			totalWidth -= colSizes[columnIndex];
			adjustment -= addedWidth;

			// NOW we can add the added width.
			colSizes[columnIndex] += addedWidth;
		}
	}

	private void resizeColumnsEqually(final int[] colSizes, int adjustment,
			int[] expandableColumns) {
		int expandableCols = expandableColumns.length;
		for (int expandableColIndex = 0; expandableColIndex < expandableCols; expandableColIndex++) {
			int expandableColumn = expandableColumns[expandableColIndex];

			int addedWidth = adjustment / expandableCols;

			colSizes[expandableColumn] = addedWidth;
			adjustment -= addedWidth;
			expandableCols--;
		}
	}

	private void calculateColumnWidthsForCellsSpanningOneColumn(int[] colSizes,
			GridCellIterator[][] rows, PrintSizeStrategy strategy) {
		for (int rowIndex = 0; rowIndex < rows.length; rowIndex++) {
			GridCellIterator[] row = rows[rowIndex];
			int col = 0;
			for (int cellIndex = 0; cellIndex < row.length; cellIndex++) {
				GridCellIterator entry = row[cellIndex];

				// ignore explicitly sized cols
				if (entry.getColspan() == 1 && !isExplicitSize(columns[col])) {
					colSizes[col] = Math.max(colSizes[col],
							computeCellWidth(entry, columns[col], strategy));
				}
				col += entry.getColspan();
			}
		}
	}

	private void calculateExplicitlySizedColumnWidths(int[] colSizes) {
		for (int col = 0; col < columns.length; col++)
			if (isExplicitSize(columns[col]))
				colSizes[col] = Math.round(columns[col].size * dpi.x / 72f);
	}

	private int[] getExpandableColumnIndices(int firstColumn, int colspan) {
		Condition[] conditions = getExpandableColumnConditions();
		for (int i = 0; i < conditions.length; i++) {
			int[] columns = findColumns(firstColumn, colspan, conditions[i]);
			if (columns != null && columns.length > 0)
				return columns;
		}

		return new int[0];
	}

	private interface Condition {
		/**
		 * Returns whether the column at the specified index satisfies the
		 * condition.
		 * 
		 * @param col
		 *            the index of the column to test.
		 * @return whether the column at the specified index satisfies the
		 *         condition.
		 */
		boolean satisfiedBy(int col);
	}

	private Condition[] getExpandableColumnConditions() {
		return new Condition[] { new Condition() {
			public boolean satisfiedBy(int col) {
				// Ungrouped columns with nonzero weight are first choice for
				// expansion.
				return !isColumnGrouped(col) && columns[col].weight > 0;
			}
		},

		new Condition() {
			public boolean satisfiedBy(int col) {
				// Grouped columns with nonzero weight are next choice
				return isColumnGrouped(col) && columns[col].weight > 0;
			}
		},

		new Condition() {
			public boolean satisfiedBy(int col) {
				// Ungrouped columns with GridPrint.PREFERRED size are next
				// choice.
				return !isColumnGrouped(col)
						&& columns[col].size == GridPrint.PREFERRED;
			}
		},

		new Condition() {
			public boolean satisfiedBy(int col) {
				// Grouped columns with GridPrint.PREFERRED size are next
				// choice.
				return isColumnGrouped(col)
						&& columns[col].size == GridPrint.PREFERRED;
			}
		},

		new Condition() {
			public boolean satisfiedBy(int col) {
				// Ungrouped columns with SWT.DEFAULT size are next choice.
				return !isColumnGrouped(col)
						&& columns[col].size == SWT.DEFAULT;
			}
		},

		new Condition() {
			public boolean satisfiedBy(int col) {
				// Grouped columns with SWT.DEFAULT size are last choice.
				return isColumnGrouped(col) && columns[col].size == SWT.DEFAULT;
			}
		} };
	}

	private int[] findColumns(Condition condition) {
		return findColumns(0, columns.length, condition);
	}

	private int[] findColumns(int start, int count, Condition condition) {
		int[] resultTemp = null;
		int matches = 0;

		final int end = start + count;
		for (int index = start; index < end; index++)
			if (condition.satisfiedBy(index)) {
				if (resultTemp == null)
					resultTemp = new int[count];
				resultTemp[matches++] = index;
			}

		if (matches == 0)
			return new int[0];

		int[] result = new int[matches];
		System.arraycopy(resultTemp, 0, result, 0, matches);
		return result;
	}

	private Point computeSize(PrintSizeStrategy strategy, int[] colSizes) {
		final GridMargins margins = look.getMargins();

		int width = computeMarginWidth() + PaperClipsUtil.sum(colSizes);
		int height = 0;

		// This algorithm is not strictly accurate but probably good enough. The
		// header and footer row heights
		// are being calculated using getMinimumSize() and getPreferredSize(),
		// which do not necessarily return
		// the total content height.

		if (header.length > 0)
			height += computeHeaderHeight(margins, strategy);
		else
			height += Math.max(margins.getBodyTop(false, true),
					margins.getBodyTop(false, false));

		height += computeMaxBodyRowHeight(strategy);

		if (footer.length > 0)
			height += computeFooterHeight(strategy, margins);
		else
			height += Math.max(margins.getBodyBottom(false, false),
					margins.getBodyBottom(false, true));

		return new Point(width, height);
	}

	private int computeHeaderHeight(final GridMargins margins,
			PrintSizeStrategy strategy) {
		int headerHeight = margins.getHeaderTop()
				+ margins.getHeaderVerticalSpacing()
				* (header.length - 1)
				+ Math.max(margins.getBodyTop(true, true),
						margins.getBodyTop(true, false));
		for (int rowIndex = 0; rowIndex < header.length; rowIndex++) {
			GridCellIterator[] row = header[rowIndex];
			int col = 0;
			int rowHeight = 0;
			for (int cellIndex = 0; cellIndex < row.length; cellIndex++) {
				GridCellIterator entry = row[cellIndex];
				// Find tallest cell in row.
				rowHeight = Math.max(rowHeight,
						strategy.computeSize(entry.getTarget()).y);
				col += entry.getColspan();
			}
			headerHeight += rowHeight;
		}
		return headerHeight;
	}

	private int computeMaxBodyRowHeight(PrintSizeStrategy strategy) {
		int maxBodyRowHeight = 0;
		for (int rowIndex = 0; rowIndex < body.length; rowIndex++) {
			GridCellIterator[] row = body[rowIndex];
			int col = 0;
			for (int cellIndex = 0; cellIndex < row.length; cellIndex++) {
				GridCellIterator entry = row[cellIndex];
				// Find the greatest height of all cells' calculated sizes.
				maxBodyRowHeight = Math.max(maxBodyRowHeight,
						strategy.computeSize(entry.getTarget()).y);
				col += entry.getColspan();
			}
		}
		return maxBodyRowHeight;
	}

	private int computeFooterHeight(PrintSizeStrategy strategy,
			final GridMargins margins) {
		int footerHeight = Math.max(margins.getBodyBottom(true, false),
				margins.getBodyBottom(true, true))
				+ margins.getFooterVerticalSpacing()
				* (footer.length - 1)
				+ margins.getFooterBottom();
		for (int rowIndex = 0; rowIndex < footer.length; rowIndex++) {
			GridCellIterator[] row = footer[rowIndex];
			int col = 0;
			int rowHeight = 0;
			for (int cellIndex = 0; cellIndex < row.length; cellIndex++) {
				GridCellIterator entry = row[cellIndex];
				// Find tallest cell in row.
				rowHeight = Math.max(rowHeight,
						strategy.computeSize(entry.getTarget()).y);
				col += entry.getColspan();
			}
			footerHeight += rowHeight;
		}
		return footerHeight;
	}

	public Point minimumSize() {
		return new Point(minimumSize.x, minimumSize.y);
	}

	public Point preferredSize() {
		return new Point(preferredSize.x, preferredSize.y);
	}

	private Condition[] getShrinkableColumnConditions() {
		/*
		 * Disabled: new Condition() { public boolean satisfiedBy( int col ) {
		 * // Search first for columns with DEFAULT size. return
		 * columns[col].size == SWT.DEFAULT; } },
		 */
		return new Condition[] { new Condition() {
			public boolean satisfiedBy(int col) {
				// Search next for columns with DEFAULT or PREFERRED size.
				int size = columns[col].size;
				return size == SWT.DEFAULT || size == GridPrint.PREFERRED;
			}
		} };
	}

	private int[] findShrinkableColumns(int extraWidth) {
		Condition[] conditions = getShrinkableColumnConditions();
		for (int i = 0; i < conditions.length; i++) {
			int[] indices = findColumns(conditions[i]);
			if (PaperClipsUtil.sumByIndex(minimumColSizes, indices) >= extraWidth)
				return indices;
		}

		return findAllColumns();
	}

	private int[] findAllColumns() {
		int[] result = new int[columns.length];
		for (int i = 0; i < result.length; i++)
			result[i] = i;
		return result;
	}

	private int[] computeColumnWidths(int width) {
		int minimumWidth = PaperClipsUtil.sum(minimumColSizes);
		int preferredWidth = PaperClipsUtil.sum(preferredColSizes);

		if (width < minimumWidth)
			return reduceMinimumColumnWidths(minimumWidth - width);
		else if (width == minimumWidth)
			return minimumColSizes;
		else if (width < preferredWidth)
			return expandMinimumColumnWidths(width - minimumWidth);
		else if (preferredWidth == width)
			return preferredColSizes;
		else
			// ( preferredWidth < width )
			return expandPreferredColumnWidthsByWeight(width - preferredWidth);
	}

	private int[] expandPreferredColumnWidthsByWeight(int extraWidth) {
		int[] weightedCols = findColumns(new Condition() {
			public boolean satisfiedBy(int col) {
				return columns[col].weight > 0;
			}
		});
		int totalWeight = 0;
		for (int i = 0; i < weightedCols.length; i++)
			totalWeight += columns[weightedCols[i]].weight;

		int[] colSizes = PaperClipsUtil.copy(preferredColSizes);
		for (int weightedColIndex = 0; weightedColIndex < weightedCols.length; weightedColIndex++) {
			int columnIndex = weightedCols[weightedColIndex];

			int columnWeight = columns[columnIndex].weight;
			int addWidth = (int) ((long) extraWidth * columnWeight / totalWeight);

			colSizes[columnIndex] += addWidth;

			// adjust extraWidth and totalWeight - eliminates round-off error
			extraWidth -= addWidth;
			totalWeight -= columnWeight;
		}

		return colSizes;
	}

	private int[] expandMinimumColumnWidths(int expansion) {
		int difference = PaperClipsUtil.sum(preferredColSizes)
				- PaperClipsUtil.sum(minimumColSizes);
		int[] colSizes = PaperClipsUtil.copy(minimumColSizes);
		for (int i = 0; i < columns.length && difference != 0 && expansion != 0; i++) {
			int columnDifference = preferredColSizes[i] - minimumColSizes[i];

			int change = (int) ((long) expansion * columnDifference / difference);

			colSizes[i] += change;

			// adjust extraWidth and difference - eliminates round-off error
			expansion -= change;
			difference -= columnDifference;
		}

		return colSizes;
	}

	private int computeMarginWidth() {
		GridMargins margins = look.getMargins();
		return margins.getLeft() + margins.getRight()
				+ margins.getHorizontalSpacing() * (columns.length - 1);
	}

	private int[] reduceMinimumColumnWidths(int reduction) {
		int[] colSizes = PaperClipsUtil.copy(minimumColSizes);

		int[] shrinkableCols = findShrinkableColumns(reduction);
		int shrinkableWidth = PaperClipsUtil.sumByIndex(colSizes,
				shrinkableCols);

		for (int i = 0; i < shrinkableCols.length && shrinkableWidth != 0
				&& reduction != 0; i++) {
			int col = shrinkableCols[i];

			int columnReduction = (int) ((long) colSizes[col] * reduction / shrinkableWidth);

			shrinkableWidth -= colSizes[col];
			colSizes[col] -= columnReduction;
			reduction -= columnReduction;
		}

		return colSizes;
	}

	public boolean hasNext() {
		return row < body.length;
	}

	private PrintPiece nextRow(final GridCellIterator[] cells,
			final int[] columnWidths, final int height, final boolean bottomOpen) {
		if (bottomOpen && rowContainsNonDefaultVertAlignment(cells))
			return null;
		if (height < 0)
			return null;

		final int[] cellWidths = calculateCellWidths(cells, columnWidths);

		PrintPiece[] pieces = layoutCellsWithNonFillVertAlignment(cells,
				height, bottomOpen, cellWidths);
		if (pieces == null)
			return null;

		final int rowHeight = calculateRowHeight(pieces, cells);

		pieces = layoutCellsWithFillVertAlignment(cells, rowHeight, cellWidths,
				pieces);
		if (pieces == null)
			return null;

		final int[] xOffsets = new int[cells.length];
		final int[] yOffsets = new int[cells.length];
		applyCellAlignment(cells, cellWidths, pieces, rowHeight, xOffsets,
				yOffsets);

		return createRowResult(pieces, xOffsets, yOffsets);
	}

	private static boolean rowContainsNonDefaultVertAlignment(
			final GridCellIterator[] cells) {
		for (int i = 0; i < cells.length; i++)
			if (!isDefaultVerticalAlignment(cells[i].getVerticalAlignment()))
				return true;
		return false;
	}

	private static boolean isDefaultVerticalAlignment(int vAlignment) {
		return vAlignment == SWT.DEFAULT || vAlignment == SWT.TOP;
	}

	private int[] calculateCellWidths(final GridCellIterator[] cells,
			final int[] columnWidths) {
		final int[] result = new int[cells.length];
		final int horzSpacing = look.getMargins().getHorizontalSpacing();
		int col = 0;
		for (int cellIndex = 0; cellIndex < cells.length; cellIndex++) {
			int colspan = cells[cellIndex].getColspan();
			result[cellIndex] = (colspan - 1) * horzSpacing
					+ PaperClipsUtil.sum(columnWidths, col, colspan);
			col += colspan;
		}
		return result;
	}

	private static PrintPiece[] layoutCellsWithNonFillVertAlignment(
			final GridCellIterator[] cells, final int height,
			final boolean bottomOpen, final int[] cellWidths) {
		final PrintPiece[] pieces = new PrintPiece[cells.length];
		for (int cellIndex = 0; cellIndex < cells.length; cellIndex++) {
			final GridCellIterator cell = cells[cellIndex];
			final PrintIterator iter = cell.getTarget();

			final int cellWidth = cellWidths[cellIndex];

			if (iter.hasNext() && cell.getVerticalAlignment() != SWT.FILL) {
				PrintPiece piece = pieces[cellIndex] = PaperClips.next(iter,
						cellWidth, height);
				if ((piece == null) || (iter.hasNext() && !bottomOpen)) {
					PaperClipsUtil.dispose(piece, pieces);
					return null;
				}
			}
		}
		return pieces;
	}

	private static int calculateRowHeight(final PrintPiece[] cellPieces,
			final GridCellIterator[] cells) {
		int maxHeight = 0;
		for (int cellIndex = 0; cellIndex < cells.length; cellIndex++) {
			GridCellIterator cell = cells[cellIndex];
			if (cell.getVerticalAlignment() == SWT.FILL)
				maxHeight = Math.max(maxHeight,
						cell.getTarget().minimumSize().y);
			else if (cellPieces[cellIndex] != null)
				maxHeight = Math.max(maxHeight,
						cellPieces[cellIndex].getSize().y);
		}
		return maxHeight;
	}

	private static PrintPiece[] layoutCellsWithFillVertAlignment(
			final GridCellIterator[] cells, final int height,
			final int[] cellWidths, final PrintPiece[] cellPieces) {
		for (int cellIndex = 0; cellIndex < cells.length; cellIndex++) {
			GridCellIterator cell = cells[cellIndex];
			PrintIterator iter = cell.getTarget();

			if (cell.getVerticalAlignment() == SWT.FILL) {
				PrintPiece piece = cellPieces[cellIndex] = PaperClips.next(
						iter, cellWidths[cellIndex], height);
				if (piece == null || iter.hasNext()) {
					PaperClipsUtil.dispose(piece, cellPieces);
					return null;
				}
			}
		}
		return cellPieces;
	}

	private void applyCellAlignment(final GridCellIterator[] cells,
			final int[] cellWidths, final PrintPiece[] pieces,
			final int rowHeight, final int[] xOffsets, final int[] yOffsets) {
		final int horzSpacing = look.getMargins().getHorizontalSpacing();
		int x = 0;
		int col = 0;

		for (int cellIndex = 0; cellIndex < cells.length; cellIndex++) {
			xOffsets[cellIndex] = x;
			yOffsets[cellIndex] = 0;

			GridCellIterator cell = cells[cellIndex];
			PrintPiece piece = pieces[cellIndex];
			if (piece != null) {
				Point size = piece.getSize();
				int hAlignment = resolveHorzAlignment(
						cell.getHorizontalAlignment(), columns[col].align);
				xOffsets[cellIndex] += getHorzAlignmentOffset(hAlignment,
						size.x, cellWidths[cellIndex]);
				yOffsets[cellIndex] += getVertAlignmentOffset(
						cell.getVerticalAlignment(), size.y, rowHeight);
			}

			x += cellWidths[cellIndex] + horzSpacing;
			col += cell.getColspan();
		}
	}

	private static int resolveHorzAlignment(int cellAlignment,
			int columnAlignment) {
		return cellAlignment == SWT.DEFAULT ? columnAlignment : cellAlignment;
	}

	private static int getHorzAlignmentOffset(int alignment, int pieceWidth,
			int totalWidth) {
		if (alignment == SWT.CENTER)
			return (totalWidth - pieceWidth) / 2;
		else if (alignment == SWT.RIGHT)
			return totalWidth - pieceWidth;
		return 0;
	}

	private static int getVertAlignmentOffset(final int alignment,
			final int pieceHeight, final int cellHeight) {
		int offset = 0;
		if (alignment == SWT.CENTER) {
			offset = (cellHeight - pieceHeight) / 2;
		} else if (alignment == SWT.BOTTOM) {
			offset = cellHeight - pieceHeight;
		}
		return offset;
	}

	private static PrintPiece createRowResult(final PrintPiece[] pieces,
			final int[] xOffsets, final int[] yOffsets) {
		List result = new ArrayList();
		for (int cellIndex = 0; cellIndex < pieces.length; cellIndex++)
			if (pieces[cellIndex] != null)
				result.add(new CompositeEntry(pieces[cellIndex], new Point(
						xOffsets[cellIndex], yOffsets[cellIndex])));
		return new CompositePiece(result);
	}

	private static boolean hasNext(GridCellIterator[] cells) {
		for (int i = 0; i < cells.length; i++)
			if (cells[i].getTarget().hasNext())
				return true;
		return false;
	}

	public PrintPiece next(final int width, int height) {
		if (!hasNext())
			PaperClips.error(SWT.ERROR_UNSPECIFIED, "No more content"); //$NON-NLS-1$

		GridMargins margins = look.getMargins();
		int[] colSizes = computeColumnWidths(width - computeMarginWidth());

		final boolean headerPresent = header.length > 0;
		final int[] headerHeights = new int[header.length];
		final int[][] headerColSpans = new int[header.length][];
		PrintPiece headerPiece = null;
		if (headerPresent) {
			height -= margins.getHeaderTop();
			headerPiece = nextHeaderPiece(colSizes, height, headerHeights,
					headerColSpans);
			if (headerPiece == null)
				return null;
			height -= headerPiece.getSize().y;
		}

		final boolean footerPresent = footer.length > 0;
		final int[] footerHeights = new int[footer.length];
		final int[][] footerColSpans = new int[footer.length][];
		PrintPiece footerPiece = null;

		if (footerPresent) {
			height -= margins.getFooterBottom();
			footerPiece = nextFooterPiece(colSizes, height, footerHeights,
					footerColSpans);
			if (footerPiece == null) {
				PaperClipsUtil.dispose(headerPiece);
				return null;
			}
			height -= footerPiece.getSize().y;
		}

		final int firstRow = row;
		final boolean topOpen = rowStarted;
		final List bodyRows = new ArrayList();
		final List bodyColSpans = new ArrayList();

		height -= margins.getBodyTop(headerPresent, topOpen);
		final PrintPiece bodyPiece = nextBodyPiece(colSizes, height, bodyRows,
				bodyColSpans, footerPresent);
		if (bodyPiece == null)
			return null;
		final boolean bottomOpen = rowStarted;

		return createResult(colSizes, headerPiece, headerHeights,
				headerColSpans, firstRow, topOpen, bodyPiece,
				PaperClipsUtil.toIntArray(bodyRows),
				PaperClipsUtil.toIntIntArray(bodyColSpans), bottomOpen,
				footerPiece, footerHeights, footerColSpans);
	}

	private PrintPiece nextHeaderPiece(final int[] colSizes, final int height,
			final int[] rowHeights, final int[][] colSpans) {
		return nextHeaderOrFooterPiece(colSizes, height, rowHeights, colSpans,
				look.getMargins().getHeaderVerticalSpacing(), header);
	}

	private PrintPiece nextFooterPiece(final int[] colSizes, final int height,
			final int[] rowHeights, final int[][] colSpans) {
		return nextHeaderOrFooterPiece(colSizes, height, rowHeights, colSpans,
				look.getMargins().getFooterVerticalSpacing(), footer);
	}

	private PrintPiece nextHeaderOrFooterPiece(final int[] colSizes,
			final int height, final int[] rowHeights, final int[][] colSpans,
			final int rowSpacing, GridCellIterator[][] headerOrFooter) {
		int y = 0;
		List entries = new ArrayList();
		for (int rowIndex = 0; rowIndex < headerOrFooter.length; rowIndex++) {
			GridCellIterator[] row = cloneRow(headerOrFooter[rowIndex]);

			colSpans[rowIndex] = new int[row.length];
			for (int cellIndex = 0; cellIndex < row.length; cellIndex++)
				colSpans[rowIndex][cellIndex] = row[cellIndex].getColspan();

			PrintPiece rowPiece = nextRow(row, colSizes, height - y, false);
			boolean hasNext = hasNext(row);

			if (rowPiece == null || hasNext) {
				PaperClipsUtil.dispose(rowPiece);
				for (Iterator iter = entries.iterator(); iter.hasNext();) {
					CompositeEntry entry = (CompositeEntry) iter.next();
					entry.dispose();
				}
				return null;
			}

			int rowHeight = rowHeights[rowIndex] = rowPiece.getSize().y;
			entries.add(new CompositeEntry(rowPiece, new Point(0, y)));

			y += rowHeight + rowSpacing;
		}

		return new CompositePiece(entries);
	}

	private PrintPiece nextBodyPiece(int[] colSizes, final int height,
			final List rowHeights, final List colSpans,
			final boolean footerPresent) {
		final GridMargins margins = look.getMargins();
		final int rowSpacing = margins.getBodyVerticalSpacing();
		final int bodyBottomSpacingOpen = margins.getBodyBottom(footerPresent,
				true);
		final int bodyBottomSpacingClosed = margins.getBodyBottom(
				footerPresent, false);

		int y = 0;
		List entries = new ArrayList();
		while (hasNext()) {
			GridCellIterator[] thisRow = cloneRow(body[row]);
			PrintPiece rowPiece = nextRow(thisRow, colSizes, height - y
					- bodyBottomSpacingClosed, rowStarted);
			boolean hasNext = hasNext(thisRow);

			if ((cellClippingEnabled || entries.isEmpty())
					&& (rowPiece == null || hasNext)) {
				thisRow = cloneRow(body[row]);
				rowPiece = nextRow(thisRow, colSizes, height - y
						- bodyBottomSpacingOpen, true);
				hasNext = true;
			}

			if (rowPiece == null)
				break;

			entries.add(new CompositeEntry(rowPiece, new Point(0, y)));
			body[row] = thisRow;

			final int[] rowColSpans = new int[thisRow.length];
			for (int cellIndex = 0; cellIndex < rowColSpans.length; cellIndex++)
				rowColSpans[cellIndex] = thisRow[cellIndex].getColspan();
			colSpans.add(rowColSpans);

			final int rowHeight = rowPiece.getSize().y;
			rowHeights.add(new Integer(rowHeight));

			rowStarted = hasNext;
			if (hasNext)
				break;

			y += rowHeight + rowSpacing;
			row++;
		}

		if (entries.isEmpty())
			return null;

		return new CompositePiece(entries);
	}

	private static GridCellIterator[] cloneRow(GridCellIterator[] row) {
		GridCellIterator[] result = (GridCellIterator[]) row.clone();
		for (int i = 0; i < result.length; i++)
			result[i] = result[i].copy();
		return result;
	}

	private PrintPiece createResult(final int[] colSizes,
			final PrintPiece headerPiece, final int[] headerRows,
			final int[][] headerColSpans, final int firstRow,
			final boolean topOpen, final PrintPiece bodyPiece,
			final int[] bodyRows, final int[][] bodyColSpans,
			final boolean bottomOpen, final PrintPiece footerPiece,
			final int[] footerRows, final int[][] footerColSpans) {
		if (bodyPiece == null) {
			if (headerPiece != null)
				headerPiece.dispose();
			if (footerPiece != null)
				footerPiece.dispose();
			return null;
		}

		List sections = new ArrayList();

		PrintPiece lookPiece = new GridLookPainterPiece(look, colSizes,
				headerRows, headerColSpans, firstRow, topOpen, bodyRows,
				bodyColSpans, bottomOpen, footerRows, footerColSpans);
		sections.add(new CompositeEntry(lookPiece, new Point(0, 0)));

		GridMargins margins = look.getMargins();
		final int x = margins.getLeft();

		int y = 0;
		if (headerPiece != null) {
			y = margins.getHeaderTop();
			sections.add(new CompositeEntry(headerPiece, new Point(x, y)));
			y += headerPiece.getSize().y;
		}

		y += margins.getBodyTop(headerPiece != null, topOpen);
		sections.add(new CompositeEntry(bodyPiece, new Point(x, y)));
		y += bodyPiece.getSize().y
				+ margins.getBodyBottom(footerPiece != null, bottomOpen);

		if (footerPiece != null)
			sections.add(new CompositeEntry(footerPiece, new Point(x, y)));

		return new CompositePiece(sections);
	}

	public PrintIterator copy() {
		return new GridIterator(this);
	}
}