package org.eclipse.nebula.jface.gridviewer;

import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.jface.viewers.CellEditor.LayoutData;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridEditor;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;

public class GridViewerEditor extends ColumnViewerEditor {
	/** Editor support for tables. */
    private GridEditor gridEditor;
    
	public GridViewerEditor(GridViewer viewer,
			ColumnViewerEditorActivationStrategy editorActivationStrategy,
			int feature) {
		super(viewer, editorActivationStrategy, feature);
		this.gridEditor = new GridEditor(viewer.getGrid());
	}

	protected StructuredSelection createSelection(Object element) 
    {
        return new StructuredSelection(element);
    }

    protected void setEditor(Control w, Item item, int fColumnNumber) 
    {
        gridEditor.setEditor(w, (GridItem) item, fColumnNumber);
    }

    protected void setLayoutData(LayoutData layoutData) 
    {
        gridEditor.grabHorizontal = layoutData.grabHorizontal;
        gridEditor.horizontalAlignment = layoutData.horizontalAlignment;
        gridEditor.minimumWidth = layoutData.minimumWidth;
    }

	public ViewerCell getFocusCell() {
		GridViewer viewer = (GridViewer) getViewer();
		Grid grid = viewer.getGrid();
		
		if( grid.getCellSelectionEnabled() ) {
			Point p = grid.getFocusCell();
			
			if( p.x >= 0 && p.y >= 0 ) {
				GridItem item = grid.getItem(p.y);
				if( item != null ) {
					ViewerRow row = viewer.getViewerRowFromItem(item);
					return row.getCell(p.x);
				}
			}
		}
		
		return null;
	}

	protected void updateFocusCell(ViewerCell focusCell, ColumnViewerEditorActivationEvent event) {
		// nothing to be done
	}
}
