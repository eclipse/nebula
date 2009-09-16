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

import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * A composite PrintPiece for displaying child PrintPieces. This class is
 * especially useful for Print implementations that perform layout of multiple
 * child Prints.
 * 
 * @author Matthew Hall
 */
public class CompositePiece implements PrintPiece {
	private final Point size;

	private final CompositeEntry[] entries;

	/**
	 * Constructs a CompositePiece with the given entries.
	 * 
	 * @param entries
	 *            an array of entries that make up this PrintPiece.
	 */
	public CompositePiece(CompositeEntry[] entries) {
		this(createList(entries));
	}

	/**
	 * Constructs a CompositePrintPiece with the given entries and explicit
	 * size. This constructor will increase the explicit size to completely
	 * contain any child entries which extend outside the given size.
	 * 
	 * @param entries
	 *            an array of entries that make up this PrintPiece.
	 * @param size
	 */
	public CompositePiece(CompositeEntry[] entries, Point size) {
		this(createList(entries), size);
	}

	private static List createList(CompositeEntry[] entries) {
		List result = new ArrayList();
		for (int i = 0; i < entries.length; i++)
			result.add(entries[i]);
		return result;
	}

	/**
	 * Constructs a composite PrintPiece with the given entries.
	 * 
	 * @param entries
	 *            an array of entries that make up this PrintPiece.
	 */
	public CompositePiece(List entries) {
		this(entries, new Point(0, 0));
	}

	/**
	 * Constructs a composite PrintPiece with the given entries and minimum
	 * size.
	 * 
	 * @param entries
	 *            a list of CompositeEntry objects describing the child
	 *            PrintPieces.
	 * @param size
	 *            a hint indicating the minimum size that should be reported
	 *            from getSize(). This constructor increase this size to fit any
	 *            entries that extend outside the given size.
	 */
	public CompositePiece(List entries, Point size) {
		Util.noNulls(entries);

		this.entries = (CompositeEntry[]) entries
				.toArray(new CompositeEntry[entries.size()]);
		this.size = new Point(size.x, size.y);

		for (int i = 0; i < this.entries.length; i++) {
			CompositeEntry entry = this.entries[i];
			Point pieceSize = entry.piece.getSize();
			this.size.x = Math.max(this.size.x, entry.offset.x + pieceSize.x);
			this.size.y = Math.max(this.size.y, entry.offset.y + pieceSize.y);
		}
	}

	public Point getSize() {
		return new Point(size.x, size.y);
	}

	public void paint(GC gc, int x, int y) {
		// SWT on OSX has problems with the clipping. A GC(Printer) always
		// returns a clipping rectangle of
		// [0,0,0,0] so that inhibits our ability to check each entry against
		// the hit rectangle. In addition it
		// appears that a GC(Image(Printer))'s clipping on OSX is not affected
		// by the GC's transform, so that
		// screws up the hit clip as well. For this reason we are no longer
		// checking entries to see if they
		// intersect the clipping region before drawing them.

		for (int i = 0; i < entries.length; i++) {
			CompositeEntry entry = entries[i];
			entry.piece.paint(gc, x + entry.offset.x, y + entry.offset.y);
		}
	}

	public void dispose() {
		for (int i = 0; i < entries.length; i++)
			entries[i].dispose();
	}
}
