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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;

/**
 * A GanttGroup is a group of GanttEvents that will all draw on the same horizontal "line" or "row" in the GanttChart (next to each other instead of vertically arranged). One
 * GanttEvent may only belong to one GanttGroup. 
 * <p />
 * One GanttEvent can only exist in one GanttGroup. If an event already has a different GanttGroup parent, the old parent will be overwritten and the new one will be set on the event.
 */
public class GanttGroup extends AbstractGanttEvent implements IGanttChartItem {

    public static final int  FIXED_ROW_HEIGHT_AUTOMATIC = -1;

    private final List       _events;
    private int              _fixedRowHeight            = FIXED_ROW_HEIGHT_AUTOMATIC;
    private int              _vAlignment                = SWT.TOP;
    private final GanttChart _chart;

    /**
     * Creates a new GanttGroup on the given GanttChart.
     * 
     * @param parent GanttChart parent
     */
    public GanttGroup(final GanttChart parent) {
        super();
        _chart = parent;
        _events = new ArrayList();
        parent.addGroup(this);
    }

    /**
     * Adds a GanttEvent to this group. Do note that if a GanttEvent exists in another group already, it will end up
     * being moved to the new group.
     * 
     * @param event GanttEvent to add.
     */
    public void addEvent(final GanttEvent event) {
        if (!_events.contains(event)) {
            _events.add(event);
        }

        event.setGanttGroup(this);
    }

    /**
     * Removes a GanttEvent from this group.
     * 
     * @param event GanttEvent to remove.
     */
    public void removeEvent(final GanttEvent event) {
        if (event.getGanttGroup() == this) {
            event.setGanttGroup(null);
        }

        _events.remove(event);
    }

    /**
     * Checks whether this GanttGroup contains a given GanttEvent.
     * 
     * @param event GanttEvent to check if it exists in this GanttGroup.
     * @return true if the event is contained in this group.
     */
    public boolean containsEvent(final GanttEvent event) {
        return _events.contains(event);
    }

    /**
     * Returns a list of all GanttEvents contained in this group.
     * 
     * @return List of GanttEvents.
     */
    public List getEventMembers() {
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
    public void setFixedRowHeight(final int fixedRowHeight) {
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
        return _vAlignment;
    }

    /**
     * Sets the vertical alignment of all contained events.
     * 
     * @param vAlignment Vertical alignment. Valid values are: SWT.TOP, SWT.CENTER, SWT.BOTTOM. Default is SWT.TOP.
     */
    public void setVerticalEventAlignment(final int vAlignment) {
        this._vAlignment = vAlignment;
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
            final GanttEvent event = ((GanttEvent) _events.get(i));
            max = Math.max(event.getHeight(), max);
        }
        return max;
    }

    public String toString() {
        return "[GanttGroup " + _events.toString() + "]";
    }
}