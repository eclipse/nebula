package org.eclipse.nebula.widgets.compositetable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Widget;


class HeaderLayout extends AbstractGridRowLayout {

    private static final int MINIMUM_COL_WIDTH = 5;

    private boolean layingOut = false;
    private int[] lastWidths = null;

    private AbstractNativeHeader header = null;
    
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

    protected Point computeSize(Composite child, int wHint, int hHint, boolean flushCache) {
        storeHeader(child);
        return super.computeSize(child, wHint, hHint, flushCache);
    }
    
    private void storeHeader(Composite child) {
        this.header = getHeader(child);
    }

    /*
     * FEATURE in SWT/Win32: When resizing a Shell larger, we get two layout
     * events from SWT.  (1) where the Shell has gotten bigger but the child
     * hasn't; (2) where both the Shell and child have gotten bigger.  When
     * the Shell is getting bigger, we want to only process events under (2).
     * 
     * The problem is that if the headerTable's contents is made larger before 
     * the child is, the headerTable will display scroll bar(s), which will
     * then be erased when event (2) arrives.  The solution is to only process
     * event (2).
     * 
     * TODO: Test on Linux/GTK.
     */
    private int lastChildWidth = -1;
    private int lastShellWidth = -1;
    
    protected void layout(final Composite child, boolean flushCache) {
        storeHeader(child);
        
        int childWidth = child.getSize().x;
        int shellWidth = child.getShell().getSize().x;
        
        if (childWidth == lastChildWidth && shellWidth > lastShellWidth) return;
        
        if (childWidth > lastChildWidth) {
            final Table headerTable = getHeader(child).headerTable;
            headerTable.addPaintListener(new PaintListener() {
                public void paintControl(PaintEvent e) {
                    headerTable.removePaintListener(this);
                    layout(child);
                }
            });
        } else {
            layout(child);
        }
        lastChildWidth = childWidth;
        lastShellWidth = shellWidth;
    }
    
    private void layout(Composite child) {
        layingOut = true;
        try {
            super.layout(child, true);
            storeLastWidths(getHeader(child).headerTable);
        } finally {
            layingOut = false;
        }
    }

    protected void storeLastWidths(Table table) {
        if (lastWidths == null) {
            lastWidths = new int[table.getColumnCount()];
        }
        TableColumn[] columns = table.getColumns();
        for (int col = 0; col < columns.length; col++) {
            lastWidths[col] = columns[col].getWidth();
        }
    }

    // Inherited from AbstractGridRowLayout ------------------------------------
    
    public int[] getWeights() {
        if (header == null) {
            return super.getWeights();
        } else {
            int[] weightsOrder = header.headerTable.getColumnOrder();
            int[] rawWeights = super.getWeights();
            int[] orderedWeights = new int[weightsOrder.length];
            for (int i = 0; i < orderedWeights.length; i++) {
                orderedWeights[i] = rawWeights[weightsOrder[i]];
            }
            return orderedWeights;
        }
    }
    
    public AbstractGridRowLayout setWeights(int[] weights) {
        if (header == null) {
            super.setWeights(weights);
        } else {
            int[] weightsOrder = header.headerTable.getColumnOrder();
            int[] orderedWeights = new int[weightsOrder.length];
            for (int i = 0; i < orderedWeights.length; i++) {
                orderedWeights[weightsOrder[i]] = weights[i];
                super.setWeights(orderedWeights);
            }
        }
        return this;
    }
    
    protected int computeMaxHeight(Composite rowOrHeader) {
        return getHeader(rowOrHeader).headerTable.getHeaderHeight();
    }

    protected Point computeColumnSize(Widget columnObject, int wHint, int hHint, boolean flush) {
        TableColumn tableColumn = (TableColumn) columnObject;
        int currentWidth = tableColumn.getWidth();
        int headerHeight = tableColumn.getParent().getHeaderHeight();
        return new Point(currentWidth, headerHeight);
    }

    protected Widget getColumnAt(Composite rowOrHeader, int offset) {
        Table headerTable = getHeader(rowOrHeader).headerTable;
        int[] columnOrder = headerTable.getColumnOrder();
        return headerTable.getColumn(columnOrder[offset]);
    }

    protected int getNumColumns(Composite rowOrHeader) {
        return getHeader(rowOrHeader).headerTable.getColumnCount();
    }

    protected void setBounds(Widget columnObject, int left, int top, int width, int height) {
        TableColumn tableColumn = (TableColumn) columnObject;
        tableColumn.setWidth(width + 2*CELL_BORDER_WIDTH);
    }
    
    // Utility methods ---------------------------------------------------------
    
