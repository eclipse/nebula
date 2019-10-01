/*******************************************************************************
 * Copyright (c) 2019 Laurent CARON and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Laurent CARON - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.snippets.grid;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Create a grid with different truncation styles.
 *
 * For a list of all Nebula Grid example snippets see
 * http://www.eclipse.org/nebula/widgets/grid/snippets.php
 */
public class GridSnippet12 {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		Grid grid = new Grid(shell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		grid.setHeaderVisible(true);

		GridColumn firstColumn = new GridColumn(grid, SWT.NONE);
		firstColumn.setText("Column 1 (START Truncation)");
		firstColumn.setWidth(150);
		firstColumn.getCellRenderer().setTruncationStyle(SWT.LEFT);

		GridColumn secondColumn = new GridColumn(grid, SWT.NONE);
		secondColumn.setText("Column 1 (MIDDLE Truncation)");
		secondColumn.setWidth(150);
		secondColumn.getCellRenderer().setTruncationStyle(SWT.CENTER);

		GridColumn thirdColumn = new GridColumn(grid, SWT.NONE);
		thirdColumn.setText("Column 1 (END Truncation)");
		thirdColumn.setWidth(150);
		thirdColumn.getCellRenderer().setTruncationStyle(SWT.RIGHT);

		for (int i = 0; i < 50; i++) {
			GridItem item = new GridItem(grid, SWT.NONE);
			item.setText(0, "Start truncation for this text (line #" + i + ")");
			item.setText(1, "Middle truncation for this text (line #" + i + ")");
			item.setText(2, "End truncation for this text (line #" + i + ")");
		}

		shell.setSize(500,400);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}