/*******************************************************************************
 * Copyright (c) 2018 Laurent CARON.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent CARON (laurent.caron@gmail.com)
 *******************************************************************************/
package org.eclipse.nebula.widgets.progresscircle.snippets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

class BasePanel {

	protected Text circleSize;
	protected Text thickness;
	protected Text delay;
	protected Button checkbox;

	protected void createCommonPart(final Group group) {
		final Color white = group.getDisplay().getSystemColor(SWT.COLOR_WHITE);

		createLeftLabel(group, "Circle size");
		circleSize = createTextWidget(group, 100);
		createConstraintsLabel(group, "(<1000)");

		createLeftLabel(group, "Thickness");
		thickness = createTextWidget(group, 10);
		createConstraintsLabel(group, "(1-50)");

		createLeftLabel(group, "Delay");
		delay = createTextWidget(group, 10);
		createConstraintsLabel(group, "(delay in millis)");
		
		createLeftLabel(group, "Show text");
		checkbox = new Button(group, SWT.CHECK);
		final GridData gd = new GridData(GridData.FILL, GridData.CENTER, false, false);
		checkbox.setLayoutData(gd);
		checkbox.setBackground(white);
		checkbox.setSelection(true);
		new Label(group, SWT.NONE);
	}

	protected void showError(final Shell shell, final String message) {
		final MessageBox mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
		mb.setText("Error");
		mb.setMessage(message);
		mb.open();
	}

	protected void createLeftLabel(Composite composite, String text) {
		final Label lbl = new Label(composite, SWT.NONE);
		lbl.setText(text);
		lbl.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		lbl.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
	}

	protected Text createTextWidget(Composite composite, int value) {
		final Text txt = new Text(composite, SWT.BORDER);
		txt.setText(String.valueOf(value));
		txt.setTextLimit(4);
		final GridData gd = new GridData(GridData.FILL, GridData.CENTER, false, false);
		gd.minimumWidth = 40;
		txt.setLayoutData(gd);
		return txt;
	}

	protected void createConstraintsLabel(Composite composite, String text) {
		final Label lbl = new Label(composite, SWT.NONE);
		lbl.setText(text);
		lbl.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		lbl.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
	}
}
