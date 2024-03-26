/*******************************************************************************
 * Copyright (c) 2020 Patrik Dufresne (http://www.patrikdufresne.com/).
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Patrik Dufresne (info at patrikdufresne dot com) - initial API and
 * implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.fontawesome;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class FontAwesomeSnippet2 {

	public static void main(final String[] args) throws IllegalArgumentException, IllegalAccessException {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("FontAwesome Snippet");
		shell.setSize(600, 600);
		shell.setLayout(new GridLayout(1, false));

		// Label
		new Label(shell, SWT.NONE).setText("In Label");
		Label text = new Label(shell, SWT.NONE);
		text.setFont(FontAwesome.getFont(22));
		text.setText(FontAwesome.code);

		// Button
		new Label(shell, SWT.NONE).setText("In Button");
		Composite comp1 = new Composite(shell, SWT.NONE);
		comp1.setLayout(new RowLayout());
		Button button1 = new Button(comp1, SWT.NONE);
		button1.setFont(FontAwesome.getFont(10));
		button1.setText(FontAwesome.plus + " plus");
		Button button2 = new Button(comp1, SWT.NONE);
		button2.setFont(FontAwesome.getFont(10));
		button2.setText(FontAwesome.minus + " minus");

		// Toolbar
		new Label(shell, SWT.NONE).setText("In ToolBar");
		ToolBar toolbar = new ToolBar(shell, SWT.NONE);
		toolbar.setFont(FontAwesome.getFont(15));
		ToolItem item1 = new ToolItem(toolbar, SWT.NONE);
		item1.setText(FontAwesome.align_left);
		ToolItem item2 = new ToolItem(toolbar, SWT.NONE);
		item2.setText(FontAwesome.align_center);
		ToolItem item3 = new ToolItem(toolbar, SWT.NONE);
		item3.setText(FontAwesome.align_right);
		new ToolItem(toolbar, SWT.SEPARATOR);
		ToolItem item4 = new ToolItem(toolbar, SWT.NONE);
		item4.setText(FontAwesome.quote_left);
		new ToolItem(toolbar, SWT.SEPARATOR);
		ToolItem item5 = new ToolItem(toolbar, SWT.NONE);
		item5.setText(FontAwesome.question);

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