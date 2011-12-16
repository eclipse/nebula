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
import org.eclipse.nebula.widgets.grid.GridColumnGroup;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/*
 * Create a grid with a collapsible column group with a seperate summary column
 * that is shown when the group is collapsed.
 *
 * For a list of all Nebula Grid example snippets see
 * http://www.eclipse.org/nebula/widgets/grid/snippets.php
 */
public class GridSnippet6 {

public static void main (String [] args) {
    Display display = new Display ();
    Shell shell = new Shell (display);
    shell.setLayout(new FillLayout());

    Grid grid = new Grid(shell,SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
    grid.setHeaderVisible(true);
    GridColumn column = new GridColumn(grid,SWT.NONE);
    column.setText("Column 1");
    column.setWidth(100);
    GridColumnGroup columnGroup = new GridColumnGroup(grid,SWT.TOGGLE);
    columnGroup.setText("Column Group");
    GridColumn column2 = new GridColumn(columnGroup,SWT.NONE);
    column2.setText("Column 2");
    column2.setWidth(60);
    column2.setSummary(false);
    GridColumn column3 = new GridColumn(columnGroup,SWT.NONE);
    column3.setText("Column 3");
    column3.setWidth(60);
    column3.setSummary(false);
    GridColumn summaryColumn = new GridColumn(columnGroup,SWT.NONE);
    summaryColumn.setText("Sum");
    summaryColumn.setWidth(93);
    summaryColumn.setDetail(false);
    summaryColumn.setSummary(true);
    GridItem item1 = new GridItem(grid,SWT.NONE);
    item1.setText("First Item");
    item1.setText(1,"100");
    item1.setText(2,"42");
    item1.setText(3,"142");
    GridItem item2 = new GridItem(grid,SWT.NONE);
    item2.setText("Second Item");
    item2.setText(1,"63");
    item2.setText(2,"92");
    item2.setText(3,"155");
    GridItem item3 = new GridItem(grid,SWT.NONE);
    item3.setText("Third Item");
    item3.setText(1,"7");
    item3.setText(2,"3");
    item3.setText(3,"10");
    
    shell.setSize(250,250);
    shell.open ();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch ()) display.sleep ();
    }
    display.dispose ();
}
} 