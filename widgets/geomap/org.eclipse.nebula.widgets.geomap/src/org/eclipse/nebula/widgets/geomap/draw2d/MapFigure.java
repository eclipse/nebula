/*******************************************************************************
 * Copyright (c) 2015 Hallvard Trætteberg.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Hallvard Trætteberg - initial implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.geomap.draw2d;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.nebula.widgets.geomap.GeoMapUtil;
import org.eclipse.nebula.widgets.geomap.PointD;
import org.eclipse.nebula.widgets.geomap.TileServer;
import org.eclipse.nebula.widgets.geomap.internal.GeoMapHelper;
import org.eclipse.nebula.widgets.geomap.internal.GeoMapHelperListener;
import org.eclipse.nebula.widgets.geomap.internal.TileRef;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * An ImageFigure that creates the image from tiles fetched using a GeoMapHelper
 * 
 * @since 3.3
 *
 */
public class MapFigure extends ImageFigure implements GeoMapHelperListener {

	private static int DEFAULT_CACHE_SIZE = 256;

	private GeoMapHelper geoMapHelper;

	private TileServer tileServer;
	private PointD location = null;
	private int zoom = 10;

	/**
	 * Sets the TileServer used for fetching tiles.
	 * 
	 * @param tileServer
	 *            The tileServer to set.
	 */
	public void setTileServer(TileServer tileServer) {
		this.tileServer = tileServer;
		invalidateImage();
	}

	/**
	 * @param zoom
	 *            The zoomLevel to set.
	 */
	public void setZoomLevel(int zoom) {
		this.zoom = zoom;
		invalidateImage();
	}

	/**
	 * Sets the location as a pair of longitude/latitude values
	 * 
	 * @param longitude
	 * @param latitude
	 */
	public void setLocation(double longitude, double latitude) {
		location = new PointD(longitude, latitude);
		invalidateImage();
	}

	private Image cachedImage = null;

	private void invalidateImage() {
		Image oldImage = getImage();
		if (oldImage != null) {
			setImage(null);
		}
		if (cachedImage == null) {
			cachedImage = oldImage;
		}
	}

	private Display display;

	private Display getDisplay() {
		if (display == null) {
			display = Display.getCurrent();
		}
		return display;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.draw2d.Figure#paint(org.eclipse.draw2d.Graphics)
	 */
	@Override
	public void paint(Graphics graphics) {
		if (getImage() == null) {
			updateImage();
		}
		super.paint(graphics);
	}

	private void updateImage() {
		updateGeoMapHelper();
		if (geoMapHelper != null) {
			Dimension size = getSize();
			if (cachedImage != null) {
				Rectangle imageSize = cachedImage.getBounds();
				if (imageSize.width != size.width
						|| imageSize.height != size.height) {
					cachedImage.dispose();
					cachedImage = null;
				}
			}
			Image image = cachedImage;
			if (image == null) {
				image = new Image(getDisplay(), size.width, size.height);
			}
			cachedImage = null;
			GC gc = new GC(image);
			geoMapHelper.paint(gc, null, new Point(size.width, size.height));
			gc.dispose();
			setImage(image);
		}
	}

	private void updateGeoMapHelper() {
		if (location != null) {
			Point position = GeoMapUtil.computePosition(location, zoom);
			if (geoMapHelper == null) {
				geoMapHelper = new GeoMapHelper(getDisplay(), position, zoom,
						DEFAULT_CACHE_SIZE);
				geoMapHelper.addGeoMapHelperListener(this);
			}
			if (tileServer != null) {
				geoMapHelper.setTileServer(tileServer);
			}
			geoMapHelper.setZoom(zoom);
			geoMapHelper.setMapPosition(position.x, position.y);
		}
	}

	@Override
	public void tileUpdated(TileRef tileRef) {
		invalidateImage();
	}

	// standalone example

	/**
	 * Minimal standalone example, used for testing
	 * 
	 * @param args
	 */
	@SuppressWarnings("nls")
	public static void main(String[] args) {

		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		FigureCanvas canvas = new FigureCanvas(shell);
		MapFigure mapFigure = new MapFigure();
		mapFigure.setZoomLevel(8);
		mapFigure.setLocation(10.4234, 63.4242);
		canvas.setContents(mapFigure);

		shell.setText("MapFigure example");
		shell.setSize(600, 600);
		shell.open();

		while (!shell.isDisposed()) {
			while (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
