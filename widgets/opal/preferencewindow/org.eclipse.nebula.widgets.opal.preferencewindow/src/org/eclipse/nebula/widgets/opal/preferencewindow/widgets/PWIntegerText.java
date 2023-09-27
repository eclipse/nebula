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
 * Laurent CARON (laurent.caron at gmail dot com) - Initial implementation and API
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.preferencewindow.widgets;

import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.swt.SWT;

/**
 * Instances of this class are text box to type Integers
 */
public class PWIntegerText extends PWText {

	/**
	 * Constructor
	 *
	 * @param label associated label
	 * @param propertyKey associated key
	 */
	public PWIntegerText(final String label, final String propertyKey) {
		super(label, propertyKey);
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWText#addVerifyListeners()
	 */
	@Override
	public void addVerifyListeners() {
		text.addListener(SWT.Verify, e -> {
			final String string = e.text;
			final char[] chars = new char[string.length()];
			string.getChars(0, chars.length, chars, 0);
			for (int i = 0; i < chars.length; i++) {
				if (!('0' <= chars[i] && chars[i] <= '9') && e.keyCode != SWT.BS && e.keyCode != SWT.DEL) {
					e.doit = false;
					return;
				}
			}
		});
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#check()
	 */
	@Override
	public void check() {
		final Object value = PreferenceWindow.getInstance().getValueFor(getPropertyKey());
		if (value == null) {
			PreferenceWindow.getInstance().setValue(getPropertyKey(), Integer.valueOf(0));
		} else {
			if (!(value instanceof Integer)) {
				throw new UnsupportedOperationException("The property '" + getPropertyKey() + "' has to be an Integer because it is associated to a integer text widget");
			}
		}
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWText#convertValue()
	 */
	@Override
	public Integer convertValue() {
		if(text.getText().isEmpty()) {
			return (Integer) 0;
		}
		return Integer.parseInt(text.getText());
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWText#getStyle()
	 */
	@Override
	public int getStyle() {
		return SWT.NONE;
	}
}
