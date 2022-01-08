/*****************************************************************************
 * Copyright (c) 2014, 2021 Fabian Prasser, Laurent Caron
 *
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Fabian Prasser - Initial API and implementation
 * Laurent Caron <laurent dot caron at gmail dot com> - Integration into the Nebula Project
 *****************************************************************************/
package org.eclipse.nebula.widgets.tiles;

/**
 * A dynamic layout
 *
 * @author Fabian Prasser
 */
public class TileLayoutDynamic extends TileLayout {

	/** Number of columns */
	private final int columns;
	/** Number of rows */
	private final int rows;

	/**
	 * Creates a new instance
	 * @param columns
	 * @param rows
	 * @param marginX
	 * @param marginY
	 */
	public TileLayoutDynamic(final int columns, final int rows, final int marginX, final int marginY) {
		super(marginX, marginY);
		this.columns = columns;
		this.rows = rows;
	}

	@Override
	public int getHeight(final Tiles<?> tiles) {
		return (tiles.getSize().y - (rows + 1) * getMarginY()) / rows;
	}

	@Override
	public int getWidth(final Tiles<?> tiles) {
		return (tiles.getSize().x - (columns + 1) * getMarginX()) / columns;
	}
}
