package org.eclipse.nebula.widgets.ganttchart.undoredo;

import org.eclipse.nebula.widgets.ganttchart.undoredo.commands.IUndoRedoCommand;

public class UndoRedoListenerAdapter implements IUndoRedoListener {

    public void canRedoChanged(boolean canRedo) {
    }

    public void canUndoChanged(boolean canUndo) {
    }

    public void commandRedone(IUndoRedoCommand command) {
    }

    public void commandUndone(IUndoRedoCommand command) {
    }

    public void undoableCommandAdded(IUndoRedoCommand command) {
    }

}
