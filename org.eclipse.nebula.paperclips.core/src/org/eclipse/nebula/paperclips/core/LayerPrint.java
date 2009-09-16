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

import org.eclipse.nebula.paperclips.core.internal.util.PrintSizeStrategy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * A Print which displays its child Prints on top each other.
 * 
 * @author Matthew Hall
 */
public class LayerPrint implements Print {
	/**
	 * Constant for the default alignment of child Prints. Value is SWT.LEFT.
	 */
	public static final int DEFAULT_ALIGN = SWT.LEFT;

	// List<LayerEntry>
	final List entries = new ArrayList();

	/**
	 * Constructs a new LayerPrint.
	 */
	public LayerPrint() {
	}

	/**
	 * Adds the given Print to this LayerPrint using the default alignment.
	 * 
	 * @param print
	 *            the Print to add.
	 * @see #DEFAULT_ALIGN
	 */
	public void add(Print print) {
		entries.add(new LayerEntry(print, DEFAULT_ALIGN));
	}

	/**
	 * Adds the given Print to this LayerPrint using the specified alignment.
	 * 
	 * @param print
	 *            the Print to add.
	 * @param align
	 *            the alignment for the Print. May be one of SWT.LEFT,
	 *            SWT.CENTER, or SWT.RIGHT.
	 */
	public void add(Print print, int align) {
		entries.add(new LayerEntry(print, align));
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entries == null) ? 0 : entries.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LayerPrint other = (LayerPrint) obj;
		if (entries == null) {
			if (other.entries != null)
				return false;
		} else if (!entries.equals(other.entries))
			return false;
		return true;
	}

	/**
	 * Returns an array of entries in this LayerPrint.
	 * 
	 * @return an array of entries in this LayerPrint.
	 */
	public LayerEntry[] getEntries() {
		return (LayerEntry[]) entries.toArray(new LayerEntry[entries.size()]);
	}

	public PrintIterator iterator(Device device, GC gc) {
		return new LayerIterator(this, device, gc);
	}
}

class LayerEntryIterator {
	final PrintIterator target;
	final int alignment;

	LayerEntryIterator(LayerEntry entry, Device device, GC gc) {
		this.target = entry.target.iterator(device, gc);
		this.alignment = entry.getHorizontalAlignment();
	}

	LayerEntryIterator(LayerEntryIterator that) {
		this.target = that.target.copy();
		this.alignment = that.alignment;
	}

	LayerEntryIterator copy() {
		return new LayerEntryIterator(this);
	}
}

class LayerIterator implements PrintIterator {
	LayerEntryIterator[] entries;

	LayerIterator(LayerPrint print, Device device, GC gc) {
		entries = new LayerEntryIterator[print.entries.size()];
		LayerEntry[] e = print.getEntries();
		for (int i = 0; i < entries.length; i++) {
			entries[i] = e[i].iterator(device, gc);
		}
	}

	LayerIterator(LayerIterator that) {
		this.entries = (LayerEntryIterator[]) that.entries.clone();
		for (int i = 0; i < entries.length; i++)
			if (entries[i].target.hasNext())
				entries[i] = entries[i].copy();
	}

	public boolean hasNext() {
		for (int i = 0; i < entries.length; i++)
			if (entries[i].target.hasNext())
				return true;
		return false;
	}

	public PrintPiece next(int width, int height) {
		if (!hasNext())
			PaperClips.error("No more content"); //$NON-NLS-1$

		PrintPiece[] pieces = nextPieces(width, height);
		if (pieces == null)
			return null;

		CompositeEntry[] entries = new CompositeEntry[pieces.length];
		for (int i = 0; i < entries.length; i++) {
			PrintPiece piece = pieces[i];
			int offset = getHorzAlignmentOffset(this.entries[i].alignment,
					piece.getSize().x, width);
			entries[i] = new CompositeEntry(piece, new Point(offset, 0));
		}
		return new CompositePiece(entries);
	}

	private PrintPiece[] nextPieces(int width, int height) {
		LayerEntryIterator[] entries = (LayerEntryIterator[]) this.entries
				.clone();

		List pieces = new ArrayList();
		for (int i = 0; i < entries.length; i++) {
			LayerEntryIterator entry = entries[i];
			if (entry.target.hasNext()) {
				PrintPiece piece = PaperClips.next(entry.target, width, height);

				if (piece == null) {
					for (Iterator iter = pieces.iterator(); iter.hasNext();)
						((PrintPiece) iter.next()).dispose();
					return null;
				}
				pieces.add(piece);
			}
		}

		// Replace instance entries with the entries that were just consumed.
		this.entries = entries;

		return (PrintPiece[]) pieces.toArray(new PrintPiece[pieces.size()]);
	}

	private int getHorzAlignmentOffset(int alignment, int pieceWidth,
			int totalWidth) {
		int offset = 0;
		switch (alignment) {
		case SWT.CENTER:
			offset = (totalWidth - pieceWidth) / 2;
			break;
		case SWT.RIGHT:
			offset = totalWidth - pieceWidth;
			break;
		}
		return offset;
	}

	Point computeSize(PrintSizeStrategy strategy) {
		Point size = new Point(0, 0);
		for (int i = 0; i < entries.length; i++) {
			LayerEntryIterator entry = entries[i];
			Point entrySize = strategy.computeSize(entry.target);
			size.x = Math.max(size.x, entrySize.x);
			size.y = Math.max(size.y, entrySize.y);
		}
		return size;
	}

	public Point minimumSize() {
		return computeSize(PrintSizeStrategy.MINIMUM);
	}

	public Point preferredSize() {
		return computeSize(PrintSizeStrategy.PREFERRED);
	}

	public PrintIterator copy() {
		return new LayerIterator(this);
	}
}