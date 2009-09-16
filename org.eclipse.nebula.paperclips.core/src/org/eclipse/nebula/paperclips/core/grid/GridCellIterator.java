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

import org.eclipse.nebula.paperclips.core.PrintIterator;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;

class GridCellIterator {
	final int hAlignment;
	final int vAlignment;
	final PrintIterator target;
	final int colspan;

	GridCellIterator(GridCell cell, Device device, GC gc) {
		this.hAlignment = cell.hAlignment;
		this.vAlignment = cell.vAlignment;
		this.target = cell.target.iterator(device, gc);
		this.colspan = cell.colspan;
	}

	private GridCellIterator(GridCellIterator that) {
		this.hAlignment = that.hAlignment;
		this.vAlignment = that.vAlignment;
		this.target = that.target.copy();
		this.colspan = that.colspan;
	}

	GridCellIterator copy() {
		return new GridCellIterator(this);
	}
}