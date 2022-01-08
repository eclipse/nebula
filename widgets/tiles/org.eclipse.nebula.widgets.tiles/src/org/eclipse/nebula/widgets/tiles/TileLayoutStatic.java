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

public class TileLayoutStatic extends TileLayout {

	/** Width*/
	private final int width;
	/** Height*/
	private final int height;

	/**
	 * Creates a new instance
	 * @param width
	 * @param height
	 * @param marginX
	 * @param marginY
	 */
	public TileLayoutStatic(final int width, final int height, final int marginX, final int marginY){
		super(marginX, marginY);
		this.width = width;
		this.height = height;
	}

	@Override
	public int getHeight(final Tiles<?> tiles) {
		return height;
	}

	@Override
	public int getWidth(final Tiles<?> tiles) {
		return width;
	}
}
