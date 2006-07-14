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
import org.eclipse.swt.nebula.widgets.grid.GridCellRenderer;
import org.eclipse.swt.nebula.widgets.grid.GridItem;
import org.eclipse.swt.nebula.widgets.grid.IInternalWidget;
import org.eclipse.swt.widgets.Display;

public class CheckCellRenderer extends GridCellRenderer
{
    private int margin = 6;

    private CheckBoxRenderer checkRenderer = new CheckBoxRenderer();

    public void paint(GC gc, Object value)
    {
        GridItem item = (GridItem)value;

        if (isSelected())
        {
            gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION));
        }
        else
        {
            if (item.getParent().isEnabled())
            {
                gc.setBackground(item.getBackground(getColumn()));
            }
            else
            {
                gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
            }
        }

        gc.fillRectangle(getBounds().x, getBounds().y, getBounds().width + 1,
                         getBounds().height + 1);

        checkRenderer.setChecked(item.getChecked(getColumn()));
        checkRenderer.setGrayed(item.getGrayed(getColumn()));
        checkRenderer.setHover(getHoverDetail().equals("check"));

        checkRenderer.setLocation(getCheckLocation());
        checkRenderer.paint(gc, null);

        gc.setForeground(item.getParent().getLineColor());

        if (item.getParent().getLinesVisible())
        {
            gc.drawLine(getBounds().x, getBounds().y + getBounds().height, getBounds().x
                                                                           + getBounds().width - 1,
                        getBounds().y + getBounds().height);
            gc.drawLine(getBounds().x + getBounds().width - 1, getBounds().y, getBounds().x
                                                                              + getBounds().width
                                                                              - 1,
                        getBounds().y + getBounds().height);
        }

    }

    public Point computeSize(GC gc, int wHint, int hHint, Object value)
    {

        return new Point(margin + checkRenderer.getSize().x + margin, checkRenderer.getSize().y);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.mozart.mwt.internal.widgets.IInternalWidget#notify(int,
     * org.eclipse.swt.graphics.Point, java.lang.Object)
     */
    public boolean notify(int event, Point point, Object value)
    {
        GridItem item = (GridItem)value;

        if (event == IInternalWidget.MouseMove)
        {
            if (overCheck(point))
            {
                setHoverDetail("check");
                return true;
            }
        }
        if (event == IInternalWidget.LeftMouseButtonDown)
        {
            if (overCheck(point))
            {
                item.setChecked(getColumn(), !item.getChecked(getColumn()));
                item.getParent().redraw();
                return true;
            }
        }

        return false;
    }

    private boolean overCheck(Point point)
    {
        Point checkAt = getCheckLocation();

        if (point.x >= checkAt.x && point.x < checkAt.x + checkRenderer.getSize().x)
        {

            if (point.y >= checkAt.y && point.y < checkAt.y + checkRenderer.getSize().y)
            {
                return true;
            }
        }
        return false;
    }

    private Point getCheckLocation()
    {

        int x = getBounds().x + (getBounds().width - checkRenderer.getSize().x) / 2;
        if (x < margin)
            x = margin;

        int y = getBounds().y + ((getBounds().height - checkRenderer.getSize().y) / 2);
        if (y < 0)
            y = 0;

        return new Point(x, y);
    }

    /**
     * {@inheritDoc}
     */
    public void setDisplay(Display display)
    {
        super.setDisplay(display);
        checkRenderer.setDisplay(display);
    }

}
