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
public class GridRowSpanSnippet {

    /**
     *
     * @param args
     */
    public static void main (String [] args) {
        Display display = new Display ();
        Shell shell = new Shell (display);
        shell.setLayout(new FillLayout());

        Grid grid = new Grid(shell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

        grid.setHeaderVisible(true);
        GridColumn column = new GridColumn(grid, SWT.NONE);
        column.setText("Column 1");
        column.setWidth(100);

        GridColumn column2 = new GridColumn(grid, SWT.NONE);
        column2.setText("Column 2");
        column2.setWidth(100);

        GridItem item1 = new GridItem(grid, SWT.NONE);
        item1.setText("First Item");
        item1.setText(1, "xxxxxxx");

        GridItem item2 = new GridItem(grid, SWT.NONE);
        item2.setText("This cell spans two rows");
        item2.setText(1, "xxxxxxx");
        item2.setRowSpan(0, 1);
        item2.setColumnSpan(0, 1);


        GridItem item3 = new GridItem(grid, SWT.NONE);
        item3.setText("Third Item");
        item3.setText(1, "xxxxxxx");

        shell.setSize(200,200);
        shell.open ();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch ()) display.sleep ();
        }

        display.dispose ();
    }
}