/*******************************************************************************
 * Copyright (c) 2012 Hallvard Tr¾tteberg.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Hallvard Tr¾tteberg - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.geomap.jface;

import org.eclipse.nebula.widgets.geomap.PointD;

/**
 * Interface to provide a location for a given element, as a PointD with longitude, latitude coordinates.
 * 
 */
public interface LocationProvider {
	/*
	 * Returns the longitude, latitude for the element
	 * or null if this elements doesn't have a location.
	 */
	public PointD getLonLat(Object element);

	/*
	 * Set the longitude, latitude for the element.
	 * Returns true if the change occurred, i.e. the operation was legal.
	 * Use setLonLat(element, getLon(element), getLat(element)) to check without side-effect.
	 */
	public boolean setLonLat(Object element, double lon, double lat);
}
