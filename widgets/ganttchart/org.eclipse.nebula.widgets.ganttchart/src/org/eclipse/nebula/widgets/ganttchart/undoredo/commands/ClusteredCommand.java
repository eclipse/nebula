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

import java.util.ArrayList;
import java.util.List;

/**
 * One command to handle many sub-commands, such as a multi-drag/drop etc. All commands inside a clustered command will be Undone/Redone at the same time.
 * 
 * @author cre
 *
 */
public class ClusteredCommand extends AbstractUndoRedoCommand {

    private final List _commands;
    
    /**
     * Creates a new Clustered Command.
     */
    public ClusteredCommand() {
        super();
        _commands = new ArrayList();
    }
    
    /**
     * Creates a new Clustered Command witha list of pre-set commands.
     */
    public ClusteredCommand(final List commands) {
        super();
        _commands = commands;
    }
    
    /**
     * Adds a new command to the cluster.
     * 
     * @param command Command to add
     */
    public void addCommand(final IUndoRedoCommand command) {
    	if (command == null) {
    		return;
    	}
    	
        _commands.add(command);
    }
    
    /**
     * Removes a command from the cluster.
     * 
     * @param command Command to remove
     */
    public void removeCommand(final IUndoRedoCommand command) {
        _commands.remove(command);
    }
    
    /**
     * Returns the number of commands that are inside the cluster.
     * 
     * @return Number of commands
     */
    public int size() {
        return _commands.size();
    }
    
    public void dispose() {
        for (int i = 0; i < _commands.size(); i++) {
            ((IUndoRedoCommand)_commands.get(i)).dispose();
        }
    }

    public void redo() {
        for (int i = 0; i < _commands.size(); i++) {
            ((IUndoRedoCommand)_commands.get(i)).redo();
        }
    }

    public void undo() {
        for (int i = 0; i < _commands.size(); i++) {
            ((IUndoRedoCommand)_commands.get(i)).undo();
        }
    }

}
