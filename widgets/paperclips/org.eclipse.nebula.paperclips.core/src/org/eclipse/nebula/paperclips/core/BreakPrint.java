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
package org.eclipse.nebula.paperclips.core;

import org.eclipse.nebula.paperclips.core.internal.piece.EmptyPiece;
import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * A print which inserts a page break (or a column break, if inside a
 * ColumnPrint).
 * <p>
 * This class is horizontally and vertically greedy. Greedy prints take up all
 * the available space on the page.
 * 
 * @author Matthew Hall
 */
public class BreakPrint implements Print {
	/**
	 * Constructs a BreakPrint.
	 */
	public BreakPrint() {
		// Nothing to do
	}

	public boolean equals(Object obj) {
		return Util.sameClass(this, obj);
	}

	public int hashCode() {
		return 39 * 29;
	}

	public PrintIterator iterator(Device device, GC gc) {
		return new BreakIterator();
	}
}

class BreakIterator implements PrintIterator {
	boolean hasNext;

	BreakIterator() {
		hasNext = true;
	}

	public PrintIterator copy() {
		return hasNext ? new BreakIterator() : this;
	}

	public boolean hasNext() {
		return hasNext;
	}

	public Point minimumSize() {
		return new Point(0, 0);
	}

	public Point preferredSize() {
		return new Point(0, 0);
	}

	public PrintPiece next(int width, int height) {
		if (!hasNext)
			PaperClips.error("No more content"); //$NON-NLS-1$

		hasNext = false;
		return new EmptyPiece(new Point(width, height));
	}
}