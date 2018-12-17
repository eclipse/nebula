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
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

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

		final FloatingText txt1 = new FloatingText(group, SWT.BORDER | SWT.SEPARATOR);
		txt1.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		txt1.getText().setMessage("Last Name + Dark Separator");
		txt1.setBackground(txt1.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));

		final FloatingText txt0 = new FloatingText(group, SWT.BORDER | SWT.SEPARATOR);
		txt0.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		txt0.getText().setMessage("First Name + Light Separator");
		txt0.setBackground(txt1.getDisplay().getSystemColor(SWT.COLOR_GRAY));

		final FloatingText txt2gap = new FloatingText(group, SWT.BORDER | SWT.SEPARATOR);
		txt2gap.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		txt2gap.getText().setMessage("2 pixel blue gap");
		txt2gap.setSeparatorSpace(2);
		txt2gap.setBackground(txt1.getDisplay().getSystemColor(SWT.COLOR_BLUE));

		final FloatingText txt2 = new FloatingText(group, SWT.BORDER | SWT.SEPARATOR);
		txt2.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		txt2.getText().setMessage("Red Separator");
		txt2.setBackground(txt1.getDisplay().getSystemColor(SWT.COLOR_RED));

		final FloatingText ratio50 = new FloatingText(group, SWT.BORDER);
		ratio50.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		ratio50.getText().setMessage("Label Ratio 70%");
		ratio50.setRatio(70);

		final FloatingText ratio90 = new FloatingText(group, SWT.BORDER);
		ratio90.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		ratio90.getText().setMessage("Label Ratio 90%");
		ratio90.setRatio(90);
		
		final FloatingText flip = new FloatingText(group, SWT.BORDER | SWT.RIGHT_TO_LEFT);
		flip.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		flip.getText().setMessage("Right to Left");

		final FloatingText ratio120 = new FloatingText(group, SWT.BORDER | SWT.SEPARATOR);
		ratio120.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		ratio120.getText().setMessage("Label Ratio 120%");
		ratio120.setRatio(120);
		ratio120.setBackground(ratio120.getDisplay().getSystemColor(SWT.COLOR_YELLOW));

		final FloatingText multi = new FloatingText(group, SWT.MULTI | SWT.WRAP);
		multi.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		multi.getText().setMessage("Multi Text");
		multi.setBackground(multi.getDisplay().getSystemColor(SWT.COLOR_RED));
		
		final FloatingText multi2 = new FloatingText(group, SWT.MULTI);
		multi2.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		multi2.getText().setMessage("Multi Text 2");

		final FloatingText passw = new FloatingText(group, SWT.PASSWORD);
		passw.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		passw.getText().setMessage("Password - Borderless");

	}
}