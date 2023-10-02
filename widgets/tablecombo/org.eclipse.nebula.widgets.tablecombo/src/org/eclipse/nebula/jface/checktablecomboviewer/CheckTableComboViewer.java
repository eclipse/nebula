
package com.airbus.ds.s3.ibd.ui.parts.properties.checkbox;

import org.eclipse.jface.viewers.AbstractTableViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.ViewerRow;
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

    public CheckTableComboViewer(Composite parent) {
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
    public CheckTableComboViewer(Composite parent, int style) {
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
    public CheckTableComboViewer(CheckTableCombo tableCombo) {
        this.tableCombo = tableCombo;
        this.hookControl(tableCombo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doClear(int index) {
        this.tableCombo.getTable().clear(index);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doClearAll() {
        this.tableCombo.getTable().clearAll();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doDeselectAll() {
        this.tableCombo.getTable().deselectAll();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Widget doGetColumn(int index) {
        return this.tableCombo.getTable().getColumn(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Item doGetItem(int index) {
        return this.tableCombo.getTable().getItem(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int doGetItemCount() {
        return this.tableCombo.getTable().getItemCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Item[] doGetItems() {
        return this.tableCombo.getTable().getItems();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Item[] doGetSelection() {
        return this.tableCombo.getTable().getSelection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int[] doGetSelectionIndices() {
        return this.tableCombo.getTable().getSelectionIndices();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int doIndexOf(Item item) {
        return this.tableCombo.getTable().indexOf((TableItem) item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doRemove(int[] indices) {
        this.tableCombo.getTable().remove(indices);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doRemove(int start, int end) {
        this.tableCombo.getTable().remove(start, end);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doRemoveAll() {
        this.tableCombo.getTable().removeAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doResetItem(Item item) {
        final TableItem tableItem = (TableItem) item;
        final int columnCount = Math.max(1, this.tableCombo.getTable().getColumnCount());
        for (int i = 0; i < columnCount; i++) {
            tableItem.setText(i, ""); //$NON-NLS-1$
            if (tableItem.getImage(i) != null) {
                tableItem.setImage(i, null);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSelect(int[] indices) {
        this.tableCombo.select(indices != null && indices.length > 0 ? indices : new int[] {-1});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSetItemCount(int count) {
        this.tableCombo.getTable().setItemCount(count);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSetSelection(Item[] items) {
        if (items != null && items.length > 0) {
            this.tableCombo.select((TableItem[]) items);
        } else {
            this.tableCombo.select(new int[] {-1});
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSetSelection(int[] indices) {
        this.tableCombo.select(indices != null && indices.length > 0 ? indices : new int[] {-1});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doShowItem(Item item) {
        this.tableCombo.getTable().showItem((TableItem) item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doShowSelection() {
        this.tableCombo.getTable().showSelection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ViewerRow internalCreateNewRowPart(int style, int rowIndex) {
        TableItem item;

        if (rowIndex >= 0) {
            item = new TableItem(this.tableCombo.getTable(), style, rowIndex);
        } else {
            item = new TableItem(this.tableCombo.getTable(), style);
        }

        return this.getViewerRowFromItem(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ColumnViewerEditor createViewerEditor() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int doGetColumnCount() {
        return this.tableCombo.getTable().getColumnCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Item getItemAt(Point point) {
        return this.tableCombo.getTable().getItem(point);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ViewerRow getViewerRowFromItem(Widget item) {
        if (this.cachedRow == null) {
            this.cachedRow = new CheckTableComboViewerRow((TableItem) item);
        } else {
            this.cachedRow.setItem((TableItem) item);
        }

        return this.cachedRow;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Control getControl() {
        return this.tableCombo;
    }

    /**
     * returns the CheckTableCombo reference.
     *
     * @return
     */
    public CheckTableCombo getCheckTableCombo() {
        return this.tableCombo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleLabelProviderChanged(LabelProviderChangedEvent event) {
        super.handleLabelProviderChanged(event);
        this.setSelection(this.getSelection());
    }
}
