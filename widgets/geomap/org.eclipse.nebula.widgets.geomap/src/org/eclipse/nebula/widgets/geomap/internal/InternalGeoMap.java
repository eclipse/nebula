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

package org.eclipse.nebula.widgets.geomap.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/**
 * <p>
 * License is EPL (Eclipse Public License)
 * http://www.eclipse.org/legal/epl-v10.html. Contact at stepan.rutz@gmx.de
 * </p>
 *
 * @author stepan.rutz, hal
 * @version $Revision$
 */
public class InternalGeoMap extends Canvas implements GeoMapPositioned, GeoMapHelperListener {

	void redraw(TileRef tile) {
		redraw();
	}

	/**
	 * The helper object for loading images.
	 */
	protected GeoMapHelper geoMapHelper;

	/**
	 * Initializes a new <code>InternalGeoMap</code>.
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
	protected InternalGeoMap(Composite parent, int style, Point mapPosition, int zoom, int cacheSize) {
		super(parent, SWT.DOUBLE_BUFFERED | style);
		geoMapHelper = new GeoMapHelper(parent.getDisplay(), mapPosition, zoom, cacheSize);
		geoMapHelper.addGeoMapHelperListener(this);

		addDisposeListener(e -> InternalGeoMap.this.geoMapHelper.dispose());
		addPaintListener(e -> InternalGeoMap.this.paintControl(e));
	}

	@Override
	public void tileUpdated(TileRef tileRef) {
		if (!isDisposed()) {
			redraw();
		}
	}

	private void paintControl(PaintEvent e) {
		geoMapHelper.paint(e.gc, new Rectangle(e.x, e.y, e.width, e.height), getSize());
	}

	/**
	 * Adds an InternalGeoMapListener
	 * 
	 * @param listener
	 */
	public void addInternalGeoMapListener(InternalGeoMapListener listener) {
		geoMapHelper.addInternalGeoMapListener(listener);
	}

	/**
	 * Removes an InternalGeoMapListener
	 * 
	 * @param listener
	 */
	public void removeInternalGeoMapListener(InternalGeoMapListener listener) {
		geoMapHelper.removeInternalGeoMapListener(listener);
	}

	// GeoMapPositioned methods

	/**
	 * @see org.eclipse.nebula.widgets.geomap.internal.GeoMapPositioned#getMapPosition()
	 */
	@Override
	public Point getMapPosition() {
		return geoMapHelper.getMapPosition();
	}

	/**
	 * @see org.eclipse.nebula.widgets.geomap.internal.GeoMapPositioned#setMapPosition(int, int)
	 */
	@Override
	public void setMapPosition(int x, int y) {
		geoMapHelper.setMapPosition(x, y);
		redraw();
	}

	/**
	 * @see org.eclipse.nebula.widgets.geomap.internal.GeoMapPositioned#getZoom()
	 */
	@Override
	public int getZoom() {
		return geoMapHelper.getZoom();
	}

	/**
	 * @see org.eclipse.nebula.widgets.geomap.internal.GeoMapPositioned#getMinZoom()
	 */
	@Override
	public int getMinZoom() {
		return geoMapHelper.getMinZoom();
	}

	/**
	 * @see org.eclipse.nebula.widgets.geomap.internal.GeoMapPositioned#getMaxZoom()
	 */
	@Override
	public int getMaxZoom() {
		return geoMapHelper.getMaxZoom();
	}

	/**
	 * @see org.eclipse.nebula.widgets.geomap.internal.GeoMapPositioned#setZoom(int)
	 */
	@Override
	public void setZoom(int zoom) {
		if (zoom == getZoom()) {
			return;
		}
		geoMapHelper.setZoom(zoom);
		redraw();
	}
}
