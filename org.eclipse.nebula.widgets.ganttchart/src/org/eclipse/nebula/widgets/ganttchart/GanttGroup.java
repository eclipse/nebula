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
 * A GanttGroup is a group of GanttEvents that will all draw on the same horizontal "line" in the GanttChart. One GanttEvent may only belong to one GanttGroup. 
 * Any adding of an event that already has a GanttGroup assigned to it will overwrite the previously set group and thus move it to the new group.
 */
public class GanttGroup extends AbstractGanttEvent implements IGanttChartItem {

	public static final int	FIXED_ROW_HEIGHT_AUTOMATIC	= -1;

	private ArrayList		_events;
	private int				_fixedRowHeight				= FIXED_ROW_HEIGHT_AUTOMATIC;
	private int				_verticalEventAlignment		= SWT.TOP;
	private GanttChart		_chart;

	/**
	 * Creates a new GanttGroup on the given GanttChart.
	 * 
	 * @param parent GanttChart parent
	 */
	public GanttGroup(GanttChart parent) {
		_chart = parent;
		_events = new ArrayList();
		parent.addGroup(this);
	}

	/**
	 * Adds a GanttEvent to this group. Do note that if a GanttEvent exists in another group already, it will end up being moved to the new group.
	 * 
	 * @param event GanttEvent to add.
	 */
	public void addEvent(GanttEvent event) {
		if (!_events.contains(event))
			_events.add(event);

		event.setGanttGroup(this);
	}

	/**
	 * Removes a GanttEvent from this group.
	 * 
	 * @param event GanttEvent to remove.
	 */
	public void removeEvent(GanttEvent event) {
		if (event.getGanttGroup() == this)
			event.setGanttGroup(null);

		_events.remove(event);
	}

	/**
	 * Checks whether this GanttGroup contains a given GanttEvent.
	 * 
	 * @param event GanttEvent to check if it exists in this GanttGroup.
	 * @return true if the event is contained in this group.
	 */
	public boolean containsEvent(GanttEvent event) {
		return _events.contains(event);
	}

	/**
	 * Returns a list of all GanttEvents contained in this group.
	 * 
	 * @return List of GanttEvents.
	 */
	public ArrayList getEventMembers() {
		return _events;
	}

	/**
	 * Returns the fixed row height of this group.
	 * 
	 * @return Fixed row height.
	 */
	public int getFixedRowHeight() {
		return _fixedRowHeight;
	}

	/**
	 * Sets the fixed row height for this group.
	 * 
	 * @param fixedRowHeight Row height in pixels.
	 */
	public void setFixedRowHeight(int fixedRowHeight) {
		this._fixedRowHeight = fixedRowHeight;
	}

	/**
	 * Whether this group is on automatic row height or if the height is fixed.
	 * 
	 * @return true if row height is calculated automatically.
	 */
	public boolean isAutomaticRowHeight() {
		return (getFixedRowHeight() == FIXED_ROW_HEIGHT_AUTOMATIC);
	}

	/**
	 * Flags this group to use automatic row height calculation.
	 */
	public void setAutomaticRowHeight() {
		setFixedRowHeight(FIXED_ROW_HEIGHT_AUTOMATIC);
	}

	/**
	 * Returns the vertical alignment of all events in this row.
	 * 
	 * @return Vertical alignment.
	 */
	public int getVerticalEventAlignment() {
		return _verticalEventAlignment;
	}

	/**
	 * Sets the vertical alignment of all contained events.
	 * 
	 * @param verticalEventAlignment Vertical alignment. Valid values are: SWT.TOP, SWT.CENTER, SWT.BOTTOM. Default is SWT.TOP.
	 */
	public void setVerticalEventAlignment(int verticalEventAlignment) {
		this._verticalEventAlignment = verticalEventAlignment;
	}

	/**
	 * Disposes this event from the chart.
	 */
	public void dispose() {
		_chart.removeGroup(this);
		_chart.redraw();
	}
	
	int getTallestEvent() {
	    int max = 0;
	    for (int i = 0; i < _events.size(); i++) {
	        GanttEvent ge = ((GanttEvent) _events.get(i));
	        max = Math.max(ge.getHeight(), max);
	    }
	    return max;
	}

	public String toString() {
		return "[GanttGroup " + _events.toString() + "]";
	}
}