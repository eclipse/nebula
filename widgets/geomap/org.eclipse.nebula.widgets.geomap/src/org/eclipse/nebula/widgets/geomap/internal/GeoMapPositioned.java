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

import org.eclipse.swt.graphics.Point;

/**
 * Interface for managing position and zoom level
 * 
 * @since 3.3
 *
 */
public interface GeoMapPositioned {

	/**
	 * Gets the position of the upper left corner of the map. The resolution
	 * depends on the zoom level.
	 * 
	 * @return the position of the upper left corner of the map
	 */
	public Point getMapPosition();

	/**
	 * Sets the position of the upper left corner of the map. The resolution
	 * depends on the zoom level.
	 * 
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 */
	public void setMapPosition(int x, int y);

	/**
	 * Gets the current zoom level
	 * 
	 * @return the current zoom level
	 */
	public int getZoom();

	/**
	 * Gets the minimum supported zoom level
	 * 
	 * @return the minimum zoom level
	 */
	public int getMinZoom();
	
	/**
	 * Gets the maximum supported zoom level
	 * 
	 * @return the maximum zoom level
	 */
	public int getMaxZoom();

	/**
	 * Sets the current zoom level
	 * 
	 * @param zoom
	 *            the new zoom level
	 */
	public void setZoom(int zoom);
}
