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
 * A layout for the tiles
 * @author Fabian Prasser
 */
public abstract class TileLayout {

	/** Margin*/
	private final int marginX;
	/** Margin*/
	private final int marginY;

	/**
	 * Creates a new instance
	 * @param marginX
	 * @param marginY
	 */
	public TileLayout(final int marginX, final int marginY){
		this.marginX = marginX;
		this.marginY = marginY;
	}

	/**
	 * Returns the height of tiles
	 * @param tiles
	 * @return
	 */
	public abstract int getHeight(Tiles<?> tiles);

	/**
	 * @return the marginX
	 */
	public int getMarginX() {
		return marginX;
	}

	/**
	 * @return the marginY
	 */
	public int getMarginY() {
		return marginY;
	}

	/**
	 * Returns the width of tiles
	 * @param tiles
	 * @return
	 */
	public abstract int getWidth(Tiles<?> tiles);
}
