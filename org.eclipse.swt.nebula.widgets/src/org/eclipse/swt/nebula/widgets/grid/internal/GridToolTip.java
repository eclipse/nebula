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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

/**
 * An in-place tooltip.
 *
 * @author cgross
 */
public class GridToolTip extends Widget
{
    private Shell shell;

    private String text;

    private int ymargin = 2;

    private int xmargin = 3;

    public GridToolTip(final Control parent)
    {
        super(parent, SWT.NONE);

        shell = new Shell(parent.getShell(), SWT.NO_TRIM | SWT.ON_TOP | SWT.NO_FOCUS | SWT.TOOL);
        shell.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
        shell.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));

        shell.addListener(SWT.Paint, new Listener()
        {
            public void handleEvent(Event e)
            {
                onPaint(e.gc);
            }
        });

        shell.addListener(SWT.MouseDown, new Listener()
        {
            public void handleEvent(Event e)
            {
                setVisible(false);
                Point p = new Point(e.x, e.y);
                p = shell.getDisplay().map(shell, parent, p);
                Event newEvent = new Event();
                newEvent.button = e.button;
                newEvent.x = p.x;
                newEvent.y = p.y;
                newEvent.stateMask = e.stateMask;
                parent.notifyListeners(SWT.MouseDown, newEvent);
            }
        });

        shell.addListener(SWT.MouseMove, new Listener()
        {
            public void handleEvent(Event e)
            {
                Point p = new Point(e.x, e.y);
                p = shell.getDisplay().map(shell, parent, p);
                Event newEvent = new Event();
                newEvent.x = p.x;
                newEvent.y = p.y;
                newEvent.button = e.button;
                newEvent.stateMask = e.stateMask;
                parent.notifyListeners(SWT.MouseMove, newEvent);
            }
        });

        shell.addListener(SWT.MouseUp, new Listener()
        {
            public void handleEvent(Event e)
            {
                Point p = new Point(e.x, e.y);
                p = shell.getDisplay().map(shell, parent, p);
                Event newEvent = new Event();
                newEvent.x = p.x;
                newEvent.y = p.y;
                newEvent.button = e.button;
                newEvent.stateMask = e.stateMask;
                parent.notifyListeners(SWT.MouseUp, newEvent);
            }
        });

        shell.addListener(SWT.MouseExit, new Listener()
        {
            public void handleEvent(Event e)
            {
                Point p = new Point(e.x, e.y);
                p = shell.getDisplay().map(shell, parent, p);

                Event newEvent = new Event();
                newEvent.x = p.x;
                newEvent.y = p.y;
                newEvent.button = e.button;
                newEvent.stateMask = e.stateMask;

                if (p.x < 0 || p.y < 0 || p.x > parent.getBounds().width
                    || p.y > parent.getBounds().height)
                {
                    newEvent.detail = SWT.TOOL;
                    parent.notifyListeners(SWT.MouseExit, newEvent);
                }
                else
                {
                    parent.notifyListeners(SWT.MouseMove, newEvent);
                }
            }
        });
    }

    private void onPaint(GC gc)
    {
        gc.drawRectangle(0, 0, shell.getSize().x - 1, shell.getSize().y - 1);

        gc.drawString(text, xmargin, ymargin, true);
    }

    public void setLocation(Point location)
    {
        shell.setLocation(location.x - xmargin, location.y - ymargin);
    }

    public void setVisible(boolean visible)
    {
        if (visible && shell.getVisible())
        {
            shell.redraw();
        }
        else
        {
            shell.setVisible(visible);
        }
    }

    public void setFont(Font font)
    {
        shell.setFont(font);
    }

    /**
     * @return the text
     */
    public String getText()
    {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text)
    {
        this.text = text;

        GC gc = new GC(shell);
        Point size = gc.stringExtent(text);
        gc.dispose();

        size.x += xmargin + xmargin;
        size.y += ymargin + ymargin;

        shell.setSize(size);

    }

    /**
     * {@inheritDoc}
     */
    protected void checkSubclass()
    {

    }

}
