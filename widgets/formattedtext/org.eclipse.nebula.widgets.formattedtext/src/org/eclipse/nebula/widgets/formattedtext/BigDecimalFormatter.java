/*******************************************************************************
 * Copyright (c) 2014 Mario Hofmann.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Mario Hofmann (eclispe@hofmann-coswig.de) - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.formattedtext;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * This class provides formatting of {@link BigDecimal} values in a {@link FormattedText}.
 * <p>
 * BigDecimalFormatter returns different numeric types based on the current
 * value in the Text field. BigDecimalFormatter is an override of NumberFormatter
 * allowing to guaranty to always return {@link BigDecimal} values ({@link Number#toString()}).
 * </p>
 */
public class BigDecimalFormatter extends NumberFormatter {
	
	/**
	 * Constructs a new instance with all defaults.
	 * 
	 * @see NumberFormatter#NumberFormatter()
	 */
	public BigDecimalFormatter() {
		super();
	}

	/**
	 * Constructs a new instance with default edit and display masks for the given
	 * locale.
	 * 
	 * @param loc the {@link Locale locale}
	 */
	public BigDecimalFormatter(Locale loc) {
		super(loc);
	}

	/**
	 * Constructs a new instance with the given edit mask and locale. Display mask
	 * is identical to the edit mask.
	 * 
	 * @param editPattern the edit mask
	 * @param loc the {@link Locale locale}
	 */
	public BigDecimalFormatter(String editPattern, Locale loc) {
		super(editPattern, loc);
	}

	/**
	 * Constructs a new instance with the given masks and locale.
	 * 
	 * @param editPattern the edit mask
	 * @param displayPattern the display mask
	 * @param loc the {@link Locale locale}
	 */
	public BigDecimalFormatter(String editPattern, String displayPattern, Locale loc) {
		super(editPattern, displayPattern, loc);
	}

	/**
	 * Constructs a new instance with the given edit and display masks. Uses the
	 * default locale.
	 *
	 * @param editPattern the edit mask
	 * @param displayPattern the display mask
	 */
	public BigDecimalFormatter(String editPattern, String displayPattern) {
		super(editPattern, displayPattern);
	}

	/**
	 * Constructs a new instance with the given edit mask. Display mask is
	 * identical to the edit mask, and locale is the default one.
	 *
	 * @param editPattern the edit mask
	 */
	public BigDecimalFormatter(String editPattern) {
		super(editPattern);
	}

	/**
	 * Returns the current value of the text control if it is a valid {@code BigDecimal}.
	 * If the buffer is flagged as modified, the value is recalculated by parsing
	 * with the {@code nfEdit} initialized with the edit pattern. If the
	 * number is not valid, returns {@code null}.
	 *
	 * @return current {@link BigDecimal} value if valid, else {@code null}
	 * @see ITextFormatter#getValue()
	 */
	public Object getValue() {
		Object value = super.getValue();
		if (value instanceof BigDecimal) {
			return (BigDecimal) value;
		} else if (value instanceof Number) {
			return new BigDecimal(((Number) value).toString());
		} else {
			return null;
		}
	}

	/**
	 * Returns the type of value this {@link ITextFormatter} handles,
	 * i.e. returns in {@link #getValue()}.<br/>
	 * A BigDecimalFormatter always returns an BigDecimal value.
	 *
	 * @return The value type.
	 */
	public Class<BigDecimal> getValueType() {
		return BigDecimal.class;
	}
	
	  /**
	   * Sets the patterns and initializes the technical attributes used to manage
	   * the operations.<p>
	   * Override the NumberFormatter implementation to handle large numbers.
	   * 
	   * @param edit edit pattern
	   * @param display display pattern
	   * @param loc Locale to use
	   * @throws IllegalArgumentException if a pattern is invalid
		 * @see com.wdev91.comp4swt.core.NumberFormatter#setPatterns(java.lang.String, java.lang.String, java.util.Locale)
	   */	
	protected void setPatterns(String edit, String display, Locale loc) {
		super.setPatterns(edit, display, loc);
		nfEdit.setParseBigDecimal(true);
		nfDisplay.setParseBigDecimal(true);
	}
}
