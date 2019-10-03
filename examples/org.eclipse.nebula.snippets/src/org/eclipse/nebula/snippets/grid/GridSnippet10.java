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
import org.eclipse.nebula.widgets.grid.Win7RendererSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/*
 * Create a simple grid with Win7 column header rendering
 *
 * For a list of all Nebula Grid example snippets see
 * http://www.eclipse.org/nebula/widgets/grid/snippets.php
 */
public class GridSnippet10 {

public static void main (String [] args) {
    Display display = new Display ();
    Shell shell = new Shell (display);
    shell.setLayout(new FillLayout());

    Grid grid = new Grid(shell,SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
    grid.setHeaderVisible(true);
    GridColumn column = new GridColumn(grid,SWT.NONE);
    column.setTree(true);
    column.setText("Column 1");
    column.setWidth(100);
    GridColumn column2 = new GridColumn(grid,SWT.NONE);
    column2.setTree(true);
    column2.setText("Column 1");
    column2.setWidth(100);
    GridColumn column3 = new GridColumn(grid,SWT.NONE);
    column3.setTree(true);
    column3.setText("Column 3");
    column3.setWidth(100);

    GridItem item1 = new GridItem(grid,SWT.NONE);
    item1.setText(0,"Root Item1");
    item1.setText(1,"Root Item2");
    item1.setText(2,"Root Item3");
    GridItem item2 = new GridItem(item1,SWT.NONE);
    item2.setText(0,"Second item1");
    item2.setText(1,"Second item2");
    item2.setText(2,"Second item3");
    GridItem item3 = new GridItem(item2,SWT.NONE);
    item3.setText(0,"Third Item1");
    item3.setText(1,"Third Item2");
    item3.setText(2,"Third Item3");

    grid.setCellSelectionEnabled(true);

    //add the "win7" column header rendering
    Win7RendererSupport.create(grid).decorate();

    shell.setSize(200,200);
    shell.open ();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch ()) display.sleep ();
    }
    display.dispose ();
}
}