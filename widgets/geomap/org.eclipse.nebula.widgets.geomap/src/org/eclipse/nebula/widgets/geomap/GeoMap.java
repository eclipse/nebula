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

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import org.eclipse.nebula.widgets.geomap.internal.DefaultMouseHandler;
import org.eclipse.nebula.widgets.geomap.internal.GeoMapHelper;
import org.eclipse.nebula.widgets.geomap.internal.InternalGeoMap;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

/**
 * GeoMap display tiles from openstreetmap as is. This simple minimal viewer
 * supports zoom around mouse-click center and has a simple api. A number of
 * tiles are cached. If you use this it will create traffic on the tileserver
 * you are using. Please be conscious about this.
 *
 * This class is a JPanel which can be integrated into any swing app just by
 * creating an instance and adding like a JLabel.
 *
 * The map has the size <code>256*1<<zoomlevel</code>. This measure is referred
 * to as map-coordinates. Geometric locations like longitude and latitude can be
 * obtained by helper methods. Note that a point in map-coordinates corresponds
 * to a given geometric position but also depending on the current zoom level.
 *
 * You can zoomIn around current mouse position by left double click. Left right
 * click zooms out.
 *
 * <p>
 * Methods of interest are
 * <ul>
 * <li>{@link #setZoom(int)} which sets the map's zoom level. Values between 1
 * and 18 are allowed.</li>
 * <li>{@link #setMapPosition(Point)} which sets the map's top left corner. (In
 * map coordinates)</li>
 * <li>{@link #setCenterPosition(Point)} which sets the map's center position.
 * (In map coordinates)</li> for the given longitude and latitude. If you want
 * to center the map around this geometric location you need to pass the result
 * to the method</li>
 * </ul>
 * </p>
 *
 * <p>
 * For performance tuning the two crucial parameters are the size of the tile
 * cache and the number of image-loader threads.
 * </p>
 *
 * </p>
 *
 * <p>
 * License is EPL (Eclipse Public License)
 * https://www.eclipse.org/legal/epl-2.0/. Contact at stepan.rutz@gmx.de
 * </p>
 *
 * @author stepan.rutz
 * @version $Revision$
 */

public class GeoMap extends InternalGeoMap {

	/**
	 * About message.
	 */
	@SuppressWarnings({ "nls" })
	public static final String ABOUT_MSG = "GeoMap - Minimal Openstreetmap/Maptile Viewer\r\n"
			+ "Requirements: Java + SWT. Opensource and licensed under EPL.\r\n" + "\r\n"
			+ "Web/Source: <a href=\"http://eclipse.org/nebula\">http://eclipse.org/nebula</a>\r\n"
			+ "Written by Stephan Rutz. Maintained by the Eclipse Nebula Project.\r\n\r\n"
			+ "Tileserver and Nominationserver are accessed online and are part of Openstreetmap.org and not of this software.\r\n";

	private Point mouseCoords = new Point(0, 0);
	private DefaultMouseHandler defaultMouseHandler = new DefaultMouseHandler(this) {
		@Override
		public Point getMapSize() {
			return getSize();
		}
	};

	private static final int DEFAULT_CACHE_SIZE = 256;

	/**
	 * Creates a new <code>GeoMap</code> using the default size for its internal
	 * cache of tiles. The map is showing the position <code>(275091, 180145</code>
	 * at zoom level <code>11</code>. In other words this constructor is best used
	 * only in debugging scenarios.
	 * 
	 * @param parent
	 *            SWT parent <code>Composite</code>
	 * @param style
	 *            SWT style as in <code>Canvas</code>, since this class inherits
	 *            from it. Double buffering is always enabed.
	 */
	public GeoMap(Composite parent, int style) {
		this(parent, style, new Point(275091, 180145), 11);
	}

	/**
	 * Creates a new <code>GeoMap</code> using the default size for its internal
	 * cache of tiles
	 * 
	 * @param parent
	 *            SWT parent <code>Composite</code>
	 * @param style
	 *            SWT style as in <code>Canvas</code>, since this class inherits
	 *            from it. Double buffering is always enabed.
	 * @param mapPosition
	 *            initial mapPosition.
	 * @param zoom
	 *            initial map zoom
	 */
	public GeoMap(Composite parent, int style, Point mapPosition, int zoom) {
		this(parent, style, mapPosition, zoom, DEFAULT_CACHE_SIZE);
	}

