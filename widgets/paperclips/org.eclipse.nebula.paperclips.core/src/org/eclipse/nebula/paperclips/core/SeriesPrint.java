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
import java.util.List;

import org.eclipse.nebula.paperclips.core.internal.util.PrintSizeStrategy;
import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * A Print which displays its child prints in series. Each element in the series
 * is displayed one at a time (no more than one child per page, although one
 * Print may span several pages).
 * <p>
 * Use this class as the top-level Print when several distinct Prints should be
 * batched into one print job, but printed on separate pages.
 * 
 * @author Matthew Hall
 */
public class SeriesPrint implements Print {
	final List items = new ArrayList();

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((items == null) ? 0 : items.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SeriesPrint other = (SeriesPrint) obj;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		return true;
	}

	/**
	 * Adds the given prints to this SeriesPrint.
	 * 
	 * @param items
	 *            the Prints to add
	 */
	public void add(Print[] items) {
		Util.noNulls(items);
		for (int i = 0; i < items.length; i++)
			this.items.add(items[i]);
	}

	/**
	 * Adds the given print to this SeriesPrint.
	 * 
	 * @param item
	 *            the Print to add
	 */
	public void add(Print item) {
		Util.notNull(item);
		items.add(item);
	}

	/**
	 * Returns the number of Prints that have been added to this SeriesPrint.
	 * 
	 * @return the number of Prints that have been added to this SeriesPrint.
	 */
	public int size() {
		return items.size();
	}

	/**
	 * Returns an array of items in the series.
	 * 
	 * @return an array of items in the series.
	 */
	public Print[] getItems() {
		return (Print[]) items.toArray(new Print[items.size()]);
	}

	public PrintIterator iterator(Device device, GC gc) {
		return new SeriesIterator(this, device, gc);
	}
}

class SeriesIterator implements PrintIterator {
	final PrintIterator[] iters;
	int index;

	SeriesIterator(SeriesPrint print, Device device, GC gc) {
		this.iters = new PrintIterator[print.items.size()];
		for (int i = 0; i < iters.length; i++)
			iters[i] = ((Print) print.items.get(i)).iterator(device, gc);

		this.index = 0;
	}

	SeriesIterator(SeriesIterator that) {
		this.iters = (PrintIterator[]) that.iters.clone();
		for (int i = index; i < iters.length; i++)
			this.iters[i] = that.iters[i].copy();

		this.index = that.index;
	}

	public boolean hasNext() {
		return index < iters.length;
	}

	private Point computeSize(PrintSizeStrategy strategy) {
		int width = 0;
		int height = 0;
		for (int i = 0; i < iters.length; i++) {
			PrintIterator iter = iters[i];
			Point printSize = strategy.computeSize(iter);
			width = Math.max(width, printSize.x);
			height = Math.max(height, printSize.y);
		}
		return new Point(width, height);
	}

	public Point minimumSize() {
		return computeSize(PrintSizeStrategy.MINIMUM);
	}

	public Point preferredSize() {
		return computeSize(PrintSizeStrategy.PREFERRED);
	}

	public PrintPiece next(int width, int height) {
		if (!hasNext())
			PaperClips.error("No more content"); //$NON-NLS-1$

		PrintIterator iter = iters[index];
		PrintPiece printPiece = PaperClips.next(iter, width, height);

		if (printPiece != null && !iter.hasNext())
			index++;

		return printPiece;
	}

	public PrintIterator copy() {
		return new SeriesIterator(this);
	}
}