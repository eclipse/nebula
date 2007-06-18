/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Michael Houston<chmeeky@h8spam.com> - initial API and implementation
 *    Tom Schindl <tom.schindl@bestsolution.at> - bug fix in: 191216
 *******************************************************************************/ 
package org.eclipse.nebula.jface.gridviewer;

import java.util.List;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Widget;

public class GridTreeViewer extends AbstractTreeViewer {
	private Grid grid;
	private GridViewerRow cachedRow;

	public GridTreeViewer(Composite parent) {
		this(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
	}

	public GridTreeViewer(Composite parent, int style) {
		this(new Grid(parent, style));
	}

	public GridTreeViewer(Grid grid) {
		this.grid = grid;
		hookControl(grid);
	}

	public Grid getGrid() {
		return grid;
	}

	protected Item getItemAt(Point point) {
		return grid.getItem(point);
	}

	protected ColumnViewerEditor createViewerEditor() {
		return new GridViewerEditor(this,
				new ColumnViewerEditorActivationStrategy(this),
				ColumnViewerEditor.DEFAULT);
	}

	protected void addTreeListener(Control control, TreeListener listener) {
		((Grid) control).addTreeListener(listener);
	}

	protected Item[] getChildren(Widget o) {
		if (o instanceof GridItem) {
			return ((GridItem) o).getItems();
		}
		if (o instanceof Grid) {
			return ((Grid) o).getItems();
		}
		return null;
	}

	protected boolean getExpanded(Item item) {
		return ((GridItem) item).isExpanded();
	}

	protected int getItemCount(Control control) {
		return ((Grid) control).getItemCount();
	}

	protected int getItemCount(Item item) {
		return ((GridItem) item).getItemCount();
	}

	protected Item[] getItems(Item item) {
		return ((GridItem) item).getItems();
	}

	protected Item getParentItem(Item item) {
		return ((GridItem) item).getParentItem();
	}

	protected Item[] getSelection(Control control) {
		return ((Grid) control).getSelection();
	}

	protected Item newItem(Widget parent, int style, int index) {
		GridItem item;

		if (parent instanceof GridItem) {
			item = (GridItem) createNewRowPart(getViewerRowFromItem(parent),
					style, index).getItem();
		} else {
			item = (GridItem) createNewRowPart(null, style, index).getItem();
		}

		return item;
	}

	/**
	 * Create a new ViewerRow at rowIndex
	 * 
	 * @param parent
	 * @param style
	 * @param rowIndex
	 * @return ViewerRow
	 */
	private ViewerRow createNewRowPart(ViewerRow parent, int style, int rowIndex) {
		if (parent == null) {
			if (rowIndex >= 0) {
				return getViewerRowFromItem(new GridItem(grid, style, rowIndex));
			}
			return getViewerRowFromItem(new GridItem(grid, style));
		}

		if (rowIndex >= 0) {
			return getViewerRowFromItem(new GridItem((GridItem) parent
					.getItem(), SWT.NONE, rowIndex));
		}

		return getViewerRowFromItem(new GridItem((GridItem) parent.getItem(),
				SWT.NONE));
	}

	protected void removeAll(Control control) {
		((Grid) control).removeAll();
	}

	protected void setExpanded(Item item, boolean expand) {
		((GridItem) item).setExpanded(expand);
	}

	protected void setSelection(List items) {
		Item[] current = getSelection(getGrid());

		// Don't bother resetting the same selection
		if (isSameSelection(items, current)) {
			return;
		}

		GridItem[] newItems = new GridItem[items.size()];
		items.toArray(newItems);
		getGrid().setSelection(newItems);
	}

	protected void showItem(Item item) {
		getGrid().showItem((GridItem) item);

	}

	public Control getControl() {
		return getGrid();
	}

	protected ViewerRow getViewerRowFromItem(Widget item) {
		if (cachedRow == null) {
			cachedRow = new GridViewerRow((GridItem) item);
		} else {
			cachedRow.setItem((GridItem) item);
		}

		return cachedRow;
	}

	protected Widget getColumnViewerOwner(int columnIndex) {
		if (columnIndex < 0
				|| (columnIndex > 0 && columnIndex >= getGrid()
						.getColumnCount())) {
			return null;
		}

		if (getGrid().getColumnCount() == 0)// Hang it off the table if it
			return getGrid();

		return getGrid().getColumn(columnIndex);
	}

	protected int doGetColumnCount() {
		return grid.getColumnCount();
	}
}
