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

package org.eclipse.nebula.widgets.ganttchart.undoredo;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.ganttchart.GanttComposite;
import org.eclipse.nebula.widgets.ganttchart.undoredo.commands.IUndoRedoCommand;

/**
 * Deals with Undo/Redo events in the chart. Implemented per Command-structure standards.
 * 
 * @author cre
 */
public class GanttUndoRedoManager {

    public static final int      STACK_SIZE = 50;

    private final List           _undoRedoEvents;
    private int                  _currentIndex;
    private int                  _maxStackSize;
    private final GanttComposite _comp;
    private final List           _listeners;

    public GanttUndoRedoManager(final GanttComposite parent, final int maxStackSize) {
        _comp = parent;

        _undoRedoEvents = new ArrayList();
        _listeners = new ArrayList();
        _maxStackSize = maxStackSize;
    }

    public List getUndoRedoEvents() {
        return _undoRedoEvents;
    }

    /**
     * Adds a listener to be notified when undo/redo possibilities change
     * 
     * @param listener
     */
    public void addUndoRedoListener(final IUndoRedoListener listener) {
        if (!_listeners.contains(listener)) {
            _listeners.add(listener);
        }
    }

    /**
     * Removes a listener from being notified when undo/redo possibilities change
     * 
     * @param listener
     */
    public void removeUndoRedoListener(final IUndoRedoListener listener) {
        _listeners.remove(listener);
    }

    /**
     * Records an undoable/redoable command
     * 
     * @param command
     */
    public void record(final IUndoRedoCommand command) {
        // ensure size etc
        fixStack();

        _undoRedoEvents.add(command);

        _currentIndex++;

        // tell listeners a command was added
        for (int i = 0; i < _listeners.size(); i++) {
            final IUndoRedoListener listener = (IUndoRedoListener) _listeners.get(i);
            listener.undoableCommandAdded(command);
        }

        updateListeners();
    }

    private void updateListeners() {
        // notify listeners
        for (int i = 0; i < _listeners.size(); i++) {
            final IUndoRedoListener listener = (IUndoRedoListener) _listeners.get(i);
            listener.canRedoChanged(canRedo());
            listener.canUndoChanged(canUndo());
        }
    }

    /**
     * Removes all undo/redo events from the stack
     */
    public void clear() {
        _undoRedoEvents.clear();
        _currentIndex = 0;

        updateListeners();
    }

    /**
     * Whether an Undo is possible.
     * 
     * @return true if user can Undo
     */
    public boolean canUndo() {
        return _currentIndex != 0 && !_undoRedoEvents.isEmpty();
    }

    /**
     * Undoes the last GanttChart action.
     * 
     * @return
     */
    public boolean undo() {
        if (!canUndo()) { return false; }

        final IUndoRedoCommand command = (IUndoRedoCommand) _undoRedoEvents.get(_currentIndex - 1);
        command.undo();

        _comp.heavyRedraw();

        _currentIndex--;
        if (_currentIndex < 0) {
            _currentIndex = 0;
        }
        updateListeners();
        for (int i = 0; i < _listeners.size(); i++) {
            final IUndoRedoListener listener = (IUndoRedoListener) _listeners.get(i);
            listener.commandUndone(command);
        }
        return true;
    }

    /**
     * Redoes the last GanttChart action.
     * 
     * @return
     */
    public boolean redo() {
        if (!canRedo()) { return false; }

        final IUndoRedoCommand command = (IUndoRedoCommand) _undoRedoEvents.get(_currentIndex);
        command.redo();

        _comp.heavyRedraw();

        _currentIndex++;
        if (_currentIndex > _undoRedoEvents.size()) {
            _currentIndex = _undoRedoEvents.size();
        }
        updateListeners();
        for (int i = 0; i < _listeners.size(); i++) {
            final IUndoRedoListener listener = (IUndoRedoListener) _listeners.get(i);
            listener.commandRedone(command);
        }
        return true;
    }

    /**
     * Whether a Redo is possible.
     * 
     * @return true if user can Redo
     */
    public boolean canRedo() {
        if (_undoRedoEvents.isEmpty()) { return false; }

        return _currentIndex != _undoRedoEvents.size();
    }

    /**
     * The current index of where the undo/redo marker is
     * 
     * @return
     */
    public int getCurrentIndex() {
        return _currentIndex;
    }

    /**
     * @param currentIndex The current index of where the undo/redo marker is
     */
    public void setCurrentIndex(int currentIndex) {
        _currentIndex = currentIndex;
    }

    /**
     * Clears up the stack of undo/redo events and keeps its size in check.
     */
    private void fixStack() {
        final List toRemove = new ArrayList();

        // first nuke any items past the current index
        for (int i = _currentIndex; i < _undoRedoEvents.size(); i++) {
            toRemove.add(_undoRedoEvents.get(i));
        }
        _undoRedoEvents.removeAll(toRemove);
        for (int i = 0; i < toRemove.size(); i++) {
            ((IUndoRedoCommand) toRemove.get(i)).dispose();
        }

        toRemove.clear();

        if (_undoRedoEvents.size() > _maxStackSize) {
            // remove from the front
            for (int i = 0; i < (_maxStackSize - _undoRedoEvents.size()); i++) {
                toRemove.add(_undoRedoEvents.get(i));
            }
        }
        for (int i = 0; i < toRemove.size(); i++) {
            ((IUndoRedoCommand) toRemove.get(i)).dispose();
        }

        _undoRedoEvents.removeAll(toRemove);
    }

    /**
     * Sets a new max undo/redo sack size, value must be a positive integer or it is ignored.
     * 
     * @param stackSize new max undo/redo stack size
     */
    public void setMaxStackSize(final int stackSize) {
        if (stackSize <= 0) { return; }

        _maxStackSize = stackSize;
    }
}
