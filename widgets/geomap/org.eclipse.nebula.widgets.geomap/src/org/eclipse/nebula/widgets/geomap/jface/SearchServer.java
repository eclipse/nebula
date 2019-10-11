/*******************************************************************************
 * Copyright (c) 2012 Hallvard Tr�tteberg.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Hallvard Tr�tteberg - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.geomap.jface;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.SAXParserFactory;

import org.eclipse.nebula.widgets.geomap.PointD;
import org.eclipse.nebula.widgets.geomap.internal.URLService;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * An abstract super class for search servers
 * 
 * @since 3.3
 *
 */
public abstract class SearchServer extends URLService {

	/**
	 * Initializes the SearchServer with a specific URL and url format
	 * 
	 * @param url
	 * @param urlFormat
	 */
	public SearchServer(String url, String urlFormat) {
		super(url, urlFormat);
	}

	/**
	 * Initializes the SearchServer with a specific URL and default url format
	 * 
	 * @param url
	 */
	public SearchServer(String url) {
		parseUrl(url, "format=xml&q={0}"); //$NON-NLS-1$
	}

	@Override
	protected Object[] getURLFormatArguments(Object ref) {
		return new Object[] { ref };
	}

	/**
	 * Gets the URL used for a specific search
	 * 
	 * @param search
	 * @return the URL
	 */
	public String getSearchURL(String search) {
		return getServiceURL(search);
	}

	/**
	 * Tries to parse s as a Double, defaulting to Double.NaN
	 * 
	 * @param s
	 *            the String to parse
	 * @return the double or NaN, if the String couldn't be parsed
	 */
	protected static double tryDouble(String s) {
		try {
			return Double.valueOf(s);
		} catch (Exception e) {
			return Double.NaN;
		}
	}

	protected boolean checkPath(String[] qNames, AbstractList<String> path) {
		for (int i = 0; i < qNames.length; i++) {
			int pathPos = path.size() - i - 1;
			if (pathPos < 0 || !qNames[qNames.length - i - 1]
					.equals(path.get(pathPos))) {
				return false;
			}
		}
		return true;
	}

	protected Object startElement(String qName, Stack<String> path,
			Attributes attributes, Stack<Object> objects) {
		return null;
	}

	protected Object characters(String qName, Stack<String> path, char[] ch,
			int start, int length, Stack<Object> objects) {
		return null;
	}

	protected Object endElement(String qName, Stack<String> path,
			Stack<Object> objects) {
		return null;
	}

	public Object[] doSearch(String search) {
		return doSearchInternal(search);
	}

	private Object[] doSearchInternal(String search) {
		final List<Object> results = new ArrayList<>();
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(false);
			final Stack<String> path = new Stack<>();
			final Stack<Object> objects = new Stack<>();
			factory.newSAXParser().parse(getSearchURL(search),
					new DefaultHandler() {
						private void addResult(Object result) {
							if (result != null) {
								results.add(result);
							}
						}

						@Override
						public void startElement(String uri, String localName,
								String qName, Attributes attributes) {
							addResult(SearchServer.this.startElement(qName,
									path, attributes, objects));
							path.push(qName);
						}

						@Override
						public void endElement(String uri, String localName,
								String qName) throws SAXException {
							addResult(SearchServer.this.endElement(qName, path,
									objects));
							path.pop();
						}

						@Override
						public void characters(char[] ch, int start, int length)
								throws SAXException {
							String qName = path.pop();
							addResult(SearchServer.this.characters(qName, path,
									ch, start, length, objects));
							path.push(qName);
						}
					});
			return results.size() > 0 ? results.toArray() : null;
		} catch (Exception e) {
		}
		return null;
	}

	public static class Result extends Located.Static {

		private double lon = Double.NaN, lat = Double.NaN;
		private String name, text;

		@Override
		public PointD getLonLat() {
			return Double.isNaN(lon) || Double.isNaN(lat) ? null
					: new PointD(lon, lat);
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name.trim();
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text.trim();
		}

		public double getLon() {
			return lon;
		}

		public void setLon(double lon) {
			this.lon = lon;
		}

		public void setLon(String lon) {
			this.lon = tryDouble(lon.trim());
		}

		public double getLat() {
			return lat;
		}

		public void setLat(double lat) {
			this.lat = lat;
		}

		public void setLat(String lat) {
			this.lat = tryDouble(lat.trim());
		}

		@Override
		public String toString() {
			return "SearchResult [text=" + text + ", location=" + getLonLat()
					+ "]";
		}
	}
}
