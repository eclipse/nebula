/*
 * Copyright (c) 2005 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */
package org.eclipse.nebula.paperclips.snippets;

import org.eclipse.nebula.paperclips.core.ColumnPrint;
import org.eclipse.nebula.paperclips.core.PaperClips;
import org.eclipse.nebula.paperclips.core.Print;
import org.eclipse.nebula.paperclips.core.PrintJob;
import org.eclipse.nebula.paperclips.core.border.BorderPrint;
import org.eclipse.nebula.paperclips.core.border.LineBorder;
import org.eclipse.nebula.paperclips.core.text.TextPrint;
import org.eclipse.nebula.paperclips.widgets.PrintViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Example for the ColumnPrint class.
 * 
 * @author Matthew
 */
public class ColumnPrintExample {
	/**
	 * Executes the ColumnPrint example.
	 * 
	 * @param args
	 *            the command line arguments.
	 */
	public static void main(String[] args) {
		final Display display = new Display();

		Shell shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setLayout(new FillLayout());
		shell.setSize(600, 600);

		final PrintViewer preview = new PrintViewer(shell, SWT.BORDER);
		preview.setPrint(createPrint());

		shell.open();

		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();

		PaperClips.print(
				new PrintJob("ColumnPrintExample.java", createPrint()),
				new PrinterData());
	}

	public static Print createPrint() {
		StringBuffer buf = new StringBuffer(11000);
		for (int i = 1; i <= 500; i++) {
			buf.append("This is sentence #").append(i).append(".  ");
			if (i % 20 == 0)
				buf.append("\n\n");
		}

		return new ColumnPrint(new BorderPrint(new TextPrint(buf.toString()),
				new LineBorder()), 3, 18);
	}
}
