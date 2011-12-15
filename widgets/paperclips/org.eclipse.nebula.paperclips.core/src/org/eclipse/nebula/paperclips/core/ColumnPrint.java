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
package org.eclipse.nebula.paperclips.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * A wrapper Print which splits its child print into multiple columns.
 * <p>
 * This class is horizontally greedy. Greedy prints take up all the available
 * space on the page.
 * <p>
 * ColumnPrint attempts to use the minimum possible vertical space on the page
 * if isCompressed() returns true (the default). This behavior can be disabled
 * by calling setCompressed(false).
 * 
 * @author Matthew Hall
 */
public class ColumnPrint implements Print {
	final Print target;
	final int columns;
	final int spacing;

	boolean compressed;

	/**
	 * Constructs a ColumnPrint with the given target, number of columns, and
	 * column spacing (expressed in points). 72 points = 1".
	 * 
	 * @param target
	 *            the print which will be split into columns.
	 * @param columns
	 *            the number of columns to display
	 * @param spacing
	 *            the spacing between each column.
	 */
	public ColumnPrint(Print target, int columns, int spacing) {
		this(target, columns, spacing, true);
	}

	/**
	 * Constructs a ColumnPrint with the given target, column count, column
	 * spacing, and compression.
	 * 
	 * @param target
	 *            the print to display in columns.
	 * @param columns
	 *            the number of columns to display.
	 * @param spacing
	 *            the spacing between each column, expressed in points. 72
	 *            points = 1".
	 * @param compressed
	 *            whether the columns on the final page are to be
	 */
	public ColumnPrint(Print target, int columns, int spacing,
			boolean compressed) {
		Util.notNull(target);
		if (spacing < 0)
			PaperClips
					.error(SWT.ERROR_INVALID_ARGUMENT, "spacing must be >= 0"); //$NON-NLS-1$
		if (columns < 2)
			PaperClips
					.error(SWT.ERROR_INVALID_ARGUMENT, "columns must be >= 2"); //$NON-NLS-1$

		this.target = target;
		this.spacing = spacing;
		this.columns = columns;
		this.compressed = compressed;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + columns;
		result = prime * result + (compressed ? 1231 : 1237);
		result = prime * result + spacing;
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ColumnPrint other = (ColumnPrint) obj;
		if (columns != other.columns)
			return false;
		if (compressed != other.compressed)
			return false;
		if (spacing != other.spacing)
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}

	/**
	 * Returns the target print being split into columns.
	 * 
	 * @return the target print being split into columns.
	 */
	public Print getTarget() {
		return target;
	}

	/**
	 * Returns the number of columns per page.
	 * 
	 * @return the number of columns per page.
	 */
	public int getColumnCount() {
		return columns;
	}

	/**
	 * Returns the spacing between columns, in points. 72 points = 1".
	 * 
	 * @return the spacing between columns, in points.
	 */
	public int getColumnSpacing() {
		return spacing;
	}

	/**
	 * Returns whether the columns are compressed to the smallest possible
	 * height on the last page.
	 * 
	 * @return whether the columns are compressed to the smallest possible
	 *         height on the last page.
	 */
	public boolean isCompressed() {
		return compressed;
	}

	/**
	 * Sets whether the columns are compressed to the smallest possible height
	 * on the last page.
	 * 
	 * @param compressed
	 *            whether to compress the columns.
	 */
	public void setCompressed(boolean compressed) {
		this.compressed = compressed;
	}

	public PrintIterator iterator(Device device, GC gc) {
		return new ColumnIterator(this, device, gc);
	}
}

class ColumnIterator implements PrintIterator {
	private PrintIterator target;
	private final int columns;
	private final int spacing;
	private final boolean compressed;

	ColumnIterator(ColumnPrint print, Device device, GC gc) {
		this.target = print.target.iterator(device, gc);
		this.columns = print.columns;
		this.spacing = Math.round(print.spacing * device.getDPI().x / 72f);
		this.compressed = print.compressed;
	}

	ColumnIterator(ColumnIterator that) {
		this.target = that.target.copy();
		this.columns = that.columns;
		this.spacing = that.spacing;
		this.compressed = that.compressed;
	}

	public Point minimumSize() {
		return computeSize(target.minimumSize());
	}

	public Point preferredSize() {
		return computeSize(target.preferredSize());
	}

	private Point computeSize(Point targetSize) {
		return new Point(targetSize.x * columns + spacing * (columns - 1),
				targetSize.y);
	}

