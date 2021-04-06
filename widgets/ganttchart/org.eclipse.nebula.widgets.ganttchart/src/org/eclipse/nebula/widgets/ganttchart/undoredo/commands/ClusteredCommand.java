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

package org.eclipse.nebula.widgets.ganttchart.undoredo.commands;

import java.util.ArrayList;
import java.util.Collections;
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
    
    /**
     * Simple getter for the list of commands that are transported by this
     * ClusteredCommand.
     * <p>
     * Note that this will only return the list of commands as is. There is
     * no transformation made to the list.
     * 
     * @return The list of commands that are transported by this ClusteredCommand.
     * 
     * @see ClusteredCommand#getFlattenedCommands()
     */
    public List getCommandList() {
    	return _commands;
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

    /**
	 * Return the individual commands that are clustered in this command.
	 * If this ClusteredCommand also contains other ClusteredCommands,
	 * they will get unpacked so there will be one flat list of 
	 * IUndoRedoCommands.
	 * 
	 * @return A unmodifiable list of participating {@link EventMoveCommand}s.
	 * 
	 * @deprecated Because the name of this method is not unique and might me interpreted wrong.
	 * 
	 * @see ClusteredCommand#getCommandList()
	 * @see ClusteredCommand#getFlattenedCommands()
	 */
    public List getCommands() {
    	return this.getFlattenedCommands();
    }
    
    /**
	 * Return the individual commands that are clustered in this command.
	 * If this ClusteredCommand also contains other ClusteredCommands,
	 * they will get unpacked so there will be one flat list of 
	 * IUndoRedoCommands.
	 * 
	 * @return A unmodifiable list of participating {@link EventMoveCommand}s.
	 * 
	 * @see ClusteredCommand#getCommandList()
	 */
	public List getFlattenedCommands() {
		ArrayList result = new ArrayList();
		for (Object command : _commands) {
			if (command instanceof EventMoveCommand)
				result.add((EventMoveCommand) command);
			else if (command instanceof ClusteredCommand)
				result.addAll(((ClusteredCommand) command).getFlattenedCommands());
		}
		return Collections.unmodifiableList(result); 
	}

	/**
	 * Return the individual events that are clustered in this command.
	 * 
	 * @return An unmodifiable list of participating {@link GanttEvent}s.
	 */
	public List getEvents() {
		ArrayList result = new ArrayList();
		for (Object command : getFlattenedCommands()) {
			if (command instanceof EventMoveCommand)
				result.add(((EventMoveCommand) command).getEvent());
			else if (command instanceof EventDeleteCommand)
				result.add(((EventDeleteCommand) command).getEvent());
		}
		return Collections.unmodifiableList(result);
	}
}
