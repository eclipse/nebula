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
 *******************************************************************************/

package org.eclipse.nebula.widgets.geomap.internal.geomapbrowser;

import java.text.NumberFormat;
import java.util.logging.Logger;

import org.eclipse.nebula.widgets.geomap.GeoMap;
import org.eclipse.nebula.widgets.geomap.GeoMapUtil;
import org.eclipse.nebula.widgets.geomap.internal.InternalGeoMapListener;
import org.eclipse.nebula.widgets.geomap.internal.TileRef;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author stepan.rutz@gmx.de
 * @version $Revision$
 */
public class InfoPage extends AbstractPage
		implements Page, InternalGeoMapListener {

	private static final Logger log = Logger
			.getLogger(InfoPage.class.getName());

	private abstract class Spec {
		final String key;
		final GeoMap geoMap;

		abstract String computeValue();

		Spec(String key) {
			this.key = key;
			this.geoMap = mapBrowser.getGeoMap();
		}
	}

	private final GeoMapBrowser mapBrowser;
	private Table table;
	private Spec[] specs;

	public InfoPage(GeoMapBrowser mapBrowser) {
		this.mapBrowser = mapBrowser;
		specs = new Spec[] { new Spec("Zoom") {
			@Override
			public String computeValue() {
				return Integer.toString(geoMap.getZoom());
			}
		}, new Spec("Map Size") {
			@Override
			public String computeValue() {
				Point size = geoMap.getSize();
				return size.x + ", " + size.y;
			}
		}, new Spec("Map Position") {
			@Override
			public String computeValue() {
				Point position = geoMap.getMapPosition();
				return position.x + ", " + position.y;
			}
		}, new Spec("Center Position") {
			@Override
			public String computeValue() {
				Point position = geoMap.getCenterPosition();
				return position.x + ", " + position.y;
			}
		}, new Spec("Paint time") {
			@Override
			public String computeValue() {
				return controlPaintTime + " ms";
			}
		}, new Spec("Number painted tiles") {
			@Override
			public String computeValue() {
				return tilePaintCount + " of "
						+ NumberFormat.getIntegerInstance()
								.format((long) (1 << geoMap.getZoom())
										* (1 << geoMap.getZoom()));
			}
		}, new Spec("Tile cache usage") {
			@Override
			public String computeValue() {
				return String.format("Usage: %3d / %3d ", tileCacheUsed,
						tileCacheSize);
			}
		}, new Spec("Longitude/Latitude") {
			@Override
			public String computeValue() {
				Point p = geoMap.getCursorPosition();
				int zoom = geoMap.getZoom();
				return format(GeoMapUtil.position2lon(p.x, zoom)) + ", "
						+ format(GeoMapUtil.position2lat(p.y, zoom));
			}
		}, };
	}

	private static String format(double d) {
		return String.format("%.5f", d);
	}

	public void updateInfos() {
		if (table == null) {
			return;
		}
		for (int i = 0; i < specs.length; ++i) {
			Spec spec = specs[i];
			TableItem item = table.getItem(i);
			item.setText(1, spec.computeValue());
		}
	}

	@Override
	protected void initContent(final PageContainer container,
			Composite composite) {
		mapBrowser.getGeoMap().addInternalGeoMapListener(this);

		addHeaderRow(container, composite, "Actions");
		addActionLink(container, composite, "<a>Back to main menu</a>",
				new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						container.showPage(0);
					}
				});

		addHeaderRow(container, composite, "Infos");
		addInfoText(container, composite,
				"The following search results were retrieved from openstreetmap.org. "
						+ "Double-click to open a location.");

		table = new Table(composite, SWT.FULL_SELECTION | SWT.MULTI
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		table.setLayoutData(
				new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableColumn column1 = new TableColumn(table, SWT.NONE);
		column1.setText("Place");
		column1.setWidth(120);
		TableColumn column2 = new TableColumn(table, SWT.NONE);
		column2.setText("Place");
		column2.setWidth(160);

		for (Spec spec : specs) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(0, spec.key);
		}

		addHeaderRow(container, composite, "Authors Remarks");
		addInfoText(container, composite,
				"The number of image fetcher threads determines how many background tasks are "
						+ "run simultaneously to fetch tiles. The search is also performed in the background. "
						+ "The tilecache fill never drops below its maximum size once its filled, but the eldest "
						+ "entries are discarded on a LRU (Least-Recently-Used) basis.");
		addInfoText(container, composite,
				"The MapWidget can be used without all of this quite useless showcase application. Its "
						+ "main purpose is to showcase the MapWidget.");

		addHeaderRow(container, composite, "Quick Help");
		addInfoText(container, composite,
				"Use the mouse wheel to zoom in and zoom out. Drag the mouse to change "
						+ "your position on the map");
		updateInfos();
	}

	@Override
	protected void widgetDisposed(DisposeEvent e) {
		mapBrowser.getGeoMap().removeInternalGeoMapListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.nebula.widgets.geomap.internal.InternalGeoMapListener#
	 * tilePainted(org.eclipse.nebula.widgets.geomap.internal.TileRef)
	 */
	@Override
	public void tilePainted(TileRef tileRef) {
		// ignore
	}

	private int tilePaintCount;
	private long controlPaintTime;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.nebula.widgets.geomap.internal.InternalGeoMapListener#
	 * controlPainted(int, long)
	 */
	@Override
	public void mapPainted(int tileCount, long time) {
		tilePaintCount = tileCount;
		controlPaintTime = time;
	}

	private int tileCacheUsed, tileCacheSize;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.nebula.widgets.geomap.internal.InternalGeoMapListener#
	 * tileCacheUpdated(int, int)
	 */
	@Override
	public void tileCacheUpdated(int used, int size) {
		tileCacheUsed = used;
		tileCacheSize = size;
	}
}
