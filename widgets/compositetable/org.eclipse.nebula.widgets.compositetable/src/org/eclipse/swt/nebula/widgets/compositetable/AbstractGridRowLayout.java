package org.eclipse.swt.nebula.widgets.compositetable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

abstract class AbstractGridRowLayout extends CompositeTableLayout {
    protected static final int CELL_BORDER_WIDTH = 2;

    /**
     * Constructor AbstractGridRowLayout.  The default constructor.  If you use this
     * constructor, you must manually specify the column weights, and possibly,
     * the fittingHorizontally property value.
     */
    public AbstractGridRowLayout() {}
    
    /**
     * Constructor AbstractGridRowLayout. Construct a AbstractGridRowLayout,
     * specifying the column weights. By default, fittingHorizontally is false.
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
    public AbstractGridRowLayout(int[] weights) {
        setWeights(weights);
    }

    /**
     * Construct a AbstractGridRowLayout, specifying both the weights and the
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
    public AbstractGridRowLayout(int[] weights, boolean fittingHorizontally) {
        setWeights(weights);
        setFittingHorizontally(fittingHorizontally);
    }
    
    protected Point computeSize(Composite child, int wHint, int hHint, boolean flushCache) {
        int preferredWidth = computePreferredWidth(child);
        int preferredHeight = computeMaxHeight(child);
        return new Point(preferredWidth, preferredHeight);
    }

    protected void layout(Composite child, boolean flushCache) {
        if (isFittingHorizontally() || isWidthWiderThanAllColumns(child)) {
            layoutWeightedHeaderOrRow(child);
        } else {
            layoutAbsoluteWidthHeaderOrRow(child);
        }
    }

    /**
     * Given the specified header or row, computes if the available width
     * is wider than the sum of all columns' preferred widths.
     * 
     * @param headerOrRow
     *            The header or row
     * @return true if the available width is wider than the sum of all columns'
     *         preferred widths; false otherwise.
     */
    protected boolean isWidthWiderThanAllColumns(Composite headerOrRow) {
        if (isFittingHorizontally()) {
            // If we're fitting horizontally, the width is never wider than
            // all columns
            return false;
        }
        int allColumnsTotalWidth = computePreferredWidth(headerOrRow);
        return getAvailableWidth(headerOrRow) > allColumnsTotalWidth;
    }

    /**
     * Returns the number of horizontal pixels available for column data.
     * 
     * @param headerOrRow The header or row object
     * @return int the number of horizontal pixels available for column data.
     */
    protected int getAvailableWidth(Composite headerOrRow) {
        return headerOrRow.getParent().getParent().getSize().x;
    }

    private int computePreferredWidth(Composite child) {
        if (isFittingHorizontally()) {
            return 1;
        }
        int allColumnsTotalWidth = 0;
        int[] colWeights = getWeights();
        for (int i = 0; i < colWeights.length; i++) {
            allColumnsTotalWidth += colWeights[i] + 2*CELL_BORDER_WIDTH;
        }
        return allColumnsTotalWidth;
    }

    private int layoutWeightedHeaderOrRow(Composite child) {
        int numChildren = getNumColumns(child);
        
        if (numChildren == 0) {
            return 50;
        }
        int maxHeight = computeMaxHeight(child);

        int[] colWeights = getWeights();
        if (isFittingHorizontally()) {
            colWeights = checkWeights(colWeights, numChildren);
        } else {
            colWeights = computeWeights(colWeights, numChildren);
        }

        int widthRemaining = child.getParent().getSize().x;
        int totalSize = widthRemaining;
        for (int i = 0; i < numChildren - 1; i++) {
            int left = totalSize - widthRemaining;
            Widget columnObject = getColumnAt(child, i);
            
            int leftPos = left + CELL_BORDER_WIDTH;
            int width = (int) (((float) colWeights[i]) / 100 * totalSize);
            int widthIncludingBorderWidth = width - 2*CELL_BORDER_WIDTH;
            int desiredHeight = computeColumnSize(columnObject, SWT.DEFAULT, 
                    SWT.DEFAULT, false).y;
            int top = computeTop(maxHeight, desiredHeight);
            
            setBounds(columnObject, leftPos, top, widthIncludingBorderWidth, 
                    desiredHeight - 1);
            widthRemaining -= width;
        }

        Widget lastColumn = getColumnAt(child, numChildren - 1);
        
        int left = totalSize - widthRemaining;
        int desiredHeight = computeColumnSize(lastColumn,
                SWT.DEFAULT, SWT.DEFAULT, false).y;
        int top = computeTop(maxHeight, desiredHeight);
        
        setBounds(lastColumn, left + CELL_BORDER_WIDTH, top,
                widthRemaining - 2*CELL_BORDER_WIDTH, desiredHeight);

        return maxHeight;
    }

