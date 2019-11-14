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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Instances of this class are checkboxes
 */
public class PWCheckbox extends PWWidget {

	/**
	 * Constructor
	 *
	 * @param label associated label
	 * @param propertyKey associated key
	 */
	public PWCheckbox(final String label, final String propertyKey) {
		super(label, propertyKey, 1, true);
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#build(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control build(final Composite parent) {
		if (getLabel() == null) {
			throw new UnsupportedOperationException("Please specify a label for a checkbox");
		}
		final Button button = new Button(parent, SWT.CHECK);
		addControl(button);
		button.setText(getLabel());
		final boolean originalSelection = (Boolean) PreferenceWindow.getInstance().getValueFor(getPropertyKey());
		button.setSelection(originalSelection);

		button.addListener(SWT.Selection, e -> {
			PreferenceWindow.getInstance().setValue(getPropertyKey(), button.getSelection());
		});
		return button;
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#check()
	 */
	@Override
	public void check() {
		final Object value = PreferenceWindow.getInstance().getValueFor(getPropertyKey());
		if (value == null) {
			PreferenceWindow.getInstance().setValue(getPropertyKey(), Boolean.valueOf(false));
		} else {
			if (!(value instanceof Boolean)) {
				throw new UnsupportedOperationException("The property '" + getPropertyKey() + "' has to be a Boolean because it is associated to a checkbox");
			}
		}
	}
}
