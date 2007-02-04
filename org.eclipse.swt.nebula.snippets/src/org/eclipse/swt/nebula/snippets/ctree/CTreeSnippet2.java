/****************************************************************************
 * Copyright (c) 2006 Jeremy Dowdall
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *****************************************************************************/
package org.eclipse.swt.nebula.snippets.ctree;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.nebula.widgets.ctree.CTree;
import org.eclipse.swt.nebula.widgets.ctree.CTreeColumn;
import org.eclipse.swt.nebula.widgets.ctree.CTreeItem;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/*
 * Create a CTableTree demonstrating the implementation of
 * custom cells.
 *
 * For a list of all Nebula CTableTree example snippets see
 * http://www.eclipse.org/nebula/widgets/ctabletree/snippets.php
 */
public class CTreeSnippet2 {
	public static void main (String [] args) {
		Display display = new Display ();
		Shell shell = new Shell (display);
		shell.setLayout(new FillLayout());

//		Composite c = new Composite(shell, SWT.NONE);
//		c.setBounds(10, 10, 100, 100);
//		
//		Button b = new Button(c, SWT.ARROW | SWT.DOWN);
//		b.setBackground(c.getBackground());
//		b.setLocation(10, 10);
//		b.setSize(b.computeSize(-1,-1));

		CTree tree = new CTree(shell, SWT.BORDER | SWT.CHECK | SWT.H_SCROLL | SWT.V_SCROLL);
//		tree.setCheckColumn(2, false);
		tree.setTreeColumn(1);
		tree.setNativeHeader(false);
		tree.setHeaderVisible(true);
//		tree.setLinesVisible(true);
		CTreeColumn column1 = new CTreeColumn(tree, SWT.LEFT);
		column1.setText("MultiLineCell");
		column1.setMoveable(true);
		column1.pack();
		CTreeColumn column2 = new CTreeColumn(tree, SWT.LEFT);
		column2.setText("null Cell / Tree Column");
		column2.setMoveable(true);
		column2.pack();
		CTreeColumn column3 = new CTreeColumn(tree, SWT.LEFT);
		column3.setText("MultiLineTextCell");
		column3.pack();
		CTreeColumn column4 = new CTreeColumn(tree, SWT.LEFT);
		column4.setText("TableCell");
		column4.setWidth(150);
		CTreeColumn column5 = new CTreeColumn(tree, SWT.LEFT);
		column5.setText("null Cell");
		column5.pack();

		for (int i = 0; i < 50; i++) {
			tree.setCellClasses(new Class[] { MultiLineCell.class, null, MultiLineTextCell.class, TableCell.class });
			CTreeItem item = new CTreeItem(tree, SWT.NONE);
			item.setText(new String[] { "word1 word2 word3", "abc", "defghi", "item " + i, "item " + i});
			for (int j = 0; j < 2; j++) {
				CTreeItem subItem = new CTreeItem(item, SWT.NONE);
				subItem.setText(new String[] { "subitem " + j, "jklmnop", "qrs" });
				tree.setCellClasses(null);
				for (int k = 0; k < 2; k++) {
					CTreeItem subsubItem = new CTreeItem(subItem, SWT.NONE);
					subsubItem.setText(new String[] { "subsubitem " + k, "tuv", "wxyz" });
				}
			}
		}

		Point size = new Point(300,300); shell.computeSize(-1, -1);
//		size.y = 300;
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