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
import org.eclipse.swt.graphics.Point;
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

		Point p = shell.getSize();
		p.y = (int) (shell.getMonitor().getBounds().height * 75) / 100;
		p.x = (int) (shell.getMonitor().getBounds().width * 50) / 100;
		shell.setSize(p);
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
		group.setText("Floating Text widgets");

		final FloatingText txt1 = new FloatingText(group, SWT.BORDER);
		txt1.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		txt1.getText().setMessage("Out of the box floating text");
		txt1.getText().setText("this is text");

		final FloatingText txt11w = new FloatingText(group, SWT.BORDER | SWT.SEPARATOR);
		txt11w.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		txt11w.getText().setMessage("Text with Separator");

		final FloatingText txt11x = new FloatingText(group, SWT.BORDER | SWT.SEPARATOR);
		txt11x.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		txt11x.getText().setMessage("Text + Dark Separator");
		txt11x.setBackground(txt11x.getDisplay().getSystemColor(SWT.COLOR_BLACK));

		final FloatingText txt11 = new FloatingText(group, SWT.BORDER | SWT.SEPARATOR);
		txt11.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		txt11.getText().setMessage("Text + Separator + Gray label");
		txt11.getLabel().setForeground(txt11.getDisplay().getSystemColor(SWT.COLOR_GRAY));

		final FloatingText txt112 = new FloatingText(group, SWT.BORDER | SWT.SEPARATOR);
		txt112.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		txt112.getText().setMessage("Last Name + Dark Separator + Label Background");
		txt112.getLabel().setBackground(txt112.getDisplay().getSystemColor(SWT.COLOR_GRAY));
		txt112.getLabel().setForeground(txt112.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		txt112.setBackground(txt112.getDisplay().getSystemColor(SWT.COLOR_BLACK));

		final FloatingText txt2gap = new FloatingText(group, SWT.BORDER | SWT.SEPARATOR);
		txt2gap.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		txt2gap.getText().setMessage("2 pixel blue gap");
		txt2gap.setSeparatorSpace(2);
		txt2gap.setBackground(txt11.getDisplay().getSystemColor(SWT.COLOR_BLUE));

		final FloatingText txt2 = new FloatingText(group, SWT.BORDER | SWT.SEPARATOR);
		txt2.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		txt2.getText().setMessage("Red Separator");
		txt2.setBackground(txt11.getDisplay().getSystemColor(SWT.COLOR_RED));

		final FloatingText passw = new FloatingText(group, SWT.PASSWORD);
		passw.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		passw.getText().setMessage("Password - Borderless");

		final FloatingText flip = new FloatingText(group, SWT.BORDER | SWT.RIGHT_TO_LEFT | SWT.SEPARATOR);
		flip.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		flip.getText().setMessage("Right to Left");

		final FloatingText ratio50 = new FloatingText(group, SWT.BORDER);
		ratio50.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		ratio50.getText().setMessage("Label Ratio 70%");
		ratio50.setRatio(70);

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

	}
}