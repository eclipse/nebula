/*******************************************************************************
 * Copyright (c) Emil Crumhorn - Hexapixel.com - emil.crumhorn@gmail.com
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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

    private static Shell _shell;
    private static int   _yLoc;
    private static Label _label;

    public static void makeDialog(IColorManager colorManager, String text, Point location, int marker) {
        Point loc = new Point(location.x, location.y);

        if (_shell != null && _shell.isDisposed() == false) {
            loc = new Point(loc.x, loc.y);

            // move shell to new location
            _shell.setLocation(loc.x, loc.y);

            // update text
            if (_yLoc == marker) {
                _label.setText(text);
                return;
            }

            _shell.dispose();
        }

        _yLoc = marker;

        _shell = new Shell(Display.getDefault().getActiveShell(), SWT.ON_TOP | SWT.TOOL);

        //shell.setBackground(colorManager.getTooltipBackgroundColor());
        _shell.setLayout(new FillLayout());

        Composite comp = new Composite(_shell, SWT.NONE);
        comp.setBackground(colorManager.getTooltipBackgroundColor());

        GridLayout fl = new GridLayout();
        fl.numColumns = 1;
        fl.marginHeight = 4;
        fl.marginWidth = 4;
        comp.setLayout(fl);

        _label = new Label(comp, SWT.LEFT);
        _label.setBackground(colorManager.getTooltipBackgroundColor());
        _label.setForeground(colorManager.getTooltipForegroundColor());
        _label.setText(text);

        _shell.pack();

        // show above code inside, automatically below otherwise
        loc = new Point(loc.x, loc.y);

        _shell.setLocation(loc);
        _shell.setVisible(true);
    }

    public static void kill() {
        if (_shell != null && _shell.isDisposed() == false) {
            _shell.dispose();
        }
    }

    public static boolean isActive() {
        return (_shell != null && !_shell.isDisposed());
    }
}
