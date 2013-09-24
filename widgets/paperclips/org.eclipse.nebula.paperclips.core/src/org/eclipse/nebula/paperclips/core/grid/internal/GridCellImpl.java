package org.eclipse.nebula.paperclips.core.grid.internal;

import org.eclipse.nebula.paperclips.core.PaperClips;
import org.eclipse.nebula.paperclips.core.Print;
import org.eclipse.nebula.paperclips.core.grid.GridCell;
import org.eclipse.nebula.paperclips.core.grid.GridPrint;
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
public class GridCellImpl implements GridCell {

	private final int hAlignment;
	private final int vAlignment;
	private final Print target;
	private final int colspan;

	/**
	 * This constructor is only here for compatibility reasons and is not
	 * intented to be used by clients.
	 * 
	 * @param hAlignment
	 *            the horizontal alignment.
	 * @param vAlignment
	 *            the vertical alignment.
	 * @param target
	 *            the target of the cell.
	 * @param colspan
	 *            the number of columns this cell spans across.
	 */

	public GridCellImpl(int hAlignment, int vAlignment, Print target,
			int colspan) {
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
		if (colspan != other.getColSpan())
			return false;
		if (hAlignment != other.getHorizontalAlignment())
			return false;
		if (target == null) {
			if (other.getContent() != null)
				return false;
		} else if (!target.equals(other.getContent()))
			return false;
		if (vAlignment != other.getVerticalAlignment())
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.nebula.paperclips.core.grid.GridCell#getAlignment()
	 */
	public Point getAlignment() {
		return new Point(hAlignment, vAlignment);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.nebula.paperclips.core.grid.GridCell#getHorizontalAlignment()
	 */
	public int getHorizontalAlignment() {
		return hAlignment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.nebula.paperclips.core.grid.GridCell#getVerticalAlignment()
	 */
	public int getVerticalAlignment() {
		return vAlignment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.nebula.paperclips.core.grid.GridCell#getContent()
	 */
	public Print getContent() {
		return target;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.nebula.paperclips.core.grid.GridCell#getColSpan()
	 */
	public int getColSpan() {
		return colspan;
	}

	private static int checkHorizontalAlignment(int hAlignment) {
		hAlignment = PaperClipsUtil.firstMatch(hAlignment, new int[] {
				SWT.DEFAULT, SWT.LEFT, SWT.CENTER, SWT.RIGHT }, 0);
		if (hAlignment == 0)
			PaperClips
					.error(SWT.ERROR_INVALID_ARGUMENT,
							"Alignment argument must be one of SWT.LEFT, SWT.CENTER, SWT.RIGHT, or SWT.DEFAULT"); //$NON-NLS-1$
		return hAlignment;
	}

	private static int checkVerticalAlignment(int vAlignment) {
		vAlignment = PaperClipsUtil.firstMatch(vAlignment, new int[] {
				SWT.DEFAULT, SWT.TOP, SWT.CENTER, SWT.BOTTOM, SWT.FILL }, 0);
		if (vAlignment == 0)
			PaperClips
					.error(SWT.ERROR_INVALID_ARGUMENT,
							"Alignment argument must be one of SWT.TOP, SWT.CENTER, SWT.BOTTOM, SWT.DEFAULT, or SWT.FILL"); //$NON-NLS-1$
		return vAlignment;
	}

	private int checkColspan(int colspan) {
		if (colspan <= 0 && colspan != GridPrint.REMAINDER)
			PaperClips.error(SWT.ERROR_INVALID_ARGUMENT,
					"colspan must be a positive number or GridPrint.REMAINDER"); //$NON-NLS-1$
		return colspan;
	}

	public GridCellIterator iterator(Device device, GC gc) {
		return new GridCellIterator(this, device, gc);
	}

}