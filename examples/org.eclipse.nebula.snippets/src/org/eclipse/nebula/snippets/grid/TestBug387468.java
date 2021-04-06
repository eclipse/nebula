package org.eclipse.nebula.snippets.grid;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Create a grid with an item that spans rows.
 *
 * For a list of all Nebula Grid example snippets see
 * http://www.eclipse.org/nebula/widgets/grid/snippets.php
 */
public class TestBug387468 {

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());

        Grid grid = new Grid(shell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

        grid.setHeaderVisible(true);
        GridColumn column = new GridColumn(grid, SWT.NONE);
        column.setText("Employee");
        column.setWidth(100);

        GridColumn column2 = new GridColumn(grid, SWT.NONE);
        column2.setText("Company");
        column2.setWidth(100);

        GridItem item1 = new GridItem(grid, SWT.NONE);
        item1.setText("Anne");
        item1.setText(1, "Company A");
        item1.setRowSpan(1, 2);

        GridItem item2 = new GridItem(grid, SWT.NONE);
        item2.setText("Jim");
        item1.setText(1, "Company A");
        item2.setRowSpan(1, 2);

        GridItem item3 = new GridItem(grid, SWT.NONE);
        item3.setText("Sara");
        item3.setText(1, "Company A");
        item3.setRowSpan(1, 2);

        GridItem item4 = new GridItem(grid, SWT.NONE);
        item4.setText("Tom");
        item4.setText(1, "Company B");
        item4.setRowSpan(1, 1);

        GridItem item5 = new GridItem(grid, SWT.NONE);
        item5.setText("Nathalie");
        item5.setText(1, "Company B");
        item5.setRowSpan(1, 1);

        GridItem item6 = new GridItem(grid, SWT.NONE);
        item6.setText("Wim");
        item6.setText(1, "Company C");
        item6.setRowSpan(1, 1);

        GridItem item7 = new GridItem(grid, SWT.NONE);
        item7.setText("Pauline");
        item7.setText(1, "Company C");
        item7.setRowSpan(1, 1);

        shell.setSize(300, 300);
        shell.open();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }

        display.dispose();
    }
}