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
package org.eclipse.nebula.paperclips.core.grid;

import org.eclipse.swt.graphics.RGB;

/**
 * Instances of this interface provide background colors to be drawn behind
 * cells in a grid. This interface is used by DefaultGridLook to provide
 * pluggable cell background behavior.
 * 
 * @author Matthew Hall
 */
public interface CellBackgroundProvider {
	/**
	 * Returns the background color to display for the given grid cell.
	 * 
	 * @param row
	 *            the row index (zero-based)
	 * @param column
	 *            the column index (zero-based). This is the grid column index,
	 *            not the cell index within the row.
	 * @param colspan
	 *            the number of grid columns that the cell occupies.
	 * @return the background color to display for the given header cell.
	 */
	public RGB getCellBackground(int row, int column, int colspan);
}
