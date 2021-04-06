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

import java.util.Stack;

import org.xml.sax.Attributes;

/**
 * A SearchServer using Open Street Map
 * 
 * @since 3.3
 *
 */
public class OsmSearchServer extends SearchServer {

	/**
	 * Initializes the OsmSearchServer
	 */
	public OsmSearchServer() {
		super("http://nominatim.openstreetmap.org/search?format=xml&", "q={0}"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/*
	 * <searchresults timestamp="Mon, 11 Jun 12 21:58:47 +0100"
	 * attribution="Data Copyright OpenStreetMap Contributors, Some Rights Reserved. CC-BY-SA 2.0."
	 * querystring="Trondheim" polygon="false"
	 * exclude_place_ids="128891033,184917,69612272,69623905,51503057,7140200"
	 * more_url=
	 * "http://nominatim.openstreetmap.org/search?format=xml&exclude_place_ids=128891033,184917,69612272,69623905,51503057,7140200&accept-language=en-us,en;q=0.5&q=Trondheim">
	 * <place place_id="128891033" osm_type="relation" osm_id="406549"
	 * place_rank="14" boundingbox=
	 * "63.3037910461426,63.5188484191895,10.0044040679932,10.7260761260986"
	 * lat="63.4091124566799" lon="10.3442806402932"
	 * display_name="Trondheim, S�r-Tr�ndelag, Norway" class="boundary"
	 * type="administrative" icon=
	 * "http://nominatim.openstreetmap.org/images/mapicons/poi_boundary_administrative.p.20.png"
	 * /> <place place_id="184917" osm_type="node" osm_id="31264142"
	 * place_rank="16" boundingbox=
	 * "63.416322937012,63.436326751709,10.385802497864,10.405803451538"
	 * lat="63.4263262" lon="10.3958034"
	 * display_name="Trondheim, S�r-Tr�ndelag, Norway" class="place" type="city"
	 * icon=
	 * "http://nominatim.openstreetmap.org/images/mapicons/poi_place_city.p.20.png"
	 * /> <place place_id="69612272" osm_type="way" osm_id="70724671"
	 * place_rank="26" boundingbox=
	 * "56.0665512084961,56.0667915344238,-3.42122840881348,-3.4209291934967"
	 * lat="56.066629370311" lon="-3.42103396739874"
	 * display_name="Trondheim, Halbeath, Fife, Scotland, KY11, United Kingdom"
	 * class="highway" type="residential"/> <place place_id="51503057"
	 * osm_type="way" osm_id="46275499" place_rank="26" boundingbox=
	 * "51.8508987426758,51.8515129089355,4.50965213775635,4.51104021072388"
	 * lat="51.8511993825579" lon="4.51021507633146"
	 * display_name="Trondheim, Vaan Park, Barendrecht, Stadsregio Rotterdam, South Holland, 2993 LL, The Netherlands"
	 * class="highway" type="unclassified"/> <place place_id="7140200"
	 * osm_type="node" osm_id="671401968" place_rank="30" boundingbox=
	 * "50.963022460938,50.983026275635,11.017996063232,11.037997016907"
	 * lat="50.9730261" lon="11.0279965"
	 * display_name="Trondheim, Neuwerkstra�e, Altstadt, Erfurt, Thuringia, 99084, Federal Republic of Germany (land mass), Europe"
	 * class="shop" type="clothes" icon=
	 * "http://nominatim.openstreetmap.org/images/mapicons/shopping_clothes.p.20.png"
	 * /> </searchresults>
	 */

	@Override
	protected Object startElement(String qName, Stack<String> path,
			Attributes attributes, Stack<Object> objects) {
		if ("place".equals(qName)) { //$NON-NLS-1$
			SearchResult result = new SearchResult();
			result.setLon(attributes.getValue("lon")); //$NON-NLS-1$
			result.setLat(attributes.getValue("lat")); //$NON-NLS-1$
			if (result.getLonLat() == null) {
				return null;
			}
			String displayName = attributes.getValue("display_name"); //$NON-NLS-1$
			result.setText(displayName);
			int pos = displayName.indexOf(","); //$NON-NLS-1$
			result.setName(
					pos > 0 ? displayName.substring(0, pos) : displayName);
			result.category = attributes.getValue("class"); //$NON-NLS-1$
			result.type = attributes.getValue("type"); //$NON-NLS-1$
			return result;
		}
		return null;
	}

	/**
	 * The SearchResult returned from the OsmSearchServer
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
