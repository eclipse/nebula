/****************************************************************************
 * Copyright (c) 2008 Jeremy Dowdall
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *****************************************************************************/

package org.eclipse.nebula.widgets.cdatetime;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

class Resources {

	private static Listener disposeListener = new Listener() {
		public void handleEvent(Event event) {
			List<String> invalids = new ArrayList<String>();
			for (Entry<String, Image> entry : images.entrySet()) {
				Image img = entry.getValue();
				if (event.display == img.getDevice()) {
					invalids.add(entry.getKey());
					if (!img.isDisposed()) {
						img.dispose();
					}
				}
			}
			for (String key : invalids) {
				images.remove(key);
			}
		}
	};

	public static final String ICON_CALENDAR = "calendar.png";
	public static final String ICON_CLOCK = "clock.png";
	public static final String ICON_BULLET = "bullet.png";
	public static final String ICON_CALENDAR_CLOCK = "dateclock.png";

	private static final String BUNDLE_NAME = Resources.class.getPackage()
			.getName() + ".messages"; //$NON-NLS-1$

	private static ResourceBundle defaultBundle;
	private static final Map<Locale, ResourceBundle> bundles = new HashMap<Locale, ResourceBundle>();

	private static final Map<String, Image> images = new HashMap<String, Image>();

	private static String getDefaultString(String key) {
		if (defaultBundle == null) {
			defaultBundle = ResourceBundle.getBundle(BUNDLE_NAME);
		}
		try {
			return defaultBundle.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	public static Image getIconBullet() {
		return getImage(ICON_BULLET);
	}

	public static Image getIconCalendar() {
		return getImage(ICON_CALENDAR);
	}

	public static Image getIconClock() {
		return getImage(ICON_CLOCK);
	}

	private static Image getImage(String name) {
		Image img = images.get(name);
		if (img == null || img.isDisposed()) {
			Display display = Display.getDefault();
			display.addListener(SWT.Dispose, disposeListener);
			InputStream inputStream = Resources.class.getResourceAsStream(name);
			img = new Image(display, inputStream);
			images.put(name, img);
		}
		return img;
	}

	public static String getString(String key) {
		return getString(key, Locale.getDefault());
	}

	public static String getString(String key, Locale locale) {
		ResourceBundle bundle = (ResourceBundle) bundles.get(locale);
		if (bundle == null) {
			bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
			bundles.put(locale, bundle); 
		}
		try {
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			return getDefaultString(key);  
		}
	}

	/**
	 * @return
	 */
	public static Image getIconCalendarClock() {
		return getImage(ICON_CALENDAR_CLOCK);
	}

}
