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
import org.eclipse.swt.widgets.Spinner;

/**
 * Instances of this class are spinners
 */
public class PWSpinner extends PWWidget {
	private final int max;
	private final int min;

	/**
	 * Constructor
	 *
	 * @param label associated label
	 * @param propertyKey associated key
	 * @param min minimum value
	 * @param max maximum value
	 */
	public PWSpinner(final String label, final String propertyKey, final int min, final int max) {
		super(label, propertyKey, label == null ? 1 : 2, false);
		this.min = min;
		this.max = max;
	}

	@Override
	public Control build(final Composite parent) {
		buildLabel(parent, GridData.CENTER);
		final Spinner spinner = new Spinner(parent, SWT.HORIZONTAL | SWT.BORDER);
		addControl(spinner);
		spinner.setMinimum(min);
		spinner.setMaximum(max);
		final Integer originalValue = (Integer) PreferenceWindow.getInstance().getValueFor(getPropertyKey());
		spinner.setSelection(originalValue.intValue());

		spinner.addListener(SWT.Modify, event -> {
			PreferenceWindow.getInstance().setValue(getPropertyKey(), Integer.valueOf(spinner.getSelection()));
		});

		return spinner;
	}

	@Override
	public void check() {
		final Object value = PreferenceWindow.getInstance().getValueFor(getPropertyKey());
		if (value == null) {
			PreferenceWindow.getInstance().setValue(getPropertyKey(), Integer.valueOf(min));
		} else {
			if (!(value instanceof Integer)) {
				throw new UnsupportedOperationException("The property '" + getPropertyKey() + "' has to be an Integer because it is associated to a spinner");
			}

			final int valueAsInt = ((Integer) value).intValue();
			if (valueAsInt < min || valueAsInt > max) {
				throw new UnsupportedOperationException("The property '" + getPropertyKey() + "' is out of range (value is " + valueAsInt + ", range is " + min + "-" + max + ")");
			}
		}
	}

}
