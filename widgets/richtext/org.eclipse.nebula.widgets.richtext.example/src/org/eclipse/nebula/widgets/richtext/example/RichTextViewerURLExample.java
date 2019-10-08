/*****************************************************************************
 *  Copyright (c) 2015, 2019 CEA LIST.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *		Laurent Caron <laurent.caron@gmail.com> - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.nebula.widgets.richtext.example;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.richtext.RichTextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class RichTextViewerURLExample {

	public static void main(String[] args) {
		Display display = new Display();

		final Shell shell = new Shell(display);
		shell.setText("SWT RichTextViewer with URL");
		shell.setSize(800, 600);

		shell.setLayout(new GridLayout(1, true));

		RichTextViewerURLExample example = new RichTextViewerURLExample();
		example.createControls(shell);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	public void createControls(Composite parent) {
		parent.setLayout(new FillLayout());

		final RichTextViewer viewer = new RichTextViewer(parent, SWT.BORDER | SWT.WRAP);
		viewer.setWordSplitRegex("\\s|\\-");// wrap after whitespace characters and delimiter
		GridDataFactory.fillDefaults().grab(true, true).span(1, 2).applyTo(viewer);

		viewer.setText("Please visite Nebula WebSite <a href=\"https://www.eclipse.org/nebula/\">Eclipse Nebula Web Site</a><br/>" + //
				"You can also go to <a href=\"https://github.com/eclipse/nebula\">Github</a>");
	}

}
