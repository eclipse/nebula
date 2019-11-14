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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;

public class GanttHeaderSpacedLayout extends Layout {

    private int        _ganttHeaderSize;
    private boolean    _calculated;
    private GanttChart _ganttChart;

    private int        _maxY;
    private int        _maxX;

    public GanttHeaderSpacedLayout() {
        super();
    }

    public GanttHeaderSpacedLayout(final GanttChart chart) {
        this();
        _ganttChart = chart;
    }

    public void setGanttChart(final GanttChart chart) {
        _ganttChart = chart;
    }

    private void recalculate(final Composite composite) {
        final Control[] children = composite.getChildren();

        if (children == null || children.length == 0) {
            return;
        }

        if (_ganttChart == null) {
            return;
        }

        int widgetHeaderHeight = 0;
        int borderHeight = 0;
        for (int i = 0; i < children.length; i++) {
            final Control child = children[i];
            final Point wantedSize = child.computeSize(SWT.DEFAULT, SWT.DEFAULT);
            _maxY += wantedSize.y;
            _maxX = Math.max(_maxX, wantedSize.x);
            if (child instanceof Tree) {
                widgetHeaderHeight = ((Tree) child).getHeaderHeight();
                borderHeight = ((Tree) child).getBorderWidth();
            } else if (child instanceof Table) {
                widgetHeaderHeight = ((Table) child).getHeaderHeight();
                borderHeight = ((Table) child).getBorderWidth();
            }
        }

        final ISettings settings = _ganttChart.getSettings();
        if (settings.drawHeader()) {
            _ganttHeaderSize = settings.getHeaderDayHeight() + settings.getHeaderMonthHeight() + settings.getEventsTopSpacer() - widgetHeaderHeight - borderHeight;
        } else {
            _ganttHeaderSize = 0;
        }

        _calculated = true;
    }

    protected Point computeSize(final Composite composite, final int hint, final int hint2, final boolean flushCache) {
        if (flushCache || !_calculated) {
            recalculate(composite);
        }

        layout(composite, false);

        return new Point(_maxX, _maxY);
    }

    protected void layout(final Composite composite, final boolean flushCache) {
        if (flushCache || !_calculated) {
            recalculate(composite);
        }

        final Control[] children = composite.getChildren();

        final Rectangle bounds = composite.getClientArea();

        int y = _ganttHeaderSize;
        for (int i = 0; i < children.length; i++) {
            final Control child = children[i];
            final Point wantedSize = child.computeSize(SWT.DEFAULT, SWT.DEFAULT);

            child.setLocation(0, y);
            child.setSize(bounds.width, bounds.height - y);
            y += wantedSize.y;
        }

    }

}
