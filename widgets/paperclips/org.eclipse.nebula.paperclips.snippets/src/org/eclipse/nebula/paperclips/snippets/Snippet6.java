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

import java.util.Calendar;

import org.eclipse.nebula.paperclips.core.LinePrint;
import org.eclipse.nebula.paperclips.core.PaperClips;
import org.eclipse.nebula.paperclips.core.Print;
import org.eclipse.nebula.paperclips.core.PrintJob;
import org.eclipse.nebula.paperclips.core.border.LineBorder;
import org.eclipse.nebula.paperclips.core.grid.CellBackgroundProvider;
import org.eclipse.nebula.paperclips.core.grid.DefaultGridLook;
import org.eclipse.nebula.paperclips.core.grid.GridLook;
import org.eclipse.nebula.paperclips.core.grid.GridPrint;
import org.eclipse.nebula.paperclips.core.page.PageDecoration;
import org.eclipse.nebula.paperclips.core.page.PageNumber;
import org.eclipse.nebula.paperclips.core.page.PageNumberPrint;
import org.eclipse.nebula.paperclips.core.page.PagePrint;
import org.eclipse.nebula.paperclips.core.text.TextPrint;
import org.eclipse.nebula.paperclips.widgets.PrintViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
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
 * Demonstrate use of PagePrint and PageNumberPrint.
 * 
 * @author Matthew
 */
public class Snippet6 {
	public static Print createPrint() {
		DefaultGridLook look = new DefaultGridLook();
		look.setCellBorder(new LineBorder());
		look.setHeaderBackground(new RGB(200, 200, 200));
		look.setBodyBackgroundProvider(new CellBackgroundProvider() {
			private final RGB evenRows = new RGB(255, 255, 200);
			private final RGB oddRows = new RGB(200, 200, 255);

			public RGB getCellBackground(int row, int column, int colspan) {
				// Alternate between light yellow and light blue every 5 rows
				return (row / 5) % 2 == 0 ? evenRows : oddRows;
			}
		});

		// create a grid 50 rows tall by 5 columns wide with dummy data.
		GridPrint grid = new GridPrint("d, d, d, d, d", look);
		for (int i = 0; i < 50; i++)
			for (int j = 0; j < 5; j++)
				grid.add(new TextPrint("row " + i + ", col " + j));

		// Page footer showing a horizontal rule with a copyright statement and
		// page
		// number underneath.
		PageDecoration footer = new PageDecoration() {
			private final Print copyrightStatement;
			private final GridLook footerLook;
			{
				int year = Calendar.getInstance().get(Calendar.YEAR);
				String copyrightText = "Copyright (c) " + year
						+ " ABC Corp.  All Rights Reserved.";

				copyrightStatement = new TextPrint(copyrightText);

				footerLook = new DefaultGridLook(5, 2);
			}

			public Print createPrint(PageNumber pageNumber) {
				GridPrint grid = new GridPrint("d:g, d", footerLook);
				grid.add(new LinePrint(SWT.HORIZONTAL), GridPrint.REMAINDER);
				grid.add(copyrightStatement);
				grid.add(new PageNumberPrint(pageNumber, SWT.RIGHT));
				return grid;
			}
		};

		PagePrint page = new PagePrint(grid);
		page.setFooter(footer);
		page.setFooterGap(2);

		return page;
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
		shell.setText("Snippet6.java");
		shell.setBounds(100, 100, 640, 480);
		shell.setLayout(new GridLayout());

		Button button = new Button(shell, SWT.PUSH);
		button.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
		button.setText("Print");

		PrintViewer viewer = new PrintViewer(shell, SWT.BORDER);
		viewer.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));
		final Print print = createPrint();
		viewer.setPrint(print);

		button.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				PrintDialog dialog = new PrintDialog(shell, SWT.NONE);
				PrinterData printerData = dialog.open();
				if (printerData != null)
					PaperClips.print(new PrintJob("Snippet6.java", print)
							.setMargins(72), printerData);
			}
		});

		shell.setVisible(true);

		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();

		display.dispose();
	}
}
