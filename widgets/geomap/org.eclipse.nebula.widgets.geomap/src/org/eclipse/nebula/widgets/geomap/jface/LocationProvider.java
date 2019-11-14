/*******************************************************************************
 * Copyright (c) 2012 Hallvard Tr�tteberg.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Hallvard Tr�tteberg - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.geomap.jface;

import org.eclipse.nebula.widgets.geomap.PointD;

/**
 * Interface to provide a location for a given element, as a PointD with
 * longitude, latitude coordinates.
 *
 */
public interface LocationProvider {
	/**
	 * Returns the longitude, latitude for the element or null if this elements
	 * doesn't have a location.
	 * 
	 * @param element
	 *            the element for which to return the corresponding geo-location
	 * @return the geo-location
	 */
	public PointD getLonLat(Object element);

	/**
	 * Set the longitude, latitude for the element. Returns true if the change
	 * occurred, i.e. the operation was legal. Use setLonLat(element,
	 * getLonLat(element).x, getLonLat(element).y) to check without side-effect.
	 * 
	 * @param element
	 *            the element for which to set the geo-location
	 * @param lon
	 *            the new longitude
	 * @param lat
	 *            the new latitude
	 * @return true if the geo-location could be set, or false, if the location
	 *         is read-only
	 */
	public boolean setLonLat(Object element, double lon, double lat);
}
