/*******************************************************************************
 * Copyright (c) Emil Crumhorn - Hexapixel.com - emil.crumhorn@gmail.com
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    emil.crumhorn@gmail.com - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.ganttchart;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public final class ColorCache {

	public static final RGB		BLACK	= new RGB(0, 0, 0);
	public static final RGB		WHITE	= new RGB(255, 255, 255);

	private static HashMap		_cache;
	private static ColorCache	_instance;

	/**
	 * Disposes all colors held in the cache and colors created when class is created.
	 * <p>
	 * <b>IMPORTANT: ONLY CALL WHEN YOU WANT TO DISPOSE THE WIDGET USING THIS CLASS!</b>
	 * <p>
	 * If you only wish to dispose colors you have created through the use of the class, please use disposeCachedColors()
	 * 
	 * @see #disposeCachedColor()
	 */
	public static void disposeAll() {
		_instance.dispose();
	}

	/**
	 * Disposes the cached colors only.
	 */
	public static void disposeCachedColor() {
		Iterator e = _cache.values().iterator();
		while (e.hasNext())
			((Color) e.next()).dispose();

		_cache.clear();
	}

	private ColorCache() {
		if (_cache == null) {
			_cache = new HashMap();
		}
	}

	private static void checkInstance() {
		if (_instance == null)
			_instance = new ColorCache();
	}

	// see disposeAll();
	private void dispose() {
		checkInstance();

		Iterator e = _cache.values().iterator();
		while (e.hasNext())
			((Color) e.next()).dispose();

		_cache.clear();
	}

	/**
	 * Returns the color white R255, G255, B255
	 * 
	 * @return White color
	 */
	public static Color getWhite() {
		checkInstance();
		return getColor(WHITE);
	}

	/**
	 * Returns the color black R0, G0, B0
	 * 
	 * @return Black color
	 */
	public static Color getBlack() {
		checkInstance();
		return getColor(BLACK);
	}

	/**
	 * Returns a color that is also cached if it has not been created before.
	 * 
	 * @param rgb RGB colors
	 * @return Color
	 */
	public static Color getColor(RGB rgb) {
		checkInstance();
		Color color = (Color) _cache.get(rgb);

		if (color == null) {
			color = new Color(Display.getCurrent(), rgb);
			_cache.put(rgb, color);
		}

		return color;
	}

	/**
	 * Returns a color that is also cached if it has not been created before.
	 * 
	 * @param r Red
	 * @param g Green
	 * @param b Blue
	 * @return Color
	 */
	public static Color getColor(int r, int g, int b) {
		checkInstance();
		RGB rgb = new RGB(r, g, b);
		return getColor(rgb);
	}

	/**
	 * Returns a random color.
	 * 
	 * @return random color
	 */
	public static Color getRandomColor() {
		checkInstance();
		Random rand = new Random();
		int r = rand.nextInt(255);
		int g = rand.nextInt(255);
		int b = rand.nextInt(255);

		return getColor(r, g, b);
	}
}
