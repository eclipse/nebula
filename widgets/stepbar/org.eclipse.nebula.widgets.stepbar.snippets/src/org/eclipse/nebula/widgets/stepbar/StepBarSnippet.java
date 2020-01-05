/*******************************************************************************
 * Copyright (c) 2019 Akuiteo (http://www.akuiteo.com).
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API and
 * implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.stepbar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * Snippet for the Stepbar widget
 */
public class StepBarSnippet {
	private static Shell shell;
	private static int index = 0;
	private static boolean error = false;
	private static Stepbar bar;

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final Display display = new Display();
		shell = new Shell(display);
		shell.setText("Stepbar Snippet");
		shell.setLayout(new GridLayout(2, true));
		shell.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

		shell.setLayout(new GridLayout(2, false));

		final String[] texts = new String[] { "Text content for the first step\n...\n...\n...",
				"Text content for the second step\nPlease enter additional data\n...\n...",
				"Text content for the last step\nAlmoste done :)\n...\n..." };

		bar = new Stepbar(shell, SWT.BOTTOM | SWT.BORDER);
		bar.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		bar.setSteps(new String[] { "First step", "Second step", "Third step" });

		final Label lbl = new Label(shell, SWT.NONE);
		lbl.setBackground(shell.getBackground());
		lbl.setText(texts[0]);
		final GridData lblGridData = new GridData(GridData.FILL, GridData.FILL, false, false, 2, 1);
		lblGridData.widthHint = 600;
		lblGridData.heightHint = 100;
		lbl.setLayoutData(lblGridData);

		final Button previous = new Button(shell, SWT.PUSH);
		previous.setText("Previous step");
		previous.setEnabled(false);
		previous.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, false));

		final Button next = new Button(shell, SWT.PUSH);
		next.setText("Next step");
		next.setLayoutData(new GridData(GridData.END, GridData.FILL, false, false));

		previous.addListener(SWT.Selection, e -> {
			if (error) {
				e.doit = false;
				return;
			}
			index--;
			next.setEnabled(true);
			previous.setEnabled(index != 0);
			lbl.setText(texts[index]);
			bar.setCurrentStep(index);
		});
		next.addListener(SWT.Selection, e -> {
			if (error) {
				e.doit = false;
				return;
			}
			index++;
			next.setEnabled(index != bar.getSteps().size() - 1);
			previous.setEnabled(true);
			lbl.setText(texts[index]);
			bar.setCurrentStep(index);
		});

		final Label separator = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setBackground(shell.getBackground());
		separator.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 2, 1));

		final GridData bottomGD = new GridData(GridData.BEGINNING, GridData.FILL, true, false, 2, 1);
		bottomGD.widthHint = 220;

		final Button toggleBottom = new Button(shell, SWT.TOGGLE);
		toggleBottom.setText("Text above the circle (SWT.TOP)");
		toggleBottom.setLayoutData(bottomGD);
		toggleBottom.addListener(SWT.Selection, e -> {
			bar.dispose();
			bar = new Stepbar(shell, (toggleBottom.getSelection() ? SWT.TOP : SWT.BOTTOM) | SWT.BORDER);
			bar.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
			bar.setSteps(new String[] { "First step", "Second step", "Third step" });
			bar.moveAbove(lbl);
			shell.layout();
		});

		final Button toggleShowAdditionalStep = new Button(shell, SWT.TOGGLE);
		toggleShowAdditionalStep.setText("Show additional step");
		toggleShowAdditionalStep.setLayoutData(bottomGD);

		toggleShowAdditionalStep.addListener(SWT.Selection, e -> {
			if (toggleShowAdditionalStep.getSelection()) {
				bar.setSteps(new String[] { "First step", "Second step", "Additional step", "Third step" });
			} else {
				bar.setSteps(new String[] { "First step", "Second step", "Third step" });
			}
		});

		final Button toggleShowError = new Button(shell, SWT.TOGGLE);
		toggleShowError.setText("Show error on step");
		toggleShowError.setLayoutData(bottomGD);
		toggleShowError.addListener(SWT.Selection, e -> {
			error = !error;
			bar.setErrorState(error);
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

}