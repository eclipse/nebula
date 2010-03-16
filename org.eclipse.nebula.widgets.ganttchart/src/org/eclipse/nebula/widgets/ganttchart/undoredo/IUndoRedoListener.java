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

package org.eclipse.nebula.widgets.ganttchart.undoredo;

import org.eclipse.nebula.widgets.ganttchart.GanttChart;
import org.eclipse.nebula.widgets.ganttchart.undoredo.commands.IUndoRedoCommand;

public interface IUndoRedoListener {

    /**
     * Notified when the undo/redo state has changed.
     * 
     * @param canRedo Whether it is possible to redo or not
     */
    void canRedoChanged(boolean canRedo);

    /**
     * Notified when the undo/redo state has changed.
     * 
     * @param canRedo Whether it is possible to undo or not
     */
    void canUndoChanged(boolean canUndo);
    
    /**
     * When a command is added to the stack that can be undone this is called. DO NOT call undo() or redo() directly
     * on this command, always go via the {@link GanttUndoRedoManager}.
     * 
     * @param command Command added to stack
     * @see GanttChart#getUndoRedoManager()
     */
    void undoableCommandAdded(IUndoRedoCommand command);
    
    /**
     * Notified when an undo has taken place.
     *  
     * @param command Command that was undone
     */
    void commandUndone(IUndoRedoCommand command);
    
    /**
     * Notified when a redo has taken place.
     * 
     * @param command Command that was redone
     */
    void commandRedone(IUndoRedoCommand command);
}
