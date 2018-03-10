/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Laurent CARON (laurent.caron@gmail.com)
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.nebulaslider.snippets;

import org.eclipse.nebula.widgets.opal.nebulaslider.NebulaSlider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A simple snippet for the TextAssist Widget
 */
public class NebulaSliderSnippet {
	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(2, false));
		shell.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

		createLeftLabel(shell, "Minimum");
		final Text min = createTextWidget(shell, 0);

		createLeftLabel(shell, "Maximum");
		final Text max = createTextWidget(shell, 1000);

		createLeftLabel(shell, "Value");
		final Text value = createTextWidget(shell, 632);

		final Button update = new Button(shell, SWT.PUSH);
		update.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false, 2, 1));
		update.setText("Redraw Slider");

		final NebulaSlider slider = new NebulaSlider(shell, SWT.NONE);
		slider.setMinimum(0);
		slider.setMaximum(1000);
		slider.setValue(632);
		slider.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

		slider.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("New value is " + slider.getValue());
			}
		});

		final GridData layoutData = new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1);
		layoutData.widthHint = 600;
		layoutData.heightHint = 50;
		slider.setLayoutData(layoutData);

		update.addListener(SWT.Selection, e -> {
			int minValue = 0;
			try {
				minValue = Integer.valueOf(min.getText());
			} catch (final NumberFormatException nfe) {
				showError(shell, "The value [" + min.getText() + "] is not a number");
				return;
			}

			int maxValue = 0;
			try {
				maxValue = Integer.valueOf(max.getText());
			} catch (final NumberFormatException nfe) {
				showError(shell, "The value [" + max.getText() + "] is not a number");
				return;
			}

			if (maxValue < minValue) {
				showError(shell, "The minimum is greater than the maximum");
				return;
			}
			//
			int newValue = 0;
			try {
				newValue = Integer.valueOf(value.getText());
			} catch (final NumberFormatException nfe) {
				showError(shell, "The value [" + value.getText() + "] is not a number");
				return;
			}
			if (newValue < minValue || newValue > maxValue) {
				showError(shell, "The value [" + newValue + "] should be between " + minValue + " and " + maxValue);
				return;
			}
			//
			slider.setMinimum(minValue);
			slider.setMaximum(maxValue);
			slider.setValue(newValue);

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

	private static void showError(final Shell shell, final String message) {
		final MessageBox mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
		mb.setText("Error");
		mb.setMessage(message);
		mb.open();
	}
}
