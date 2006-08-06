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
package org.eclipse.swt.nebula.snippets.ctabletree;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.nebula.widgets.ctabletree.CTableTree;
import org.eclipse.swt.nebula.widgets.ctabletree.CTableTreeItem;
import org.eclipse.swt.nebula.widgets.ctabletree.ccontainer.CContainerColumn;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/*
 * Create a CTableTree with the Tree in the second column.
 * Compare with SWT Snippet "Create a Tree with columns".
 *
 * For a list of all Nebula CTableTree example snippets see
 * http://www.eclipse.org/nebula/widgets/ctabletree/snippets.php
 */
public class CTableTreeSnippet2 {
	public static void main (String [] args) {
		Display display = new Display ();
		Shell shell = new Shell (display);
		shell.setLayout(new FillLayout());

		CTableTree CTableTree = new CTableTree(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		CTableTree.setHeaderVisible(true);
		CContainerColumn column1 = new CContainerColumn(CTableTree, SWT.LEFT);
		column1.setText("Column 1");
		column1.setWidth(100);
		CContainerColumn column2 = new CContainerColumn(CTableTree, SWT.CENTER);
		column2.setText("Column 2");
		column2.setWidth(200);
		CContainerColumn column3 = new CContainerColumn(CTableTree, SWT.RIGHT);
		column3.setText("Column 3");
		column3.setWidth(300);

		CTableTree.setTreeColumn(1);

		Class[] classes = new Class[] { null, null, TableCell.class };
		for (int i = 0; i < 4; i++) {
			CTableTreeItem item = new CTableTreeItem(CTableTree, SWT.NONE, classes);
			item.setText(new String[] { "item " + i, "abc", "defghi" });
			for (int j = 0; j < 4; j++) {
				CTableTreeItem subItem = new CTableTreeItem(item, SWT.NONE);
				subItem.setText(new String[] { "subitem " + j, "jklmnop", "qrs" });
				for (int k = 0; k < 4; k++) {
					CTableTreeItem subsubItem = new CTableTreeItem(subItem, SWT.NONE);
					subsubItem.setText(new String[] { "subsubitem " + k, "tuv", "wxyz" });
				}
			}
		}

        Point size = shell.computeSize(-1, -1);
        size.y = 300;
        Rectangle screen = display.getMonitors()[0].getBounds();
        shell.setBounds(
            (screen.width-size.x)/2,
            (screen.height-size.y)/2,
            size.x,
            size.y
            );
		shell.open ();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}
} 