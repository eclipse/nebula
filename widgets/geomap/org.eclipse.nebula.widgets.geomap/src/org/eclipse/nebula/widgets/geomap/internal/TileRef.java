/*******************************************************************************
 * Copyright (c) 2008, 2012 Stepan Rutz.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Stepan Rutz - initial implementation
 *    Hallvard Trætteberg - further cleanup and development
 *******************************************************************************/

package org.eclipse.nebula.widgets.geomap.internal;

/**
 * A single tile in the map. A tile has an x and y coordinate, but both only
 * make sense in the context of a given zoom level. The tile's zoom level is
 * given by <code>z</code>.
 *
 * <p>
 * For caching the tiles support some equals and hashCode behavior that makes
 * them suitable as key-objects in java-util collections.
 * </p>
 */
public final class TileRef {

	/**
	 * The x coordinate of this TileRef
	 */
	public final int x;

	/**
	 * The y coordinate of this TileRef
	 */
	public final int y;

	/**
	 * The z coordinate, i.e. zoom level, of this TileRef
	 */
	public final int z;

	/**
	 * Initializes this TileRef with x, y and z coordinates
	 * 
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 * @param z
	 *            the z coordinate
	 */
	public TileRef(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	@SuppressWarnings("nls")
	public String toString() {
		return "Tile {" + x + ", " + y + " @ " + z + "}";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		TileRef other = (TileRef) obj;
		return x == other.x && y == other.y && z == other.z;
	}
}
