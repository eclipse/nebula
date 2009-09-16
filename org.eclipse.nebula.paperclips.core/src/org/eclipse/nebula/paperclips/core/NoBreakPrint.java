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

import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * A print wrapper which prevents its target from being broken into multiple
 * pieces when printed. If there isn't enough room to print the target in one
 * piece on the current page (or column, if it's inside a ColumnPrint), it will
 * be printed on the next page (or column).
 * 
 * <p>
 * Care must be taken when using this class to avoid unprintable documents. If
 * the target of a NoBreakPrint does not fit in the available space on the print
 * device, the entire document will fail to print.
 * 
 * @author Matthew Hall
 */
public class NoBreakPrint implements Print {
	private final Print target;

	/**
	 * Constructs a NoBreakPrint with the given target.
	 * 
	 * @param target
	 *            the print to
	 */
	public NoBreakPrint(Print target) {
		Util.notNull(target);
		this.target = target;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		NoBreakPrint other = (NoBreakPrint) obj;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}

	/**
	 * Returns the print which will not be broken across pages.
	 * 
	 * @return the print which will not be broken across pages.
	 */
	public Print getTarget() {
		return target;
	}

	public PrintIterator iterator(Device device, GC gc) {
		return new NoBreakIterator(target.iterator(device, gc));
	}
}

class NoBreakIterator implements PrintIterator {
	private PrintIterator target;

	NoBreakIterator(PrintIterator target) {
		Util.notNull(target);
		this.target = target;
	}

	public PrintIterator copy() {
		return new NoBreakIterator(target.copy());
	}

	public boolean hasNext() {
		return target.hasNext();
	}

	public Point minimumSize() {
		return target.minimumSize();
	}

	public Point preferredSize() {
		return target.preferredSize();
	}

	public PrintPiece next(int width, int height) {
		// Use a test iterator so we preserve the original iterator
		PrintIterator iter = target.copy();

		PrintPiece result = PaperClips.next(iter, width, height);
		if (result == null)
			return result;

		if (iter.hasNext()) // Failed to layout the whole target in one piece
			return null;

		this.target = iter;
		return result;
	}
}