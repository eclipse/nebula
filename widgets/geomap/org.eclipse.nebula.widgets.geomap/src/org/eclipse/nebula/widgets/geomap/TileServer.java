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
import java.text.MessageFormat;

/**
 * This class encapsulates a tileserver, which has the concept
 * of a baseurl and a maximum zoom level. 
 */
public class TileServer {

    private String url;
    private String urlFormat = "{0}/{1}/{2}.png"; // slippy format, must match getURLFormatArguments
    private final int maxZoom;
    private boolean broken;

    private void parseUrl(String url) {
		int pos = url.indexOf("{");
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

    protected Object[] getURLFormatArguments(TileRef tile) {
    	return new Object[]{String.valueOf(tile.z), String.valueOf(tile.x), String.valueOf(tile.y)};
    }
    
    protected String getTileURL(TileRef tile, String urlFormat, Object[] formatArguments) {
    	return url + MessageFormat.format(urlFormat, formatArguments);
    }
    
    public String getTileURL(TileRef tile) {
    	return (urlFormat != null ? getTileURL(tile, urlFormat, getURLFormatArguments(tile)) : null);
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