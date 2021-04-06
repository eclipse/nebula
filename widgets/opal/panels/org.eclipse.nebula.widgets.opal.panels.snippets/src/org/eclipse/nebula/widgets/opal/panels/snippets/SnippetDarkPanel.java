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
 * Laurent CARON (laurent.caron at gmail dot com) - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.panels.snippets;

import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.nebula.widgets.opal.dialog.Dialog;
import org.eclipse.nebula.widgets.opal.panels.DarkPanel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * This snippet demonstrates the dark panel
 *
 */
public class SnippetDarkPanel {
	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell();
		shell.setText("Dark Panel Sample");
		shell.setLayout(new GridLayout(2, false));

		createRow(shell, "First Name");
		createRow(shell, "Last Name");
		createRow(shell, "E-mail");
		createRow(shell, "Phone number");

		createButtons(shell);

		shell.setSize(shell.computeSize(400, 400));
		SWTGraphicUtil.centerShell(shell);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	private static void createRow(final Shell shell, final String label) {
		final Label lbl = new Label(shell, SWT.NONE);
		lbl.setText(label);
		lbl.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

		final Text text = new Text(shell, SWT.SINGLE | SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	}

	private static void createButtons(final Shell shell) {
		final DarkPanel p = new DarkPanel(shell);

		final Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		composite.setLayout(new GridLayout(2, false));

		final Button ok = new Button(composite, SWT.PUSH);
		ok.setText("Ok");
		ok.setLayoutData(new GridData(SWT.END, SWT.END, true, true));
		ok.addListener(SWT.Selection, e -> {
			p.show();
			Dialog.isConfirmed("Confirmation", "Are you sure you want to save this form ?");
			p.hide();
		});

		final Button cancel = new Button(composite, SWT.PUSH);
		cancel.setText("Cancel");
		cancel.setLayoutData(new GridData(SWT.CENTER, SWT.END, false, true));
		cancel.addListener(SWT.Selection, e -> {
			shell.dispose();
		});
	}

}
