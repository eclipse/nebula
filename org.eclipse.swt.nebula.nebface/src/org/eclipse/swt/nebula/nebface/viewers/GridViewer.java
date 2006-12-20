/*******************************************************************************
 * Copyright (c) 2006 Tom Schindl and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tom Schindl <tom.schindl@bestsolution.at> - initial API and implementation
 *     IBM Corporation
 *******************************************************************************/
package org.eclipse.swt.nebula.nebface.viewers;

import org.eclipse.jface.viewers.AbstractViewerEditor;
import org.eclipse.jface.viewers.BaseTableViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.jface.viewers.CellEditor.LayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.nebula.widgets.grid.Grid;
import org.eclipse.swt.nebula.widgets.grid.GridEditor;
import org.eclipse.swt.nebula.widgets.grid.GridItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Widget;

public class GridViewer extends BaseTableViewer {
	private static final int DEFAULT_STYLE = SWT.MULTI | SWT.H_SCROLL
			| SWT.V_SCROLL | SWT.BORDER;

	private Grid grid;

	private GridEditor gridEditor;

	public GridViewer(Composite parent) {
		this(parent, DEFAULT_STYLE);
	}

	public GridViewer(Composite parent, int style) {
		this(new Grid(parent, style));
	}

	public GridViewer(Grid gridControl) {
		super();
        grid = gridControl;
		gridEditor = new GridEditor(gridControl);
        hookControl(grid);
	}

	public Grid getGrid() {
		return grid;
	}

	protected ViewerRow createNewRowPart(int style, int rowIndex) {
		GridItem item;

		if (rowIndex >= 0) {
			item = new GridItem(grid, style, rowIndex);
		} else {
			item = new GridItem(grid, style);
		}

		return getViewerRowFromItem(item);
	}

    protected ViewerRow getViewerRowFromItem(Widget item) {
        ViewerRow part = (ViewerRow) item.getData(ViewerRow.ROWPART_KEY);

        if (part == null) {
            part = new GridViewerRow(((GridItem) item));
        }

        return part;
    }

	protected AbstractViewerEditor createViewerEditor() {
        return new AbstractViewerEditor(this) {

            protected StructuredSelection createSelection(Object element) {
                return new StructuredSelection(element);
            }

            protected Item[] getSelection() {
                return getGrid().getSelection();
            }

            protected void setEditor(Control w, Item item, int fColumnNumber) {
                gridEditor.setEditor(w, (GridItem) item, fColumnNumber);
            }

            protected void setLayoutData(LayoutData layoutData) {
                gridEditor.grabHorizontal = layoutData.grabHorizontal;
                gridEditor.horizontalAlignment = layoutData.horizontalAlignment;
                gridEditor.minimumWidth = layoutData.minimumWidth;
            }

            protected void showSelection() {
                getGrid().showSelection();
            }

        };
	}

	protected void internalClear(int index) {
		// TODO NEEDS IMP
	}

	protected void internalClearAll() {
		// TODO NEEDS IMP
	}

	protected void internalDeselectAll() {
		grid.deselectAll();
	}

	protected Widget internalGetColumn(int index) {
		return grid.getColumn(index);
	}

	protected int internalGetColumnCount() {
		return grid.getColumnCount();
	}

	protected Widget[] internalGetColumns() {
		return grid.getColumns();
	}

	protected Item internalGetItem(int index) {
		return grid.getItem(index);
	}

	protected Item internalGetItem(Point point) {
		return grid.getItem(point);
	}

	protected int internalGetItemCount() {
		return grid.getItemCount();
	}

	protected Item[] internalGetItems() {
		return grid.getItems();
	}

	protected Widget[] internalGetSelection() {
		return grid.getSelection();
	}

	protected int[] internalGetSelectionIndices() {
		return grid.getSelectionIndices();
	}

	protected int internalIndexOf(Item item) {
		return grid.indexOf((GridItem) item);
	}

	protected void internalRemove(int start, int end) {
		grid.remove(start, end);
	}

	protected void internalRemove(int[] indices) {
		grid.remove(indices);
	}

	protected void internalRemoveAll() {
		grid.removeAll();
	}

	protected void internalSetItemCount(int count) {
		// TODO NEEDS IMP
	}

	protected void internalSetSelection(Item[] items) {
		if (items != null) {
			grid.setSelection(new GridItem[0]);
		} else {
			GridItem[] tmp = new GridItem[items.length];
			System.arraycopy(items, 0, tmp, 0, items.length);
			grid.setSelection(tmp);
		}

	}

	protected void internalSetSelection(int[] indices) {
		grid.setSelection(indices);
	}

	protected void internalShowItem(Item item) {
		grid.showItem((GridItem) item);
	}

	protected void internalShowSelection() {
		grid.showSelection();
	}

	protected void internalSetControl(Control table) {
		grid = (Grid) table;
	}

	public Control getControl() {
		return grid;
	}

    protected Item getItemAt(Point point)
    {
        return grid.getItem(point);
    }

}
