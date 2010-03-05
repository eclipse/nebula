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

package org.eclipse.nebula.widgets.ganttchart.undoredo.commands;

import java.util.Calendar;

import org.eclipse.nebula.widgets.ganttchart.GanttEvent;
import org.eclipse.nebula.widgets.ganttchart.GanttSection;

/**
 * Represents one GanttEvent DND action
 * 
 * @author cre
 */
public class EventMoveCommand extends AbstractUndoRedoCommand {

    private GanttEvent _event;
    private Calendar   _startDateBefore;
    private Calendar   _startDateAfter;
    private Calendar   _endDateBefore;
    private Calendar   _endDateAfter;
    private Calendar   _revisedStartDateBefore;
    private Calendar   _revisedStartDateAfter;
    private Calendar   _revisedEndDateBefore;
    private Calendar   _revisedEndDateAfter;
    private int        _indexBefore;
    private int        _indexAfter;
    private int        _sectionBefore;
    private int        _sectionAfter;

    /**
     * Creates a new undoable/redoable Move Event.
     * 
     * @param event {@link GanttEvent} being moved
     * @param startDateBefore Estimated start date prior to move
     * @param startDateAfter Estimated start date after to move
     * @param endDateBefore Estimated end date before move
     * @param endDateAfter Estimated end date after move
     * @param revisedStartDateBefore Revised start date prior to move
     * @param revisedStartDateAfter Revised start date after move
     * @param revisedEndDateBefore Revised end date prior to move
     * @param revisedEndDateAfter Revised end date after move
     * @param sectionBefore {@link GanttSection} index (of all GanttSections) prior to move
     * @param sectionAfter {@link GanttSection} index (of all GanttSections) after move
     * @param indexBefore Index of event in {@link GanttSection} sectionBefore prior to move
     * @param indexAfter Index of event in {@link GanttSection} sectionAfter after to move
     */
    public EventMoveCommand(GanttEvent event, Calendar startDateBefore, Calendar startDateAfter, Calendar endDateBefore, Calendar endDateAfter, Calendar revisedStartDateBefore, Calendar revisedStartDateAfter, Calendar revisedEndDateBefore, Calendar revisedEndDateAfter, GanttSection sectionBefore,
            GanttSection sectionAfter, int indexBefore, int indexAfter) {
        _event = event;
        _startDateBefore = (Calendar) startDateBefore.clone();
        _startDateAfter = (Calendar) startDateAfter.clone();

        if (revisedStartDateBefore != null) {
            _revisedStartDateBefore = (Calendar) revisedStartDateBefore.clone();
        }
        if (revisedStartDateAfter != null) {
            _revisedStartDateAfter = (Calendar) revisedStartDateAfter.clone();
        }

        _endDateBefore = (Calendar) endDateBefore.clone();
        _endDateAfter = (Calendar) endDateAfter.clone();
        if (revisedEndDateBefore != null) {
            _revisedEndDateBefore = (Calendar) revisedEndDateBefore.clone();
        }
        if (revisedEndDateAfter != null) {
            _revisedEndDateAfter = (Calendar) revisedEndDateAfter.clone();
        }

        _indexBefore = indexBefore;
        _indexAfter = indexAfter;
        
        if (sectionBefore != null) {
            _sectionBefore = _event.getParentComposite().getGanttSections().indexOf(sectionBefore);
        }
        else {
            _sectionBefore = -1;
        }
        if (sectionAfter != null) {
            _sectionAfter = _event.getParentComposite().getGanttSections().indexOf(sectionAfter);
        }
        else {
            _sectionAfter = -1;
        }
    }

    public void dispose() {
    }

    public void redo() {
        _event.setStartDate(_startDateAfter);
        _event.setEndDate(_endDateAfter);

        // we know we're setting valid dates, so force validation to off or we'll get funky results
        _event.setRevisedStart(_revisedStartDateAfter, false);
        _event.setRevisedEnd(_revisedEndDateAfter, false);

        if (_sectionAfter != -1 && _indexAfter > -1) {
            _event.reparentToNewGanttSection(_indexAfter, (GanttSection)_event.getParentComposite().getGanttSections().get(_sectionAfter));
        }
    }

