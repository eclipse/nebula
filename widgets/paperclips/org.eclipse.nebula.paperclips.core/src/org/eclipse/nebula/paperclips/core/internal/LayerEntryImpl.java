package org.eclipse.nebula.paperclips.core.internal;

import org.eclipse.nebula.paperclips.core.LayerEntry;
import org.eclipse.nebula.paperclips.core.LayerEntryIterator;
import org.eclipse.nebula.paperclips.core.Print;
import org.eclipse.nebula.paperclips.core.internal.util.PaperClipsUtil;
import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;

/**
 * Instances in this class represent an entry in a LayerPrint.
 * 
 * @author Matthew Hall
 */
public class LayerEntryImpl implements LayerEntry {

	private final Print target;
	private final int align;

	/**
	 * Create a new layer entry.
	 * 
	 * @param target
	 *            the target print of this entry.
	 * @param align
	 *            the horizontal alignment applied to the target.
	 */
	public LayerEntryImpl(Print target, int align) {
		Util.notNull(target);
		this.target = target;
		this.align = checkAlign(align);
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + align;
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
		LayerEntry other = (LayerEntry) obj;
		if (align != other.getHorizontalAlignment())
			return false;
		if (target == null) {
			if (other.getTarget() != null)
				return false;
		} else if (!target.equals(other.getTarget()))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.nebula.paperclips.core.internal.LayerEntry#getTarget()
	 */
	public Print getTarget() {
		return target;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.nebula.paperclips.core.internal.LayerEntry#getHorizontalAlignment
	 * ()
	 */
	public int getHorizontalAlignment() {
		return align;
	}

	private static int checkAlign(int align) {
		return PaperClipsUtil.firstMatch(align, new int[] { SWT.LEFT,
				SWT.CENTER, SWT.RIGHT }, SWT.LEFT);
	}

	/**
	 * @param device
	 * @param gc
	 * @return
	 */
	public LayerEntryIterator iterator(Device device, GC gc) {
		return new LayerEntryIteratorImpl(this, device, gc);
	}
}