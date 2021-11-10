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
 * This is the abstract class for all text widgets (except textarea)
 */
public abstract class PWText extends PWWidget {

	protected Text text;

	/**
	 * Constructor
	 *
	 * @param label associated label
	 * @param propertyKey associated property key
	 */
	public PWText(final String label, final String propertyKey) {
		super(label, propertyKey, label == null ? 1 : 2, false);
		setGrabExcessSpace(true);
	}

	@Override
	public Control build(final Composite parent) {
		buildLabel(parent, GridData.CENTER);
		text = new Text(parent, SWT.BORDER | getStyle());
		addControl(text);
		addVerifyListeners();
		text.setText(PreferenceWindow.getInstance().getValueFor(getPropertyKey()).toString());
		text.addListener(SWT.Modify, event -> {
			PreferenceWindow.getInstance().setValue(getPropertyKey(), convertValue());
		});
		return text;
	}

	/**
	 * Add the verify listeners
	 */
	public abstract void addVerifyListeners();

	/**
	 * @return the value of the data typed by the user in the correct format
	 */
	public abstract Object convertValue();

	/**
	 * @return the style (SWT.NONE or SWT.PASSWORD)
	 */
	public abstract int getStyle();

}
