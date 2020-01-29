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
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * The GanttControlParent is a class that will lay out a Tree or Table so that it matches up with a chart. Basically it
 * will push the control down to to line up with where the header of a Gantt chart ends. <br>
 * <br>
 * It is suggested that users look at the source of this class and implement their own if they need any specific
 * modifications.
 */
public class GanttControlParent extends Composite implements PaintListener {

    private GanttChart              __ganttChart;
    private GanttHeaderSpacedLayout _layout = new GanttHeaderSpacedLayout();

    public GanttControlParent(Composite parent, int style) {
        super(parent, style);

        init();
    }

    public GanttControlParent(Composite parent, int style, GanttChart chart) {
        super(parent, style);

        __ganttChart = chart;

        init();
    }

    private void init() {
        addPaintListener(this);

        setLayout(_layout);
    }

    public void setGanttChart(GanttChart chart) {
        __ganttChart = chart;
        _layout.setGanttChart(chart);
    }

    public GanttChart getGanttChart() {
        return __ganttChart;
    }

    public void paintControl(PaintEvent e) {
        GC gc = e.gc;

        Rectangle bounds = getBounds();
        if (__ganttChart != null) {
            IColorManager colorManager = __ganttChart.getColorManager();
            gc.setForeground(colorManager.getWeekdayBackgroundColorTop());
            gc.setBackground(colorManager.getWeekdayBackgroundColorBottom());
            gc.fillGradientRectangle(0, 0, bounds.width, bounds.height, true);
        } else {
            gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
            gc.fillRectangle(bounds);
        }
    }

}
