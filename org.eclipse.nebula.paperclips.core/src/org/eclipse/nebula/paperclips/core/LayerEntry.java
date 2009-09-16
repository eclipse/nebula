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
package org.eclipse.nebula.paperclips.core;

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
public class LayerEntry {
	final Print target;
	final int align;

	LayerEntry(Print target, int align) {
		Util.notNull(target);
		this.target = target;
		this.align = checkAlign(align);
	}

	LayerEntry(LayerEntry that) {
		this.target = that.target;
		this.align = that.align;
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
		if (align != other.align)
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}

	/**
	 * Returns the target print of this entry.
	 * 
	 * @return the target print of this entry.
	 */
	public Print getTarget() {
		return target;
	}

	/**
	 * Returns the horizontal alignment applied to the target.
	 * 
	 * @return the horizontal alignment applied to the target.
	 */
	public int getHorizontalAlignment() {
		return align;
	}

	private static int checkAlign(int align) {
		return PaperClipsUtil.firstMatch(align, new int[] { SWT.LEFT,
				SWT.CENTER, SWT.RIGHT }, SWT.LEFT);
	}

	LayerEntry copy() {
		return new LayerEntry(this);
	}

	LayerEntryIterator iterator(Device device, GC gc) {
		return new LayerEntryIterator(this, device, gc);
	}
}