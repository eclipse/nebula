/*******************************************************************************
 * Copyright (c) 2005, 2007 ewuillai.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eric Wuillai (eric@wdev91.com) - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.formattedtext;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.eclipse.swt.SWT;

/**
 * This class provides formatting of <code>Date</code> values in a
 * <code>FormattedText</code>, resticting the edit and display to the time part.
 * Supports a subset of time patterns defined in <code>SimpleDateFormat</code>
 * for input.<p>
 * 
 * See <code>DateTimeFormatter</code> for a full description of patterns, given
 * that only patterns chars related to time are allowed.
 * 
 * <h4>Examples</h4>
 * <ul>
 * 	 <li><code>new TimeFormatter("HH:mm")</code> - 15:45:11 will edit
 * 	 and display as "15:45".</li>
 *   <li><code>new TimeFormatter("hh:mm a, "h:m a")</code> - 05:05 AM will edit
 *   as "05:05 AM" and display as "5:5 AM".</li>
 * </ul>
 */
public class TimeFormatter extends DateTimeFormatter {
  /**
   * Constructs a new instance with all defaults :
   * <ul>
   *   <li>edit mask in SHORT time format for the default locale</li>
   *   <li>display mask identical to the edit mask</li>
   *   <li>default locale</li>
   * </ul>
   */
	public TimeFormatter() {
		super();
	}

  /**
   * Constructs a new instance with default edit and display masks for the given
   * locale.
   * 
   * @param loc locale
   */
	public TimeFormatter(Locale loc) {
		super(loc);
	}

	/**
   * Constructs a new instance with the given edit mask. Display mask is
   * identical to the edit mask, and locale is the default one.
   * 
   * @param editPattern edit mask
	 */
	public TimeFormatter(String editPattern) {
		super(editPattern);
	}

	/**
   * Constructs a new instance with the given edit mask and locale. Display mask
   * is identical to the edit mask.
   * 
   * @param editPattern edit mask
   * @param loc locale
	 */
	public TimeFormatter(String editPattern, Locale loc) {
		super(editPattern, loc);
	}

	/**
   * Constructs a new instance with the given edit and display masks. Uses the
   * default locale.
   * 
   * @param editPattern edit mask
   * @param displayPattern display mask
	 */
	public TimeFormatter(String editPattern, String displayPattern) {
		super(editPattern, displayPattern);
	}

	/**
   * Constructs a new instance with the given masks and locale.
   * 
   * @param editPattern edit mask
   * @param displayPattern display mask
   * @param loc locale
	 */
	public TimeFormatter(String editPattern, String displayPattern, Locale loc) {
		super(editPattern, displayPattern, loc);
	}

	/**
   * Returns the default edit pattern for the given <code>Locale</code>.<p>
   * 
   * A <code>DateFormat</code> object is instanciated with SHORT format for
   * both the time part for the given locale. The corresponding pattern
   * string is then retrieved by calling the <code>toPattern</code>.<p>
   * 
   * Default patterns are stored in a cache with ISO3 language and country codes
   * as key. So they are computed only once by locale.
   * 
   * @param loc locale
   * @return edit pattern for the locale
   */
	public String getDefaultEditPattern(Locale loc) {
		if ( loc == null ) {
			loc = Locale.getDefault();
		}
		String key = "TI" + loc.toString(); //$NON-NLS-1$
		String pattern = (String) cachedPatterns.get(key);
		if ( pattern == null ) {
			DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT, loc);
			if ( ! (df instanceof SimpleDateFormat) ) {
				SWT.error(SWT.ERROR_INVALID_ARGUMENT, null,
				          "No default pattern for locale " + loc.getDisplayName()); //$NON-NLS-1$
			}
			pattern = ((SimpleDateFormat) df).toPattern();
			cachedPatterns.put(key, pattern);
		}
		return pattern;
	}

	/**
	 * Checks if a given char is valid for the edit pattern. This method
	 * overrides the parent method, restricting autorized chars to time patterns.
	 * 
	 * @param c pattern char
	 * @throws IllegalArgumentException if not valid
	 * @see DateTimeFormatter#isValidCharPattern(char)
	 */
	protected void isValidCharPattern(char c) {
		switch (c) {
			case 'y' :
			case 'M' :
			case 'd' :
			case 'D' :
			case 'G' :
			case 'w' :
			case 'W' :
			case 'F' :
			case 'E' :
			case 'k' :
			case 'K' :
			case 'z' :
			case 'Z' :
				SWT.error(SWT.ERROR_INVALID_ARGUMENT, null, "Invalid time pattern : " + c); //$NON-NLS-1$
		}
	}
}
