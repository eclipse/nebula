/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Laurent CARON (laurent.caron at gmail dot com) - Initial implementation and API
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.commons;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * This class provides useful String manipulation methods
 * 
 */
public class StringUtil {

	/**
	 * Returns a "safe" string representation. If source is null, return an
	 * empty string
	 * 
	 * @param source source string
	 * @return the string representation of the source (without space) if the
	 *         source is not <code>null</code>, or an empty string otherwise
	 */
	public static String safeToString(final Object source) {
		return source == null ? "" : source.toString().trim();
	}

	/**
	 * Check if a string is empty or null
	 * 
	 * @param source source string
	 * @return <code>true</code> is the string is empty or null,
	 *         <code>false</code> otherwise
	 */
	public static boolean isEmpty(final String source) {
		return source == null || source.trim().isEmpty();
	}

	/**
	 * Converts exception stack trace as string
	 * 
	 * @param exception exception to convert
	 * @return a string that contains the exception
	 */
	public static final String stackStraceAsString(final Throwable exception) {
		final StringWriter stringWriter = new StringWriter();
		exception.printStackTrace(new PrintWriter(stringWriter));
		return stringWriter.toString();
	}

	/**
	 * Insert a string in a middle of another string
	 * 
	 * @param source source string
	 * @param newEntry string to insert into source
	 * @param position position to insert source
	 * @return the new string
	 */
	public static String insertString(final String source, final String newEntry, final int position) {
		final StringBuilder sb = new StringBuilder();
		sb.append(source.substring(0, position)).append(newEntry).append(source.substring(position));
		return sb.toString();
	}

	/**
	 * Remove a character in a String
	 * 
	 * @param source source string
	 * @param position position of the character to remove
	 * @return the string without the character
	 */
	public static String removeCharAt(final String source, final int position) {
		final StringBuilder sb = new StringBuilder();
		if (position == source.length()) {
			return source;
		}
		sb.append(source.substring(0, position)).append(source.substring(position + 1));
		return sb.toString();
	}
}
