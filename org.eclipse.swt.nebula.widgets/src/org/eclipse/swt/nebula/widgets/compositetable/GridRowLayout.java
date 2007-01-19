/* 
 * Copyright (C) 2005 David Orme <djo@coconut-palm-software.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Orme                  - Initial API and implementation
 *     Coconut Palm Software, Inc. - API cleanup
 *     Pampered Chef, Inc.         - Moved to standalone layout manager
 */
package org.eclipse.swt.nebula.widgets.compositetable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Class GridRowLayout.  A layout manager for CompositeTable header and row objects
 * that implement a tabular (grid) layout.
 * 
 * @author djo
 */
public class GridRowLayout extends CompositeTableLayout {

    private static final int CELL_BORDER_WIDTH = 2;

    /**
     * Constructor GridRowLayout.  The default constructor.
     */
    public GridRowLayout() {}
    
    /**
     * @param weights
     */
    public GridRowLayout(int[] weights) {
        setWeights(weights);
    }

    /**
     * @param weights
     * @param fittingHorizontally
     */
    public GridRowLayout(int[] weights, boolean fittingHorizontally) {
        setWeights(weights);
        setFittingHorizontally(fittingHorizontally);
    }
    
    protected Point computeSize(Composite child, int wHint, int hHint, boolean flushCache) {
        int preferredWidth = computePreferredWidth(child);
        int preferredHeight = computeMaxHeight(child.getChildren());
        return new Point(preferredWidth, preferredHeight);
    }

    protected void layout(Composite child, boolean flushCache) {
        layoutHeaderOrRow(child);
    }

    /**
     * (non-API) Method layoutHeaderOrRow. If a header or row object does not
     * have a layout manager, this method will automatically be called to layout
     * the child controls of that header or row object.
     * 
     * @param child
     *            The child object to layout.
     * @param isHeader
     *            If we're laying out a header or a row object
     * @return the height of the header or row
     */
    int layoutHeaderOrRow(Composite child) {
        if (isFittingHorizontally() || isWidthWiderThanAllColumns(child)) {
            return layoutWeightedHeaderOrRow(child);
        }
        return layoutAbsoluteWidthHeaderOrRow(child);
    }
    
    boolean isWidthWiderThanAllColumns(Composite child) {
        if (isFittingHorizontally()) {
            // If we're fitting horizontally, the width is never wider than
            // all columns
            return false;
        }
        int allColumnsTotalWidth = computePreferredWidth(child);
        return child.getParent().getParent().getSize().x > allColumnsTotalWidth;
    }

    private int computePreferredWidth(Composite child) {
        if (isFittingHorizontally()) {
            return child.getSize().x;
        }
        int allColumnsTotalWidth = 0;
        for (int i = 0; i < weights.length; i++) {
            allColumnsTotalWidth += weights[i] + 2*CELL_BORDER_WIDTH;
        }
        return allColumnsTotalWidth;
    }

    private int layoutWeightedHeaderOrRow(Composite child) {
        Control[] children = child.getChildren();
        if (children.length == 0) {
            return 50;
        }
        int maxHeight = computeMaxHeight(children);

        int[] weights = getWeights();
        if (isFittingHorizontally()) {
            weights = checkWeights(weights, children.length);
        } else {
            weights = computeWeights(weights, children.length);
        }

        int widthRemaining = child.getParent().getSize().x;
        int totalSize = widthRemaining;
        for (int i = 0; i < children.length - 1; i++) {
            int left = totalSize - widthRemaining;
            int desiredHeight = children[i].computeSize(SWT.DEFAULT,
                    SWT.DEFAULT, false).y;
            int top = computeTop(maxHeight, desiredHeight);
            int width = (int) (((float) weights[i]) / 100 * totalSize);
            children[i].setBounds(left + CELL_BORDER_WIDTH, top, 
                    width - 2*CELL_BORDER_WIDTH, desiredHeight);
            widthRemaining -= width;
        }

        int left = totalSize - widthRemaining;
        int desiredHeight = children[children.length - 1].computeSize(
                SWT.DEFAULT, SWT.DEFAULT, false).y;
        int top = computeTop(maxHeight, desiredHeight);
        children[children.length - 1].setBounds(left + CELL_BORDER_WIDTH, top,
                widthRemaining - 2*CELL_BORDER_WIDTH, desiredHeight);

        return maxHeight;
    }

    private int layoutAbsoluteWidthHeaderOrRow(Composite child) {
        Control[] children = child.getChildren();
        if (children.length == 0) {
            return 50;
        }
        int maxHeight = computeMaxHeight(children);

        int[] weights = getWeights();
        int left = 0;
        for (int i = 0; i < children.length; i++) {
            int desiredHeight = children[i].computeSize(SWT.DEFAULT,
                    SWT.DEFAULT, false).y;
            int top = computeTop(maxHeight, desiredHeight);
            children[i].setBounds(left + 2, top, weights[i], desiredHeight);
            left += weights[i] + 2*CELL_BORDER_WIDTH;
        }

        return maxHeight;
    }

    private int computeTop(int maxHeight, int desiredHeight) {
        return maxHeight - desiredHeight - 1;
    }

    private int computeMaxHeight(Control[] children) {
        int maxHeight = 0;
        for (int i = 0; i < children.length; i++) {
            int height = children[i].computeSize(SWT.DEFAULT, SWT.DEFAULT,
                    false).y;
            if (maxHeight < height) {
                maxHeight = height;
            }
        }
        ++maxHeight;
        return maxHeight;
    }

    private int[] computeWeights(int[] weights, int numChildren) {
        if (weights.length != numChildren) {
            return checkWeights(weights, numChildren);
        }
        int allColumnsTotalWidth = 0;
        for (int i = 0; i < weights.length; i++) {
            allColumnsTotalWidth += weights[i];
        }
        int[] realWeights = new int[numChildren];
        int total = 100;
        for (int i = 0; i < realWeights.length; i++) {
            realWeights[i] = (int) (((double) weights[i])
                    / allColumnsTotalWidth * 100);
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

    /**
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
     * absolute width in pixels of the corresponding column.
     * <p>
     * This property is ignored if the programmer has set a layout manager on
     * the header and/or the row prototype objects.
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
     * absolute width in pixels of the corresponding column.
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
    public GridRowLayout setWeights(int[] weights) {
        this.weights = weights;
        return this;
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
    public GridRowLayout setFittingHorizontally(boolean fittingHorizontally) {
        this.fittingHorizontally = fittingHorizontally;
        return this;
    }
}
