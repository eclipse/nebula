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

import javax.annotation.Generated;

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

		final FloatingText txt0 = new FloatingText(group, SWT.BORDER | SWT.SEPARATOR);
		txt0.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		txt0.getText().setMessage("First Name + Light Separator");

		final FloatingText txt1 = new FloatingText(group, SWT.BORDER | SWT.SEPARATOR);
		txt1.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		txt1.getText().setMessage("Last Name + Dark Separator");
		txt1.setBackground(txt1.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		
		final FloatingText ratio50 = new FloatingText(group, SWT.BORDER);
		ratio50.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		ratio50.getText().setMessage("Label Ratio 70%");
		ratio50.setRatio(70);
		ratio50.setBackground(ratio50.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		
		final FloatingText ratio90 = new FloatingText(group, SWT.BORDER);
		ratio90.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		ratio90.getText().setMessage("Label Ratio 100%");
		ratio90.setRatio(100);
		ratio90.setBackground(ratio90.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));

		final FloatingText passw = new FloatingText(group, SWT.PASSWORD);
		passw.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		passw.getText().setMessage("Password - Borderless");

		final FloatingText floatPrompt = new FloatingText(group, SWT.BORDER);
		floatPrompt.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		PromptSupport.setPrompt("Floating with Opal Prompt Support", floatPrompt.getText());

		final FloatingText blackWhite = new FloatingText(group, SWT.BORDER | SWT.SEPARATOR);
		blackWhite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		blackWhite.setBackgroundColors(group.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		blackWhite.setForegroundColors(group.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		PromptSupport.setPrompt("Black White Separator", blackWhite.getText());

		final FloatingText greenred = new FloatingText(group, SWT.BORDER | SWT.SEPARATOR);
		greenred.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		greenred.setForegroundColors(group.getDisplay().getSystemColor(SWT.COLOR_GREEN));
		greenred.setBackgroundColors(group.getDisplay().getSystemColor(SWT.COLOR_RED));
		greenred.setBackground(ratio90.getDisplay().getSystemColor(SWT.COLOR_YELLOW));
		PromptSupport.setPrompt("Green Red Yello", greenred.getText());

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