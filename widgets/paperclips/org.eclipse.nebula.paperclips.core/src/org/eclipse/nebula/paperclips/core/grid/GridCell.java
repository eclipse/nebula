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

import org.eclipse.nebula.paperclips.core.Print;
import org.eclipse.swt.graphics.Point;

/**
 * Instances of this interface represent a single cell in a GridPrint.
 * 
 * @author Matthew Hall
 */
public interface GridCell {

	/**
	 * Returns a Point representing the horizontal and vertical alignment
	 * applied to the cell's content.
	 * 
	 * @return a Point representing the horizontal and vertical alignment
	 *         applied to the cell's content.
	 */
	Point getAlignment();

	/**
	 * Returns the horizontal alignment applied to the cell content.
	 * 
	 * @return the horizontal alignment applied to the cell content.
	 */
	int getHorizontalAlignment();

	/**
	 * Returns the vertical alignment applied to the cell content.
	 * 
	 * @return the vertical alignment applied to the cell content.
	 */
	int getVerticalAlignment();

	/**
	 * Returns the content print of the cell.
	 * 
	 * @return the content print of the cell.
	 */
	Print getContent();

	/**
	 * Returns the number of columns this cell spans across.
	 * 
	 * @return the number of columns this cell spans across.
	 */
	int getColSpan();

}