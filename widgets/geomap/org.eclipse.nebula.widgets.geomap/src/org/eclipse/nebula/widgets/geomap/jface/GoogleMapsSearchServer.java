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

package org.eclipse.nebula.widgets.geomap.jface;

import java.util.Stack;

import org.xml.sax.Attributes;

/**
 * A SearchServer using Google maps
 *
 * @since 3.3
 *
 */
public class GoogleMapsSearchServer extends SearchServer {

	/**
	 * Initializes the GoogleMapsSearchServer
	 */
	public GoogleMapsSearchServer() {
		super("http://maps.googleapis.com/maps/api/geocode/xml?", //$NON-NLS-1$
				"address={0}&sensor=false"); //$NON-NLS-1$
	}

	/**
	 * Geodecode response. See javadoc viewer for readable format.
	 *
	 * <pre>
	 * &lt;GeocodeResponse>
	 * 		&lt;status>OK&lt;/status>
	 * 		&lt;result>
	 * 			&lt;type>locality&lt;/type>
	 * 			&lt;type>political&lt;/type>
	 * 			&lt;formatted_address>Trondheim, Norway&lt;/formatted_address>
	 * 			&lt;address_component>
	 * 				&lt;long_name>Trondheim&lt;/long_name>
	 * 				&lt;short_name>Trondheim&lt;/short_name>
	 * 				&lt;type>locality&lt;/type>
	 * 				&lt;type>political&lt;/type>
	 * 			&lt;/address_component>
	 * 			&lt;address_component>
	 * 				&lt;long_name>Trondheim&lt;/long_name>
	 * 				&lt;short_name>Trondheim&lt;/short_name>
	 * 				&lt;type>administrative_area_level_2&lt;/type>
	 * 				&lt;type>political&lt;/type>
	 * 				&lt;/address_component>
	 * 			&lt;address_component>
	 * 				&lt;long_name>Sor-Trondelag&lt;/long_name>
	 * 				&lt;short_name>Sor-Trondelag&lt;/short_name>
	 * 				&lt;type>administrative_area_level_1&lt;/type>
	 * 				&lt;type>political&lt;/type>
	 * 			&lt;/address_component>
	 * 			&lt;address_component>
	 * 				&lt;long_name>Norway&lt;/long_name>
	 * 				&lt;short_name>NO&lt;/short_name>
	 * 				&lt;type>country&lt;/type>
	 * 				&lt;type>political&lt;/type>
	 * 			&lt;/address_component>
	 * 			&lt;geometry>
	 * 				&lt;location>
	 * 					&lt;lat>63.4305149&lt;/lat>
	 * 					&lt;lng>10.3950528&lt;/lng>
	 * 				&lt;/location>
	 * 				&lt;location_type>APPROXIMATE&lt;/location_type>
	 * 				&lt;viewport>
	 * 					&lt;southwest>
	 * 						&lt;lat>63.3905609&lt;/lat>
	 * 						&lt;lng>10.2669934&lt;/lng>
	 * 					&lt;/southwest>
	 * 					&lt;northeast>
	 * 						&lt;lat>63.4704133&lt;/lat>
	 * 						&lt;lng>10.5231122&lt;/lng>
	 * 					&lt;/northeast>
	 * 				&lt;/viewport>
	 * 				&lt;bounds>
	 * 					&lt;southwest>
	 * 						&lt;lat>63.3805535&lt;/lat>
	 * 						&lt;lng>10.2981588&lt;/lng>
	 * 					&lt;/southwest>
	 * 					&lt;northeast>
	 * 						&lt;lat>63.4569189&lt;/lat>
	 * 						&lt;lng>10.5793654&lt;/lng>
	 * 					&lt;/northeast>
	 * 				&lt;/bounds>
	 * 			&lt;/geometry>
	 * 		&lt;/result>
	 * 	&lt;/GeocodeResponse>
	 * </pre>
	 */

	@Override
	protected Object startElement(String qName, Stack<String> path,
			Attributes attributes, Stack<Object> objects) {
		if ("result".equals(qName)) { //$NON-NLS-1$
			objects.push(new SearchResult());
		}
		return null;
	}

	@Override
	protected Object characters(String qName, Stack<String> path, char[] ch,
			int start, int length, Stack<Object> objects) {
		if (objects.size() > 0 && objects.peek() instanceof SearchResult) {
			SearchResult result = (SearchResult) objects.peek();
			if ("type".equals(qName) && "result".equals(path.peek())) { //$NON-NLS-1$ //$NON-NLS-2$
				String s = new String(ch, start, length).trim();
				if (result.category == null) {
					result.category = s;
				} else {
					result.type = s;
				}
			} else if ("formatted_address".equals(qName) //$NON-NLS-1$
					&& "result".equals(path.peek())) { //$NON-NLS-1$
				result.setText(new String(ch, start, length));
			} else if ("short_name".equals(qName) && result.getName() == null) { //$NON-NLS-1$
				result.setName(new String(ch, start, length));
			} else if ("lng".equals(qName) && "location".equals(path.peek())) { //$NON-NLS-1$ //$NON-NLS-2$
				result.setLon(new String(ch, start, length).trim());
			} else if ("lat".equals(qName) && "location".equals(path.peek())) { //$NON-NLS-1$ //$NON-NLS-2$
				result.setLat(new String(ch, start, length).trim());
			}
		}
		return null;
	}

	@Override
	protected Object endElement(String qName, Stack<String> path,
			Stack<Object> objects) {
		if ("result".equals(qName) && objects.size() > 0 //$NON-NLS-1$
				&& objects.peek() instanceof SearchResult) {
			SearchResult result = (SearchResult) objects.peek();
			if (result.getLonLat() != null) {
				return result;
			}
		}
		return null;
	}

	/**
	 * The SearchResult returned from the GoogleMapsSearchServer
	 *
	 * @since 3.3
	 *
	 */
	public static final class SearchResult extends Result {
		private String type;
		private String category;

		/**
		 * Gets the result type
		 *
		 * @return the result type
		 */
		public String getType() {
			return type;
		}

		/**
		 * Gets the result category
		 *
		 * @return the result category
		 */
		public String getCategory() {
			return category;
		}

		@Override
		@SuppressWarnings("nls")
		public String toString() {
			return "SearchResult [text=" + getText() + ", location="
					+ getLonLat() + ", type=" + type + ", category=" + category
					+ "]";
		}
	}
}
