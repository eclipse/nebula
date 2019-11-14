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

import org.eclipse.nebula.widgets.ganttchart.GanttSection;
import org.eclipse.nebula.widgets.ganttchart.undoredo.commands.IUndoRedoCommand;

/**
 * Represents one GanttSection delete action that can be undone/redone.
 * <p>
 * Note that this command is not added internally. This is because there is
 * no code that automatically deletes sections from your GanttComposite.
 * You need to create and record this command together with the code that
 * removes the section from your composite.
 */
public class SectionDeleteCommand implements IUndoRedoCommand {

    private GanttSection 	_section;
    private int        		_index;

    /**
     * Creates a new undoable/redoable delete Event.
     * 
     * @param section {@link GanttSection} being deleted
     * @param index Index of {@link GanttSection} prior to delete
     */
    public SectionDeleteCommand(final GanttSection section, final int index) {
        _section = section;
        _index = index;
    }

	public void undo() {
    	_section.getParentComposite().addSection(_section, _index);
	}

	public void redo() {
		_section.getParentComposite().removeSection(_section);
	}

	public void dispose() {
	}

    public GanttSection getSection() {
        return _section;
    }

    public void setSection(final GanttSection section) {
        _section = section;
    }

    public int getIndex() {
        return _index;
    }

    public void setIndex(final int index) {
        _index = index;
    }

    @SuppressWarnings("nls")
	public String toString() {
        final StringBuffer buf = new StringBuffer(200);
        buf.append("[SectionDelete: ");
        buf.append(_section);
        buf.append(" was deleted at index ");
        buf.append(_index);
        buf.append(']');
        return buf.toString();
    }

}
