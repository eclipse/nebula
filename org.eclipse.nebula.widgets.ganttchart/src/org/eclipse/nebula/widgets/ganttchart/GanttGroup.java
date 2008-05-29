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

import java.util.ArrayList;

import org.eclipse.swt.SWT;

/**
 * A GanttGroup is a group of GanttEvents that will all draw on the same horizontal "line"
 * in the GanttChart. One GanttEvent may only belong to one GanttGroup, any adding
 * of an event that already has a GanttGroup assigned to it will overwrite the previously 
 * set group.
 */
public class GanttGroup implements IGanttChartItem {

	public static final int FIXED_ROW_HEIGHT_AUTOMATIC = -1;
	
	private ArrayList mEvents;
    private int fixedRowHeight = FIXED_ROW_HEIGHT_AUTOMATIC;
    private int verticalEventAlignment = SWT.TOP;    
	
	public GanttGroup(GanttChart parent) {
		mEvents = new ArrayList();
		parent.addGroup(this);
	}
	
	public void addEvent(GanttEvent event) {
		if (!mEvents.contains(event))
			mEvents.add(event);
		
		event.setGanttGroup(this);
	}
	
	public void removeEvent(GanttEvent event) {
		if (event.getGanttGroup() == this)
			event.setGanttGroup(null);
		
		mEvents.remove(event);
	}
	
	public boolean containsEvent(GanttEvent event) {
		return mEvents.contains(event);
	}
	
	public ArrayList getEventMembers() {
		return mEvents;
	}
	
	public int getFixedRowHeight() {
		return fixedRowHeight;
	}

	public void setFixedRowHeight(int fixedRowHeight) {
		this.fixedRowHeight = fixedRowHeight;
	}
	
	public boolean isAutomaticRowHeight() {
		return (getFixedRowHeight() == FIXED_ROW_HEIGHT_AUTOMATIC);
	}
	
	public void setAutomaticRowHeight() {
		setFixedRowHeight(FIXED_ROW_HEIGHT_AUTOMATIC);
	}
	
	public int getVerticalEventAlignment() {
		return verticalEventAlignment;
	}

	public void setVerticalEventAlignment(int verticalEventAlignment) {
		this.verticalEventAlignment = verticalEventAlignment;
	}
	
	public String toString() {
		return "[GanttGroup " + mEvents.toString() + "]";
	}
}