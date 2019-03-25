/*******************************************************************************
 * Copyright (c) 2018 Laurent CARON All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API
 * and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.roundedcheckbox.snippets;

import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.nebula.widgets.roundedcheckbox.RoundedCheckbox;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * This snippet demonstrates the RoundedCheckBox widget
 *
 */
public class RoundedCheckBoxSnippet {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(2, false));

		// ------------------------- Checkboxes
		final Label lbl1 = new Label(shell, SWT.NONE);
		lbl1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		lbl1.setText("Simple Checkbox (2 states)");

		final Button button1 = new Button(shell, SWT.CHECK);
		button1.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));

		final Label lbl2 = new Label(shell, SWT.NONE);
		lbl2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		lbl2.setText("Simple Checkbox (Inderminate)");

		final Button button2 = new Button(shell, SWT.CHECK);
		button2.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		button2.setGrayed(true);

		// ------------------------- Rounded Checkboxes
		final Label lbl3 = new Label(shell, SWT.NONE);
		lbl3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		lbl3.setText("Rounded Checkbox (2 states)");

		final RoundedCheckbox button3 = new RoundedCheckbox(shell, SWT.NONE);
		button3.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));

		final Label lbl4 = new Label(shell, SWT.NONE);
		lbl4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		lbl4.setText("Rounded Checkbox (2 states, selected)");

		final RoundedCheckbox button4 = new RoundedCheckbox(shell, SWT.NONE);
		button4.setSelection(true);
		button4.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));

		final Label lbl5 = new Label(shell, SWT.NONE);
		lbl5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		lbl5.setText("Rounded Checkbox (3 states, selected)");

		final RoundedCheckbox button5 = new RoundedCheckbox(shell, SWT.NONE);
		button5.setSelection(true);
		button5.setGrayed(true);
		button5.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));

		// ------------------------- Rounded Checkboxes
		final Label lbl6 = new Label(shell, SWT.NONE);
		lbl6.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		lbl6.setText("Rounded Checkbox (Disabled,2 states)");

		final RoundedCheckbox button6 = new RoundedCheckbox(shell, SWT.NONE);
		button6.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		button6.setEnabled(false);

		final Label lbl7 = new Label(shell, SWT.NONE);
		lbl7.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		lbl7.setText("Rounded Checkbox (Disabled,2 states, selected)");

		final RoundedCheckbox button7 = new RoundedCheckbox(shell, SWT.NONE);
		button7.setSelection(true);
		button7.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		button7.setEnabled(false);

		final Label lbl8 = new Label(shell, SWT.NONE);
		lbl8.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		lbl8.setText("Rounded Checkbox (Disabled,3 states, selected)");

		final RoundedCheckbox button8 = new RoundedCheckbox(shell, SWT.NONE);
		button8.setSelection(true);
		button8.setGrayed(true);
		button8.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		button8.setEnabled(false);

		shell.setSize(400, 350);
		shell.open();
		SWTGraphicUtil.centerShell(shell);

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		display.dispose();
	}

}
