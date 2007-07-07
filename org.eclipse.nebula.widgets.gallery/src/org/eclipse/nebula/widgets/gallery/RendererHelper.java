/*******************************************************************************
 * Copyright (c) 2006-2007 Nicolas Richeton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors :
 *    Nicolas Richeton (nicolas.richeton@gmail.com) - initial API and implementation
 *    Richard Michalsky - bug 195439
 *******************************************************************************/
package org.eclipse.nebula.widgets.gallery;

import org.eclipse.swt.graphics.GC;

/**
 * Renderer Helper
 * <p>
 * NOTE: THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT. THIS IS A
 * PRE-RELEASE ALPHA VERSION. USERS SHOULD EXPECT API CHANGES IN FUTURE
 * VERSIONS.
 * </p>
 * 
 * @author Nicolas Richeton (nicolas.richeton@gmail.com)
 * @contributor Richard Michalsky
 * 
 */
public class RendererHelper {
	private static final String ELLIPSIS = "...";

	/**
	 * Shorten the given text <code>text</code> so that its length doesn't
	 * exceed the given width. The default implementation replaces characters in
	 * the center of the original string with an ellipsis ("..."). Override if
	 * you need a different strategy.
	 * 
	 * Note: Code originally from org.eclipse.cwt.CLabel
	 * 
	 * @param gc
	 *            the gc to use for text measurement
	 * @param t
	 *            the text to shorten
	 * @param width
	 *            the width to shorten the text to, in pixels
	 * @return the shortened text
	 */
	static protected String createLabel(String text, GC gc, int width) {

		if (text == null)
			return null;

		final int extent = gc.textExtent(text).x;

		if (extent > width) {
			final int w = gc.textExtent(ELLIPSIS).x;
			if (width <= w) {
				return text;
			}
			final int l = text.length();
			int max = l / 2;
			int min = 0;
			int mid = (max + min) / 2 - 1;
			if (mid <= 0) {
				return text;
			}
			while (min < mid && mid < max) {
				final String s1 = text.substring(0, mid);
				final String s2 = text.substring(l - mid, l);
				final int l1 = gc.textExtent(s1).x;
				final int l2 = gc.textExtent(s2).x;
				if (l1 + w + l2 > width) {
					max = mid;
					mid = (max + min) / 2;
				} else if (l1 + w + l2 < width) {
					min = mid;
					mid = (max + min) / 2;
				} else {
					min = max;
				}
			}
			if (mid == 0) {

				return text;
			}
			String result = text.substring(0, mid) + ELLIPSIS + text.substring(l - mid, l);

			return result;
		} else {
			return text;
		}
	}

}
