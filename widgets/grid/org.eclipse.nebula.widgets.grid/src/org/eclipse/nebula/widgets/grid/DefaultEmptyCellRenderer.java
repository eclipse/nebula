/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    chris.gross@us.ibm.com - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.grid;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * The empty cell renderer.
 *
 * @author chris.gross@us.ibm.com
 * @since 2.0.0
 */
public class DefaultEmptyCellRenderer extends GridCellRenderer
{

    /**
     * {@inheritDoc}
     */
    public void paint(GC gc, Object value)
    {

        Grid table = null;
        if (value instanceof Grid)
            table = (Grid)value;

        GridItem item;
        if (value instanceof GridItem)
        {
            item = (GridItem)value;
            table = item.getParent();
        }

        boolean drawBackground = true;

        if (isSelected())
        {
        	boolean hasFocus = table.isFocusOnGrid();
        	Color backgroundColor = hasFocus?getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION):getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
            Color foregroundColor = hasFocus?getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT):getDisplay().getSystemColor(SWT.COLOR_BLACK);
        	gc.setBackground(backgroundColor);
            gc.setForeground(foregroundColor);
        }
        else
        {
            if (table.isEnabled())
            {
                drawBackground = false;
            }
            else
            {
                gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
            }
            gc.setForeground(table.getForeground());
        }

        if (drawBackground)
            gc.fillRectangle(getBounds().x, getBounds().y, getBounds().width + 1,
                         getBounds().height);

        if (table.getLinesVisible())
        {
            gc.setForeground(table.getLineColor());
            gc.drawLine(getBounds().x, getBounds().y + getBounds().height, getBounds().x
                                                                           + getBounds().width,
                        getBounds().y + getBounds().height);
            gc.drawLine(getBounds().x + getBounds().width - 1, getBounds().y, getBounds().x
                                                                              + getBounds().width
                                                                              - 1,
                        getBounds().y + getBounds().height);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Point computeSize(GC gc, int wHint, int hHint, Object value)
    {
        return new Point(wHint, hHint);
    }

    /**
     * {@inheritDoc}
     */
    public boolean notify(int event, Point point, Object value)
    {
        return false;
    }

}
