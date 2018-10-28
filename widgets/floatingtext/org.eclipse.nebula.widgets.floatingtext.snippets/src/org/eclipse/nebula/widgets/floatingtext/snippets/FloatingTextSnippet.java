/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Laurent CARON (laurent.caron@gmail.com) - initial API and
 * implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.floatingtext.snippets;

import org.eclipse.nebula.widgets.floatingtext.FloatingText;
import org.eclipse.nebula.widgets.opal.promptsupport.PromptSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A simple snipper for the PromptSupport utilities
 *
 */
public class FloatingTextSnippet {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(1, true));

		createText(new Group(shell, SWT.NONE));

		shell.pack();
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();

	}

	private static void createText(final Group group) {
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		group.setText("Text widgets");

		final FloatingText txt0 = new FloatingText(group, SWT.BORDER);
		txt0.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		txt0.getText().setMessage("First Name");

		final FloatingText txt1 = new FloatingText(group, SWT.BORDER);
		txt1.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		txt1.getText().setMessage("Last Name");

		final FloatingText passw = new FloatingText(group, SWT.BORDER | SWT.PASSWORD);
		passw.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		passw.getText().setMessage("Password");
		
		final FloatingText floatPrompt = new FloatingText(group, SWT.BORDER);
		floatPrompt.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		PromptSupport.setPrompt("Floating Prompt Support", floatPrompt.getText());
		
		final FloatingText blackWhite = new FloatingText(group, SWT.BORDER);
		blackWhite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		blackWhite.setBackground(group.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		blackWhite.setForeground(group.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		PromptSupport.setPrompt("Black White", blackWhite.getText());

		final FloatingText multi = new FloatingText(group, SWT.BORDER | SWT.MULTI);
		multi.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		PromptSupport.setPrompt("MultiText", multi.getText());

		final Text txt2 = new Text(group, SWT.BORDER);
		txt2.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		PromptSupport.setPrompt("Normal Text", txt2);

		final Text normalMulti = new Text(group, SWT.BORDER | SWT.MULTI);
		normalMulti.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		PromptSupport.setPrompt("Normal Multi Text", normalMulti);
	}
}