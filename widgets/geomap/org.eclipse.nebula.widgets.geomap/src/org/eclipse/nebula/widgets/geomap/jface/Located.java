/*******************************************************************************
 * Copyright (c) 2012 Hallvard Tr�tteberg.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Hallvard Tr�tteberg - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.geomap.jface;

import org.eclipse.nebula.widgets.geomap.PointD;

/**
 * Interface to provide a location for an object, as a PointD with longitude, latitude coordinates.
 * 
 */
public interface Located {

	/**
	 * Returns the longitude, latitude for this object
	 * or null if this object doesn't have a location.
	 * @return the longitude, latitude as a PointD
	 */
	public PointD getLonLat();

	/**
	 * Set the longitude, latitude for this object.
	 * Returns true if the change occurred, i.e. the operation was legal.
	 * Use setLonLat(getLonLat().x, getLonLat().y) to check without side-effect.
	 * @param lon the new longitude
	 * @param lat the new latitude
	 * @return if it was actually changed
	 */
	public boolean setLonLat(double lon, double lat);
	
	/**
	 * A base implementation for read-only locations
	 * @since 3.3
	 *
	 */
	public abstract class Static implements Located {

		public boolean setLonLat(double lon, double lat) {
			return false;
		}
		
	}
}
