/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http\://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors\:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.geomap.jface;

import java.util.Stack;

import org.xml.sax.Attributes;

/**
 * A SearchServer using Google maps
 * @since 3.3
 *
 */
public class GoogleMapsSearchServer extends SearchServer {

	/**
	 * Initializes the GoogleMapsSearchServer
	 */
	public GoogleMapsSearchServer() {
		super("http://maps.googleapis.com/maps/api/geocode/xml?", "address={0}&sensor=false");  //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/*
	<GeocodeResponse>
		<status>OK</status>
		<result>
			<type>locality</type>
			<type>political</type>
			<formatted_address>Trondheim, Norway</formatted_address>
			<address_component>
				<long_name>Trondheim</long_name>
				<short_name>Trondheim</short_name>
				<type>locality</type>
				<type>political</type>
			</address_component>
			<address_component>
				<long_name>Trondheim</long_name>
				<short_name>Trondheim</short_name>
				<type>administrative_area_level_2</type>
				<type>political</type>
				</address_component>
			<address_component>
				<long_name>Sor-Trondelag</long_name>
				<short_name>Sor-Trondelag</short_name>
				<type>administrative_area_level_1</type>
				<type>political</type>
			</address_component>
			<address_component>
				<long_name>Norway</long_name>
				<short_name>NO</short_name>
				<type>country</type>
				<type>political</type>
			</address_component>
			<geometry>
				<location>
					<lat>63.4305149</lat>
					<lng>10.3950528</lng>
				</location>
				<location_type>APPROXIMATE</location_type>
				<viewport>
					<southwest>
						<lat>63.3905609</lat>
						<lng>10.2669934</lng>
					</southwest>
					<northeast>
						<lat>63.4704133</lat>
						<lng>10.5231122</lng>
					</northeast>
				</viewport>
				<bounds>
					<southwest>
						<lat>63.3805535</lat>
						<lng>10.2981588</lng>
					</southwest>
					<northeast>
						<lat>63.4569189</lat>
						<lng>10.5793654</lng>
					</northeast>
				</bounds>
			</geometry>
		</result>
	</GeocodeResponse>
	*/

	@Override
	protected Object startElement(String qName, Stack<String> path, Attributes attributes, Stack<Object> objects) {
    	if ("result".equals(qName)) { //$NON-NLS-1$
            objects.push(new SearchResult());
        }
		return null;
	}

	@Override
    protected Object characters(String qName, Stack<String> path, char[] ch, int start, int length, Stack<Object> objects) {
		if (objects.size() > 0 && objects.peek() instanceof SearchResult) {
			SearchResult result = (SearchResult) objects.peek();
			if ("type".equals(qName) && "result".equals(path.peek())) {  //$NON-NLS-1$ //$NON-NLS-2$
				String s = new String(ch, start, length).trim();
				if (result.category == null) {
					result.category = s;
				} else {
					result.type = s;
				}
			} else if ("formatted_address".equals(qName) && "result".equals(path.peek())) {  //$NON-NLS-1$ //$NON-NLS-2$
				result.setText(new String(ch, start, length));
			} else if ("short_name".equals(qName) && result.getName() == null) { //$NON-NLS-1$
				result.setName(new String(ch, start, length));
			} else if ("lng".equals(qName) && "location".equals(path.peek())) {  //$NON-NLS-1$ //$NON-NLS-2$
				result.setLon(new String(ch, start, length).trim());
			} else if ("lat".equals(qName) && "location".equals(path.peek())) {  //$NON-NLS-1$ //$NON-NLS-2$
				result.setLat(new String(ch, start, length).trim());
	        }
		}
		return null;
	}

	@Override
	protected Object endElement(String qName, Stack<String> path, Stack<Object> objects) {
		if ("result".equals(qName) && objects.size() > 0 && objects.peek() instanceof SearchResult) { //$NON-NLS-1$
			SearchResult result = (SearchResult) objects.peek();
			if (result.getLonLat() != null) {
				return result;
			}
		}
		return null;
	}

	/**
	 * The SearchResult returned from the GoogleMapsSearchServer
	 * @since 3.3
	 *
	 */
    public static final class SearchResult extends Result {
        private String type;
        private String category;

        /**
         * Gets the result type
         * @return the result type
         */
        public String getType() {
            return type;
        }

        /**
         * Gets the result category
         * @return the result category
         */
        public String getCategory() {
            return category;
        }

        @SuppressWarnings("nls")
		public String toString() {
            return "SearchResult [text=" + getText() + ", location=" + getLonLat() + ", type=" + type + ", category=" + category + "]";
        }
    }
}
