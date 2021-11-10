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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Instances of this class are Combo
 *
 */
public class PWCombo extends PWWidget {

	private final List<Object> data;
	private final boolean editable;

	/**
	 * Constructor
	 *
	 * @param label associated label
	 * @param propertyKey associated key
	 */
	public PWCombo(final String label, final String propertyKey, final Object... values) {
		this(label, propertyKey, false, values);
	}

	/**
	 * Constructor
	 *
	 * @param label associated label
	 * @param propertyKey associated key
	 */
	public PWCombo(final String label, final String propertyKey, final boolean editable, final Object... values) {
		super(label, propertyKey, label == null ? 1 : 2, false);
		data = new ArrayList<Object>(Arrays.asList(values));
		this.editable = editable;
	}

	@Override
	public Control build(final Composite parent) {
		buildLabel(parent, GridData.CENTER);

		final Combo combo = new Combo(parent, SWT.BORDER | (editable ? SWT.NONE : SWT.READ_ONLY));
		addControl(combo);

		for (int i = 0; i < data.size(); i++) {
			final Object datum = data.get(i);
			combo.add(datum.toString());
			if (datum.equals(PreferenceWindow.getInstance().getValueFor(getPropertyKey()))) {
				combo.select(i);
			}
		}

		combo.addListener(SWT.Modify, event -> {
			PreferenceWindow.getInstance().setValue(getPropertyKey(), PWCombo.this.data.get(combo.getSelectionIndex()));
		});

		return combo;
	}

	@Override
	public void check() {
		final Object value = PreferenceWindow.getInstance().getValueFor(getPropertyKey());
		if (value == null) {
			PreferenceWindow.getInstance().setValue(getPropertyKey(), null);
		} else {
			if (editable && !(value instanceof String)) {
				throw new UnsupportedOperationException("The property '" + getPropertyKey() + "' has to be a String because it is associated to an editable combo");
			}

			if (!data.isEmpty()) {
				if (!value.getClass().equals(data.get(0).getClass())) {
					throw new UnsupportedOperationException("The property '" + getPropertyKey() + "' has to be a " + data.get(0).getClass() + " because it is associated to a combo");
				}
			}

		}
	}
}
