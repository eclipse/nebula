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
import org.eclipse.swt.widgets.Widget;

/**
 * Class GridRowLayout.  A layout manager for CompositeTable header and row objects
 * that implement a tabular (grid) layout.
 * 
 * @author djo
 */
public class GridRowLayout extends AbstractGridRowLayout {

    /**
     * Constructor GridRowLayout.  The default constructor.  If you use this
     * constructor, you must manually specify the column weights, and possibly,
     * the fittingHorizontally property value.
     */
    public GridRowLayout() {
        super();
    }

    /**
     * Constructor GridRowLayout. Construct a GridRowLayout, specifying the
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
    public GridRowLayout(int[] weights, boolean fittingHorizontally) {
        super(weights, fittingHorizontally);
    }

    /**
     * Construct a GridRowLayout, specifying both the weights and the
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
    public GridRowLayout(int[] weights) {
        super(weights);
    }

    protected int computeMaxHeight(Composite rowOrHeader) {
        Control[] children = rowOrHeader.getChildren();
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

    protected Point computeColumnSize(Widget columnObject, int wHint, int hHint, boolean flush) {
        Control control = (Control) columnObject;
        return control.computeSize(wHint, hHint, flush);
    }

    protected Widget getColumnAt(Composite rowOrHeader, int offset) {
        return rowOrHeader.getChildren()[offset];
    }

    protected int getNumColumns(Composite rowOrHeader) {
        return rowOrHeader.getChildren().length;
    }

    protected void setBounds(Widget columnObject, int left, int top, int width, int height) {
        Control control = (Control) columnObject;
        control.setBounds(left, top, width, height);
    }
}
