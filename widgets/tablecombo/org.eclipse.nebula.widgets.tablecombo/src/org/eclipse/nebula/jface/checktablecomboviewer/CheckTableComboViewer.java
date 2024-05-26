/****************************************************************************
 * Copyright (c) 2000 - 2024 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *  Olivier Titillon  - Initial PR
 *  Laurent CARON <laurent dot caron at gmail dot ccom> - Port to Nebula
 *****************************************************************************/

package org.eclipse.nebula.jface.checktablecomboviewer;

import org.eclipse.jface.viewers.AbstractTableViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.nebula.widgets.checktablecombo.CheckTableCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

/**
 * CheckTableComboViewer is basically identical to the TableViewer class with a
 * few modifications to reference the Table within the TableCombo widget
 * instead of a parent Table widget.
 */
public class CheckTableComboViewer extends AbstractTableViewer {

	private final CheckTableCombo tableCombo;

	/**
	 * The cached row which is reused all over
	 */
	private CheckTableComboViewerRow cachedRow;

	/**
	 * Creates a check table viewer on a newly-created table control under the given
	 * parent. The table control is created using the given style bits. The viewer
	 * has no input, no content provider, a default label provider, no sorter, and
	 * no filters. The table has no columns.
	 *
	 * @param parent
	 *            the parent control
	 */
	public CheckTableComboViewer(final Composite parent) {
		this(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
	}

	/**
	 * Creates a table viewer on a newly-created table control under the given
	 * parent. The table control is created using the given style bits. The
	 * viewer has no input, no content provider, a default label provider, no
	 * sorter, and no filters. The table has no columns.
	 *
	 * @param parent
	 *            the parent control
	 * @param style
	 *            SWT style bits
	 */
	public CheckTableComboViewer(final Composite parent, final int style) {
		this(new CheckTableCombo(parent, style));
	}

	/**
	 * Creates a table viewer on the given table control. The viewer has no
	 * input, no content provider, a default label provider, no sorter, and no
	 * filters.
	 *
	 * @param table
	 *            the table control
	 */
	public CheckTableComboViewer(final CheckTableCombo tableCombo) {
		this.tableCombo = tableCombo;
		hookControl(tableCombo);
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTableViewer#doClear(int)
	 */
	@Override
	protected void doClear(final int index) {
		tableCombo.getTable().clear(index);
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTableViewer#doClearAll()
	 */
	@Override
	protected void doClearAll() {
		tableCombo.getTable().clearAll();
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTableViewer#doDeselectAll()
	 */
	@Override
	protected void doDeselectAll() {
		tableCombo.getTable().deselectAll();
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTableViewer#doGetColumn(int)
	 */
	@Override
	protected Widget doGetColumn(final int index) {
		return tableCombo.getTable().getColumn(index);
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTableViewer#doGetItem(int)
	 */
	@Override
	protected Item doGetItem(final int index) {
		return tableCombo.getTable().getItem(index);
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTableViewer#doGetItemCount()
	 */
	@Override
	protected int doGetItemCount() {
		return tableCombo.getTable().getItemCount();
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTableViewer#doGetItems()
	 */
	@Override
	protected Item[] doGetItems() {
		return tableCombo.getTable().getItems();
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTableViewer#doGetSelection()
	 */
	@Override
	protected Item[] doGetSelection() {
		return tableCombo.getTable().getSelection();
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTableViewer#doGetSelectionIndices()
	 */
	@Override
	protected int[] doGetSelectionIndices() {
		return tableCombo.getTable().getSelectionIndices();
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTableViewer#doIndexOf(org.eclipse.swt.widgets.Item)
	 */
	@Override
	protected int doIndexOf(final Item item) {
		return tableCombo.getTable().indexOf((TableItem) item);
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTableViewer#doRemove(int[])
	 */
	@Override
	protected void doRemove(final int[] indices) {
		tableCombo.getTable().remove(indices);
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTableViewer#doRemove(int, int)
	 */
	@Override
	protected void doRemove(final int start, final int end) {
		tableCombo.getTable().remove(start, end);
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTableViewer#doRemoveAll()
	 */
	@Override
	protected void doRemoveAll() {
		tableCombo.getTable().removeAll();
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTableViewer#doResetItem(org.eclipse.swt.widgets.Item)
	 */
	@Override
	protected void doResetItem(final Item item) {
		final TableItem tableItem = (TableItem) item;
		final int columnCount = Math.max(1, tableCombo.getTable().getColumnCount());
		for (int i = 0; i < columnCount; i++) {
			tableItem.setText(i, ""); //$NON-NLS-1$
			if (tableItem.getImage(i) != null) {
				tableItem.setImage(i, null);
			}
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTableViewer#doSelect(int[])
	 */
	@Override
	protected void doSelect(final int[] indices) {
		tableCombo.select(indices != null && indices.length > 0 ? indices : new int[] {-1});
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTableViewer#doSetItemCount(int)
	 */
	@Override
	protected void doSetItemCount(final int count) {
		tableCombo.getTable().setItemCount(count);
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTableViewer#doSetSelection(org.eclipse.swt.widgets.Item[])
	 */
	@Override
	protected void doSetSelection(final Item[] items) {
		if (items != null && items.length > 0) {
			tableCombo.select((TableItem[]) items);
		} else {
			tableCombo.select(new int[] {-1});
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTableViewer#doSetSelection(int[])
	 */
	@Override
	protected void doSetSelection(final int[] indices) {
		tableCombo.select(indices != null && indices.length > 0 ? indices : new int[] {-1});
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTableViewer#doShowItem(org.eclipse.swt.widgets.Item)
	 */
	@Override
	protected void doShowItem(final Item item) {
		tableCombo.getTable().showItem((TableItem) item);
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTableViewer#doShowSelection()
	 */
	@Override
	protected void doShowSelection() {
		tableCombo.getTable().showSelection();
	}

	/**
	 * @see org.eclipse.jface.viewers.AbstractTableViewer#internalCreateNewRowPart(int,
	 *      int)
	 */
	@Override
	protected ViewerRow internalCreateNewRowPart(final int style, final int rowIndex) {
		TableItem item;

		if (rowIndex >= 0) {
			item = new TableItem(tableCombo.getTable(), style, rowIndex);
		} else {
			item = new TableItem(tableCombo.getTable(), style);
		}

		return getViewerRowFromItem(item);
	}

	/**
	 * @see org.eclipse.jface.viewers.ColumnViewer#createViewerEditor()
	 */
	@Override
	protected ColumnViewerEditor createViewerEditor() {
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ColumnViewer#doGetColumnCount()
	 */
	@Override
	protected int doGetColumnCount() {
		return tableCombo.getTable().getColumnCount();
	}

	/**
	 * @see org.eclipse.jface.viewers.ColumnViewer#getItemAt(org.eclipse.swt.graphics.Point)
	 */
	@Override
	protected Item getItemAt(final Point point) {
		return tableCombo.getTable().getItem(point);
	}

	/**
	 * @see org.eclipse.jface.viewers.ColumnViewer#getViewerRowFromItem(org.eclipse.swt.widgets.Widget)
	 */
	@Override
	protected ViewerRow getViewerRowFromItem(final Widget item) {
		if (cachedRow == null) {
			cachedRow = new CheckTableComboViewerRow((TableItem) item);
		} else {
			cachedRow.setItem((TableItem) item);
		}
		return cachedRow;
	}

	/**
	 * @see org.eclipse.jface.viewers.Viewer#getControl()
	 */
	@Override
	public Control getControl() {
		return tableCombo;
	}

	/**
	 * returns the CheckTableCombo reference.
	 *
	 * @return
	 */
	public CheckTableCombo getCheckTableCombo() {
		return tableCombo;
	}

	/**
	 * @see org.eclipse.jface.viewers.StructuredViewer#handleLabelProviderChanged(org.eclipse.jface.viewers.LabelProviderChangedEvent)
	 */
	@Override
	protected void handleLabelProviderChanged(final LabelProviderChangedEvent event) {
		super.handleLabelProviderChanged(event);
		this.setSelection(getSelection());
	}
}