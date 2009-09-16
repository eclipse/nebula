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
import org.eclipse.nebula.paperclips.core.PaperClips;
import org.eclipse.nebula.paperclips.core.Print;
import org.eclipse.nebula.paperclips.core.PrintJob;
import org.eclipse.nebula.paperclips.core.grid.GridPrint;
import org.eclipse.nebula.paperclips.core.page.PageDecoration;
import org.eclipse.nebula.paperclips.core.page.PageNumber;
import org.eclipse.nebula.paperclips.core.page.PageNumberPrint;
import org.eclipse.nebula.paperclips.core.page.PagePrint;
import org.eclipse.nebula.paperclips.core.text.TextPrint;
import org.eclipse.nebula.paperclips.widgets.PrintPreview;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * Demonstrate use of PagePrint and friends PageDecoration, PageNumberPrint, and
 * PageNumber.
 * 
 * @author Matthew
 */
public class PagePrintExample {
	/**
	 * Executes the GridPrint example.
	 * 
	 * @param args
	 *            the command line arguments.
	 */
	public static void main(String[] args) {
		final Display display = new Display();

		Shell shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setText("PagePrintExample.java");
		shell.setLayout(new GridLayout());
		shell.setSize(600, 800);

		final PrintJob job = new PrintJob("PagePrintExample.java",
				createPrint());

		Composite buttonPanel = new Composite(shell, SWT.NONE);
		buttonPanel
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		buttonPanel.setLayout(new RowLayout(SWT.HORIZONTAL));

		final PrintPreview preview = new PrintPreview(shell, SWT.BORDER);

		Button prev = new Button(buttonPanel, SWT.PUSH);
		prev.setText("<< Prev");
		prev.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				preview.setPageIndex(Math.max(preview.getPageIndex() - 1, 0));
			}
		});

		Button next = new Button(buttonPanel, SWT.PUSH);
		next.setText("Next >>");
		next.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				preview.setPageIndex(Math.min(preview.getPageIndex() + 1,
						preview.getPageCount() - 1));
			}
		});

		Button print = new Button(buttonPanel, SWT.PUSH);
		print.setText("Print");
		print.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				PaperClips.print(job, new PrinterData());
			}
		});

		preview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		preview.setFitHorizontal(true);
		preview.setFitVertical(true);
		preview.setPrintJob(job);

		shell.open();

		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();

		display.dispose();
	}

	public static Print createPrint() {
		PageDecoration header = createHeader();
		Print body = createBody();
		PageDecoration footer = createFooter();

		PagePrint page = new PagePrint(header, body, footer);
		page.setHeaderGap(72 / 4);
		page.setFooterGap(72 / 8);

		return page;
	}

	public static PageDecoration createHeader() {
		PageDecoration header = new PageDecoration() {
			public Print createPrint(PageNumber pageNumber) {
				// Only show a header on the first page
				if (pageNumber.getPageNumber() == 0) {
					ImageData imageData = new ImageData(PagePrintExample.class
							.getResourceAsStream("logo.png"));
					ImagePrint image = new ImagePrint(imageData);
					image.setDPI(300, 300);
					return image;
				}

				return null;
			}
		};
		return header;
	}

	public static PageDecoration createFooter() {
		PageDecoration footer = new PageDecoration() {
			public Print createPrint(PageNumber pageNumber) {
				GridPrint grid = new GridPrint("d:g, r:d");
				grid.add(new TextPrint(
						"Copyright 2006 ABC Corp, All Rights Reserved"));
				grid.add(new PageNumberPrint(pageNumber, SWT.RIGHT));
				return grid;
			}
		};
		return footer;
	}

	public static Print createBody() {
		GridPrint grid = new GridPrint();

		final int ROWS = 200;
		final int COLS = 4;
		for (int c = 0; c < COLS; c++) {
			grid.addColumn("d:g");
			grid.addHeader(new TextPrint("Column " + c));
		}

		for (int r = 0; r < ROWS; r++)
			for (int c = 0; c < COLS; c++)
				grid.add(new TextPrint("Cell (" + c + ", " + r + ")"));

		return grid;
	}
}
