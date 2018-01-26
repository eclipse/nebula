/*******************************************************************************
 * Copyright (c) 2008, 2012 Stepan Rutz.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
