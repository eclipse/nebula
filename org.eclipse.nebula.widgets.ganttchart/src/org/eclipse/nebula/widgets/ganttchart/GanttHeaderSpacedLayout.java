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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;

public class GanttHeaderSpacedLayout extends Layout {

	private int mGanttHeaderSize;
	private boolean mCalculated;
	private GanttChart mGanttChart;
	
	private int maxY;
	private int maxX; 
	
	public GanttHeaderSpacedLayout() {
		
	}
	
	public GanttHeaderSpacedLayout(GanttChart chart) {
		mGanttChart = chart;
	}
	
	public void setGanttChart(GanttChart chart) {
		mGanttChart = chart;
	}
	
	private void recalculate(Composite composite) {
		Control [] children = composite.getChildren();
		
		if (children == null || children.length == 0)
			return;
		
		if (mGanttChart == null)
			return;			

		int widgetHeaderHeight = 0;
		int borderHeight = 0;
		for (int i = 0; i < children.length; i++) {
			Control child = children[i];
			Point wantedSize = child.computeSize(SWT.DEFAULT, SWT.DEFAULT);			
			maxY += wantedSize.y;
			maxX = Math.max(maxX, wantedSize.x);
			if (child instanceof Tree) {
				widgetHeaderHeight = ((Tree)child).getHeaderHeight();
				borderHeight = ((Tree)child).getBorderWidth();
			}
			else if (child instanceof Table) {
				widgetHeaderHeight = ((Table)child).getHeaderHeight();
				borderHeight = ((Table)child).getBorderWidth();
			}
		}
		
		ISettings settings = mGanttChart.getSettings();
		if (settings.drawHeader()) {
			mGanttHeaderSize = settings.getHeaderDayHeight() + settings.getHeaderMonthHeight() + settings.getEventsTopSpacer() - widgetHeaderHeight - borderHeight;
		}
		else
			mGanttHeaderSize = 0;
						
		mCalculated = true;
	}
	
	protected Point computeSize(Composite composite, int hint, int hint2, boolean flushCache) {
		if (flushCache || !mCalculated)
			recalculate(composite);
	
		layout(composite, false);
		
		return new Point(maxX, maxY);
	}

	protected void layout(Composite composite, boolean flushCache) {	
		if (flushCache || !mCalculated)
			recalculate(composite);
		
		Control [] children = composite.getChildren();

		Rectangle bounds = composite.getClientArea();
		
		int y = mGanttHeaderSize;
		for (int i = 0; i < children.length; i++) {
			Control child = children[i];
			Point wantedSize = child.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			
			child.setLocation(0, y);
			child.setSize(bounds.width, bounds.height-y);
			y += wantedSize.y;
		}
		
	}

	
}
