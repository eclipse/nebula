/*******************************************************************************
 * Copyright (c) 2012 Laurent CARON.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Laurent CARON (laurent.caron at gmail dot com) - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.calculator.snippets;

import org.eclipse.nebula.widgets.opal.calculator.CalculatorCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * A simple snippet for the CalculatorCombo Widget
 */
public class CalculatorComboSnippet {
	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(2, false));

		final Label label = new Label(shell, SWT.NONE);
		label.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		label.setText("Calculator combo:");

		final CalculatorCombo combo = new CalculatorCombo(shell, SWT.NONE);
		combo.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));

		combo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				System.out.println("New value is " + combo.getValue());

			}
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
