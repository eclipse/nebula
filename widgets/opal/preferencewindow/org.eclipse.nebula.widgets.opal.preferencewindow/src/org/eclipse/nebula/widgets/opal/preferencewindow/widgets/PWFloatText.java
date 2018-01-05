/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - Initial
 * implementation and API
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.preferencewindow.widgets;

import org.eclipse.nebula.widgets.opal.commons.StringUtil;
import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

/**
 * Instances of this class are text box to type floats
 */
public class PWFloatText extends PWText {

	/**
	 * Constructor
	 *
	 * @param label associated label
	 * @param propertyKey associated key
	 */
	public PWFloatText(final String label, final String propertyKey) {
		super(label, propertyKey);
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWText#addVerifyListeners()
	 */
	@Override
	public void addVerifyListeners() {
		text.addVerifyListener(new VerifyListener() {

			@Override
			public void verifyText(final VerifyEvent e) {
				if (e.character != 0 && !Character.isDigit(e.character) && e.keyCode != SWT.BS && e.keyCode != SWT.DEL
						&& e.character != '.' && e.character != ',') {
					e.doit = false;
					return;
				}

				e.doit = verifyEntry(e.text, e.keyCode);

			}
		});

	}

	/**
	 * Check if an entry is a float
	 *
	 * @param entry text typed by the user
	 * @param keyCode key code
	 * @return true if the user typed a float value, false otherwise
	 */
	private boolean verifyEntry(final String entry, final int keyCode) {
		final String work;
		if (keyCode == SWT.DEL) {
			work = StringUtil.removeCharAt(text.getText(), text.getCaretPosition());
		} else if (keyCode == SWT.BS && text.getCaretPosition() == 0) {
			work = StringUtil.removeCharAt(text.getText(), text.getCaretPosition() - 1);
		} else if (keyCode == 0) {
			work = entry;
		} else {
			work = StringUtil.insertString(text.getText(), entry, text.getCaretPosition());
		}

		try {
			Double.parseDouble(work.replace(',', '.'));
		} catch (final NumberFormatException nfe) {
			return false;
		}

		return true;
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#check()
	 */
	@Override
	public void check() {
		final Object value = PreferenceWindow.getInstance().getValueFor(getPropertyKey());
		if (value == null) {
			PreferenceWindow.getInstance().setValue(getPropertyKey(), new Float(0));
		} else {
			if (!(value instanceof Float)) {
				throw new UnsupportedOperationException("The property '" + getPropertyKey()
						+ "' has to be a Float because it is associated to a float text widget");
			}
		}
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWText#convertValue()
	 */
	@Override
	public Object convertValue() {
		return Float.parseFloat(text.getText());
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWText#getStyle()
	 */
	@Override
	public int getStyle() {
		return SWT.NONE;
	}

}
