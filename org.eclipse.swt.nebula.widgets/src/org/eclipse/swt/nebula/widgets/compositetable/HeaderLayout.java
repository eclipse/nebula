package org.eclipse.swt.nebula.widgets.compositetable;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Widget;


public class HeaderLayout extends AbstractGridRowLayout {
    /**
     * Constructor HeaderLayout.  The default constructor.  If you use this
     * constructor, you must manually specify the column weights, and possibly,
     * the fittingHorizontally property value.
     */
    public HeaderLayout() {
        super();
    }
    
    /**
     * Constructor HeaderLayout. Construct a HeaderLayout, specifying the
     * column weights. By default, fittingHorizontally is false.
     * 
     * @param weights
     *            int[] The amount of weight desired for each column in the
     *            table. If fittingHorizontally is set to true, the sum of all
     *            weights must be 100 and each weight indicates the percentage
     *            of the whole table that each column will occupy. If
     *            fittingHorizontally is set to false, each weight is the
     *            minimum width of the column in pixels. If the table is
     *            narrower than can fit all widths, CompositeTable will display
     *            a horizontal scroll bar. If the table is wider than can fit
     *            all widths, the columns are scaled so that the entire table
     *            fills the desired space and the ratios of the column widths
     *            remains constant. fittingHorizontally defaults to false.
     */
    public HeaderLayout(int[] weights) {
        super(weights);
    }
    
    /**
     * Construct a HeaderLayout, specifying both the weights and the
     * fittingHorizontally property.
     * 
     * @param weights
     *            int[] The amount of weight desired for each column in the
     *            table. If fittingHorizontally is set to true, the sum of all
     *            weights must be 100 and each weight indicates the percentage
     *            of the whole table that each column will occupy. If
     *            fittingHorizontally is set to false, each weight is the
     *            minimum width of the column in pixels. If the table is
     *            narrower than can fit all widths, CompositeTable will display
     *            a horizontal scroll bar. If the table is wider than all
     *            minimum column widths, the columns will be scaled so that the
     *            ratios of the actual widths remains constant and all columns
     *            fit exactly in the available space. fittingHorizontally
     *            defaults to false.
     * 
     * @param fittingHorizontally
     *            If true, the weights are interpreted as percentages and the
     *            column widths are scaled so that each column occupies the
     *            percentage of the total width indicated by its weight. If
     *            false, the weights are interpreted as minimum column widths.
     *            If the table is narrower than can accommodate those widths,
     *            CompositeTable will display a horizontal scroll bar. If the
     *            table is wider than all minimum column widths, the columns
     *            will be scaled so that the ratios of the actual widths remains
     *            constant and all columns fit exactly in the available space.
     */
    public HeaderLayout(int[] weights, boolean fittingHorizontally) {
        super(weights, fittingHorizontally);
    }
    
    protected void layout(Composite child, boolean flushCache) {
        layingOut = true;
        try {
            super.layout(child, flushCache);
        } finally {
            layingOut = false;
        }
    }
    
    private boolean layingOut = false;
    
    private AbstractHeader castHeader(Widget rowOrHeader) {
        if (!(rowOrHeader instanceof AbstractHeader)) {
            throw new IllegalArgumentException("HeaderLayout must be used on an AbstractHeader");
        }
        return (AbstractHeader) rowOrHeader;
    }
    
    private AbstractHeader getHeader(Widget rowOrHeader) {
        AbstractHeader header = castHeader(rowOrHeader);
        if (headerControlListener == null) {
//            headerControlListener = new HeaderControlListener();
//            header.addColumnControlListener(headerControlListener);
//            header.addDisposeListener(headerDisposeListener);
        }
        return header;
    }

    protected int computeMaxHeight(Composite rowOrHeader) {
        return getHeader(rowOrHeader).headerTable.getHeaderHeight();
    }

    protected Point computeSize(Widget columnObject, int wHint, int hHint, boolean flush) {
        TableColumn tableColumn = (TableColumn) columnObject;
        int currentWidth = tableColumn.getWidth();
        int headerHeight = tableColumn.getParent().getHeaderHeight();
        return new Point(currentWidth, headerHeight);
    }

    protected Widget getColumnAt(Composite rowOrHeader, int offset) {
        return getHeader(rowOrHeader).headerTable.getColumn(offset);
    }

    protected int getNumColumns(Composite rowOrHeader) {
        return getHeader(rowOrHeader).headerTable.getColumnCount();
    }

    protected void setBounds(Widget columnObject, int left, int top, int width, int height) {
        TableColumn tableColumn = (TableColumn) columnObject;
        tableColumn.setWidth(width + 2*CELL_BORDER_WIDTH);
    }
    
    private void adjustWeights(AbstractHeader header, TableColumn resizedColumn) {
        if (isFittingHorizontally()) {
            adjustWeightedHeader(header, resizedColumn);
        }
        adjustAbsoluteWidthHeader(header, resizedColumn);
        header.layout();
    }
    
    private void adjustWeightedHeader(AbstractHeader header, TableColumn resizedColumn) {
        int totalAvailableWidth = getAvailableWidth(header);
        
        Table table = resizedColumn.getParent();
        TableColumn[] columns = table.getColumns();

        int resizedColumnNumber = 0;
        int newTotalWidth = 0;
        
        for (int i = 0; i < columns.length; i++) {
            newTotalWidth += columns[i].getWidth();
            if (columns[i] == resizedColumn) {
                resizedColumnNumber = i;
            }
        }
        
        
    }

    private void adjustAbsoluteWidthHeader(AbstractHeader header, TableColumn resizedColumn) {
        if (isWidthWiderThanAllColumns(header)) {
            
        } else {
            
        }
    }

    // Event listeners --------------------------------------------------------
    
    private ControlListener headerControlListener = null;
    
    private class HeaderControlListener implements ControlListener {
        public void controlMoved(ControlEvent e) {
        }

        public void controlResized(ControlEvent e) {
            if (!layingOut) {
                TableColumn tableColumn = (TableColumn) e.widget;
                AbstractHeader header = castHeader(tableColumn.getParent().getParent());
                adjustWeights(header, tableColumn);
            }
        }
    }
    
    private DisposeListener headerDisposeListener = new DisposeListener() {
        public void widgetDisposed(DisposeEvent e) {
            castHeader(e.widget).removeColumnControlListener(headerControlListener);
        }
    };
}