    private int layoutAbsoluteWidthHeaderOrRow(Composite child) {
        int numChildren = getNumColumns(child);
        if (numChildren == 0) {
            return 50;
        }
        int maxHeight = computeMaxHeight(child);

        int[] colWidths = getWeights();
        int left = 0;
        for (int i = 0; i < numChildren; i++) {
            Widget column = getColumnAt(child, i);
            int desiredHeight = computeColumnSize(column, SWT.DEFAULT,
                    SWT.DEFAULT, false).y;
            int top = computeTop(maxHeight, desiredHeight);
            setBounds(column, left + 2, top, colWidths[i], desiredHeight);
            left += colWidths[i] + 2*CELL_BORDER_WIDTH;
        }

        return maxHeight;
    }

    /**
     * Return the number of columns in the specified row or header.
     * 
     * @param rowOrHeader The row or header object.
     * @return int the number of columns in the specified row or header.
     */
    protected abstract int getNumColumns(Composite rowOrHeader);

    /**
     * Return the maximum desired height of each of the row or header's children.
     * 
     * @param rowOrHeader The row or header Composite
     * @return int the maximum desired height of each of the row or header's children.
     */
    protected abstract int computeMaxHeight(Composite rowOrHeader);

    /**
     * Return the SWT Widget representing the specified column.
     * @param rowOrHeader The header or row object
     * @param offset The column's offset.
     * @return The SWT Widget.
     */
    protected abstract Widget getColumnAt(Composite rowOrHeader, int offset);
    
    /**
     * Compute and return the preferred size of the specified column object,
     * passing the usual SWT wHint, hHint, and flush parameters.
     * 
     * @param columnObject The column object
     * @param wHint SWT.DEFAULT or a preferred width as an int
     * @param hHint SWT.DEFAULT or a preferred height as an int
     * @param flush If any cached size should be flushed and recomputed.
     * @return Point the preferred size.
     */
    protected abstract Point computeColumnSize(Widget columnObject, int wHint, int hHint, boolean flush);
    
    /**
     * Set the bounds of the specified column object.  Any of the parameters may
     * be ignored if necessary (for example, a real Table header will ignore the
     * top and height parameters).
     * 
     * @param columnObject The column object to place
     * @param left The column's left coordinate
     * @param top The column's top coordinate
     * @param width The column's width
     * @param height The column's height
     */
    protected abstract void setBounds(Widget columnObject, int left, int top, int width, int height);
    
    private int computeTop(int maxHeight, int desiredHeight) {
        return ((maxHeight - desiredHeight) / 2);
    }

    private int[] computeWeights(int[] weights, int numChildren) {
        if (weights.length != numChildren) {
            return checkWeights(weights, numChildren);
        }
        
        int[] realWeights = new int[numChildren];
        int total = 100;
        for (int i = 0; i < realWeights.length; i++) {
            realWeights[i] = (int) (((double) weights[i])
                    / getSumOfAllWeights() * 100);
            total -= realWeights[i];
        }
        
        int i = 0;
        while (total > 0) {
            ++realWeights[i];
            --total;
            ++i;
            if (i >= realWeights.length) {
                i = 0;
            }
        }
        return realWeights;
    }

