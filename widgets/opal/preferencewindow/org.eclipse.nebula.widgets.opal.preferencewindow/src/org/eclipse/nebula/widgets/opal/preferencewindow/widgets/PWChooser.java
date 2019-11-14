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

import org.eclipse.nebula.widgets.opal.commons.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Abstract class for chooser widgets
 *
 */
public abstract class PWChooser extends PWWidget {

	/**
	 * Constructor
	 *
	 * @param label associated label
	 * @param propertyKey associated key
	 */
	public PWChooser(final String label, final String propertyKey) {
		super(label, propertyKey, 3, false);
		setGrabExcessSpace(false);
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#build(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control build(final Composite parent) {
		final Label label = new Label(parent, SWT.NONE);

		if (getLabel() == null) {
			throw new UnsupportedOperationException("You need to set a label for a directory or a dialog chooser");
		} else {
			label.setText(getLabel());
		}
		addControl(label);
		final GridData labelGridData = new GridData(GridData.END, GridData.BEGINNING, false, false);
		labelGridData.horizontalIndent = getIndent();
		label.setLayoutData(labelGridData);

		final Text text = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
		addControl(text);
		final GridData textGridData = new GridData(GridData.FILL, GridData.BEGINNING, true, false);
		text.setLayoutData(textGridData);

		final Button button = new Button(parent, SWT.PUSH);
		addControl(button);
		final GridData buttonGridData = new GridData(GridData.FILL, GridData.BEGINNING, false, false);
		buttonGridData.widthHint = 150;
		button.setText(ResourceManager.getLabel(ResourceManager.CHOOSE) + "...");
		button.setLayoutData(buttonGridData);

		setButtonAction(text, button);

		return button;

	}

	/**
	 * Code executed when the user presses the button
	 *
	 * @param text text box
	 * @param button associated button
	 */
	protected abstract void setButtonAction(Text text, Button button);

}