	public boolean hasNext() {
		return target.hasNext();
	}

	int[] computeColSizes(int width) {
		int[] colSizes = new int[columns];
		int availableWidth = width - spacing * (columns - 1);
		for (int i = 0; i < colSizes.length; i++) {
			colSizes[i] = availableWidth / (columns - i);
			availableWidth -= colSizes[i];
		}
		return colSizes;
	}

	Point[] computeColOffsets(int[] colSizes) {
		Point[] colOffsets = new Point[columns];
		int xOffset = 0;
		for (int i = 0; i < columns; i++) {
			colOffsets[i] = new Point(xOffset, 0);
			xOffset += colSizes[i] + spacing;
		}
		return colOffsets;
	}

	/**
	 * Iterates across the given column sizes and returns an array of
	 * PrintPieces to fill those columns, or null if there was insufficient room
	 * to continue iterating. A backup of the given iterator should be taken
	 * before invoking this method! If null is returned, the given iterator is
	 * corrupt and should no longer be used!
	 * 
	 * @param colSizes
	 *            an array of column sizes
	 * @param height
	 *            the height
	 * @return an array of PrintPieces for the given column sizes, or null
	 */
	PrintPiece[] nextColumns(PrintIterator iterator, int[] colSizes, int height) {
		List pieces = new ArrayList();
		for (int i = 0; i < columns && iterator.hasNext(); i++) {
			PrintPiece piece = PaperClips.next(iterator, colSizes[i], height);

			if (piece == null)
				return disposePieces(pieces);

			pieces.add(piece);
		}

		return (PrintPiece[]) pieces.toArray(new PrintPiece[pieces.size()]);
	}

	private PrintPiece[] disposePieces(List pieces) {
		for (Iterator iter = pieces.iterator(); iter.hasNext();) {
			PrintPiece piece = (PrintPiece) iter.next();
			piece.dispose();
		}
		return null;
	}

	PrintPiece createResult(PrintPiece[] pieces, int[] colSizes) {
		CompositeEntry[] entries = new CompositeEntry[pieces.length];

		Point[] offsets = computeColOffsets(colSizes);
		for (int i = 0; i < pieces.length; i++)
			entries[i] = new CompositeEntry(pieces[i], offsets[i]);

		return new CompositePiece(entries);
	}

	public PrintPiece next(int width, int height) {
		int[] colSizes = computeColSizes(width);

		// Iterate on a copy in case any column fails to layout.
		PrintIterator iter = target.copy();
		PrintPiece[] columns = nextColumns(iter, colSizes, height);
		if (columns == null)
			return null;

		// The target was completely consumed. If compressed property is true,
		// close the gap until we find the
		// smallest height that completely consumes the target's contents.
		if (!iter.hasNext() && compressed)
			return nextCompressed(colSizes, iter, columns);

		this.target = iter;
		return createResult(columns, colSizes);
	}

	private PrintPiece nextCompressed(int[] colSizes, PrintIterator iter,
			PrintPiece[] columns) {
		int highestInvalidHeight = 0;
		int lowestValidHeight = getMaxHeight(columns);

		// Remember the best results
		PrintIterator bestIteration = iter;
		PrintPiece[] bestColumns = columns;

		while (lowestValidHeight > highestInvalidHeight + 1) {
			int testHeight = (lowestValidHeight + highestInvalidHeight + 1) / 2;

			iter = target.copy();
			columns = nextColumns(iter, colSizes, testHeight);

			if (columns == null) {
				highestInvalidHeight = testHeight;
			} else if (iter.hasNext()) {
				highestInvalidHeight = testHeight;
				disposePieces(columns);
			} else {
				disposePieces(bestColumns);

				bestIteration = iter;
				bestColumns = columns;
				lowestValidHeight = getMaxHeight(bestColumns);
			}
		}

		// Now that we've narrowed down the target's best iteration, we can
		// update the state of this iterator and
		// return the result.
		this.target = bestIteration;
		return createResult(bestColumns, colSizes);
	}

	private int getMaxHeight(PrintPiece[] pieces) {
		int result = 0;
		for (int i = 0; i < pieces.length; i++)
			result = Math.max(result, pieces[i].getSize().y);
		return result;
	}

	private void disposePieces(PrintPiece[] pieces) {
		for (int i = 0; i < pieces.length; i++)
			pieces[i].dispose();
	}

	public PrintIterator copy() {
		return new ColumnIterator(this);
	}
}