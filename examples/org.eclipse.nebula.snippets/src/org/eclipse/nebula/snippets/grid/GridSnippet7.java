/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.snippets.grid;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/*
 * Create a grid with an item that spans columns.
 *
 * For a list of all Nebula Grid example snippets see
 * http://www.eclipse.org/nebula/widgets/grid/snippets.php
 */
public class GridSnippet7 {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		final Grid grid = new Grid(shell, SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL);
		grid.setHeaderVisible(true);
		grid.setFooterVisible(true);

		GridColumn column = new GridColumn(grid, SWT.NONE);
		column.setText("Column 1");
		column.setWidth(100);
		column.setHeaderControl(new CCombo(grid, SWT.READ_ONLY | SWT.BORDER));
		column.setMoveable(true);

		GridColumn column2 = new GridColumn(grid, SWT.NONE);
		column2.setText("Column 2");
		column2.setWidth(100);
		column2.setMoveable(true);
		column2.setHeaderControl(new Text(grid, SWT.BORDER));

		for (int i = 0; i < 100; i++) {
			GridItem item1 = new GridItem(grid, SWT.NONE);
			item1.setText("First Item");
			item1.setText(1, "xxxxxxx");
			GridItem item2 = new GridItem(grid, SWT.NONE);
			item2.setText("This cell spans both columns");
			item1.setText(1, "xxxxxxx");
			item2.setColumnSpan(0, 1);
			GridItem item3 = new GridItem(grid, SWT.NONE);
			item3.setText("Third Item");
			item1.setText(1, "xxxxxxx");
		}

		grid.addMouseListener(new MouseAdapter() {

			public void mouseDown(MouseEvent e) {
				if (e.y < grid.getHeaderHeight()) {
					System.out.println("header click");
				}
			}

		});

		shell.setSize(200, 200);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}