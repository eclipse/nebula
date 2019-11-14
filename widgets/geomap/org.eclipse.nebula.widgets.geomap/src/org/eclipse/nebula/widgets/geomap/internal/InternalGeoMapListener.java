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
 * Interface for tapping into internal details of an InternalGeoMap
 */
public interface InternalGeoMapListener {

	/**
	 * Notifies listener that a tile has been (re)painted
	 * 
	 * @param tileRef
	 *            the reference to the painted tile
	 */
	public void tilePainted(TileRef tileRef);

	/**
	 * Notifies listener that the control has been (re)painted
	 * 
	 * @param tileCount
	 *            the number of tiles that where painted
	 * @param time
	 *            the time it took
	 */
	public void mapPainted(int tileCount, long time);

	/**
	 * Notifies listener that the tile cache has been updated
	 * 
	 * @param used
	 *            the used slots of the cache
	 * @param size
	 *            the size of the cache
	 */
	public void tileCacheUpdated(int used, int size);
}
