/*******************************************************************************
 * Copyright (c) Emil Crumhorn - Hexapixel.com - emil.crumhorn@gmail.com
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    emil.crumhorn@gmail.com - initial API and implementation
 *******************************************************************************/ 

package org.eclipse.nebula.widgets.ganttchart;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * Shows any text right above an event (or any location really, but that's the idea) and does not dispose the shell
 * until told to do so. Any recurring calls simply re-position the shell.
 */
class GanttDateTip {

    private static Shell shell;
    private static int yLoc;
    private static Label label;

    public static void makeDialog(IColorManager colorManager, String text, Point location, int marker) {    	    	
    	Point loc = new Point(location.x, location.y);
    	
        if (shell != null && shell.isDisposed() == false) {
        	loc = new Point(loc.x, loc.y);

            // move shell to new location
            shell.setLocation(loc.x, loc.y);

            // update text
            if (yLoc == marker) {
                label.setText(text);
                return;
            }

            shell.dispose();
        }

        yLoc = marker;

        shell = new Shell(Display.getDefault().getActiveShell(), SWT.ON_TOP | SWT.TOOL);
        
        //shell.setBackground(colorManager.getTooltipBackgroundColor());
        shell.setLayout(new FillLayout());
        
        Composite comp = new Composite(shell, SWT.NONE);
        comp.setBackground(colorManager.getTooltipBackgroundColor());

        GridLayout fl = new GridLayout();
        fl.numColumns = 1;
        fl.marginHeight = 4;
        fl.marginWidth = 4;
        comp.setLayout(fl);

        label = new Label(comp, SWT.LEFT);
        label.setBackground(colorManager.getTooltipBackgroundColor());
        label.setForeground(colorManager.getTooltipForegroundColor());
        label.setText(text);

        shell.pack();

        // show above code inside, automatically below otherwise
        loc = new Point(loc.x, loc.y);

        shell.setLocation(loc);
        shell.setVisible(true);
    }

    public static void kill() {
        if (shell != null && shell.isDisposed() == false) {
            shell.dispose();
        }
    }

    public static boolean isActive() {
        return (shell != null && !shell.isDisposed());
    }
}
