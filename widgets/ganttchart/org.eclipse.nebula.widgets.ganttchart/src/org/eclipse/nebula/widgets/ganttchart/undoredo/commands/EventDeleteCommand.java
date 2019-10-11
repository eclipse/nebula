/*******************************************************************************
 * Copyright (c) 2013 Dirk Fauth and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Dirk Fauth <dirk.fauth@gmail.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.ganttchart.undoredo.commands;

import org.eclipse.nebula.widgets.ganttchart.GanttEvent;
import org.eclipse.nebula.widgets.ganttchart.GanttSection;
import org.eclipse.nebula.widgets.ganttchart.IGanttEventListener;
import org.eclipse.nebula.widgets.ganttchart.undoredo.commands.IUndoRedoCommand;

/**
 * Represents one GanttEvent delete action that can be undone/redone.
 * <p>
 * Note that this command is not added internally. But you are able to 
 * create and add this type of command within your custom
 * {@link IGanttEventListener#eventsDeleteRequest(java.util.List, org.eclipse.swt.events.MouseEvent)} 
 */
public class EventDeleteCommand implements IUndoRedoCommand {

    private GanttEvent 		_event;
    private int        		_index;
    private GanttSection    _section;

    /**
     * Creates a new undoable/redoable delete Event.
     * 
     * @param event {@link GanttEvent} being deleted
     * @param section {@link GanttSection} index (of all GanttSections) prior to delete
     * @param index Index of event in {@link GanttSection} section prior to delete
     */
    public EventDeleteCommand(final GanttEvent event, final GanttSection section, final int index) {
        _event = event;
        _index = index;
        _section = section;
    }

	public void undo() {
    	_event.getParentComposite().addEvent(_event, _index);

    	if (_section != null) {
    		_section.addGanttEvent(_index, _event);
    	}
	}

	public void redo() {
		_event.getParentComposite().removeEvent(_event);

		if (_section != null) {
    		_section.removeGanttEvent(_event);
    	}
	}

	public void dispose() {
	}

    public GanttEvent getEvent() {
        return _event;
    }

    public void setEvent(final GanttEvent event) {
        _event = event;
    }

    public int getIndex() {
        return _index;
    }

    public void setIndex(final int index) {
        _index = index;
    }

    public GanttSection getSection() {
        return _section;
    }

    public void setSection(final GanttSection section) {
        _section = section;
    }
    
    @SuppressWarnings("nls")
	public String toString() {
        final StringBuffer buf = new StringBuffer(200);
        buf.append("[EventDelete: ");
        buf.append(_event);
        buf.append(" was deleted from section ");
        buf.append(_section);
        buf.append(" at index ");
        buf.append(_index);
        buf.append(']');
        return buf.toString();
    }

}