	private class MouseCoordsHandler implements MouseListener, MouseMoveListener {

		private void setMouseCoords(MouseEvent e) {
			mouseCoords = new Point(e.x, e.y);
		}

		@Override
		public void mouseDown(MouseEvent e) {
			setMouseCoords(e);
		}

		@Override
		public void mouseMove(MouseEvent e) {
			setMouseCoords(e);
		}

		@Override
		public void mouseUp(MouseEvent e) {
			setMouseCoords(e);
		}

		@Override
		public void mouseDoubleClick(MouseEvent e) {
			setMouseCoords(e);
		}
	}

	/**
	 * Creates a new <code>GeoMap</code> using the default size for its internal
	 * cache of tiles
	 * 
	 * @param parent
	 *            SWT parent <code>Composite</code>
	 * @param style
	 *            SWT style as in <code>Canvas</code>, since this class inherits
	 *            from it. Double buffering is always enabed.
	 * @param mapPosition
	 *            initial mapPosition.
	 * @param zoom
	 *            initial map zoom
	 * @param cacheSize
	 *            initial cache size, eg number of tile-images that are kept in
	 *            cache to prevent reloading from the network.
	 */
	public GeoMap(Composite parent, int style, Point mapPosition, int zoom, int cacheSize) {
		super(parent, style, mapPosition, zoom, cacheSize);
		MouseCoordsHandler mouseCoordsHandler = new MouseCoordsHandler();
		addMouseListener(mouseCoordsHandler);
		addMouseMoveListener(mouseCoordsHandler);
		addMouseHandler(defaultMouseHandler);
	}

	/**
	 * Returns the default mouse handler, so it may be configured or removed.
	 * 
	 * @return the default mouse handler
	 */
	public DefaultMouseHandler getDefaultMouseHandler() {
		return defaultMouseHandler;
	}

	/**
	 * Adds listener to appropriate listener lists depending on the listener
	 * interfaces that are implemented.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addMouseHandler(EventListener listener) {
		if (listener instanceof MouseListener) {
			addMouseListener((MouseListener) listener);
		}
		if (listener instanceof MouseMoveListener) {
			addMouseMoveListener((MouseMoveListener) listener);
		}
		if (listener instanceof MouseTrackListener) {
			addMouseTrackListener((MouseTrackListener) listener);
		}
		if (listener instanceof MouseWheelListener) {
			addMouseWheelListener((MouseWheelListener) listener);
		}
		if (listener instanceof PaintListener) {
			addPaintListener((PaintListener) listener);
		}
	}

	/**
	 * Removes listener from appropriate listener lists depending on the listener
	 * interfaces that are implemented.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void removeMouseHandler(EventListener listener) {
		if (listener instanceof MouseListener) {
			removeMouseListener((MouseListener) listener);
		}
		if (listener instanceof MouseMoveListener) {
			removeMouseMoveListener((MouseMoveListener) listener);
		}
		if (listener instanceof MouseTrackListener) {
			removeMouseTrackListener((MouseTrackListener) listener);
		}
		if (listener instanceof MouseWheelListener) {
			removeMouseWheelListener((MouseWheelListener) listener);
		}
		if (listener instanceof PaintListener) {
			removePaintListener((PaintListener) listener);
		}
	}

	//

	/**
	 * Returns the current TileServer of this GeoMap.
	 * 
	 * @return the current TileServer
	 */
	public TileServer getTileServer() {
		return geoMapHelper.getTileServer();
	}

	/**
	 * Sets the current TileServer of this GeoMap. Note that this will clear the map
	 * and reload the tiles using the new TileServer.
	 * 
	 * @param tileServer
	 *            the TileServer
	 */
	public void setTileServer(TileServer tileServer) {
		geoMapHelper.setTileServer(tileServer);
	}

	//

	private List<GeoMapListener> geoMapListeners;

	/**
	 * Adds a GeoMapListener, that will be notified of changes to the position and
	 * zoom level
	 * 
	 * @param listener
	 *            the GeoMapListener
	 */
	public void addGeoMapListener(GeoMapListener listener) {
		if (geoMapListeners == null) {
			geoMapListeners = new ArrayList<>();
		}
		geoMapListeners.add(listener);
	}

	/**
	 * Removes a GeoMapListener, so it no longer will be notified of changes to the
	 * position and zoom level
	 * 
	 * @param listener
	 *            the GeoMapListener
	 */
	public void removeGeoMapListener(GeoMapListener listener) {
		if (geoMapListeners != null) {
			geoMapListeners.remove(listener);
		}
	}

