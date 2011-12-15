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
 * Example for the GridPrint class.
 * 
 * @author Matthew
 */
public class GridPrintVerticalAlignmentExample {
	/**
	 * Executes the GridPrint example.
	 * 
	 * @param args
	 *            the command line arguments.
	 */
	public static void main(String[] args) {
		final Display display = new Display();

		Shell shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setText("GridPrintVerticalAlignmentExample.java");
		shell.setLayout(new GridLayout());
		shell.setSize(600, 800);

		final PrintJob job = new PrintJob(
				"GridPrintVerticalAlignmentExample.java", createPrint());

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
		DefaultGridLook look = new DefaultGridLook(5, 5);
		look.setHeaderGap(5);
		GridPrint grid = new GridPrint("d:g, d, d:g, d, d:g, d, d:g", look);

		ImageData imageData = new ImageData(
				GridPrintVerticalAlignmentExample.class
						.getResourceAsStream("logo.png"));
		ImagePrint image = new ImagePrint(imageData);
		image.setDPI(300, 300);

		Print verticalRule = new LinePrint(SWT.VERTICAL);

		grid.addHeader(SWT.CENTER, SWT.DEFAULT, new TextPrint("Column 1"));
		grid.addHeader(SWT.DEFAULT, SWT.FILL, verticalRule);
		grid.addHeader(SWT.CENTER, SWT.DEFAULT, new TextPrint("Column 2"));
		grid.addHeader(SWT.DEFAULT, SWT.FILL, verticalRule);
		grid.addHeader(SWT.CENTER, SWT.DEFAULT, new TextPrint("Column 3"));
		grid.addHeader(SWT.DEFAULT, SWT.FILL, verticalRule);
		grid.addHeader(SWT.CENTER, SWT.DEFAULT, new TextPrint("Column 4"));

		grid.addHeader(new LinePrint(SWT.HORIZONTAL), GridPrint.REMAINDER);

		grid.add(SWT.LEFT, SWT.CENTER, image);
		grid.add(SWT.DEFAULT, SWT.FILL, verticalRule);
		grid.add(SWT.DEFAULT, SWT.DEFAULT,
				new TextPrint("triple\nline\nleft\n"));
		grid.add(SWT.DEFAULT, SWT.FILL, verticalRule);
		grid.add(SWT.CENTER, SWT.CENTER, new TextPrint("double line\ncenter",
				SWT.CENTER));
		grid.add(SWT.DEFAULT, SWT.FILL, verticalRule);
		grid.add(SWT.RIGHT, SWT.BOTTOM, new TextPrint("single line right"));

		grid.add(new LinePrint(SWT.HORIZONTAL), GridPrint.REMAINDER);

		grid.add(SWT.CENTER, SWT.CENTER, new TextPrint(
				"several\nlines\nof\ntext\nhere", SWT.CENTER));
		grid.add(SWT.DEFAULT, SWT.FILL, verticalRule);
		grid.add(SWT.LEFT, SWT.FILL, verticalRule);
		grid.add(SWT.DEFAULT, SWT.FILL, verticalRule);
		grid.add(SWT.CENTER, SWT.FILL, verticalRule);
		grid.add(SWT.DEFAULT, SWT.FILL, verticalRule);
		grid.add(SWT.RIGHT, SWT.FILL, verticalRule);

		return grid;
	}
}
