/*******************************************************************************
 * Copyright (c) 2019 Laurent CARON. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Laurent CARON (laurent.caron@gmail.com) - initial API and
 * implementation
 *******************************************************************************/

package org.eclipse.nebula.snippets.grid;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This snippet show custom header font
 */
public class GridSnippet11 {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		Grid grid = new Grid(shell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		grid.setHeaderVisible(true);
		grid.setCellSelectionEnabled(true);
		grid.setRowHeaderVisible(true);

		Font[] fonts = new Font[4];
		fonts[0] = new Font(display, "Consolas", 11, SWT.NONE);
		fonts[1] = new Font(display, "Arial", 12, SWT.ITALIC);
		fonts[2] = new Font(display, "Calibri", 10, SWT.BOLD);
		fonts[3] = new Font(display, "Courier New", 13, SWT.BOLD | SWT.ITALIC);

		int fontIndex = 0;
		for (int colCount = 0; colCount < 4; colCount++) {
			GridColumn column = new GridColumn(grid, SWT.NONE);
			column.setText("Column " + (colCount + 1));
			column.setWidth(100);
			for (int rowCount = 0; rowCount < 50; rowCount++) {
				final GridItem item;
				if (colCount == 0) {
					item = new GridItem(grid, SWT.NONE);
					item.setHeaderFont(fonts[fontIndex % 4]);
					fontIndex++;
				} else {
					item = grid.getItem(rowCount);
				}
				item.setText(colCount, (rowCount + 1) + "." + (colCount + 1));
			}
		}

		// Need to call it after items has been created, because the size has to be recomputed.
		grid.setRowHeaderVisible(true);


		shell.setSize(500, 500);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		for (Font f : fonts) {
			f.dispose();
		}

		display.dispose();
	}
}