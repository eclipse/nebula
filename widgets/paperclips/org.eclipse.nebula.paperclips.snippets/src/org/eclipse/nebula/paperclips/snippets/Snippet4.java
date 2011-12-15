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
import org.eclipse.nebula.paperclips.core.Print;
import org.eclipse.nebula.paperclips.core.PrintJob;
import org.eclipse.nebula.paperclips.core.ScalePrint;
import org.eclipse.nebula.paperclips.core.border.LineBorder;
import org.eclipse.nebula.paperclips.core.grid.DefaultGridLook;
import org.eclipse.nebula.paperclips.core.grid.GridPrint;
import org.eclipse.nebula.paperclips.core.text.TextPrint;
import org.eclipse.nebula.paperclips.widgets.PrintPreview;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * Demonstrate use of ScalePrint.
 * 
 * @author Matthew
 */
public class Snippet4 {
	public static Print createPrint() {
		// Using "preferred" size columns, to force the document to be wider
		// than the page. In most cases it is
		// recommended to use "d" for "default" columns, which can shrink when
		// needed.
		DefaultGridLook look = new DefaultGridLook();
		look.setCellBorder(new LineBorder());
		GridPrint grid = new GridPrint(look);

		final int ROWS = 60;
		final int COLS = 10;

		for (int i = 0; i < COLS; i++)
			grid.addColumn("p");

		for (int r = 0; r < ROWS; r++)
			for (int c = 0; c < COLS; c++)
				grid.add(new TextPrint("Row " + r + " Col " + c));

		return new ScalePrint(grid);
	}

	/**
	 * Executes the snippet.
	 * 
	 * @param args
	 *            command-line args.
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		final Shell shell = new Shell(display);
		shell.setText("Snippet4.java");
		shell.setBounds(100, 100, 640, 480);
		shell.setLayout(new GridLayout(3, false));

		Button prevPage = new Button(shell, SWT.PUSH);
		prevPage.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT, false,
				false));
		prevPage.setText("Previous Page");

		Button nextPage = new Button(shell, SWT.PUSH);
		nextPage.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT, false,
				false));
		nextPage.setText("Next Page");

		Button printButton = new Button(shell, SWT.PUSH);
		printButton.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT, false,
				false));
		printButton.setText("Print");

		final PrintPreview preview = new PrintPreview(shell, SWT.BORDER);
		preview
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3,
						1));
		final PrintJob job = new PrintJob("Snippet4.java", createPrint());
		preview.setPrintJob(job);

		prevPage.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				int page = Math.max(preview.getPageIndex() - 1, 0);
				preview.setPageIndex(page);
			}
		});

		nextPage.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				int page = Math.min(preview.getPageIndex() + 1, preview
						.getPageCount() - 1);
				preview.setPageIndex(page);
			}
		});

		printButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				PrintDialog dialog = new PrintDialog(shell, SWT.NONE);
				PrinterData printerData = dialog.open();
				if (printerData != null) {
					PaperClips.print(job, printerData);
					// Update the preview to display according to the selected
					// printer.
					preview.setPrinterData(printerData);
				}
			}
		});

		shell.setVisible(true);

		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();

		display.dispose();
	}
}
