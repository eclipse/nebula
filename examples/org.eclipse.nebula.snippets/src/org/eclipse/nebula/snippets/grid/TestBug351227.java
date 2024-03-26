package org.eclipse.nebula.snippets.grid;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class TestBug351227 {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		Grid grid = new Grid(shell, SWT.BORDER | SWT.V_SCROLL);
		grid.setHeaderVisible(true);
		grid.setCellSelectionEnabled(true);

		for (int colCount = 0; colCount < 4; colCount++) {
			GridColumn column = new GridColumn(grid, SWT.NONE);
			column.setText("Column " + (colCount + 1));
			column.setWidth(100);
			for (int rowCount = 0; rowCount < 50; rowCount++) {
				final GridItem item;
				if (colCount == 0) {
					item = new GridItem(grid, SWT.NONE);
				} else {
					item = grid.getItem(rowCount);
				}
				item.setText(colCount, (rowCount + 1) + "." + (colCount + 1));
			}
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