    private AbstractNativeHeader asHeader(Widget rowOrHeader) {
        if (!(rowOrHeader instanceof AbstractNativeHeader)) {
            throw new IllegalArgumentException("HeaderLayout must be used on an AbstractHeader");
        }
        return (AbstractNativeHeader) rowOrHeader;
    }
    
    private AbstractNativeHeader getHeader(Widget rowOrHeader) {
        AbstractNativeHeader header = asHeader(rowOrHeader);
        if (headerControlListener == null) {
            headerControlListener = new HeaderControlListener();
            header.addColumnControlListener(headerControlListener);
            header.addDisposeListener(headerDisposeListener);
        }
        return header;
    }

    private boolean resizedColumnIsNotTheLastColumn(int resizedColumnNumber, Table table) {
        return resizedColumnNumber < table.getColumnCount()-1;
    }
    
    private int computePercentage(int totalAvailableWidth, int columnWidth) {
        return (int) (((double) columnWidth) / totalAvailableWidth * 100);
    }

    // Called from event handlers ----------------------------------------------
    
    private void adjustWeights(AbstractNativeHeader header, TableColumn resizedColumn) {
        int totalAvailableWidth = getAvailableWidth(header);
        int resizedColumnNumber = 0;
        int newTotalWidth = 0;
        
        TableColumn[] columns = resizedColumn.getParent().getColumns();
        for (int i = 0; i < columns.length; i++) {
            newTotalWidth += columns[i].getWidth();
            if (columns[i] == resizedColumn) {
                resizedColumnNumber = i;
            }
        }

        Table table = resizedColumn.getParent();
        int[] columnOrder = table.getColumnOrder();
        int resizedColumnPosition = 0;
        
        for (int i = 0; i < columnOrder.length; i++) {
            if (columnOrder[i] == resizedColumnNumber) {
                resizedColumnPosition = i;
                break;
            }
        } 
        
        if (resizedColumnIsNotTheLastColumn(resizedColumnPosition, resizedColumn.getParent())) {
            // Compute resized column width change and make sure the resized 
            // column's width is sane
            int resizedColumnWidth = resizedColumn.getWidth();
            
            // int columnWidthChange = lastWidths[resizedColumnPosition] - resizedColumnWidth;
            int columnWidthChange = lastWidths[columnOrder[resizedColumnPosition]] - resizedColumnWidth;
            
            int columnWidthChangeTooFar = MINIMUM_COL_WIDTH - resizedColumnWidth;
            if (columnWidthChangeTooFar > 0) {
                columnWidthChange -= columnWidthChangeTooFar;
                resizedColumnWidth = MINIMUM_COL_WIDTH;
                resizedColumn.setWidth(resizedColumnWidth);
            }
            
            // Fix the width of the column to the right of the resized column
            int columnToTheRightOfResizedColumnWidth = 
                lastWidths[columnOrder[resizedColumnPosition+1]] + columnWidthChange;
            
            // int columnToTheRightOfResizedColumnWidth = 
            //     lastWidths[resizedColumnPosition+1] + columnWidthChange;
            
            columnWidthChangeTooFar = MINIMUM_COL_WIDTH - columnToTheRightOfResizedColumnWidth;
            if (columnWidthChangeTooFar > 0) {
                columnWidthChange += columnWidthChangeTooFar;
                resizedColumnWidth -= columnWidthChangeTooFar;
                resizedColumn.setWidth(resizedColumnWidth);
                columnToTheRightOfResizedColumnWidth = MINIMUM_COL_WIDTH;
            }
            TableColumn columnToTheRightOfResizedColumn = columns[columnOrder[resizedColumnPosition+1]];
            columnToTheRightOfResizedColumn.setWidth(columnToTheRightOfResizedColumnWidth);

            if (isFittingHorizontally()) {
                adjustWeightedHeader(header, resizedColumnPosition,
                        resizedColumn, columnToTheRightOfResizedColumn,
                        totalAvailableWidth, newTotalWidth);
            } else {
                // Fix the weights based on if the column sizes are being scaled
                if (isWidthWiderThanAllColumns(header)) {
                    adjustScaledAbsoluteWidthWeights(resizedColumnPosition,
                            resizedColumnWidth,
                            columnToTheRightOfResizedColumnWidth, 
                            header.getSize().x);
                } else {
                    adjustNonScaledAbsoluteWidthWeights(resizedColumnPosition,
                            resizedColumnWidth,
                            columnToTheRightOfResizedColumnWidth);
                }
            }
            
            fireColumnResizedEvent(resizedColumnPosition,
                            resizedColumnWidth,
                            columnToTheRightOfResizedColumnWidth);
        } else {
            // Re-layout; the rightmost column can't be resized
            layout(header, true);
        }
    }
    
    private List columnControlListeners = new ArrayList();
    
