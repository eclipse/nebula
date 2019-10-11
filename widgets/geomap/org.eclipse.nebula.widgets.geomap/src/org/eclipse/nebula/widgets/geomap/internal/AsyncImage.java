/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors\:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.geomap.internal;

import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

/**
 * An async image that loads itself in the background on an image-fetcher
 * thread. Once its loaded it will trigger a redraw. Sometimes redraws that are
 * not really necessary can be triggered, but that is not relevant in terms of
 * performance for this swt-component.
 *
 */
@SuppressWarnings("serial")
class AsyncImage extends AtomicReference<ImageData> implements Runnable {

	private final GeoMapHelper geoMapHelper;
	private final TileRef tile;
	private final String tileUrl;

	private volatile long stamp;
	// private final AtomicReference<ImageData> imageData = new
	// AtomicReference<ImageData>();
	private Image image = null; // might as well be thread-local

	AsyncImage(GeoMapHelper geoMapHelper, TileRef tile, String tileUrl) {
		this.geoMapHelper = geoMapHelper;
		this.stamp = this.geoMapHelper.zoomStamp.longValue();
		this.tile = tile;
		this.tileUrl = tileUrl;
		this.geoMapHelper.executor
				.execute(new FutureTask<>(this, Boolean.TRUE));
	}

	private Runnable removeTileFromCacheRunnable = new Runnable() {
		public void run() {
			AsyncImage.this.geoMapHelper.cache.remove(tile);
		}
	};
	private Runnable tileUpdatedRunnable = new Runnable() {
		public void run() {
			AsyncImage.this.geoMapHelper.tileUpdated(tile);
		}
	};

	@Override
	public void run() {
		if (stamp != this.geoMapHelper.zoomStamp.longValue()) {
			try {
				// here is a race, we just live with.
				if (!this.geoMapHelper.getDisplay().isDisposed()) {
					this.geoMapHelper.getDisplay()
							.asyncExec(removeTileFromCacheRunnable);
				}
			} catch (SWTException e) {
				// ignore
			}
			return;
		}
		try {
			URLConnection con = new URL(tileUrl).openConnection();
			con.setRequestProperty("User-Agent", //$NON-NLS-1$
					"org.eclipse.nebula.widgets.geomap.GeoMap"); //$NON-NLS-1$
			set(new ImageData(con.getInputStream()));
			try {
				// here is a race, we just live with.
				if (!this.geoMapHelper.getDisplay().isDisposed()) {
					this.geoMapHelper.getDisplay()
							.asyncExec(tileUpdatedRunnable);
				}
			} catch (SWTException e) {
				// ignore
			}
		} catch (Exception e) {
			// log.log(Level.SEVERE, "failed to load imagedata from url: " +
			// tileUrl, e);
		}
	}

	public Image getImage(Display display) {
		checkThread(display);
		if (image == null && get() != null) {
			image = new Image(display, get());
		}
		return image;
	}

	public void dispose() {
		checkThread(geoMapHelper.getDisplay());
		if (image != null) {
			image.dispose();
			image = null;
		}
	}

	private void checkThread(Display display) {
		// jdk 1.6 bug from checkWidget still fails here
		if (display.getThread() != Thread.currentThread()) {
			throw new IllegalStateException(
					"Wrong thread to pick up the image"); //$NON-NLS-1$
		}
	}
}