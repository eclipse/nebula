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

import java.math.BigInteger;
import java.util.Locale;

/**
 * This class provides formatting of {@link BigInteger} values in a {@link FormattedText}.
 * <p>
 * BigIntegerFormatter returns different numeric types based on the current
 * value in the Text field. BigIntegerFormatter is an override of NumberFormatter
 * allowing to guaranty to always return {@link BigInteger} values ({@link Number#longValue()}).
 * </p>
 */
public class BigIntegerFormatter extends NumberFormatter {
	/**
	 * Constructs a new instance with all defaults.
	 *
	 * @see NumberFormatter#NumberFormatter()
	 */
	public BigIntegerFormatter() {
		super();
	}

	/**
	 * Constructs a new instance with default edit and display masks for the given
	 * locale.
	 *
	 * @param loc the {@link Locale locale}
	 */
	public BigIntegerFormatter(Locale loc) {
		super(loc);
	}

	/**
	 * Constructs a new instance with the given edit mask and locale. Display mask
	 * is identical to the edit mask.
	 *
	 * @param editPattern the edit mask
	 * @param loc the {@link Locale locale}
	 */
	public BigIntegerFormatter(String editPattern, Locale loc) {
		super(editPattern, loc);
	}

	/**
	 * Constructs a new instance with the given masks and locale.
	 *
	 * @param editPattern the edit mask
	 * @param displayPattern the display mask
	 * @param loc the {@link Locale locale}
	 */
	public BigIntegerFormatter(String editPattern, String displayPattern, Locale loc) {
		super(editPattern, displayPattern, loc);
	}

	/**
	 * Constructs a new instance with the given edit and display masks. Uses the
	 * default locale.
	 *
	 * @param editPattern the edit mask
	 * @param displayPattern the display mask
	 */
	public BigIntegerFormatter(String editPattern, String displayPattern) {
		super(editPattern, displayPattern);
	}

	/**
	 * Constructs a new instance with the given edit mask. Display mask is
	 * identical to the edit mask, and locale is the default one.
	 *
	 * @param editPattern the edit mask
	 */
	public BigIntegerFormatter(String editPattern) {
		super(editPattern);
	}

	/**
	 * Returns the current value of the text control if it is a valid {@code BigInteger}.
	 * If the buffer is flagged as modified, the value is recalculated by parsing
	 * with the {@code nfEdit} initialized with the edit pattern. If the
	 * number is not valid, returns {@code null}.
	 *
	 * @return current {@link BigInteger} value if valid, else {@code null}
	 * @see ITextFormatter#getValue()
	 */
	public Object getValue() {
		Object value = super.getValue();
		if (value instanceof BigInteger) {
			return value;
		} else if (value instanceof Number) {
			return new BigInteger(((Number) value).toString());
		} else {
			return null;
		}
	}

	/**
	 * Returns the type of value this {@link ITextFormatter} handles,
	 * i.e. returns in {@link #getValue()}.<br>
	 * An BigIntegerFormatter always returns an BigInteger value.
	 *
	 * @return The value type.
	 */
	public Class<BigInteger> getValueType() {
		return BigInteger.class;
	}
}
