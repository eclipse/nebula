/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
public class GridSnippet8 {

	private static String LONG_TEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum lectus augue, pulvinar quis cursus nec, imperdiet nec ante. Cras sit amet arcu et enim adipiscing pellentesque. Suspendisse mi felis, dictum a lobortis nec, placerat in diam. Proin lobortis tortor at nunc facilisis aliquet. Praesent eget dignissim orci. Ut iaculis bibendum.";
	private static String MEDIUM_TEXT = "this a a text that is a bit longer, but not too long. This row should have a smaller height than row #1";

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		final Grid grid = new Grid(shell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		grid.setHeaderVisible(true);
		grid.setAutoHeight(true);
		grid.setAutoWidth(true);

		GridColumn column1 = new GridColumn(grid, SWT.NONE);
		column1.setText("Column 1");
		column1.setWidth(150);
		column1.setWordWrap(true);

		GridColumn column2 = new GridColumn(grid, SWT.NONE);
		column2.setText("Column 2");
		column2.setWidth(200);
		column2.setWordWrap(true);

		GridItem item1 = new GridItem(grid, SWT.NONE);
		item1.setText(0, "Item 1, Column 0: " + LONG_TEXT);
		item1.setText(1, "Item 1, Column 1: " + LONG_TEXT);

		GridItem item2 = new GridItem(grid, SWT.NONE);
		item2.setText("Item 2, Columns 0-1: " + LONG_TEXT);
		item2.setColumnSpan(0, 1);

		GridItem item3 = new GridItem(grid, SWT.NONE);
		item3.setText(0, "Item 3, Column 0: " + MEDIUM_TEXT);
		item3.setText(1, "Item 3, Column 1: " + MEDIUM_TEXT);

		shell.setSize(400, 400);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

}