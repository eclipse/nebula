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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * Instances of this class are text areas
 *
 */
public class PWTextarea extends PWWidget {

	/**
	 * Constructor
	 *
	 * @param label associated label
	 * @param propertyKey associated key
	 */
	public PWTextarea(final String label, final String propertyKey) {
		super(label, propertyKey, label == null ? 1 : 2, false);
		setGrabExcessSpace(true);
		setHeight(50);
		setWidth(350);
	}

	@Override
	public Control build(final Composite parent) {
		buildLabel(parent, GridData.BEGINNING);

		final Text text = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		addControl(text);
		text.setText(PreferenceWindow.getInstance().getValueFor(getPropertyKey()).toString());
		text.addListener(SWT.FocusOut, event -> {
			PreferenceWindow.getInstance().setValue(getPropertyKey(), text.getText());
		});

		return text;
	}

	@Override
	public void check() {
		final Object value = PreferenceWindow.getInstance().getValueFor(getPropertyKey());
		if (value == null) {
			PreferenceWindow.getInstance().setValue(getPropertyKey(), "");
		} else {
			if (!(value instanceof String)) {
				throw new UnsupportedOperationException("The property '" + getPropertyKey() + "' has to be a String because it is associated to a textarea");
			}
		}
	}

}
