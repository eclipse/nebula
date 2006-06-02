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
import org.eclipse.swt.nebula.widgets.grid.AbstractRenderer;

public class DefaultRowHeaderRenderer extends AbstractRenderer
{

    int leftMargin = 6;

    int rightMargin = 6;

    int topMargin = 3;

    int bottomMargin = 3;

    public void paint(GC gc, Object value)
    {
        String num = ((Integer)value).toString();

        gc.setFont(getDisplay().getSystemFont());

        gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

        gc.fillRectangle(getBounds().x, getBounds().y, getBounds().width, getBounds().height + 1);

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

        int x = leftMargin;
        int width = getBounds().width - leftMargin;

        width -= rightMargin;

        gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));

        int y = getBounds().y + (getBounds().height - gc.stringExtent(num).y) / 2;

        if (isSelected())
        {
            gc.drawString(TextUtils.getShortString(gc, num, width), getBounds().x + x + 1, y + 1);
        }
        else
        {
            gc.drawString(TextUtils.getShortString(gc, num, width), getBounds().x + x, y);
        }

    }

    public Point computeSize(GC gc, int wHint, int hHint, Object value)
    {
        String num = ((Integer)value).toString();

        int x = 0;

        x += leftMargin;

        x += gc.stringExtent(num).x + rightMargin;

        int y = 0;

        y += topMargin;

        y += gc.getFontMetrics().getHeight();

        y += bottomMargin;

        return new Point(x, y);
    }

}
