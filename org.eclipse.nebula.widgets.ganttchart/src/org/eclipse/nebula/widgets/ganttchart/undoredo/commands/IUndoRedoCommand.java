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

public interface IUndoRedoCommand {

    /**
     * Undoes an event in the chart. This should put the event back to the state it was <b>prior</b> to the event taking place. 
     */
    void undo();
    
    /**
     * Redoes an event in the chart. This should put the event back to the state it was <b>after</b> the event took place.
     */
    void redo();
    
    /**
     * Called when the event is about to be destroyed. If any resources need to be cleaned up you should do so here.
     */
    void dispose();
    
}
