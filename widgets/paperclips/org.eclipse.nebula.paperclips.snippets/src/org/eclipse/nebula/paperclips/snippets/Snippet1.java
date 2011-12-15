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

import org.eclipse.nebula.paperclips.core.ImagePrint;
import org.eclipse.nebula.paperclips.core.PaperClips;
import org.eclipse.nebula.paperclips.core.Print;
import org.eclipse.nebula.paperclips.core.PrintJob;
import org.eclipse.nebula.paperclips.core.border.LineBorder;
import org.eclipse.nebula.paperclips.core.grid.DefaultGridLook;
import org.eclipse.nebula.paperclips.core.grid.GridColumn;
import org.eclipse.nebula.paperclips.core.grid.GridPrint;
import org.eclipse.nebula.paperclips.core.text.TextPrint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * Demonstrates how to print the contents of a Table widget. This snippet uses
 * the GridPrint, TextPrint, and ImagePrint classes.
 * 
 * @author Matthew
 */
public class Snippet1 {
	public static Print createPrint(Table table) {
		// Create GridPrint with all columns at default size.

		DefaultGridLook look = new DefaultGridLook();
		look.setCellBorder(new LineBorder());
		RGB background = table.getDisplay().getSystemColor(
				SWT.COLOR_WIDGET_BACKGROUND).getRGB();
		look.setHeaderBackground(background);
		look.setFooterBackground(background);

		GridPrint grid = new GridPrint(look);

		// Add header and footer to match table column names.
		TableColumn[] columns = table.getColumns();
		for (int i = 0; i < columns.length; i++) {
			TableColumn col = columns[i];

			// Add the column to the grid with alignment applied, default width,
			// no
			// weight
			grid.addColumn(new GridColumn(col.getAlignment(), SWT.DEFAULT, 0));

			Print cell = createCell(col.getImage(), col.getText(), SWT.CENTER);
			grid.addHeader(cell);
			grid.addFooter(cell);
		}

		// Add content rows
		TableItem[] items = table.getItems();
		for (int i = 0; i < items.length; i++) {
			TableItem item = items[i];
			for (int j = 0; j < columns.length; j++)
				grid.add(createCell(item.getImage(j), item.getText(j),
						columns[j].getAlignment()));
		}

		return grid;
	}

	public static Print createCell(Image image, String text, int align) {
		if (image == null)
			return new TextPrint(text, align);

		GridPrint grid = new GridPrint("p, d");
		grid.add(new ImagePrint(image.getImageData(), image.getDevice()
				.getDPI()));
		grid.add(new TextPrint(text, align));
		return grid;
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
		shell.setText("Snippet1.java");
		shell.setBounds(100, 100, 640, 480);
		shell.setLayout(new GridLayout());

		Button button = new Button(shell, SWT.PUSH);
		button.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
		button.setText("Print the table");

		final Table table = new Table(shell, SWT.BORDER);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// Set up Table widget with dummy data.
		for (int i = 0; i < 5; i++)
			new TableColumn(table, SWT.LEFT).setText("Column " + i);

		for (int row = 0; row < 100; row++) {
			TableItem item = new TableItem(table, SWT.NONE);
			for (int col = 0; col < 5; col++)
				item.setText(col, "Cell [" + col + ", " + row + "]");
		}

		table.setHeaderVisible(true);
		TableColumn[] columns = table.getColumns();
		for (int i = 0; i < columns.length; i++)
			columns[i].pack();

		button.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				PrintDialog dialog = new PrintDialog(shell, SWT.NONE);
				PrinterData printerData = dialog.open();
				if (printerData != null) {
					Print print = createPrint(table);
					PaperClips.print(new PrintJob("Snippet1.java", print)
							.setMargins(72), printerData);
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
