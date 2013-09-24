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
package org.eclipse.nebula.paperclips.core.grid.internal;

import org.eclipse.nebula.paperclips.core.PrintIterator;
import org.eclipse.nebula.paperclips.core.grid.GridCell;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;

public class GridCellIterator {
	final int hAlignment;
	final int vAlignment;
	final PrintIterator target;
	final int colspan;

	public GridCellIterator(GridCell cell, Device device, GC gc) {
		this.hAlignment = cell.getHorizontalAlignment();
		this.vAlignment = cell.getVerticalAlignment();
		this.target = cell.getContent().iterator(device, gc);
		this.colspan = cell.getColSpan();
	}

	private GridCellIterator(GridCellIterator that) {
		this.hAlignment = that.hAlignment;
		this.vAlignment = that.vAlignment;
		this.target = that.target.copy();
		this.colspan = that.colspan;
	}

	public int getHorizontalAlignment() {
		return hAlignment;
	}

	public int getVerticalAlignment() {
		return vAlignment;
	}

	public PrintIterator getTarget() {
		return target;
	}

	public int getColspan() {
		return colspan;
	}

	public GridCellIterator copy() {
		return new GridCellIterator(this);
	}
}