    /*
     * Compute and return a weights array where each weight is the percentage
     * the corresponding column should occupy of the entire control width. If
     * the elements in the supplied weights array add up to 100 and the length
     * of the array is the same as the number of columns, the supplied weights
     * array is used. Otherwise, this method computes and returns a weights
     * array that makes each column an equal size.
     * 
     * @param weights
     *            The default or user-supplied weights array.
     * @param numChildren
     *            The number of child controls.
     * @return The weights array that will be used by the layout manager.
     */
    private int[] checkWeights(int[] weights, int numChildren) {
        if (weights.length == numChildren) {
            int sum = 0;
            for (int i = 0; i < weights.length; i++) {
                sum += weights[i];
            }
            if (sum == 100) {
                return weights;
            }
        }

        // Either the number of weights doesn't match or they don't add up.
        // Compute something sane and return that instead.
        int[] result = new int[numChildren];
        int weight = 100 / numChildren;
        int extra = 100 % numChildren;
        for (int i = 0; i < result.length - 1; i++) {
            result[i] = weight;
            if (extra > 0) {
                result[i]++;
                extra--;
            }
        }
        result[numChildren - 1] = weight + extra;
        return result;
    }

    private int[] weights = new int[0];

    /**
     * Method getWeights. If isFittingHorizontally, returns an array
     * representing the percentage of the total width each column is allocated
     * or null if no weights have been specified.
     * <p>
     * If !isFittingHorizontally, returns an array where each element is the
     * minimum width in pixels of the corresponding column.
     * 
     * @return the current weights array or null if no weights have been
     *         specified.
     */
    public int[] getWeights() {
        return weights;
    }

    /**
     * Method setWeights. If isFittingHorizontally, specifies an array
     * representing the percentage of the total width each column is allocated
     * or null if no weights have been specified.
     * <p>
     * If !isFittingHorizontally, specifies an array where each element is the
     * minimum width in pixels of the corresponding column.
     * <p>
     * This property is ignored if the programmer has set a layout manager on
     * the header and/or the row prototype objects.
     * <p>
     * The number of elements in the array must match the number of columns and
     * if isFittingHorizontally, the sum of all elements must equal 100. If
     * either of these constraints is not true, this property will be ignored
     * and all columns will be created equal in width.
     * 
     * @param weights
     *            the weights to use if the CompositeTable is automatically
     *            laying out controls.
     * @return this
     */
    public AbstractGridRowLayout setWeights(int[] weights) {
        this.weights = weights;
        sumOfAllWeights = 0;
        for (int i = 0; i < weights.length; i++) {
            sumOfAllWeights += weights[i];
        }
        
        return this;
    }

    private int sumOfAllWeights;
    
    /**
     * Returns the sum of all the weights in the weights property
     * 
     * @return the sum of all the weights in the weights property
     */
    public int getSumOfAllWeights() {
        return sumOfAllWeights;
    }

    private boolean fittingHorizontally = false;

    /**
     * Method isFittingHorizontally. Returns if the CompositeTable control will
     * scale the widths of all columns so that they all fit into the available
     * space.  The default value is false.
     * 
     * @return Returns true if the table's actual width is set to equal the
     *         visible width; false otherwise.
     */
    public boolean isFittingHorizontally() {
        return fittingHorizontally;
    }

    /**
     * Method setFittingHorizontally. Sets if the CompositeTable control will
     * scale the widths of all columns so that they all fit into the available
     * space.  The default value is false.
     * 
     * @param fittingHorizontally
     *            true if the table's actual width is set to equal the visible
     *            width; false otherwise.
     * @return this
     */
    public AbstractGridRowLayout setFittingHorizontally(boolean fittingHorizontally) {
        this.fittingHorizontally = fittingHorizontally;
        return this;
    }

}
