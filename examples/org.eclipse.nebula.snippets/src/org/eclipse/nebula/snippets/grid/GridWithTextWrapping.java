/*******************************************************************************
 *  Copyright (c) 2010 Weltevree Beheer BV, Remain Software & Industrial-TSI and others
 * 
 * All rights reserved. 
 * This program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Wim S. Jongman - based on code from {@link GridSnippet2}
 ******************************************************************************/

package org.eclipse.nebula.snippets.grid;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * Create a grid with an items that have text wrapping.
 * 
 * For a list of all Nebula Grid example snippets see
 * http://www.eclipse.org/nebula/widgets/grid/snippets.php
 */
public class GridWithTextWrapping {

	private static Grid fGrid;

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		fGrid = new Grid(shell, SWT.BORDER | SWT.V_SCROLL);

		fGrid.setTreeLinesVisible(false);
		fGrid.setWordWrapHeader(true);
		fGrid.setHeaderVisible(true);
		GridColumn column = new GridColumn(fGrid, SWT.NONE);
		column.setWordWrap(true);
		column.setText("Column 1");
		column.setWidth(100);
		GridColumn column2 = new GridColumn(fGrid, SWT.NONE);
		column2.setText("Column 2");
		column2.setWidth(100);

		GridItem item1 = new GridItem(fGrid, SWT.NONE);
		item1.setText("First Item. First Item. First Item.");
		item1.setText(1, "xxxxxxx");

		System.out.println("item2");
		final GridItem item2 = new GridItem(fGrid, SWT.NONE);
		item2.setText("This cell contains a lot of text. This cell contains a lot of text");
		item2.setText(1, "xxxxxxx");
		column.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				calculateHeight();
			}
		});

		GridItem item3 = new GridItem(fGrid, SWT.NONE);
		item3.setText("Third Item. Third Item. Third Item. Third Item. Third Item. ");
		item3.setText(1, "xxxxxxx");

		calculateHeight();

		shell.setSize(200, 200);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	protected static void calculateHeight() {
		for (GridItem item : fGrid.getItems()) {
			GC gc = new GC(item.getDisplay());
			GridColumn gridColumn = fGrid.getColumn(0);
			Point textBounds = gridColumn.getCellRenderer().computeSize(gc, gridColumn.getWidth(), SWT.DEFAULT, item);
			gc.dispose();
			item.setHeight(textBounds.y);
		}
	}
}