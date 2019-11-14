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

import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParserFactory;

import org.eclipse.nebula.widgets.geomap.GeoMap;
import org.eclipse.nebula.widgets.geomap.GeoMapUtil;
import org.eclipse.nebula.widgets.geomap.OsmTileServer;
import org.eclipse.nebula.widgets.geomap.PointD;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A {link {@link Page} that allows searching use the OSM Nominatim tool. The
 * OSM namefinder has its own usage policies. Please check them out before you
 * use it.
 *
 * @author stepan.rutz@gmx.de
 * @version $Revision$
 */
public class SearchPage extends AbstractPage implements Page {

	private static final Logger log = Logger
			.getLogger(SearchPage.class.getName());

	public static final class SearchResult {
		private String type;
		private double lat, lon;
		private String name;
		private String category;
		private String info;

		public SearchResult() {
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public double getLat() {
			return lat;
		}

		public void setLat(double lat) {
			this.lat = lat;
		}

		public double getLon() {
			return lon;
		}

		public void setLon(double lon) {
			this.lon = lon;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public String getInfo() {
			return info;
		}

		public void setInfo(String info) {
			this.info = info;
		}

		@Override
		public String toString() {
			return "SearchResult [category=" + category + ", info=" + info
					+ ", lat=" + lat + ", lon=" + lon + ", name=" + name
					+ ", type=" + type + "]";
		}
	}

	private final GeoMapBrowser mapBrowser;
	private ProgressBar progressBar;
	private GridData progressBarLayoutData;
	private Text searchText;
	private boolean searching;

	private ArrayList<SearchResult> results = new ArrayList<>();
	private Link searchLink;

	public SearchPage(GeoMapBrowser mapBrowser) {
		this.mapBrowser = mapBrowser;
	}

	@Override
	protected void widgetDisposed(DisposeEvent e) {
		// nothing to dispose
	}

	@Override
	protected void initContent(final PageContainer container,
			Composite composite) {
		addHeaderRow(container, composite, "Infopanel");
		addInfoText(container, composite,
				"Toggling the tile-server changes the way "
						+ "the map is rendered. Click on the mapinfos link to see details "
						+ "about the currently rendered part of the map.");

		addActionLink(container, composite, "<a>Toggle Rendering</a>",
				new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						GeoMap geoMap = mapBrowser.getGeoMap();
						int next = (Arrays.asList(OsmTileServer.TILESERVERS)
								.indexOf(geoMap.getTileServer()) + 1)
								% OsmTileServer.TILESERVERS.length;
						geoMap.setTileServer(OsmTileServer.TILESERVERS[next]);
					}
				});

		addActionLink(container, composite, "Show <a>Technical Mapinfos</a>",
				new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						mapBrowser.getPageContainer()
								.showPage(mapBrowser.getPageContainer()
										.indexOfPage(mapBrowser.getInfoPage()));
					}
				});
		addActionLink(container, composite, "Show <a>Europe</a>",
				new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						GeoMap geoMap = mapBrowser.getGeoMap();
						geoMap.setZoom(5);
						Point position = GeoMapUtil.computePosition(
								new PointD(5.5, 52.2), geoMap.getZoom());
						geoMap.setCenterPosition(position);
						geoMap.redraw();
					}
				});

		addHeaderRow(container, composite, "Search location");
		addInfoText(container, composite,
				"Enter any location or landmark site to search openstreetmap's "
						+ "genuine namefinder database. Hit return to perform the search.");

		{
			Label label = new Label(composite, SWT.NONE);
			label.setText("Search:");
			label.setLayoutData(new GridData());
			container.adapt(label);
		}
		{
			Composite wrap = new Composite(composite, SWT.NONE);
			wrap.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			container.adapt(wrap);

			GridLayout layout = new GridLayout(2, false);
			// layout.marginLeft = 8;
			// layout.marginHeight = 0;
			wrap.setLayout(layout);

			searchText = new Text(wrap, SWT.BORDER | SWT.SEARCH);
			searchText.setLayoutData(
					new GridData(SWT.FILL, SWT.CENTER, true, false));
			searchText.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					startSearch();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					startSearch();
				}
			});
			searchLink = new Link(wrap, SWT.NONE);
			searchLink.setText("<a>Go</a>");
			container.adapt(searchLink);
			searchLink.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					startSearch();
				}
			});
		}

		progressBar = new ProgressBar(composite,
				SWT.SMOOTH | SWT.INDETERMINATE);
		container.adapt(progressBar);
		progressBarLayoutData = new GridData(GridData.FILL, GridData.BEGINNING,
				true, false, 2, 1);
		progressBarLayoutData.exclude = true;
		progressBar.setVisible(false);
		progressBar.setLayoutData(progressBarLayoutData);

		addHeaderRow(container, composite, "Actions");
		addInfoText(container, composite, GeoMap.ABOUT_MSG);
	}

	void startSearch() {
		if (searching) {
			return;
		}
		final String search = searchText.getText();
		if (search == null || search.length() == 0) {
			return;
		}

		searching = true;
		setupSearchGui();
		Thread t = new Thread(() -> doSearchInternal(search));
		t.setName("Background Seacher \"" + search + "\"");
		t.start();
	}

	private void setupSearchGui() {
		progressBarLayoutData.exclude = false;
		progressBar.setVisible(true);
		searchText.setEnabled(false);
		searchLink.setEnabled(false);
		progressBar.getParent().layout();
	}

	private void tearDownSearchGui() {
		progressBarLayoutData.exclude = true;
		progressBar.setVisible(false);
		searchText.setEnabled(true);
		searchLink.setEnabled(true);
		progressBar.getParent().layout();
	}

	private static final String NAMEFINDER_URL_FORMAT = "http://nominatim.openstreetmap.org/search?format=xml&q={0}"; //$NON-NLS-1$

	private void doSearchInternal(final String newSearch) {
		results.clear();
		try {
			String args = URLEncoder.encode(newSearch.trim(), "UTF-8"); //$NON-NLS-1$
			String path = MessageFormat.format(NAMEFINDER_URL_FORMAT, args);
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(false);
			factory.newSAXParser().parse(path, new DefaultHandler() {
				private StringBuilder chars;

				@Override
				public void startElement(String uri, String localName,
						String qName, Attributes attributes) {
					if ("place".equals(qName)) { //$NON-NLS-1$
						SearchResult result = new SearchResult();
						result.setType(attributes.getValue("type")); //$NON-NLS-1$
						result.setLat(tryDouble(attributes.getValue("lat"))); //$NON-NLS-1$
						result.setLon(tryDouble(attributes.getValue("lon"))); //$NON-NLS-1$
						result.setName(attributes.getValue("display_name")); //$NON-NLS-1$
						results.add(result);
					}
				}

				@Override
				public void endElement(String uri, String localName,
						String qName) throws SAXException {
					// ignore
				}

				@Override
				public void characters(char[] ch, int start, int length)
						throws SAXException {
					if (chars != null) {
						chars.append(ch, start, length);
					}
				}

				private double tryDouble(String s) {
					try {
						return Double.valueOf(s);
					} catch (Exception e) {
						return 0d;
					}
				}
			});
		} catch (Exception e) {
			log.log(Level.SEVERE, "failed to search for \"" + newSearch + "\"",
					e);
		}
		getComposite().getDisplay().asyncExec(() -> {
			try {
				ResultsPage resultsPage = mapBrowser.getResultsPage();
				resultsPage.setSearch(newSearch);
				mapBrowser.getPageContainer().showPage(
						mapBrowser.getPageContainer().indexOfPage(resultsPage));
				resultsPage.setResults(
						results.toArray(new SearchResult[results.size()]));
			} finally {
				searching = false;
				tearDownSearchGui();
			}
		});
	}
}
