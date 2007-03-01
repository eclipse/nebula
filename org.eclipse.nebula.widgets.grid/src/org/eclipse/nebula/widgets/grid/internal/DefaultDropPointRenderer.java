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
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

public class DefaultDropPointRenderer extends AbstractRenderer
{

    public void paint(GC gc, Object value)
    {
        gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));

        gc.fillPolygon(new int[] {getBounds().x + 0, getBounds().y + 4, getBounds().x + 4,
                                  getBounds().y + 0, getBounds().x + 8, getBounds().y + 4,
                                  getBounds().x + 7, getBounds().y + 5, getBounds().x + 6,
                                  getBounds().y + 5, getBounds().x + 4, getBounds().y + 3,
                                  getBounds().x + 2, getBounds().y + 5, getBounds().x + 1,
                                  getBounds().y + 5 });

        gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));

        gc.drawPolyline(new int[] {getBounds().x + 0, getBounds().y + 4, getBounds().x + 4,
                                   getBounds().y + 0, getBounds().x + 8, getBounds().y + 4,
                                   getBounds().x + 7, getBounds().y + 5, getBounds().x + 6,
                                   getBounds().y + 5, getBounds().x + 4, getBounds().y + 3,
                                   getBounds().x + 2, getBounds().y + 5, getBounds().x + 1,
                                   getBounds().y + 5 });

    }

    public Point computeSize(GC gc, int wHint, int hHint, Object value)
    {
        return new Point(9, 7);
    }

    // public void paint(GC gc, Object value)
    // {
    // gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
    //
    // gc.fillPolygon(new int[] {getBounds().x + 0, getBounds().y + 6,
    // getBounds().x + 5,
    // getBounds().y + 1, getBounds().x + 9, getBounds().y + 6 });
    // }
    //
    // public Point computeSize(GC gc, int wHint, int hHint, Object value)
    // {
    // return new Point(10, 6);
    // }

}
