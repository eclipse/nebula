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
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

class GanttToolTip {

    private static boolean _codeHoverBelow = false;

    private static Shell   _shell;
    private static String  _lastText;
    private static Point   _lastLocation;

    public static void makeDialog(final IColorManager colorManager, final String name, final String text, final Point location) {
        internalMakeDialog(colorManager, name, text, null, null, null, location);
    }

    public static void makeDialog(final IColorManager colorManager, final String name, final String text, final String text2, final Point location) {
        internalMakeDialog(colorManager, name, text, text2, null, null, location);
    }

    public static void makeDialog(final IColorManager colorManager, final String name, final String text, final String text2, final String text3, final Point location) {
        internalMakeDialog(colorManager, name, text, text2, text3, null, location);
    }

    public static void makeDialog(final IColorManager colorManager, final String name, final String text, final String text2, final String text3, final String text4, final Point location) {
        internalMakeDialog(colorManager, name, text, text2, text3, text4, location);
    }

    private static void internalMakeDialog(final IColorManager colorManager, final String name, final String text, final String text2, final String text3, final String text4, final Point loc) {

        Point location = new Point(loc.x, loc.y);

        if (_shell != null && !_shell.isDisposed()) {

            if (!_codeHoverBelow) {
                location = new Point(location.x, location.y - _shell.getSize().y - 20);
            }

            // same text as before, same place as before, let tooltip stand
            if (_lastText != null && _lastText.equals(text) && _lastLocation != null && _lastLocation.equals(location)) { return; }

            _shell.dispose();
        }

        _lastText = text;

        _shell = new Shell(Display.getDefault().getActiveShell(), SWT.ON_TOP | SWT.TOOL);

        final RowLayout rowLayout = new RowLayout();
        rowLayout.marginLeft = 1;
        rowLayout.marginRight = 1;
        rowLayout.marginTop = 1;
        rowLayout.marginBottom = 1;
        _shell.setLayout(rowLayout);

        _shell.setBackground(colorManager.getTooltipBackgroundColor());

        final Composite comp = new Composite(_shell, SWT.NULL);
        comp.setBackground(colorManager.getTooltipBackgroundColor());

        final FillLayout fillLayout = new FillLayout();
        fillLayout.type = SWT.VERTICAL;
        fillLayout.marginHeight = 0;
        fillLayout.marginWidth = 0;
        comp.setLayout(fillLayout);

        final CLabel label = new CLabel(comp, SWT.LEFT);
        label.setBackground(colorManager.getTooltipBackgroundColor());
        label.setForeground(colorManager.getTooltipForegroundColor());
        label.setText(name);
        Font cur = label.getFont();
        cur = applyBoldFont(cur);
        label.setFont(cur);

        final CLabel textLabel = new CLabel(comp, SWT.LEFT);
        textLabel.setBackground(colorManager.getTooltipBackgroundColor());
        if (text4 != null) {
            textLabel.setForeground(colorManager.getTooltipForegroundColorFaded());
        }
        textLabel.setText(text);

        if (text2 != null) {
            final CLabel text2Label = new CLabel(comp, SWT.LEFT);
            text2Label.setBackground(colorManager.getTooltipBackgroundColor());
            text2Label.setForeground(colorManager.getTooltipForegroundColorFaded());
            text2Label.setText(text2);
        }
        if (text3 != null) {
            final CLabel text3Label = new CLabel(comp, SWT.LEFT);
            text3Label.setBackground(colorManager.getTooltipBackgroundColor());
            text3Label.setForeground(colorManager.getTooltipForegroundColorFaded());
            text3Label.setText(text3);
        }

        if (text4 != null) {
            final CLabel text4Label = new CLabel(comp, SWT.LEFT);
            text4Label.setBackground(colorManager.getTooltipBackgroundColor());
            text4Label.setForeground(colorManager.getTooltipForegroundColorFaded());
            text4Label.setText(text4);
        }

        _shell.pack();

        // show above code inside, automatically below otherwise
        if (!_codeHoverBelow) {
            location = new Point(location.x, location.y - _shell.getSize().y - 10);
        }

        _shell.setLocation(location);
        _lastLocation = location;

        _shell.setVisible(true);
    }

    private static Font applyBoldFont(final Font font) {
        if (font == null) {
            return null;
        }

        final FontData[] fontDataArray = font.getFontData();
        if (fontDataArray == null) { return null; }
        for (int index = 0; index < fontDataArray.length; index++) {
            final FontData fData = fontDataArray[index];
            fData.setStyle(SWT.BOLD);
        }

        return new Font(Display.getDefault(), fontDataArray);
    }

    public static void kill() {
        if (_shell != null && !_shell.isDisposed()) {
            _shell.dispose();
        }
    }

    public static boolean isActive() {
        return (_shell != null && !_shell.isDisposed());
    }
}
