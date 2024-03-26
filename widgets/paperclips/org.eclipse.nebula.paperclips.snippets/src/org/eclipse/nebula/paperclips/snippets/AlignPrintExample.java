/*
 * Copyright (c) 2006 Matthew Hall and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */
package org.eclipse.nebula.paperclips.snippets;

import org.eclipse.nebula.paperclips.core.AlignPrint;
import org.eclipse.nebula.paperclips.core.PaperClips;
import org.eclipse.nebula.paperclips.core.Print;
import org.eclipse.nebula.paperclips.core.PrintJob;
import org.eclipse.swt.SWT;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Prints the contents of TutorialExample2, but centered horizontally and
 * vertically on the page.
 * 
 * @author Matthew
 */
public class AlignPrintExample {
	public static Print createPrint() {
		Print print = TutorialExample2.createPrint();
		return new AlignPrint(print, SWT.CENTER, SWT.CENTER);
	}

	/**
	 * Prints the BreakPrintExample to the default printer.
	 * 
	 * @param args
	 *            command-line args
	 */
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display, SWT.SHELL_TRIM);
		PrintDialog dialog = new PrintDialog(shell, SWT.NONE);
		PrinterData printerData = dialog.open();
		shell.dispose();
		display.dispose();
		if (printerData != null)
			PaperClips.print(new PrintJob("AlignPrintExample.java",
					createPrint()), printerData);
	}
}
