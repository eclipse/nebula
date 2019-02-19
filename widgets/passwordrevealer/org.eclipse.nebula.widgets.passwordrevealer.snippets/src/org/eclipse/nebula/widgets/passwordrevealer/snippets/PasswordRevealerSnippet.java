/*******************************************************************************
 * Copyright (c) 2019 Laurent CARON. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Laurent CARON (laurent.caron@gmail.com)
 *******************************************************************************/
package org.eclipse.nebula.widgets.passwordrevealer.snippets;

import org.eclipse.nebula.widgets.passwordrevealer.PasswordRevealer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * A snippet for the Password Revealer Widget
 */
public class PasswordRevealerSnippet {
	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(1, false));
		final Color white = display.getSystemColor(SWT.COLOR_WHITE);
		shell.setBackground(white);

		final Label lbl1 = new Label(shell, SWT.NONE);
		lbl1.setText("Password Revealer:");
		final GridData gd1 = new GridData(GridData.BEGINNING, GridData.CENTER, true, false);
		gd1.widthHint = 150;
		lbl1.setBackground(white);
		lbl1.setLayoutData(gd1);

		final PasswordRevealer revealer = new PasswordRevealer(shell, SWT.NONE);
		final GridData gd = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gd.widthHint = 250;
		revealer.setLayoutData(gd);
		revealer.setBackground(white);

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
