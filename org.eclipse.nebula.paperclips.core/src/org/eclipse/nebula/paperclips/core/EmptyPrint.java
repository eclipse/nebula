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

import org.eclipse.nebula.paperclips.core.internal.piece.EmptyPiece;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * A Print which displays nothing but takes up space. Useful for putting blank
 * cells in a GridPrint.
 * 
 * @author Matthew
 */
public class EmptyPrint implements Print {
	final int width;
	final int height;

	/**
	 * Constructs an EmptyPrint with size (0, 0).
	 */
	public EmptyPrint() {
		this(0, 0);
	}

	/**
	 * Constructs an EmptyPrint with the given size.
	 * 
	 * @param width
	 *            width of the Print, in points (72pts = 1").
	 * @param height
	 *            height of the Print, in points (72pts = 1").
	 */
	public EmptyPrint(int width, int height) {
		this.width = checkDimension(width);
		this.height = checkDimension(height);
	}

	/**
	 * Constructs an EmptyPrint with the given size.
	 * 
	 * @param size
	 *            the size, in points (72pts = 1").
	 */
	public EmptyPrint(Point size) {
		this(size.x, size.y);
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + width;
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmptyPrint other = (EmptyPrint) obj;
		if (height != other.height)
			return false;
		if (width != other.width)
			return false;
		return true;
	}

	/**
	 * Returns the size of the empty space.
	 * 
	 * @return the size of the empty space.
	 */
	public Point getSize() {
		return new Point(width, height);
	}

	private int checkDimension(int dim) {
		if (dim < 0)
			PaperClips.error(SWT.ERROR_INVALID_ARGUMENT,
					"EmptyPrint dimensions must be >= 0"); //$NON-NLS-1$
		return dim;
	}

	public PrintIterator iterator(Device device, GC gc) {
		return new EmptyIterator(device, this);
	}
}

class EmptyIterator implements PrintIterator {
	private final Point size;

	private boolean hasNext = true;

	EmptyIterator(Device device, EmptyPrint target) {
		Point dpi = device.getDPI();
		this.size = new Point(Math.round(target.width * dpi.x / 72f), Math
				.round(target.height * dpi.y / 72f));
	}

	EmptyIterator(EmptyIterator that) {
		this.size = that.size;
		this.hasNext = that.hasNext;
	}

	public boolean hasNext() {
		return hasNext;
	}

	public PrintPiece next(int width, int height) {
		if (size.x > width || size.y > height)
			return null;

		hasNext = false;

		return new EmptyPiece(size);
	}

	public Point minimumSize() {
		return new Point(size.x, size.y);
	}

	public Point preferredSize() {
		return new Point(size.x, size.y);
	}

	public PrintIterator copy() {
		return new EmptyIterator(this);
	}
}