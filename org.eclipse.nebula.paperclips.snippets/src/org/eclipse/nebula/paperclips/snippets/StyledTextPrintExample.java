/*
 * Copyright (c) 2007 Matthew Hall and others.
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
import org.eclipse.nebula.paperclips.core.text.StyledTextPrint;
import org.eclipse.nebula.paperclips.core.text.TextStyle;
import org.eclipse.nebula.paperclips.widgets.PrintPreview;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * Demonstrates use of the StyledTextPrint class.
 * 
 * @author Matthew
 */
public class StyledTextPrintExample {
	/**
	 * Executes the StyledTextPrint example.
	 * 
	 * @param args
	 *            the command line arguments.
	 */
	public static void main(String[] args) {
		final Display display = new Display();

		final Shell shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setText("StyledTextPrintExample.java");
		shell.setLayout(new GridLayout());
		shell.setSize(600, 800);

		final PrintJob job = new PrintJob("StyledTextPrintExample.java",
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
				PrintDialog dialog = new PrintDialog(shell);
				PrinterData printerData = dialog.open();
				if (printerData != null)
					PaperClips.print(job, printerData);
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
		StyledTextPrint doc = new StyledTextPrint();

		TextStyle normal = new TextStyle().font("Arial", 14, SWT.NORMAL);
		TextStyle bold = normal.fontStyle(SWT.BOLD);
		TextStyle big = normal.fontHeight(20);
		TextStyle italic = normal.fontStyle(SWT.ITALIC);
		TextStyle monospace = normal.fontName("Courier");
		TextStyle underline = normal.underline();
		TextStyle strikeout = normal.strikeout();

		doc
				.setStyle(normal)
				.append("This snippet demonstrates the use of ")
				.append("StyledTextPrint", monospace)
				.append(" for creating bodies of styled text.")
				.newline()
				.newline()
				.append("StyledTextPrint", monospace)
				.append(" makes sure that ")
				.append("text ", bold)
				.append("of ", italic)
				.append("different ", normal.fontHeight(20))
				.append("font ", normal.fontHeight(42))
				.append("names,", normal.fontName("Courier"))
				.append(" sizes, ", normal.fontHeight(10))
				.append("and ")
				.append("styles", normal.underline())
				.append(" are aligned correctly along the base line.")
				.newline()
				.newline()
				.append("With ")
				.append("StyledTextPrint", monospace)
				.append(
						" you can embed any other printable element alongside the text.  ")
				.append("For example, here is an image ")
				.append(createSampleImage())
				.append(" and a horizontal line")
				.append(new LinePrint(SWT.HORIZONTAL))
				.newline()
				.setStyle(italic)
				.append(
						"Note that some elements like GridPrint tend to be broken unnaturally across lines, and "
								+ "therefore may not be suitable for use in a StyledTextPrint.")
				.setStyle(normal).newline().newline().append(
						"Many text styles are possible such as ").append(
						"bold print", bold).append(", ").append("italic print",
						italic).append(", ")
				.append("strikeout text", strikeout).append(", ").append(
						"underlined text", underline).append(", or ").append(
						"any combination of the above",
						normal.fontStyle(SWT.BOLD | SWT.ITALIC).strikeout()
								.underline()).append(".").newline().newline()
				.append("You can also set ").append("foreground colors",
						normal.foreground(0x00A000)).append(" or ").append(
						"background colors", normal.background(0xFFFFA0))
				.append(" on the text through the TextStyle class.").newline()
				.newline().append("Enjoy!", big);
		return doc;
	}

	private static ImagePrint createSampleImage() {
		return new ImagePrint(new ImageData(StyledTextPrintExample.class
				.getResourceAsStream("sp.png")), new Point(600, 600));
	}
}
