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

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.nebula.widgets.geomap.internal.TileRef;
import org.eclipse.nebula.widgets.geomap.internal.URLService;

/**
 * This class encapsulates a tileserver, which has the concept of a baseurl and
 * a distinct range of zoom levels.
 */
public class TileServer extends URLService {

	private String urlFormat = "{0}/{1}/{2}.png"; // slippy format //$NON-NLS-1$
													// z/x/y, must match
													// getURLFormatArguments
	private final int minZoom, maxZoom;

	// See https://raw.github.com/follesoe/MapReplace/master/js/interceptors.js
	// for a list of tile servers

	/**
	 * Initializes a TileServer
	 * 
	 * @param url
	 *            the base url of the TileServer
	 * @param minZoom
	 *            the min zoom level supported by this TileServer
	 * @param maxZoom
	 *            the max zoom level supported by this TileServer
	 * @param urlFormat
	 *            the format of the url parameters that are appended to the base url
	 */
	public TileServer(String url, int minZoom, int maxZoom, String urlFormat) {
		super(url, urlFormat);
		this.minZoom = minZoom;
		this.maxZoom = maxZoom;
	}

	/**
	 * Initializes a TileServer using the default slippy format
	 * 
	 * @param url
	 *            the base url of the TileServer
	 * @param maxZoom
	 *            the max zoom level supported by this TileServer
	 */
	public TileServer(String url, int maxZoom) {
		parseUrl(url, "{0}/{1}/{2}.png"); //$NON-NLS-1$
		this.minZoom = 0;
		this.maxZoom = maxZoom;
	}

	@Override
	protected Object[] getURLFormatArguments(Object ref) {
		TileRef tile = (TileRef) ref;
		return new Object[] { String.valueOf(tile.z), String.valueOf(tile.x), String.valueOf(tile.y) };
	}

	@SuppressWarnings("javadoc")
	protected Map<String, String> createZXYMap(TileRef tile, String zKey, String xKey, String yKey) {
		Map<String, String> formatMap = new HashMap<>();
		formatMap.put(zKey, String.valueOf(tile.z));
		formatMap.put(xKey, String.valueOf(tile.x));
		formatMap.put(yKey, String.valueOf(tile.y));
		return formatMap;
	}

	@SuppressWarnings("javadoc")
	protected Map<String, String> getURLFormatMap(TileRef tile) {
		return createZXYMap(tile, "{0}", "{1}", "{2}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@SuppressWarnings("javadoc")
	protected String getTileURL(TileRef tile, String urlFormat, Object[] formatArguments) {
		return getURL() + MessageFormat.format(urlFormat, formatArguments);
	}

	@SuppressWarnings("javadoc")
	protected String getTileURL(TileRef tile, String urlFormat, Map<String, String> formatMap) {
		for (String key : formatMap.keySet()) {
			urlFormat = urlFormat.replace(key, formatMap.get(key));
		}
		return getURL() + urlFormat;
	}

	@SuppressWarnings("javadoc")
	public String getTileURL(TileRef tile) {
		if (urlFormat != null) {
			Object[] urlFormatArguments = getURLFormatArguments(tile);
			if (urlFormatArguments != null) {
				return getTileURL(tile, urlFormat, urlFormatArguments);
			}
			Map<String, String> urlFormatMap = getURLFormatMap(tile);
			if (urlFormatMap != null) {
				return getTileURL(tile, urlFormat, urlFormatMap);
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return getURL();
	}

	/**
	 * Gets the min zoom level supported by this TileServer
	 * 
	 * @return the min zoom level
	 */
	public int getMinZoom() {
		return minZoom;
	}

	/**
	 * Gets the max zoom level supported by this TileServer
	 * 
	 * @return the max zoom level
	 */
	public int getMaxZoom() {
		return maxZoom;
	}
}
