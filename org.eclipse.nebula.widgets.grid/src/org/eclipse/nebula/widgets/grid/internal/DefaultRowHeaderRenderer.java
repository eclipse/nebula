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
package org.eclipse.nebula.widgets.grid.internal;

import org.eclipse.nebula.widgets.grid.AbstractRenderer;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

public class DefaultRowHeaderRenderer extends AbstractRenderer
{

    int leftMargin = 6;

    int rightMargin = 8;

    int topMargin = 3;

    int bottomMargin = 3;

    public void paint(GC gc, Object value)
    {
        GridItem item = (GridItem) value;
        
        String text = getHeaderText(item);

        gc.setFont(getDisplay().getSystemFont());
        
        gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        
        if (isSelected() && item.getParent().getCellSelectionEnabled())
        {
            gc.setBackground(item.getParent().getCellHeaderSelectionBackground());
        }

        gc.fillRectangle(getBounds().x, getBounds().y, getBounds().width, getBounds().height + 1);

        if (!item.getParent().getCellSelectionEnabled())
        {
            if (isSelected())
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
    
            if (!isSelected())
            {
                gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
                gc.drawLine(getBounds().x + 1, getBounds().y + 1,
                            getBounds().x + getBounds().width - 2, getBounds().y + 1);
                gc.drawLine(getBounds().x + 1, getBounds().y + 1, getBounds().x + 1,
                            getBounds().y + getBounds().height - 2);
            }
    
            if (isSelected())
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
    
            if (!isSelected())
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

        int x = leftMargin;
        int width = getBounds().width - leftMargin;

        width -= rightMargin;

        gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));

        int y = getBounds().y + (getBounds().height - gc.stringExtent(text).y) / 2;

        if (isSelected() && !item.getParent().getCellSelectionEnabled())
        {
            gc.drawString(TextUtils.getShortString(gc, text, width), getBounds().x + x + 1, y + 1);
        }
        else
        {
            gc.drawString(TextUtils.getShortString(gc, text, width), getBounds().x + x, y);
        }

    }

    public Point computeSize(GC gc, int wHint, int hHint, Object value)
    {
        GridItem item = (GridItem) value;

        String text = getHeaderText(item);

        int x = 0;

        x += leftMargin;

        x += gc.stringExtent(text).x + rightMargin;

        int y = 0;

        y += topMargin;

        y += gc.getFontMetrics().getHeight();

        y += bottomMargin;

        return new Point(x, y);
    }
    
    private String getHeaderText(GridItem item)
    {
        String text = item.getHeaderText();
        if (text == null)
        {
            text = (item.getParent().indexOf(item) + 1) + "";
        }
        return text;
    }

}
