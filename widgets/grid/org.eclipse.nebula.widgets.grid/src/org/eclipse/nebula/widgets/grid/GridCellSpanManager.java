/*******************************************************************************
 * Copyright (c) 2009 Claes Rosell
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Claes Rosell<claes.rosell@solme.se>    - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.grid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Rectangle;

class GridCellSpanManager {
	List<Rectangle> listOfCellSpanRectangles = new ArrayList<>();
	Rectangle lastUsedCellSpanRectangle = null;

	protected void addCellSpanInfo(int colIndex, int rowIndex, int colSpan,
			int rowSpan) {
		Rectangle rect = new Rectangle(colIndex, rowIndex, colSpan + 1,
				rowSpan + 1);
		this.listOfCellSpanRectangles.add(rect);
	}

	private Rectangle findSpanRectangle(int columnIndex, int rowIndex) {
		Iterator<Rectangle> iter = listOfCellSpanRectangles.iterator();
		while (iter.hasNext()) {
			Rectangle cellSpanRectangle = iter.next();
			if (cellSpanRectangle.contains(columnIndex, rowIndex)) {
				return cellSpanRectangle;
			}
		}
		return null;
	}

	protected boolean skipCell(int columnIndex, int rowIndex) {
		this.lastUsedCellSpanRectangle = this.findSpanRectangle(columnIndex,
				rowIndex);
		return this.lastUsedCellSpanRectangle != null;
	}

	protected void consumeCell(int columnIndex, int rowIndex) {
		Rectangle rectangleToConsume = null;

		if (this.lastUsedCellSpanRectangle != null
				&& this.lastUsedCellSpanRectangle.contains(columnIndex,
						rowIndex)) {
			rectangleToConsume = this.lastUsedCellSpanRectangle;
		} else {
			rectangleToConsume = this.findSpanRectangle(columnIndex, rowIndex);
		}

		if (rectangleToConsume != null) {
			if (columnIndex >= rectangleToConsume.x
					+ (rectangleToConsume.width - 1)
					&& rowIndex >= (rectangleToConsume.y
							+ rectangleToConsume.height - 1)) {
				this.listOfCellSpanRectangles.remove(rectangleToConsume);
			}
		}
	}
}
