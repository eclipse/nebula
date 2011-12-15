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

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * Splits a Print into multiple PrintPieces, according to the space available on
 * the graphics device. PrintIterators are created by
 * {@link Print#iterator(Device, GC)}, and are initialized with the graphics
 * device passed to that method.
 * 
 * @author Matthew Hall
 */
public interface PrintIterator {
	/**
	 * Identifies whether any PrintPieces remain.
	 * 
	 * @return whether any PrintPieces remain.
	 */
	public boolean hasNext();

	/**
	 * Returns the next PrintPiece for the Print.
	 * <p>
	 * If all of the remaining contents of the Print will fit in the given
	 * space, the returned PrintPiece will include all remaining contents, and
	 * subsequent calls to {@link PrintIterator#hasNext() } will return
	 * <code>false</code>.
	 * <p>
	 * If some, but not all of the remaining contents will fit in the given
	 * space, the returned PrintPiece will contain as much of the contents as
	 * possible, and subsequent calls to {@link PrintIterator#hasNext() } will
	 * return <code>true</code>.
	 * <p>
	 * If there is insufficient space for any of the remaining contents in the
	 * given space, <code>null</code> is returned, and subsequent calls to
	 * {@link PrintIterator#hasNext() } will return <code>true</code>.
	 * <p>
	 * If subsequent calls to PrintIterator#hasNext() return <code>true</code>,
	 * this PrintIterator cannot fit any more in the given print area. Future
	 * calls to this method should provide a fresh print area. At the top level,
	 * each returned PrintPiece contains an entire page.
	 * <p>
	 * <b>Note</b>: PrintIterator classes should call
	 * {@link PaperClips#next(PrintIterator, int, int)} instead of calling this
	 * method directly, to gain automatic results checking to ensure all Print
	 * classes are well-behaved.
	 * 
	 * @param width
	 *            the width available on the graphics device for this iteration.
	 * @param height
	 *            the height available on the graphics device for this
	 *            iteration.
	 * @return a PrintPiece that paints the next part of the Print, or null if
	 *         the print area is too small. The size of the returned PrintPiece
	 *         must NOT exceed the width and height indicated.
	 */
	public PrintPiece next(int width, int height);

	/**
	 * Returns the minimum size PrintPiece that this Print should be broken
	 * into.
	 * <p>
	 * Note that the size calculated by this method is a "preferred minimum," or
	 * the smallest size that the Print should normally be broken into. For a
	 * TextPrint, this is the size of the widest individual word, in pixels.
	 * <p>
	 * This is distinct from the "absolute minimum," which is the smallest size
	 * that a Print could possibly be broken into. For a TextPrint, this is the
	 * size of the widest individual <em>letter</em>, in pixels.
	 * 
	 * @return a Point indicating the minimum size PrintPiece this PrintIterator
	 *         should be broken into.
	 */
	public Point minimumSize();

	/**
	 * Returns the smallest size PrintPiece that this Print would be broken into
	 * if print space was unlimited.
	 * <p>
	 * For a TextPrint, this is the size of the widest line (or the whole
	 * TextPrint, if there are no line breaks), in pixels.
	 * 
	 * @return a Point indicating the smallest size PrintPiece that this Print
	 *         would be broken into if print space was unlimited.
	 */
	public Point preferredSize();

	/**
	 * Returns a copy of this PrintIterator, with all relevant internal states.
	 * This method allows a containing iterator to "back up" the current state
	 * of its child iterators before invoking <code>next(int, int)</code> on
	 * them. The containing iterator can then safely attempt iterating its
	 * child(ren) in a variety of ways before selecting which way is the most
	 * appropriate.
	 * 
	 * @return a deep clone of the target with all relevant internal states.
	 */
	public PrintIterator copy();
}