	private void fireCenterChanged() {
		if (geoMapListeners != null) {
			for (GeoMapListener listener : geoMapListeners) {
				listener.centerChanged(this);
			}
		}
	}

	private void fireZoomChanged() {
		if (geoMapListeners != null) {
			for (GeoMapListener listener : geoMapListeners) {
				listener.zoomChanged(this);
			}
		}
	}

	//

	/**
	 * Sets the position of the upper left corner of this GeoMap, without any
	 * panning effect.
	 * 
	 * @param mapPosition
	 *            the new position
	 */
	public void setMapPosition(Point mapPosition) {
		setMapPosition(mapPosition.x, mapPosition.y);
	}

	/**
	 * Sets the position of the upper left corner of this GeoMap, without any
	 * panning effect.
	 * 
	 * @param x
	 *            the x-coordinate of the position
	 * @param y
	 *            the y-coordinate of the position
	 */
	@Override
	public void setMapPosition(int x, int y) {
		super.setMapPosition(x, y);
		fireCenterChanged();
	}

	/**
	 * Translates the position of the upper left corner of this GeoMap, without any
	 * panning effect.
	 * 
	 * @param tx
	 *            the relative distance in x-direction
	 * @param ty
	 *            the relative distance in y-direction
	 */
	public void translateMapPosition(int tx, int ty) {
		GeoMapUtil.translateMapPosition(this, tx, ty);
	}

	/**
	 * Sets the zoom level, without any transition effect.
	 * 
	 * @param zoom
	 *            the new zoom level
	 */
	@Override
	public void setZoom(int zoom) {
		super.setZoom(zoom);
		fireZoomChanged();
	}

	/**
	 * Zooms in, while ensuring that the pivot point remains at the same screen
	 * location.
	 * 
	 * @param pivot
	 *            the point that will remain at the same screen location.
	 */
	public void zoomIn(Point pivot) {
		GeoMapUtil.zoomIn(this, pivot);
		redraw();
	}

	/**
	 * Zooms out, while ensuring that the pivot point remains at the same screen
	 * location.
	 * 
	 * @param pivot
	 *            the point that will remain at the same screen location.
	 */
	public void zoomOut(Point pivot) {
		GeoMapUtil.zoomOut(this, pivot);
		redraw();
	}

	/**
	 * Zooms into and centers on the specified rectangle.
	 * 
	 * @param rect
	 *            the rectangle
	 */
	public void zoomTo(Rectangle rect) {
		GeoMapUtil.zoomTo(this, getSize(), rect, -1);
		redraw();
	}

	/**
	 * Returns the position of the center of this GeoMap. The coordinates depend on
	 * the zoom level.
	 * 
	 * @return the position of the center of this GeoMap
	 */
	public Point getCenterPosition() {
		org.eclipse.swt.graphics.Point size = getSize();
		Point mapPosition = getMapPosition();
		return new Point(mapPosition.x + size.x / 2, mapPosition.y + size.y / 2);
	}

	/**
	 * Sets the position of the center of this GeoMap, without any panning effect.
	 * 
	 * @param mapPosition
	 *            the new position
	 */
	public void setCenterPosition(Point mapPosition) {
		org.eclipse.swt.graphics.Point size = getSize();
		setMapPosition(mapPosition.x - size.x / 2, mapPosition.y - size.y / 2);
	}

	/**
	 * Returns the map position of the mouse cursor.
	 * 
	 * @return the map position of the mouse cursor
	 */
	public Point getCursorPosition() {
		Point mapPosition = getMapPosition();
		return new Point(mapPosition.x + mouseCoords.x, //
				mapPosition.y + mouseCoords.y);
	}

	public Point computeSize(int wHint, int hHint, boolean changed) {
		int widthHint, heightHint;
		if (wHint == SWT.DEFAULT) {
			widthHint = GeoMapHelper.TILE_SIZE * 3; // Arbitrary size
		} else {
			widthHint = wHint;
		}
		if (hHint == SWT.DEFAULT) {
			heightHint = GeoMapHelper.TILE_SIZE * 3; // Arbitrary size
		} else {
			heightHint = hHint;
		}

		return super.computeSize(widthHint, heightHint, changed);
	}

	public Point computeSize(int wHint, int hHint) {
		return computeSize(wHint, hHint, true);
	}

}
