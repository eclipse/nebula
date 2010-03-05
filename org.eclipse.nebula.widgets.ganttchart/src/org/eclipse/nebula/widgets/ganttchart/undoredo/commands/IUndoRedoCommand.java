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
     * Undoes the event. This should put the event back to the state it was prior to the event taking place. 
     */
    public void undo();
    
    /**
     * Redoes the event. This should put the event back to the state it was after the event took plaace.
     */
    public void redo();
    
    /**
     * Called when the event will be destroyed, if any resources need to be cleaned up do it here.
     */
    public void dispose();
    
}
