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

import org.eclipse.nebula.widgets.progresscircle.ProgressCircle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

class PercentagePanel extends BasePanel {

	public PercentagePanel(Shell shell) {
		final Color white = shell.getDisplay().getSystemColor(SWT.COLOR_WHITE);

		final Group group = new Group(shell, SWT.NONE);
		group.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		group.setText("Percentage");
		group.setLayout(new GridLayout(3, false));
		group.setBackground(white);

		createLeftLabel(group, "Percent");
		final Text percent = createTextWidget(group, 60);
		createConstraintsLabel(group, "(1-100)");

		createCommonPart(group);

		final Button update = new Button(group, SWT.PUSH);
		update.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false, 3, 1));
		update.setText("Redraw circle");

		final ProgressCircle circle = new ProgressCircle(group, SWT.NONE);
		final GridData gdCircle = new GridData(GridData.CENTER, GridData.CENTER, true, true, 3, 1);
		gdCircle.minimumHeight = gdCircle.minimumWidth = 200;
		circle.setBackground(white);
		circle.setLayoutData(gdCircle);
		circle.setTextPattern(ProgressCircle.PERCENTAGE_PATTERN);

		circle.setSelection(60);
		circle.setThickness(10);
		circle.setCircleSize(100);
		circle.setShowText(true);

		update.addListener(SWT.Selection, e -> {
			int percentage = 0;
			try {
				percentage = Integer.valueOf(percent.getText());
			} catch (final NumberFormatException nfe) {
				showError(shell, "The value [" + percent.getText() + "] is not a number");
				return;
			}
			if (percentage < 0 || percentage > 100) {
				showError(shell, "The value [" + percentage + "] should be between 0 and 100");
				return;
			}
			//
			int newCircleSize = 0;
			try {
				newCircleSize = Integer.valueOf(circleSize.getText());
			} catch (final NumberFormatException nfe) {
				showError(shell, "The value [" + circleSize.getText() + "] is not a number");
				return;
			}
			if (newCircleSize > 1000) {
				showError(shell, "The value [" + newCircleSize + "] should be between lower than 1000");
				return;
			}
			//
			int newThickness = 0;
			try {
				newThickness = Integer.valueOf(thickness.getText());
			} catch (final NumberFormatException nfe) {
				showError(shell, "The value [" + thickness.getText() + "] is not a number");
				return;
			}
			if (newThickness < 1 || newThickness > 50) {
				showError(shell, "The value [" + newThickness + "] should be between 1 and 50");
				return;
			}
			int newDelay = 0;
			try {
				newDelay = Integer.valueOf(delay.getText());
			} catch (final NumberFormatException nfe) {
				showError(shell, "The value [" + delay.getText() + "] is not a number");
				return;
			}
			if (newDelay < 1 || newDelay > 5000) {
				showError(shell, "The value [" + newDelay + "] should be between 1 and 5000");
				return;
			}
			//
			circle.setAnimationDelay(newDelay);
			circle.setSelection(percentage);
			circle.setThickness(newThickness);
			circle.setCircleSize(newCircleSize);
			circle.setShowText(checkbox.getSelection());

		});
	}

}
