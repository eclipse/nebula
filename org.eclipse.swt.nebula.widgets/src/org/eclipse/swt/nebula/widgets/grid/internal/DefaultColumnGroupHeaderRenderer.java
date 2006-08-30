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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.nebula.widgets.grid.GridColumnGroup;
import org.eclipse.swt.nebula.widgets.grid.GridHeaderRenderer;
import org.eclipse.swt.nebula.widgets.grid.IInternalWidget;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

public class DefaultColumnGroupHeaderRenderer extends GridHeaderRenderer
{
    int leftMargin = 6;

    int rightMargin = 6;

    int topMargin = 3;

    int bottomMargin = 3;

    int imageSpacing = 3;

    private ExpandToggleRenderer toggleRenderer = new ExpandToggleRenderer();

    public void paint(GC gc, Object value)
    {
        GridColumnGroup group = (GridColumnGroup)value;

        if (isSelected())
        {
            gc.setBackground(group.getParent().getCellHeaderSelectionBackground());
        }
        else
        {
            gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        }

        gc.fillRectangle(getBounds().x, getBounds().y, getBounds().width + 1,
                         getBounds().height + 1);

        int x = leftMargin;

        if (group.getImage() != null)
        {
            gc.drawImage(group.getImage(), getBounds().x + x, getBounds().y + topMargin);
            x += group.getImage().getBounds().width + imageSpacing;
        }

        int width = getBounds().width - x - rightMargin;
        if ((group.getStyle() & SWT.TOGGLE) != 0)
        {
            width -= toggleRenderer.getSize().x;
        }        

        gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));

        gc.drawString(TextUtils.getShortString(gc, group.getText(), width), getBounds().x + x,
                      getBounds().y + topMargin);

        if ((group.getStyle() & SWT.TOGGLE) != 0)
        {
            toggleRenderer.setHover(isHover() && getHoverDetail().equals("toggle"));
            toggleRenderer.setExpanded(group.getExpanded());
            toggleRenderer.setBounds(getToggleBounds());
            toggleRenderer.paint(gc, null);
        }

        gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));

        gc.drawLine(getBounds().x + getBounds().width - 1, getBounds().y, getBounds().x
                                                                          + getBounds().width - 1,
                    getBounds().y + getBounds().height - 1);
        gc.drawLine(getBounds().x, getBounds().y + getBounds().height - 1, getBounds().x
                                                                           + getBounds().width - 1,
                    getBounds().y + getBounds().height - 1);

    }

    public Point computeSize(GC gc, int wHint, int hHint, Object value)
    {
        GridColumnGroup group = (GridColumnGroup)value;

        int x = 0;

        x += leftMargin;

        x += gc.stringExtent(group.getText()).x + rightMargin;

        int y = 0;

        y += topMargin;

        y += gc.getFontMetrics().getHeight();

        y += bottomMargin;

        if (group.getImage() != null)
        {
            x += group.getImage().getBounds().width + imageSpacing;

            y = Math.max(y, topMargin + group.getImage().getBounds().height + bottomMargin);
        }

        return new Point(x, y);
    }

    public boolean notify(int event, Point point, Object value)
    {
        GridColumnGroup group = (GridColumnGroup)value;
        
        if ((group.getStyle() & SWT.TOGGLE) != 0)
        {
            if (event == IInternalWidget.LeftMouseButtonDown)
            {
                if (getToggleBounds().contains(point))
                {                    
                    group.setExpanded(!group.getExpanded());

                    if (group.getExpanded())
                    {
                        group.notifyListeners(SWT.Expand,new Event());
                    }
                    else
                    {
                        group.notifyListeners(SWT.Collapse,new Event());
                    }
                    return true;
                }
            }
            else
            {
                if (getToggleBounds().contains(point))
                {
                    setHoverDetail("toggle");
                    return true;
                }
            }
        }

        return false;
    }

    private Rectangle getToggleBounds()
    {
        int x = getBounds().x + getBounds().width - toggleRenderer.getBounds().width - rightMargin;
        int y = getBounds().y + (getBounds().height - toggleRenderer.getBounds().height) / 2;

        return new Rectangle(x, y, toggleRenderer.getBounds().width,
                             toggleRenderer.getBounds().height);
    }

    /**
     * {@inheritDoc}
     */
    public void setDisplay(Display display)
    {
        super.setDisplay(display);
        toggleRenderer.setDisplay(display);
    }

    /** 
     * {@inheritDoc}
     */
    public Rectangle getTextBounds(Object value, boolean preferred)
    {
        GridColumnGroup group = (GridColumnGroup)value;

        int x = leftMargin;

        if (group.getImage() != null)
        {
            x += group.getImage().getBounds().width + imageSpacing;
        }

        Rectangle bounds = new Rectangle(x, topMargin, 0, 0);
        
        GC gc = new GC(group.getParent());
        gc.setFont(group.getParent().getFont());

        Point p = gc.stringExtent(group.getText());
        
        bounds.height = p.y;
        
        if (preferred)
        {
            bounds.width = p.x;
        }
        else
        {
            int width = getBounds().width - x - rightMargin;
            if ((group.getStyle() & SWT.TOGGLE) != 0)
            {
                width -= toggleRenderer.getSize().x;
            }  
            bounds.width = width;
        }        

        gc.dispose();
        return bounds;
    }    
}
