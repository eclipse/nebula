/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    chris.gross@us.ibm.com - initial API and implementation
 *******************************************************************************/ 
package org.eclipse.swt.nebula.widgets.grid.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.nebula.widgets.grid.AbstractInternalWidget;
import org.eclipse.swt.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.widgets.Display;

public class DefaultColumnHeaderRenderer extends AbstractInternalWidget
{

    int leftMargin = 6;

    int rightMargin = 6;

    int topMargin = 3;

    int bottomMargin = 3;

    int arrowMargin = 6;

    int imageSpacing = 3;

    private SortArrowRenderer arrowRenderer = new SortArrowRenderer();

    public Point computeSize(GC gc, int wHint, int hHint, Object value)
    {
        GridColumn column = (GridColumn)value;

        int x = 0;

        x += leftMargin;

        x += gc.stringExtent(column.getText()).x + rightMargin;

        int y = 0;

        y += topMargin;

        y += gc.getFontMetrics().getHeight();

        y += bottomMargin;

        if (column.getImage() != null)
        {
            x += column.getImage().getBounds().width + imageSpacing;

            y = Math.max(y, topMargin + column.getImage().getBounds().height + bottomMargin);
        }

        return new Point(x, y);
    }

    public void paint(GC gc, Object value)
    {
        paint(gc, (GridColumn)value);
    }

    public void paint(GC gc, GridColumn column)
    {

        boolean flat = (column.getParent().isCellSelection() && !column.getMoveable());
        
        boolean drawSelected = ((isMouseDown() && isHover()));

        gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

        if (flat && isSelected())
        {
            gc.setBackground(column.getParent().getCellSelectionBackground());
        }
        
        gc.fillRectangle(getBounds().x, getBounds().y, getBounds().width + 1,
                         getBounds().height + 1);

        int pushedDrawingOffset = 0;
        if (drawSelected)
        {
            pushedDrawingOffset = 1;
        }

        int x = leftMargin;

        if (column.getImage() != null)
        {
            gc.drawImage(column.getImage(), getBounds().x + x + pushedDrawingOffset,
                         getBounds().y + topMargin + pushedDrawingOffset);
            x += column.getImage().getBounds().width + imageSpacing;
        }

        int width = getBounds().width - x;

        if (column.getSort() == SWT.NONE)
        {
            width -= rightMargin;
        }
        else
        {
            width -= arrowMargin + arrowRenderer.getSize().x + arrowMargin;
        }

        gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));

        int y = getBounds().y + getBounds().height - bottomMargin - gc.getFontMetrics().getHeight();

        gc.drawString(TextUtils.getShortString(gc, column.getText(), width), getBounds().x + x
                                                                             + pushedDrawingOffset,
                      y + pushedDrawingOffset,true);

        if (column.getSort() != SWT.NONE)
        {
            arrowRenderer.setSelected(column.getSort() == SWT.UP);
            if (drawSelected)
            {
                arrowRenderer
                    .setLocation(
                                 getBounds().x + getBounds().width - arrowMargin
                                     - arrowRenderer.getBounds().width + 1,
                                 getBounds().y
                                     + ((getBounds().height - arrowRenderer.getBounds().height) / 2)
                                     + 1);
            }
            else
            {
                arrowRenderer
                    .setLocation(
                                 getBounds().x + getBounds().width - arrowMargin
                                     - arrowRenderer.getBounds().width,
                                 getBounds().y
                                     + ((getBounds().height - arrowRenderer.getBounds().height) / 2));
            }
            arrowRenderer.paint(gc, null);
        }

        if (!flat)
        {
              
            if (drawSelected)
            {
                gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
            }
            else
            {
                gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
            }
    
            gc.drawLine(getBounds().x, getBounds().y, getBounds().x + getBounds().width - 1,
                        getBounds().y);
            gc.drawLine(getBounds().x, getBounds().y, getBounds().x, getBounds().y + getBounds().height
                                                                     - 1);
    
            if (!drawSelected)
            {
                gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
                gc.drawLine(getBounds().x + 1, getBounds().y + 1,
                            getBounds().x + getBounds().width - 2, getBounds().y + 1);
                gc.drawLine(getBounds().x + 1, getBounds().y + 1, getBounds().x + 1,
                            getBounds().y + getBounds().height - 2);
            }
    
            if (drawSelected)
            {
                gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
            }
            else
            {
                gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));
            }
            gc.drawLine(getBounds().x + getBounds().width - 1, getBounds().y, getBounds().x
                                                                              + getBounds().width - 1,
                        getBounds().y + getBounds().height - 1);
            gc.drawLine(getBounds().x, getBounds().y + getBounds().height - 1, getBounds().x
                                                                               + getBounds().width - 1,
                        getBounds().y + getBounds().height - 1);
            
            if (!drawSelected)
            {
                gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
                gc.drawLine(getBounds().x + getBounds().width - 2, getBounds().y + 1,
                            getBounds().x + getBounds().width - 2, getBounds().y + getBounds().height
                                                                   - 2);
                gc.drawLine(getBounds().x + 1, getBounds().y + getBounds().height - 2,
                            getBounds().x + getBounds().width - 2, getBounds().y + getBounds().height
                                                                   - 2);
            }
            
        }
        else
        {
            gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));

            gc.drawLine(getBounds().x + getBounds().width - 1, getBounds().y, getBounds().x
                                                                              + getBounds().width - 1,
                        getBounds().y + getBounds().height - 1);
            gc.drawLine(getBounds().x, getBounds().y + getBounds().height - 1, getBounds().x
                                                                               + getBounds().width - 1,
                        getBounds().y + getBounds().height - 1);
        }
        

    }

    /**
     * {@inheritDoc}
     */
    public void setDisplay(Display display)
    {
        super.setDisplay(display);
        arrowRenderer.setDisplay(display);
    }

    public boolean notify(int event, Point point, Object value)
    {
        // TODO Auto-generated method stub
        return false;
    }

}
