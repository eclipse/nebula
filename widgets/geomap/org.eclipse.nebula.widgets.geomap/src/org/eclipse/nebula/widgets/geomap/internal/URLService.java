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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;

/**
 * Abstract super class for URL-based services with parameters
 * 
 * @since 3.3
 *
 */
public abstract class URLService {

	private String url;
	private String urlFormat;

	protected void parseUrl(String url, String defaultUrlFormat) {
		int pos = url.indexOf("{");
		if (pos > 0) {
			this.url = url.substring(0, pos);
			this.urlFormat = url.substring(pos);
		} else {
			this.url = url;
			this.urlFormat = defaultUrlFormat;
		}
	}

	protected URLService() {
	}

	protected URLService(String url, String urlFormat) {
		this.url = url;
		this.urlFormat = urlFormat;
	}

	protected abstract Object[] getURLFormatArguments(Object ref);

	protected String getServiceURL(Object ref, String urlFormat,
			Object[] formatArguments) {
		String[] encodedArgs = new String[formatArguments.length];
		for (int i = 0; i < encodedArgs.length; i++) {
			encodedArgs[i] = formatArguments[i].toString().trim();
			try {
				encodedArgs[i] = URLEncoder.encode(encodedArgs[i], "UTF-8");
			} catch (UnsupportedEncodingException e) {
			}
		}
		return url + MessageFormat.format(urlFormat, encodedArgs);
	}

	protected String getServiceURL(Object ref) {
		return urlFormat != null
				? getServiceURL(ref, urlFormat, getURLFormatArguments(ref))
				: null;
	}

	@Override
	public String toString() {
		return url;
	}

	public String getURL() {
		return url;
	}
}