    public void undo() {
        _event.setStartDate(_startDateBefore);
        _event.setEndDate(_endDateBefore);

        // we know we're setting valid dates, so force validation to off or we'll get funky results
        _event.setRevisedStart(_revisedStartDateBefore, false);
        _event.setRevisedEnd(_revisedEndDateBefore, false);
                
        if (_sectionBefore != -1 && _indexBefore > -1) {            
            _event.reparentToNewGanttSection(_indexBefore, (GanttSection)_event.getParentComposite().getGanttSections().get(_sectionBefore));
        }
    }

    public GanttEvent getEvent() {
        return _event;
    }

    public void setEvent(GanttEvent event) {
        _event = event;
    }

    public Calendar getStartDateBefore() {
        return _startDateBefore;
    }

    public void setStartDateBefore(Calendar startDateBefore) {
        _startDateBefore = startDateBefore;
    }

    public Calendar getStartDateAfter() {
        return _startDateAfter;
    }

    public void setStartDateAfter(Calendar startDateAfter) {
        _startDateAfter = startDateAfter;
    }

    public Calendar getEndDateBefore() {
        return _endDateBefore;
    }

    public void setEndDateBefore(Calendar endDateBefore) {
        _endDateBefore = endDateBefore;
    }

    public Calendar getEndDateAfter() {
        return _endDateAfter;
    }

    public void setEndDateAfter(Calendar endDateAfter) {
        _endDateAfter = endDateAfter;
    }

    public int getIndexBefore() {
        return _indexBefore;
    }

    public void setIndexBefore(int indexBefore) {
        _indexBefore = indexBefore;
    }

    public int getIndexAfter() {
        return _indexAfter;
    }

    public void setIndexAfter(int indexAfter) {
        _indexAfter = indexAfter;
    }

    public int getSectionBefore() {
        return _sectionBefore;
    }

    public void setSectionBefore(int sectionBefore) {
        _sectionBefore = sectionBefore;
    }

    public int getSectionAfter() {
        return _sectionAfter;
    }

    public void setSectionAfter(int sectionAfter) {
        _sectionAfter = sectionAfter;
    }

    public Calendar getRevisedStartDateBefore() {
        return _revisedStartDateBefore;
    }

    public void setRevisedStartDateBefore(Calendar revisedStartDateBefore) {
        _revisedStartDateBefore = revisedStartDateBefore;
    }

    public Calendar getRevisedStartDateAfter() {
        return _revisedStartDateAfter;
    }

    public void setRevisedStartDateAfter(Calendar revisedStartDateAfter) {
        _revisedStartDateAfter = revisedStartDateAfter;
    }

    public Calendar getRevisedEndDateBefore() {
        return _revisedEndDateBefore;
    }

    public void setRevisedEndDateBefore(Calendar revisedEndDateBefore) {
        _revisedEndDateBefore = revisedEndDateBefore;
    }

    public Calendar getRevisedEndDateAfter() {
        return _revisedEndDateAfter;
    }

    public void setRevisedEndDateAfter(Calendar revisedEndDateAfter) {
        _revisedEndDateAfter = revisedEndDateAfter;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("[EventMove: ");
        buf.append(_event);
        buf.append(" was moved from section ");
        buf.append(_sectionBefore);
        buf.append(" to ");
        buf.append(_sectionAfter);
        buf.append('\n');

        buf.append("\tEsti Start: ");
        buf.append(quickFormat(_startDateBefore));
        buf.append(" -> ");
        buf.append(quickFormat(_startDateAfter));
        buf.append('\n');

        buf.append("\tEsti End: ");
        buf.append(quickFormat(_endDateBefore));
        buf.append(" -> ");
        buf.append(quickFormat(_endDateAfter));
        buf.append('\n');

        buf.append("\tRe Start: ");
        buf.append(quickFormat(_revisedStartDateBefore));
        buf.append(" -> ");
        buf.append(quickFormat(_revisedStartDateAfter));
        buf.append('\n');

        buf.append("\tRe End: ");
        buf.append(quickFormat(_revisedEndDateBefore));
        buf.append(" -> ");
        buf.append(quickFormat(_revisedEndDateAfter));
        buf.append('\n');

        buf.append(']');
        return buf.toString();
    }

    private String quickFormat(Calendar cal) {
        if (cal == null) return "<null>";

        return cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DATE);
    }

}
