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
 * of a baseurl and a maximum zoom level. 
 */
public class TileServer {

    private String url;
    private String urlFormat = "${z}/${x}/${y}.png"; // slippy format
    private final int maxZoom;
    private boolean broken;

    private void parseUrl(String url) {
		int pos = url.indexOf("${");
		if (pos > 0) {
			this.url = url.substring(0, pos);
			this.urlFormat = url.substring(pos);
		} else {
			this.url = url; 
		}
    }

    public TileServer(String url, int maxZoom, String urlFormat) {
    	this.url = url;
        this.maxZoom = maxZoom;
        this.urlFormat = urlFormat;
    }

    public TileServer(String url, int maxZoom) {
    	parseUrl(url);
    	this.maxZoom = maxZoom;
    }

    protected String getTileURL(TileRef tile, String urlFormat) {
    	return url + (urlFormat
    			.replace("${z}", String.valueOf(tile.z))
    			.replace("${x}", String.valueOf(tile.x))
    			.replace("${y}", String.valueOf(tile.y)));
    }
    
    public String getTileURL(TileRef tile) {
    	return (urlFormat != null ? getTileURL(tile, urlFormat) : null);
    }
    
    public String toString() {
        return url;
    }

    public int getMaxZoom() {
        return maxZoom;
    }
    public String getURL() {
        return url;
    }

    public boolean isBroken() {
        return broken;
    }

    public void setBroken(boolean broken) {
        this.broken = broken;
    }
}