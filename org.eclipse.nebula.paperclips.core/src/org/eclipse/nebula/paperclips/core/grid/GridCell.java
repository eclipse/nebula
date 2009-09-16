/*
 * Copyright (c) 2007 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */
package org.eclipse.nebula.paperclips.core.grid;

import org.eclipse.nebula.paperclips.core.PaperClips;
import org.eclipse.nebula.paperclips.core.Print;
import org.eclipse.nebula.paperclips.core.internal.util.PaperClipsUtil;
import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * Instances of this class represent a single cell in a GridPrint.
 * 
 * @author Matthew Hall
 */
public class GridCell {
	final int hAlignment;
	final int vAlignment;
	final Print target;
	final int colspan;

	GridCell(int hAlignment, int vAlignment, Print target, int colspan) {
		Util.notNull(target);
		this.hAlignment = checkHorizontalAlignment(hAlignment);
		this.vAlignment = checkVerticalAlignment(vAlignment);
		this.target = target;
		this.colspan = checkColspan(colspan);
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + colspan;
		result = prime * result + hAlignment;
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		result = prime * result + vAlignment;
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GridCell other = (GridCell) obj;
		if (colspan != other.colspan)
			return false;
		if (hAlignment != other.hAlignment)
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		if (vAlignment != other.vAlignment)
			return false;
		return true;
	}

	/**
	 * Returns a Point representing the horizontal and vertical alignment
	 * applied to the cell's content.
	 * 
	 * @return a Point representing the horizontal and vertical alignment
	 *         applied to the cell's content.
	 */
	public Point getAlignment() {
		return new Point(hAlignment, vAlignment);
	}

	/**
	 * Returns the horizontal alignment applied to the cell content.
	 * 
	 * @return the horizontal alignment applied to the cell content.
	 */
	public int getHorizontalAlignment() {
		return hAlignment;
	}

	/**
	 * Returns the vertical alignment applied to the cell content.
	 * 
	 * @return the vertical alignment applied to the cell content.
	 */
	public int getVerticalAlignment() {
		return vAlignment;
	}

	/**
	 * Returns the content print of the cell.
	 * 
	 * @return the content print of the cell.
	 */
	public Print getContent() {
		return target;
	}

	/**
	 * Returns the number of columns this cell spans across.
	 * 
	 * @return the number of columns this cell spans across.
	 */
	public int getColSpan() {
		return colspan;
	}

	private static int checkHorizontalAlignment(int hAlignment) {
		hAlignment = PaperClipsUtil.firstMatch(hAlignment, new int[] {
				SWT.DEFAULT, SWT.LEFT, SWT.CENTER, SWT.RIGHT }, 0);
		if (hAlignment == 0)
			PaperClips
					.error(
							SWT.ERROR_INVALID_ARGUMENT,
							"Alignment argument must be one of SWT.LEFT, SWT.CENTER, SWT.RIGHT, or SWT.DEFAULT"); //$NON-NLS-1$
		return hAlignment;
	}

	private static int checkVerticalAlignment(int vAlignment) {
		vAlignment = PaperClipsUtil.firstMatch(vAlignment, new int[] {
				SWT.DEFAULT, SWT.TOP, SWT.CENTER, SWT.BOTTOM, SWT.FILL }, 0);
		if (vAlignment == 0)
			PaperClips
					.error(
							SWT.ERROR_INVALID_ARGUMENT,
							"Alignment argument must be one of SWT.TOP, SWT.CENTER, SWT.BOTTOM, SWT.DEFAULT, or SWT.FILL"); //$NON-NLS-1$
		return vAlignment;
	}

	private int checkColspan(int colspan) {
		if (colspan <= 0 && colspan != GridPrint.REMAINDER)
			PaperClips.error(SWT.ERROR_INVALID_ARGUMENT,
					"colspan must be a positive number or GridPrint.REMAINDER"); //$NON-NLS-1$
		return colspan;
	}

	GridCellIterator iterator(Device device, GC gc) {
		return new GridCellIterator(this, device, gc);
	}
}