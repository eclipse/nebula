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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.paperclips.core.internal.LayerEntryImpl;
import org.eclipse.nebula.paperclips.core.internal.LayerIterator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;

/**
 * A Print which displays its child Prints on top each other.
 * 
 * @author Matthew Hall
 */
public class LayerPrint implements Print {
	/**
	 * Constant for the default alignment of child Prints. Value is SWT.LEFT.
	 */
	public static final int DEFAULT_ALIGN = SWT.LEFT;

	// List<LayerEntry>
	final List entries = new ArrayList();

	/**
	 * Constructs a new LayerPrint.
	 */
	public LayerPrint() {
	}

	/**
	 * Adds the given Print to this LayerPrint using the default alignment.
	 * 
	 * @param print
	 *            the Print to add.
	 * @see #DEFAULT_ALIGN
	 */
	public void add(Print print) {
		entries.add(new LayerEntryImpl(print, DEFAULT_ALIGN));
	}

	/**
	 * Adds the given Print to this LayerPrint using the specified alignment.
	 * 
	 * @param print
	 *            the Print to add.
	 * @param align
	 *            the alignment for the Print. May be one of SWT.LEFT,
	 *            SWT.CENTER, or SWT.RIGHT.
	 */
	public void add(Print print, int align) {
		entries.add(new LayerEntryImpl(print, align));
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entries == null) ? 0 : entries.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LayerPrint other = (LayerPrint) obj;
		if (entries == null) {
			if (other.entries != null)
				return false;
		} else if (!entries.equals(other.entries))
			return false;
		return true;
	}

	/**
	 * Returns an array of entries in this LayerPrint.
	 * 
	 * @return an array of entries in this LayerPrint.
	 */
	public LayerEntry[] getEntries() {
		return (LayerEntry[]) entries.toArray(new LayerEntry[entries.size()]);
	}

	public PrintIterator iterator(Device device, GC gc) {
		return new LayerIterator(this, device, gc);
	}
}
