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
package org.eclipse.nebula.snippets.pshelf;
 

import org.eclipse.nebula.widgets.pshelf.PShelf;
import org.eclipse.nebula.widgets.pshelf.PShelfItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/*
 * Creates a PShelf.
 *
 * For a list of all Nebula PShelf example snippets see
 * http://www.eclipse.org/nebula/widgets/pshelf/snippets.php
 */
public class PShelfSnippet1 {

public static void main (String [] args) {
    Display display = new Display ();
    Shell shell = new Shell (display);
    shell.setLayout(new FillLayout());

    PShelf shelf = new PShelf(shell, SWT.NONE);
    
//  Optionally, change the renderer
//  shelf.setRenderer(new RedmondShelfRenderer());
    
    PShelfItem item1 = new PShelfItem(shelf,SWT.NONE);
    item1.setText("First Item");
    
    item1.getBody().setLayout(new FillLayout());
    
    Table table = new Table(item1.getBody(),SWT.NONE);
    TableColumn col1 = new TableColumn(table,SWT.NONE);
    col1.setText("Column");
    table.setHeaderVisible(true);
    col1.setWidth(100);
    TableItem tableItem = new TableItem(table,SWT.NONE);
    tableItem.setText("table item");
    
    PShelfItem item2 = new PShelfItem(shelf,SWT.NONE);
    item2.setText("Second Item");
    
    item2.getBody().setLayout(new FillLayout());
    
    Text text = new Text(item2.getBody(),SWT.WRAP);
    text.setText("Blah blah blah");
    
    shell.setSize(200,200);
    shell.open ();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch ()) display.sleep ();
    }
    display.dispose ();
}
} 