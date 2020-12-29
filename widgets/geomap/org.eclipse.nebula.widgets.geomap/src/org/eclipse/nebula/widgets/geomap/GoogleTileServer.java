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

package org.eclipse.nebula.widgets.geomap;

/**
 * This class encapsulates a tileserver, which has the concept of a baseurl and
 * a maximum zoon level.
 */
public final class GoogleTileServer extends TileServer {

	/**
	 * Initializes a Google maps TileServer
	 * 
	 * @param url
	 *            the url to the server
	 * @param maxZoom
	 *            the max zoom level supported by this server
	 */
	public GoogleTileServer(String url, int maxZoom) {
		super(url, 0, maxZoom, "&z={0}&x={1}&y={2}"); //$NON-NLS-1$
	}

	/**
	 * A couple of default Google tile servers
	 */
	public static final GoogleTileServer[] TILESERVERS = {
			new GoogleTileServer(
					"http://mt1.google.com/vt/lyrs=m@129&hl=en&s=Galileo", 18), //$NON-NLS-1$
			new GoogleTileServer(
					"http://mt2.google.com/vt/lyrs=m@129&hl=en&s=Galileo", 18), //$NON-NLS-1$
	};
}
