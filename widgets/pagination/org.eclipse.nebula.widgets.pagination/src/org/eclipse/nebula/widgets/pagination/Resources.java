/*******************************************************************************
 * Copyright (C) 2011 Angelo Zerr <angelo.zerr@gmail.com>, Pascal Leclercq <pascal.leclercq@gmail.com>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo ZERR - initial API and implementation
 *     Pascal Leclercq - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.pagination;

import java.util.Locale;
import java.util.ResourceBundle;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

/**
 * Resources helper used to messages resources from bundle and SWT {@link Color}
 * .
 */
public class Resources {

	/** Bundle name constant */
	public static final String BUNDLE_NAME = "org.eclipse.nebula.widgets.pagination.resources"; //$NON-NLS-1$

	public static final String PaginationRenderer_results = "PaginationRenderer.results";
	public static final String PaginationRenderer_previous = "PaginationRenderer.previous";
	public static final String PaginationRenderer_next = "PaginationRenderer.next";
	public static final String PaginationRenderer_separator="PaginationRenderer.separator";
	public static final String PaginationRenderer_itemsPerPage = "PaginationRenderer.itemsPerPage";
	public static final String PaginationRenderer_page = "PaginationRenderer.page";

	public static final String THREE_DOT = "...";

	/**
	 * Returns the text of the given messageKey according the given locale.
	 * 
	 * @param messageKey
	 *            the message key.
	 * @param locale
	 *            the locale.
	 * @return
	 */
	public static String getText(String messageKey, Locale locale) {
		if (locale == null) {
			return ResourceBundle.getBundle(BUNDLE_NAME).getString(messageKey);
		}
		return ResourceBundle.getBundle(BUNDLE_NAME, locale).getString(
				messageKey);
	}

	/**
	 * Create or get instance of SWT {@link Color} from the given {@link RGB}.
	 * 
	 * @param rgb
	 * @return
	 */
	public static Color getColor(RGB rgb) {
		String key = rgb.toString();
		Color color = JFaceResources.getColorRegistry().get(key);
		if (color == null) {
			JFaceResources.getColorRegistry().put(key, rgb);
		}
		return JFaceResources.getColorRegistry().get(key);
	}
}
