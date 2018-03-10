/*******************************************************************************
 * Copyright (c) 2018 Laurent CARON. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Laurent CARON (laurent.caron@gmail.com)
 *******************************************************************************/
package org.eclipse.nebula.widgets.progresscircle.snippets;

import org.eclipse.nebula.widgets.progresscircle.ProgressCircle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A snippet for the ProgressCircle Widget
 */
public class ProgressCircleSnippet {
	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(3, false));
		shell.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

		createLeftLabel(shell, "Percent");
		final Text percent = createTextWidget(shell, 60);
		createConstraintsLabel(shell, "(1-100)");

		createLeftLabel(shell, "Circle size");
		final Text circleSize = createTextWidget(shell, 100);
		createConstraintsLabel(shell, "(<1000)");

		createLeftLabel(shell, "Thickness");
		final Text thickness = createTextWidget(shell, 10);
		createConstraintsLabel(shell, "(1-50)");

		createLeftLabel(shell, "Show percentage");
		final Button checkbox = new Button(shell, SWT.CHECK);
		final GridData gd = new GridData(GridData.FILL, GridData.CENTER, false, false);
		checkbox.setLayoutData(gd);
		checkbox.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		checkbox.setSelection(true);
		new Label(shell, SWT.NONE);

		final Button update = new Button(shell, SWT.PUSH);
		update.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false, 3, 1));
		update.setText("Redraw circle");

		final ProgressCircle circle = new ProgressCircle(shell, SWT.NONE);
		final GridData gdCircle = new GridData(GridData.CENTER, GridData.CENTER, true, true, 3, 1);
		gdCircle.minimumHeight = gdCircle.minimumWidth = 200;
		circle.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		circle.setLayoutData(gdCircle);

		circle.setPercentage(60);
		circle.setThickness(10);
		circle.setCircleSize(100);
		circle.setShowPercentage(true);

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
			//
			circle.setPercentage(percentage);
			circle.setThickness(newThickness);
			circle.setCircleSize(newCircleSize);
			circle.setShowPercentage(checkbox.getSelection());

		});

		shell.pack();
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	private static void showError(final Shell shell, final String message) {
		final MessageBox mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
		mb.setText("Error");
		mb.setMessage(message);
		mb.open();
	}

	private static void createLeftLabel(Shell shell, String text) {
		final Label lbl = new Label(shell, SWT.NONE);
		lbl.setText(text);
		lbl.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		lbl.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
	}

	private static Text createTextWidget(Shell shell, int value) {
		final Text txt = new Text(shell, SWT.BORDER);
		txt.setText(String.valueOf(value));
		txt.setTextLimit(3);
		final GridData gd = new GridData(GridData.FILL, GridData.CENTER, false, false);
		gd.minimumWidth = 40;
		txt.setLayoutData(gd);
		return txt;
	}

	private static void createConstraintsLabel(Shell shell, String text) {
		final Label lbl = new Label(shell, SWT.NONE);
		lbl.setText(text);
		lbl.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		lbl.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
	}

}
