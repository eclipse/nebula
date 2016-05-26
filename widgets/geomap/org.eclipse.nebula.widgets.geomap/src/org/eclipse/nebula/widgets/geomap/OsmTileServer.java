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

package org.eclipse.nebula.widgets.geomap;

/**
 * This class encapsulates a tileserver, which has the concept
 * of a baseurl and a maximum zoom level. 
 */
public final class OsmTileServer extends TileServer {

	/**
	 * Initializes an Open Streat Map TileServer
	 * @param url the url to the server
	 * @param maxZoom the max zoom level supported by this server
	 */
    public OsmTileServer(String url, int maxZoom) {
    	super(url, maxZoom);
    }

    /**
     * A default OSM tile server
     */
    public static final OsmTileServer[] TILESERVERS = {
        new OsmTileServer("http://a.tile.openstreetmap.org/", 18), //$NON-NLS-1$
    };
}
