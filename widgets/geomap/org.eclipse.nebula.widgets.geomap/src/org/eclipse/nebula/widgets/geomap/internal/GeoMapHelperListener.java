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
public interface GeoMapHelperListener {

	/**
	 * Notifies listener that a tile has been updated and may need (re)painting
	 * 
	 * @param tileRef
	 *            the reference to the updated tile
	 */
	public void tileUpdated(TileRef tileRef);
}
