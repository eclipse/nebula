/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http\://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors\:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.geomap;

/**
 * This class encapsulates a tileserver, which has the concept
 * of a baseurl and a maximum zoon level. 
 */
public final class OsmTileServer extends TileServer {

    public OsmTileServer(String url, int maxZoom) {
    	super(url, maxZoom);
    }

    public static final OsmTileServer[] TILESERVERS = {
        new OsmTileServer("http://tile.openstreetmap.org/", 18),
        new OsmTileServer("http://tah.openstreetmap.org/Tiles/tile/", 17),
    };
}
