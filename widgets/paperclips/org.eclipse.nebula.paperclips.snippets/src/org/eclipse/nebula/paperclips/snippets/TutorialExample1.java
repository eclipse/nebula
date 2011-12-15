/*
 * Copyright (c) 2006 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */
package org.eclipse.nebula.paperclips.snippets;

import org.eclipse.nebula.paperclips.core.PaperClips;
import org.eclipse.nebula.paperclips.core.PrintJob;
import org.eclipse.nebula.paperclips.core.text.TextPrint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * First example in the PaperClips online tutorial.
 */
public class TutorialExample1 {
	/**
	 * Prints the words, "Hello PaperClips!"
	 * 
	 * @param args
	 *            command-line arguments.
	 */
	public static void main(String[] args) {
		// Create the document
		TextPrint text = new TextPrint("Hello PaperClips!");

		// Show the print dialog
		Display display = new Display();
		Shell shell = new Shell(display);
		PrintDialog dialog = new PrintDialog(shell, SWT.NONE);
		PrinterData printerData = dialog.open();
		shell.dispose();
		display.dispose();

		// Print the document to the printer the user selected.
		if (printerData != null) {
			PrintJob job = new PrintJob("TutorialExample1.java", text);
			job.setMargins(72);
			PaperClips.print(job, printerData);
		}
	}
}
