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

import java.net.URL;

/**
 * This class encapsulates a tileserver, which has the concept
 * of a baseurl and a maximum zoon level. 
 */
public final class GoogleTileServer extends TileServer {

    public GoogleTileServer(String url, int maxZoom) {
    	super(url, maxZoom, "&z={0}&x={1}&y={2}");
    }

    public static final GoogleTileServer[] TILESERVERS = {
        new GoogleTileServer("http://mt1.google.com/vt/lyrs=m@129&hl=en&s=Galileo", 18),
        new GoogleTileServer("http://mt2.google.com/vt/lyrs=m@129&hl=en&s=Galileo", 18),
    };
}
