package org.eclipse.nebula.widgets.compositetable;

public abstract class ColumnControlListener {
    public abstract void columnResized(int resizedColumnNumber,
            int resizedColumnWidth, int columnToTheRightOfResizedColumnWidth);

    public abstract void columnMoved(int[] newColumnOrder);
}
