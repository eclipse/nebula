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

import org.eclipse.nebula.paperclips.core.ImagePrint;
import org.eclipse.nebula.paperclips.core.LinePrint;
import org.eclipse.nebula.paperclips.core.PaperClips;
import org.eclipse.nebula.paperclips.core.Print;
import org.eclipse.nebula.paperclips.core.PrintJob;
import org.eclipse.nebula.paperclips.core.grid.DefaultGridLook;
import org.eclipse.nebula.paperclips.core.grid.GridPrint;
import org.eclipse.nebula.paperclips.core.text.TextPrint;
import org.eclipse.nebula.paperclips.widgets.PrintViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Example for the GridPrint class.
 * 
 * @author Matthew
 */
public class GridPrintExample {
	/**
	 * Executes the GridPrint example.
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

		PaperClips.print(new PrintJob("GridPrintExample", createPrint()),
				new PrinterData());
	}

	public static Print createPrint() {
		GridPrint grid = new GridPrint("r:72, p, d, r:d:g(3), r:d:g",
				new DefaultGridLook(5, 5));

		ImageData imageData = new ImageData(GridPrintExample.class
				.getResourceAsStream("logo.png"));
		ImagePrint image = new ImagePrint(imageData);
		image.setDPI(300, 300);

		grid.add(SWT.CENTER, image, GridPrint.REMAINDER);

		FontData fontData = new FontData("Arial", 10, SWT.BOLD);

		grid.add(new TextPrint("This column is 72 pts wide no matter what",
				fontData, SWT.RIGHT));
		grid.add(new TextPrint("Preferred size", fontData));
		grid.add(new TextPrint("Default width column", fontData));
		grid.add(new TextPrint("This is another default width column",
				fontData, SWT.CENTER));
		grid.add(new TextPrint("Default width column", fontData, SWT.RIGHT),
				GridPrint.REMAINDER);
		grid.add(new LinePrint(), GridPrint.REMAINDER);
		grid.add(SWT.CENTER, new TextPrint(
				"LOTS AND LOTS AND LOTS AND LOTS AND LOTS OF TEXT", fontData,
				SWT.CENTER), GridPrint.REMAINDER);

		GridPrint child = new GridPrint("d:g, d:g", new DefaultGridLook(10, 10));
		child.add(new TextPrint("This is a line with some text.", fontData));
		child
				.add(new TextPrint(
						"This is a line with lots of text.  Where is all this text coming from??",
						fontData));

		grid.add(SWT.LEFT, child, GridPrint.REMAINDER);

		return grid;
	}
}
