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

import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * A wrapper print that aligns its target vertically and/or horizontally. An
 * AlignPrint is vertically greedy when the vertical alignment is SWT.CENTER or
 * SWT.BOTTOM, and horizontally greedy when the horizontal alignment is
 * SWT.CENTER and SWT.RIGHT.
 * 
 * @author Matthew Hall
 */
public class AlignPrint implements Print {
	private static final int DEFAULT_HORIZONTAL_ALIGN = SWT.LEFT;
	private static final int DEFAULT_VERTICAL_ALIGN = SWT.TOP;

	final Print target;
	final int hAlign;
	final int vAlign;

	/**
	 * Constructs a new AlignPrint.
	 * 
	 * @param target
	 *            the print being aligned.
	 * @param hAlign
	 *            the horizontal alignment. One of SWT.LEFT, SWT.CENTER,
	 *            SWT.RIGHT, or SWT.DEFAULT.
	 * @param vAlign
	 *            the vertical alignment. One of SWT.TOP, SWT.CENTER,
	 *            SWT.BOTTOM, or SWT.DEFAULT.
	 */
	public AlignPrint(Print target, int hAlign, int vAlign) {
		Util.notNull(target);
		this.target = target;
		this.hAlign = checkHAlign(hAlign);
		this.vAlign = checkVAlign(vAlign);
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + hAlign;
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		result = prime * result + vAlign;
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AlignPrint other = (AlignPrint) obj;
		if (hAlign != other.hAlign)
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		if (vAlign != other.vAlign)
			return false;
		return true;
	}

	/**
	 * Returns the wrapped print being aligned
	 * 
	 * @return the wrapped print being aligned
	 */
	public Print getTarget() {
		return target;
	}

	/**
	 * Returns a Point with the x and y fields set to the horizontal and
	 * vertical alignment, respectively.
	 * 
	 * @return a Point with the x and y fields set to the horizontal and
	 *         vertical alignment, respectively.
	 */
	public Point getAlignment() {
		return new Point(hAlign, vAlign);
	}

	private static int checkHAlign(int hAlign) {
		if (hAlign == SWT.LEFT || hAlign == SWT.CENTER || hAlign == SWT.RIGHT)
			return hAlign;
		if (hAlign == SWT.DEFAULT)
			return DEFAULT_HORIZONTAL_ALIGN;
		PaperClips.error(SWT.ERROR_INVALID_ARGUMENT,
				"hAlign must be one of SWT.LEFT, SWT.CENTER or SWT.RIGHT"); //$NON-NLS-1$
		return hAlign;
	}

	private static int checkVAlign(int vAlign) {
		if (vAlign == SWT.TOP || vAlign == SWT.CENTER || vAlign == SWT.BOTTOM)
			return vAlign;
		if (vAlign == SWT.DEFAULT)
			return DEFAULT_VERTICAL_ALIGN;
		PaperClips.error(SWT.ERROR_INVALID_ARGUMENT,
				"vAlign must be one of SWT.TOP, SWT.CENTER or SWT.BOTTOM"); //$NON-NLS-1$
		return vAlign;
	}

	public PrintIterator iterator(Device device, GC gc) {
		return new AlignIterator(this, device, gc);
	}
}

class AlignIterator implements PrintIterator {
	private final PrintIterator target;
	private final int hAlign;
	private final int vAlign;

	AlignIterator(AlignPrint print, Device device, GC gc) {
		this.target = print.target.iterator(device, gc);
		this.hAlign = print.hAlign;
		this.vAlign = print.vAlign;
	}

	AlignIterator(AlignIterator that) {
		this.target = that.target.copy();
		this.hAlign = that.hAlign;
		this.vAlign = that.vAlign;
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
		PrintPiece piece = PaperClips.next(target, width, height);
		if (piece == null)
			return null;

		Point size = piece.getSize();
		Point offset = new Point(0, 0);

		if (hAlign == SWT.CENTER)
			offset.x = (width - size.x) / 2;
		else if (hAlign == SWT.RIGHT)
			offset.x = width - size.x;

		if (hAlign != SWT.LEFT)
			size.x = width;

		if (vAlign == SWT.CENTER)
			offset.y = (height - size.y) / 2;
		else if (vAlign == SWT.BOTTOM)
			offset.y = height - size.y;

		if (vAlign != SWT.TOP)
			size.y = height;

		CompositeEntry entry = new CompositeEntry(piece, offset);

		return new CompositePiece(new CompositeEntry[] { entry }, size);
	}

	public PrintIterator copy() {
		return new AlignIterator(this);
	}
}