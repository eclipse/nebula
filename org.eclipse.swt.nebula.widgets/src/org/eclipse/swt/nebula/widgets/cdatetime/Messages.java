/****************************************************************************
* Copyright (c) 2006 Jeremy Dowdall
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
*****************************************************************************/

package org.eclipse.swt.nebula.widgets.cdatetime;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Utility class used to access localized string. Can open and maintain an internal map of
 * several property files at once, thus supporting multiple locales simultaneously.
 */
public class Messages {
	private static final String BUNDLE_NAME = "org.eclipse.swt.nebula.widgets.cdatetime.messages"; //$NON-NLS-1$

	private static final Map bundles = new HashMap();

	/**
	 * Returns the string represented by the given key for the system's default locale.  If a
	 * property file for the locale or the key cannot be found, the key itself is returned 
	 * prefixed and postfixed with exclamation points ("!invalid_key!").
	 * @param key a key representing a string to look up
	 * @return the translated string
	 * @see #getString(String, Locale)
	 */
	public static String getString(String key) {
		return getString(key, Locale.getDefault());
	}
	
	/**
	 * Returns the string represented by the given key for the system's default locale.  If a
	 * property file for the locale or the key cannot be found, the key itself is returned 
	 * prefixed and postfixed with exclamation points ("!invalid_key!").
	 * @param key a key representing a string to look up
	 * @return the translated string
	 * @see #getString(String)
	 */
	public static String getString(String key, Locale locale) {
		ResourceBundle bundle = (ResourceBundle) bundles.get(locale);
		if(bundle == null) {
			bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
			bundles.put(locale, bundle);
		}
		try {
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
