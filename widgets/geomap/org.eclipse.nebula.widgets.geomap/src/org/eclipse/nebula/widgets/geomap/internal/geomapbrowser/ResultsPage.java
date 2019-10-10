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

import java.util.logging.Logger;

import org.eclipse.nebula.widgets.geomap.GeoMap;
import org.eclipse.nebula.widgets.geomap.GeoMapUtil;
import org.eclipse.nebula.widgets.geomap.PointD;
import org.eclipse.nebula.widgets.geomap.internal.geomapbrowser.SearchPage.SearchResult;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author stepan.rutz@gmx.de
 * @version $Revision$
 */
public class ResultsPage extends AbstractPage implements Page {

	private static final Logger log = Logger
			.getLogger(ResultsPage.class.getName());

	private final GeoMapBrowser mapBrowser;
	private Table table;

	private String search = "";
	private SearchResult[] results = new SearchResult[0];

	private Link descriptionText;

	public ResultsPage(GeoMapBrowser mapBrowser) {
		this.mapBrowser = mapBrowser;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public SearchResult[] getResults() {
		return results.clone();
	}

	public void setResults(SearchResult[] results) {
		this.results = results.clone();
		table.removeAll();
		for (SearchResult result : results) {

			String shortName = result.getName();
			shortName = shortName.replaceAll("\\s(.*)$", "");
			String linkBody = shortName;
			if (result.getType() != null && result.getType().length() > 0) {
				linkBody += " [" + result.getType() + "]";
			}

			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(0, linkBody);
		}
	}

	@Override
	protected void initContent(final PageContainer container,
			Composite composite) {
		addHeaderRow(container, composite, "Actions");
		addActionLink(container, composite, "<a>Back to main menu</a>",
				new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						container.showPage(0);
					}
				});

		addHeaderRow(container, composite, "Results");
		addInfoText(container, composite,
				"The following search results were retrieved from openstreetmap.org. "
						+ "Double-click to open a location.");

		table = new Table(composite, SWT.FULL_SELECTION | SWT.MULTI
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		table.setLayoutData(
				new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		table.setHeaderVisible(!true);
		table.setLinesVisible(true);
		TableColumn column1 = new TableColumn(table, SWT.NONE);
		column1.setText("Place");
		column1.setWidth(260);

		addHeaderRow(container, composite, "Description");
		descriptionText = addInfoText(container, composite, "");
		GridData layoutData = (GridData) descriptionText.getLayoutData();
		layoutData.minimumHeight = 100;
		layoutData.grabExcessVerticalSpace = false;
		layoutData.heightHint = 160;

		table.addListener(SWT.Selection, event -> {
			if (event.detail == SWT.CHECK) {
				return;
			}
			TableItem item = (TableItem) event.item;
			int index = table.indexOf(item);
			if (index >= 0 && index < results.length) {
				SearchResult result = results[index];
				String name = result.getName();
				name = name.replaceAll("\\[.*?\\]", "");
				name = name.replaceAll("<.*?>", "");
				if (result.getCategory() != null) {
					name += " " + result.getCategory();
				}
				descriptionText.setText(name);

				descriptionText.getParent().layout();
			}
		});

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				Point point = new Point(e.x, e.y);
				TableItem item = table.getItem(point);
				if (item == null) {
					return;
				}
				int index = table.indexOf(item);
				if (index >= 0 && index < results.length) {
					SearchResult result = results[index];
					GeoMap geoMap = mapBrowser.getGeoMap();
					// geoMap.setZoom(result.getZoom() < 1 || result.getZoom() >
					// mapWidget.getTileServer().getMaxZoom() ? 8 :
					// result.getZoom());
					Point position = GeoMapUtil.computePosition(
							new PointD(result.getLon(), result.getLat()),
							geoMap.getZoom());
					geoMap.setCenterPosition(position);
					geoMap.redraw();
				}
			}
		});
	}

	@Override
	protected void widgetDisposed(DisposeEvent e) {
	}
}
