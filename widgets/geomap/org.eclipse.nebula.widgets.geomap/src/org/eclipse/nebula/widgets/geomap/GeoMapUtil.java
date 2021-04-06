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

import org.eclipse.nebula.widgets.geomap.internal.GeoMapPositioned;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * @since 3.3
 *
 */
public class GeoMapUtil {

    /* must be the same as GeoMapHelper's TILE_SIZE */
    private static final int TILE_SIZE = 256;

    /**
     * Returns the pair of longitude, latitude for a position at a certain zoom
     * level
     * 
     * @param position the position
     * @param zoom     the zoom level
     * @return the pair of longitude, latitude as a PointD
     */
    public static PointD getLongitudeLatitude(Point position, int zoom) {
	return new PointD(GeoMapUtil.position2lon(position.x, zoom), GeoMapUtil.position2lat(position.y, zoom));
    }

    /**
     * Returns the position at a certain zoom level for a pair of longitude,
     * latitude
     * 
     * @param coords the pair of longitude, latitude
     * @param zoom   the zoom level
     * @return the position as a Point
     */
    public static Point computePosition(PointD coords, int zoom) {
	int x = GeoMapUtil.lon2position(coords.x, zoom);
	int y = GeoMapUtil.lat2position(coords.y, zoom);
	return new Point(x, y);
    }

    /**
     * Converts position to longitude.
     * 
     * @param x position x coord (pixels in this swt control)
     * @param z the current zoom level.
     * @return the longitude
     */
    public static double position2lon(int x, int z) {
	double xmax = TILE_SIZE * (1 << z);
	return x / xmax * 360.0 - 180;
    }

    /**
     * Converts position to latitude.
     * 
     * @param y position y coord (pixels in this swt control)
     * @param z the current zoom level.
     * @return the latitude
     */
    public static double position2lat(int y, int z) {
	double ymax = TILE_SIZE * (1 << z);
	return Math.toDegrees(Math.atan(Math.sinh(Math.PI - 2.0 * Math.PI * y / ymax)));
    }

    /**
     * Converts longitude to x position.
     * 
     * @param lon the longitude
     * @param z   the zoom level.
     * @return the x position
     */
    public static int lon2position(double lon, int z) {
	double xmax = TILE_SIZE * (1 << z);
	return (int) Math.floor((lon + 180) / 360 * xmax);
    }

    /**
     * Converts latitude to y position.
     * 
     * @param lat the latitude
     * @param z   the zoom level.
     * @return the y position
     */
    public static int lat2position(double lat, int z) {
	double ymax = TILE_SIZE * (1 << z);
	return (int) Math.floor(
		(1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * ymax);
    }

    // helper methods operating on position and/or zoom

    /**
     * Translates the position of the upper left corner of this GeoMap, without any
     * panning effect.
     * 
     * @param geoMap the geoMap
     * @param tx     the relative distance in x-direction
     * @param ty     the relative distance in y-direction
     */
    public static void translateMapPosition(GeoMapPositioned geoMap, int tx, int ty) {
	Point mapPosition = geoMap.getMapPosition();
	geoMap.setMapPosition(mapPosition.x + tx, mapPosition.y + ty);
    }

    /**
     * Zooms in, while ensuring that the pivot point remains at the same screen
     * location.
     * 
     * @param geoMap the geoMap
     * @param pivot  the point that will remain at the same screen location.
     */
    public static void zoomIn(GeoMapPositioned geoMap, Point pivot) {
	if (geoMap.getZoom() >= geoMap.getMaxZoom()) {
	    return;
	}
	Point mapPosition = geoMap.getMapPosition();
	int dx = pivot.x;
	int dy = pivot.y;
	geoMap.setMapPosition(mapPosition.x * 2 + dx, mapPosition.y * 2 + dy);
	geoMap.setZoom(geoMap.getZoom() + 1);
    }

    /**
     * Zooms out, while ensuring that the pivot point remains at the same screen
     * location.
     * 
     * @param geoMap the geoMap
     * @param pivot  the point that will remain at the same screen location.
     */
    public static void zoomOut(GeoMapPositioned geoMap, Point pivot) {
	if (geoMap.getZoom() <= geoMap.getMinZoom()) {
	    return;
	}
	Point mapPosition = geoMap.getMapPosition();
	int dx = pivot.x;
	int dy = pivot.y;
	geoMap.setMapPosition((mapPosition.x - dx) / 2, (mapPosition.y - dy) / 2);
	geoMap.setZoom(geoMap.getZoom() - 1);
    }

    static private int zoomMargin = 10;

    /**
     * Zooms into and centers on the specified rectangle.
     * 
     * @param geoMap  the geoMap
     * @param mapSize the size of the map, containes the zoom rectangle
     * @param rect    the rectangle
     * @param maxZoom the maximum level to zoom to, or -1 to default to the geoMap's
     *                max zoom
     */
    public static void zoomTo(GeoMapPositioned geoMap, Point mapSize, Rectangle rect, int maxZoom) {
	Rectangle zoomRectangle = new Rectangle(rect.x, rect.y, rect.width, rect.height);
	// don't try to zoom if the map is too small, e.g. if the map hasn't
	// been layed out
	if (mapSize.x < zoomMargin || mapSize.y < zoomMargin) {
	    return;
	}
	if (maxZoom < geoMap.getMinZoom()) {
	    maxZoom = geoMap.getMaxZoom();
	}
	int zoom = geoMap.getZoom();
	// compute zoom by zooming out until the rectangle fits within the
	// viewport
	while (zoom > geoMap.getMinZoom() && (mapSize.x < zoomRectangle.width + zoomMargin
		|| mapSize.y < zoomRectangle.height + zoomMargin || zoom > maxZoom)) {
	    // zoom out and scale zoom rectangle down
	    zoom--;
	    zoomRectangle.x /= 2;
	    zoomRectangle.y /= 2;
	    zoomRectangle.width /= 2;
	    zoomRectangle.height /= 2;
	}
	// compute zoom by zooming in as long as the rectangle will fit within
	// the viewport
	while (mapSize.x > zoomRectangle.width * 2 + zoomMargin && mapSize.y > zoomRectangle.height * 2 + zoomMargin
		&& zoom < maxZoom) {
	    // zoom in and scale zoom rectangle up
	    zoom++;
	    zoomRectangle.x *= 2;
	    zoomRectangle.y *= 2;
	    zoomRectangle.width *= 2;
	    zoomRectangle.height *= 2;
	}
	geoMap.setMapPosition(zoomRectangle.x + (zoomRectangle.width - mapSize.x) / 2,
		zoomRectangle.y + (zoomRectangle.height - mapSize.y) / 2);
	geoMap.setZoom(zoom);
    }
}
