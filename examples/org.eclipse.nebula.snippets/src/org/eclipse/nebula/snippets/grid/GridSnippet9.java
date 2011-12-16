/*******************************************************************************
 *  Copyright (c) 2010 Weltevree Beheer BV, Remain Software & Industrial-TSI and others
 * 
 * All rights reserved. 
 * This program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Wim S. Jongman - based on code from https://bugs.eclipse.org/bugs/show_bug.cgi?id=153729
 ******************************************************************************/

package org.eclipse.nebula.snippets.grid;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridEditor;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class GridSnippet9 {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		final Grid grid = new Grid(shell, SWT.BORDER);
		grid.setLinesVisible(true);
		for (int i = 0; i < 3; i++) {
			GridColumn column = new GridColumn(grid, SWT.NONE);
			column.setWidth(150);
		}

		for (int i = 1; i < 2; i++) {
			GridItem item1 = new GridItem(grid, SWT.NONE);
			item1.setText("Item " + i);
			GridItem item2 = new GridItem(grid, SWT.NONE);
			item2.setText("This cell spans both columns");
			item2.setColumnSpan(0, 1);
			GridItem item3 = new GridItem(grid, SWT.NONE);
			item3.setText("Item " + (i + 1));
		}

		GridItem[] items = grid.getItems();
		for (int i = 0; i < items.length; i++) {
			GridEditor editor = new GridEditor(grid);
			CCombo combo = new CCombo(grid, SWT.NONE);
			combo.setText("CCombo Widget " + i);
			combo.add("item 1");
			combo.add("item 2");
			combo.add("item 3");
			editor.minimumWidth = 50;
			editor.grabHorizontal = true;
			editor.setEditor(combo, items[i], 0);

			editor = new GridEditor(grid);
			Text text = new Text(grid, SWT.NONE);
			text.setText("Text " + i);
			editor.grabHorizontal = true;
			editor.setEditor(text, items[i], 1);
			editor = new GridEditor(grid);
			
			Button button = new Button(grid, SWT.CHECK);
			button.setText("Check me");
			button.pack();
			editor.minimumWidth = button.getSize().x;
			editor.horizontalAlignment = SWT.LEFT;
			editor.setEditor(button, items[i], 2);
		}

		shell.setSize(500, 500);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
