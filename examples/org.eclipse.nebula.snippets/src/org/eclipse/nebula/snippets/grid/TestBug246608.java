/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     compeople AG    - wrote GridSnippet8 based on GridSnippet2
 *******************************************************************************/
package org.eclipse.nebula.snippets.grid;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/*
 * Create a grid with word-wrapping columns.
 *
 * For a list of all Nebula Grid example snippets see
 * http://www.eclipse.org/nebula/widgets/grid/snippets.php
 */
public class TestBug246608 {

	private static String MEDIUM_TEXT = "this a a text that is a bit longer, but not too long. This row should have a smaller height than row #1";

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		final Grid grid = new Grid(shell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		grid.setHeaderVisible(true);
		grid.setAutoWidth(true);

		GridColumn column1 = new GridColumn(grid, SWT.NONE);
		column1.setText("Column 1");
		column1.setWidth(150);
		column1.setWordWrap(true);
		column1.setVerticalAlignment(SWT.TOP);

		GridColumn column2 = new GridColumn(grid, SWT.NONE);
		column2.setText("Column 2");
		column2.setWidth(200);
		column2.setWordWrap(true);
		column2.setVerticalAlignment(SWT.CENTER);

		GridColumn column3 = new GridColumn(grid, SWT.NONE);
		column3.setText("Column 3");
		column3.setWidth(200);
		column3.setVerticalAlignment(SWT.BOTTOM);

		GridItem item1 = new GridItem(grid, SWT.NONE);
		item1.setText(0, "Item 1, Column 0: " + MEDIUM_TEXT);
		item1.setText(1, "Item 1, Column 1: " + MEDIUM_TEXT);
		item1.setText(2, "Item 1, Column 2: " + MEDIUM_TEXT);
		item1.setHeight(150);

		GridItem item2 = new GridItem(grid, SWT.NONE);
		item2.setText("Item 2, Columns 0-1: " + MEDIUM_TEXT);
		item2.setColumnSpan(0, 1);
		item2.setText(2, "Item 2, Column 2: Dummy");
		item2.setHeight(100);

		GridItem item3 = new GridItem(grid, SWT.NONE);
		item3.setText(0, "Item 3, Column 0: " + MEDIUM_TEXT);
		item3.setText(1, "Item 3, Column 1: " + MEDIUM_TEXT);
		item3.setText(2, "Item 3, Column 2: Column");
		item3.setHeight(120);

		shell.setSize(600, 500);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

}