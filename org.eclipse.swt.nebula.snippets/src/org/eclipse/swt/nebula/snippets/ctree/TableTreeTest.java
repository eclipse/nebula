package org.eclipse.swt.nebula.snippets.ctree;

import org.eclipse.nebula.widgets.ctree.CTree;
import org.eclipse.nebula.widgets.ctree.CTreeColumn;
import org.eclipse.nebula.widgets.ctree.CTreeItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;



public class TableTreeTest {
	
	
	public static void main (String [] args) {
		Display display = new Display ();
		Shell shell = new Shell (display);
		shell.setLayout(new FillLayout());

		CTree ctt = new CTree(shell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		ctt.setHeaderVisible(true);
		ctt.setLinesVisible(true);
		ctt.setSelectable(false);
		CTreeColumn column1 = new CTreeColumn(ctt, SWT.LEFT);
		column1.setText("Name");
		column1.pack();

		// TextCell should display this data (data[i] in i-th column) and update it when users change the Text.
		String [] data = new String[] { "foo", "bar", "foo2", "bar2", "blub"};

		ctt.setCellClasses(new Class[] { TextCell.class });
		
		for (int i = 0; i < 5; i++) {
			CTreeItem item =new CTreeItem(ctt, SWT.NONE);
			item.setData(data);
		}
		

		shell.open ();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}
	
} 