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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Instances of this class are a group of radio buttons
 *
 */
public class PWRadio extends PWWidget {

	private final List<Object> data;
	private final List<Button> buttons;

	/**
	 * Constructor
	 *
	 * @param label associated label
	 * @param propertyKey associated key
	 */
	public PWRadio(final String label, final String prop, final Object... values) {
		super(null, prop, label == null ? 1 : 2, false);
		data = new ArrayList<Object>(Arrays.asList(values));
		buttons = new ArrayList<Button>();
	}

	@Override
	public Control build(final Composite parent) {
		buildLabel(parent, GridData.BEGINNING);

		final Composite composite = new Composite(parent, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = gridLayout.marginWidth = 0;
		composite.setLayout(gridLayout);

		for (final Object datum : data) {
			final Button button = new Button(composite, SWT.RADIO);
			addControl(button);
			button.setText(datum.toString());
			button.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
			button.setSelection(datum.equals(PreferenceWindow.getInstance().getValueFor(getPropertyKey())));
			button.setData(datum);
			button.addListener(SWT.Selection, event -> {
				if (button.getSelection()) {
					PreferenceWindow.getInstance().setValue(getPropertyKey(), button.getData());
				}
			});

			buttons.add(button);
		}
		return composite;
	}

	@Override
	public void check() {
		final Object value = PreferenceWindow.getInstance().getValueFor(getPropertyKey());
		if (value == null) {
			PreferenceWindow.getInstance().setValue(getPropertyKey(), null);
		} else {
			if (!data.isEmpty()) {
				if (!value.getClass().equals(data.get(0).getClass())) {
					throw new UnsupportedOperationException("The property '" + getPropertyKey() + "' has to be a " + data.get(0).getClass() + " because it is associated to a combo");
				}
			}
		}
	}

}
