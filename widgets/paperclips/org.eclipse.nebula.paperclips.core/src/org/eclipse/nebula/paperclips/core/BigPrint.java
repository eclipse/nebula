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
import org.eclipse.swt.graphics.Region;

/**
 * A wrapper for prints whose minimum size is too large to fit on one page. The
 * target's content is divided across multiple pages like a spreadsheet. Pages
 * are printed in order left-to-right, then top-to-bottom.
 * <p>
 * <em>Note that this print lays out content under the assumption that every page will have the same
 * pixel width and height.</em> If a BigPrint is wrapped in a print that
 * violates this expectation, it is likely that the output will skip and/or
 * repeat certain portions of the target's content. Some examples of this
 * behavior:
 * <ul>
 * <li>BorderPrint changes the available page height of the target, depending on
 * whether the top and bottom borders are open or closed.
 * <li>ColumnPrint often changes the width from column to column, if the total
 * width is not evenly divisible by the number of columns.
 * </ul>
 * 
 * @author Matthew Hall
 */
public final class BigPrint implements Print {
	private final Print target;

	/**
	 * Constructs a BigPrint.
	 * 
	 * @param target
	 */
	public BigPrint(Print target) {
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
		BigPrint other = (BigPrint) obj;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}

	/**
	 * Returns the wrapped print which is being split across pages.
	 * 
	 * @return the wrapped print which is being split across pages.
	 */
	public Print getTarget() {
		return target;
	}

	public PrintIterator iterator(Device device, GC gc) {
		return new BigIterator(target, device, gc);
	}
}

class BigIterator implements PrintIterator {
	private final PrintIterator target;
	private final Device device;

	private PrintPiece currentPiece;
	private int xOffset;
	private int yOffset;

	BigIterator(Print target, Device device, GC gc) {
		Util.notNull(device, gc, target);
		this.target = target.iterator(device, gc);
		this.device = device;
		currentPiece = null;
		xOffset = 0;
		yOffset = 0;
	}

	BigIterator(BigIterator that) {
		this.target = that.target.copy();
		this.device = that.device;

		this.currentPiece = that.currentPiece;
		this.xOffset = that.xOffset;
		this.yOffset = that.yOffset;
	}

	public Point minimumSize() {
		return target.minimumSize();
	}

	public Point preferredSize() {
		return target.preferredSize();
	}

	public boolean hasNext() {
		return currentPiece != null || target.hasNext();
	}

	// Returns a point whose x and y fields represent the required pages wide
	// and tall, respectively
	private Point estimatePagesRequired(int width, int height) {
		if (width <= 0 || height <= 0)
			return new Point(0, 0);

		Point pref = target.preferredSize();
		Point prefPages = new Point(pref.x / width, pref.y / height);

		Point min = target.minimumSize();

		// Adding width-1 rounds up page count w/out floating point op
		// Same goes for adding height-1
		Point minPages = new Point(Math.max((min.x + width - 1) / width, 1),
				Math.max((min.y + height - 1) / height, 1));

		return new Point(Math.max(prefPages.x, minPages.x), Math.max(
				prefPages.y, minPages.y));
	}

	public PrintPiece next(int width, int height) {
		if (!hasNext())
			PaperClips.error("No more content"); //$NON-NLS-1$

		if (currentPiece == null) {
			Point pages = estimatePagesRequired(width, height);
			currentPiece = PaperClips.next(target, width * pages.x, height
					* pages.y);
			if (currentPiece == null)
				return null; // Iteration fails

			// Reset the offset for the new piece.
			xOffset = 0;
			yOffset = 0;
		}

		PrintPiece result = new BigPiece(currentPiece,
				new Point(width, height), xOffset, yOffset);

		// Advance cursor on current piece.
		xOffset += width;
		if (xOffset >= currentPiece.getSize().x) {
			xOffset = 0;
			yOffset += height;
		}
		if (yOffset >= currentPiece.getSize().y) {
			currentPiece = null;
		}

		return result;
	}

	public PrintIterator copy() {
		return new BigIterator(this);
	}
}

class BigPiece implements PrintPiece {
	private final PrintPiece target;
	private final Point size;
	private final Point offset;

	BigPiece(PrintPiece target, Point size, int xOffset, int yOffset) {
		Util.notNull(target, size);
		this.target = target;
		this.size = new Point(size.x, size.y);
		this.offset = new Point(xOffset, yOffset);
	}

	public Point getSize() {
		return new Point(size.x, size.y);
	}

	public void paint(GC gc, int x, int y) {
		// Remember clipping region
		Region region = new Region();
		gc.getClipping(region);

		// Set clipping region so only the portion of the target we want is
		// printed.
		gc.setClipping(x, y, size.x, size.y);

		// Paint the target.
		target.paint(gc, x - offset.x, y - offset.y);

		// Restore clipping region
		gc.setClipping(region);
		region.dispose();
	}

	public void dispose() {
		target.dispose();
	}
}