    void addColumnControlListener(ColumnControlListener l) {
        columnControlListeners.add(l);
    }
    
    void removeColumnControlListener(ColumnControlListener l) {
        columnControlListeners.remove(l);
    }
    
    private void fireColumnResizedEvent(int resizedColumnNumber, int resizedColumnWidth, int columnToTheRightOfResizedColumnWidth) {
        for (Iterator i = columnControlListeners.iterator(); i.hasNext();) {
            ColumnControlListener l = (ColumnControlListener) i.next();
            l.columnResized(resizedColumnNumber,
                    resizedColumnWidth,
                    columnToTheRightOfResizedColumnWidth);
        }
    }

    private void fireColumnMovedEvent(int[] newColumnOrder) {
        for (Iterator i = columnControlListeners.iterator(); i.hasNext();) {
            ColumnControlListener l = (ColumnControlListener) i.next();
            l.columnMoved(newColumnOrder);
        }
    }

    private void adjustWeightedHeader(AbstractNativeHeader header,
            int resizedColumnPosition, TableColumn resizedColumn,
            TableColumn columnToTheRightOfResizedColumn,
            int totalAvailableWidth, int newTotalWidth) 
    {
        // Adjust percentage of the resized column and the column to the right
        int[] weights = getWeights();
        int resizedColumnPercentage = computePercentage(totalAvailableWidth,
                resizedColumn.getWidth());
        weights[resizedColumnPosition] = resizedColumnPercentage;
        giveRemainderToWeightAtColumn(resizedColumnPosition+1);
        setWeights(weights);
    }

    private void adjustScaledAbsoluteWidthWeights(int resizedColumnPosition, 
            int resizedColumnWidth, int columnToTheRightOfResizedColumnWidth, 
            int headerWidth) 
    {
        int sumOfAllWeights = getSumOfAllWeights();
        double scalingFactor = (double)headerWidth / (double) sumOfAllWeights;
        int unscaledResizedColumWidth = (int)(resizedColumnWidth / scalingFactor);
        int unscaledColumnToTheRightOfResizedColumnWidth = 
            (int)(columnToTheRightOfResizedColumnWidth / scalingFactor);
        
        adjustNonScaledAbsoluteWidthWeights(resizedColumnPosition,
                unscaledResizedColumWidth,
                unscaledColumnToTheRightOfResizedColumnWidth);
    }

    private void adjustNonScaledAbsoluteWidthWeights(int resizedColumnPosition, 
            int resizedColumnWidth, int columnToTheRightOfResizedColumnWidth) 
    {
        int[] weights = getWeights();
        int oldWeightSum = getSumOfAllWeights();
        int currentWeightSum = oldWeightSum;
        
        currentWeightSum += weights[resizedColumnPosition] - resizedColumnWidth;
        currentWeightSum += weights[resizedColumnPosition+1] 
                                    - columnToTheRightOfResizedColumnWidth;
        int weightSumChange = oldWeightSum - currentWeightSum;
        columnToTheRightOfResizedColumnWidth -= weightSumChange;
        
        weights[resizedColumnPosition] = resizedColumnWidth;
        weights[resizedColumnPosition+1] = columnToTheRightOfResizedColumnWidth;
        setWeights(weights);
    }

    private void giveRemainderToWeightAtColumn(int columnNumber) {
        int[] weights = getWeights();
        int totalWeightPercentage = 0;
        for (int i = 0; i < weights.length; i++) {
            totalWeightPercentage += weights[i];
        }
        int spareWidthPercentage = 100 - totalWeightPercentage;
        weights[columnNumber] += spareWidthPercentage;
    }
    
    // Event listeners --------------------------------------------------------
    
    private ControlListener headerControlListener = null;
    
    private boolean wasResized = true;
    
    private class HeaderControlListener implements ControlListener {
        public void controlMoved(ControlEvent e) {
            // Eat the move event that is fired after resize events
            if (wasResized) {
                wasResized = false;
                return;
            }
            Table table = header.headerTable;
            fireColumnMovedEvent(table.getColumnOrder());
            storeLastWidths(table);
        }

        public void controlResized(ControlEvent e) {
            if (lastWidths == null) return;
            wasResized = true;
            if (!layingOut) {
                layingOut = true;
                try {
                    TableColumn tableColumn = (TableColumn) e.widget;
                    AbstractNativeHeader header = asHeader(tableColumn.getParent().getParent());
                    adjustWeights(header, tableColumn);
                    storeLastWidths(tableColumn.getParent());
                } finally {
                    layingOut = false;
                }
            }
        }
    }
    
    private DisposeListener headerDisposeListener = new DisposeListener() {
        public void widgetDisposed(DisposeEvent e) {
            asHeader(e.widget).removeColumnControlListener(headerControlListener);
        }
    };

}

