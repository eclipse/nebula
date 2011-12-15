/*******************************************************************************
 * Copyright (c) 2005, 2009 Eric Wuillai.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eric Wuillai (eric@wdev91.com) - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.formattedtext;

import java.util.Locale;

import org.eclipse.swt.SWT;

/**
 * Formatter for percent values.<p>
 * This formatter extends the <code>NumberFormatter</code> :
 * - add the percent symbol in the suffix area.
 * - convert the number value in a percent : multiply by 100 on setValue() and
 *   divide by 100 on getValue().
 */
public class PercentFormatter extends NumberFormatter {
	/**
   * Constructs a new instance with all defaults :
   * <ul>
   *   <li>edit mask from NumberPatterns for the default locale</li>
   *   <li>display mask identical to the edit mask</li>
   *   <li>default locale</li>
   * </ul>
	 */
	public PercentFormatter() {
	  super();
  }

	/**
   * Constructs a new instance with default edit and display masks for the given
   * locale.
   * 
   * @param loc locale
	 */
	public PercentFormatter(Locale loc) {
	  super(loc);
  }

	/**
   * Constructs a new instance with the given edit mask and locale. Display mask
   * is identical to the edit mask.
   * 
   * @param editPattern edit mask
   * @param loc locale
	 */
	public PercentFormatter(String editPattern, Locale loc) {
	  super(editPattern, loc);
  }

	/**
   * Constructs a new instance with the given masks and locale.
   * 
   * @param editPattern edit mask
   * @param displayPattern display mask
   * @param loc locale
	 */
	public PercentFormatter(String editPattern, String displayPattern, Locale loc) {
	  super(editPattern, displayPattern, loc);
  }

	/**
   * Constructs a new instance with the given edit and display masks. Uses the
   * default locale.
   * 
   * @param editPattern edit mask
   * @param displayPattern display mask
	 */
	public PercentFormatter(String editPattern, String displayPattern) {
	  super(editPattern, displayPattern);
  }

	/**
   * Constructs a new instance with the given edit mask. Display mask is
   * identical to the edit mask, and locale is the default one.
   * 
   * @param editPattern edit mask
	 */
	public PercentFormatter(String editPattern) {
	  super(editPattern);
  }

  /**
   * Returns the current value of the text control if it is a valid <code>Number</code>.
   * If the buffer is flagged as modified, the value is recalculated by parsing
   * with the <code>nfEdit</code> initialized with the edit pattern. If the
   * number is not valid, returns <code>null</code>.
   * 
   * @return current number value if valid, <code>null</code> else
   * @see ITextFormatter#getValue()
   */
	public Object getValue() {
		Number val = (Number) super.getValue();
		if ( val != null ) {
			val = new Double(((Number) val).doubleValue() / 100);
		}
		return val;
	}

  /**
   * Sets the patterns and initializes the technical attributes used to manage
   * the operations.<p>
   * Override the NumberFormatter implementation to add the percent symbol to
   * the masks.
   * 
   * @param edit edit pattern
   * @param display display pattern
   * @param loc Locale to use
   * @throws IllegalArgumentException if a pattern is invalid
	 * @see com.wdev91.comp4swt.core.NumberFormatter#setPatterns(java.lang.String, java.lang.String, java.util.Locale)
   */
	protected void setPatterns(String edit, String display, Locale loc) {
		super.setPatterns(edit, display, loc);
		setSuffix(EMPTY + symbols.getPercent());
	}

	/**
   * Sets the value to edit. The value provided must be a <code>Number</code>.
   * The value provided is multiplied by 100.
   * 
   * @param value number value
   * @throws IllegalArgumentException if not a number
   * @see ITextFormatter#setValue(java.lang.Object)
	 */
	public void setValue(Object value) {
		if ( value != null ) {
			if ( ! (value instanceof Number) ) {
				SWT.error(SWT.ERROR_INVALID_ARGUMENT, null, "Value must be a Number"); //$NON-NLS-1$
			}
			super.setValue(new Double(((Number) value).doubleValue() * 100));
		} else {
			super.setValue(value);
		}
	}
}
