/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - Initial
 * implementation and API
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.preferencewindow.widgets;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.nebula.widgets.opal.commons.ResourceManager;
import org.eclipse.nebula.widgets.opal.dialog.Dialog;
import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.swt.SWT;

/**
 * Instances of this class are text box used to type URL
 */
public class PWURLText extends PWText {

	/**
	 * Constructor
	 *
	 * @param label associated label
	 * @param propertyKey associated key
	 */
	public PWURLText(final String label, final String propertyKey) {
		super(label, propertyKey);
		setWidth(200);
	}

	@Override
	public void addVerifyListeners() {
		text.addListener(SWT.FocusOut, event -> {
			try {
				new URL(PWURLText.this.text.getText());
			} catch (final MalformedURLException e) {
				Dialog.error(ResourceManager.getLabel(ResourceManager.APPLICATION_ERROR), ResourceManager.getLabel(ResourceManager.VALID_URL));
				event.doit = false;
				PWURLText.this.text.forceFocus();
			}
		});

	}

	@Override
	public void check() {
		final Object value = PreferenceWindow.getInstance().getValueFor(getPropertyKey());
		if (value == null) {
			PreferenceWindow.getInstance().setValue(getPropertyKey(), "");
		} else {
			if (!(value instanceof String)) {
				throw new UnsupportedOperationException("The property '" + getPropertyKey() + "' has to be a String because it is associated to a URL text box");
			}

			final String str = (String) value;
			if (str.equals("")) {
				PreferenceWindow.getInstance().setValue(getPropertyKey(), "");
				return;
			}

			try {
				new URL(str);
			} catch (final MalformedURLException e) {
				throw new UnsupportedOperationException("The property '" + getPropertyKey() + "' has a value (" + value + ") that is not an URL");
			}
		}
	}

	@Override
	public Object convertValue() {
		return text.getText();
	}

	@Override
	public int getStyle() {
		return SWT.NONE;
	}

}
