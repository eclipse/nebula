
package com.airbus.ds.s3.ibd.ui.parts.properties.checkbox;

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

/**
 * CheckTableComboViewerRow is basically identical to the TableRow class with a
 * few modifications to reference the CheckTableComboViewerRow row instead of a standar
 * TableViewer row.
 */
public class CheckTableComboViewerRow extends ViewerRow {
    private TableItem item;

    /**
     * Create a new instance of the receiver from item.
     *
     * @param item
     */
    CheckTableComboViewerRow(TableItem item) {
        this.item = item;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rectangle getBounds(int columnIndex) {
        return this.item.getBounds(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rectangle getBounds() {
        return this.item.getBounds();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Widget getItem() {
        return this.item;
    }

    void setItem(TableItem item) {
        this.item = item;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getColumnCount() {
        return this.item.getParent().getColumnCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Color getBackground(int columnIndex) {
        return this.item.getBackground(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Font getFont(int columnIndex) {
        return this.item.getFont(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Color getForeground(int columnIndex) {
        return this.item.getForeground(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Image getImage(int columnIndex) {
        return this.item.getImage(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getText(int columnIndex) {
        return this.item.getText(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBackground(int columnIndex, Color color) {
        this.item.setBackground(columnIndex, color);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFont(int columnIndex, Font font) {
        this.item.setFont(columnIndex, font);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setForeground(int columnIndex, Color color) {
        this.item.setForeground(columnIndex, color);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setImage(int columnIndex, Image image) {
        final Image oldImage = this.item.getImage(columnIndex);
        if (oldImage != image) {
            this.item.setImage(columnIndex, image);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setText(int columnIndex, String text) {
        this.item.setText(columnIndex, text == null ? "" : text); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Control getControl() {
        return this.item.getParent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewerRow getNeighbor(int direction, boolean sameLevel) {
        if (direction == ViewerRow.ABOVE) {
            return this.getRowAbove();
        } else if (direction == ViewerRow.BELOW) {
            return this.getRowBelow();
        } else {
            throw new IllegalArgumentException("Illegal value of direction argument."); //$NON-NLS-1$
        }
    }

    private ViewerRow getRowAbove() {
        final int index = this.item.getParent().indexOf(this.item) - 1;

        if (index >= 0) {
            return new CheckTableComboViewerRow(this.item.getParent().getItem(index));
        }

        return null;
    }

    private ViewerRow getRowBelow() {
        final int index = this.item.getParent().indexOf(this.item) + 1;

        if (index < this.item.getParent().getItemCount()) {
            final TableItem tmp = this.item.getParent().getItem(index);
            // TODO NULL can happen in case of VIRTUAL => How do we deal with that
            if (tmp != null) {
                return new CheckTableComboViewerRow(tmp);
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TreePath getTreePath() {
        return new TreePath(new Object[] {this.item.getData()});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone() {
        return new CheckTableComboViewerRow(this.item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getElement() {
        return this.item.getData();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getVisualIndex(int creationIndex) {
        final int[] order = this.item.getParent().getColumnOrder();

        for (int i = 0; i < order.length; i++) {
            if (order[i] == creationIndex) {
                return i;
            }
        }

        return super.getVisualIndex(creationIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCreationIndex(int visualIndex) {
        if (this.item != null && !this.item.isDisposed() && this.hasColumns() && this.isValidOrderIndex(visualIndex)) {
            return this.item.getParent().getColumnOrder()[visualIndex];
        }
        return super.getCreationIndex(visualIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rectangle getTextBounds(int index) {
        return this.item.getTextBounds(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rectangle getImageBounds(int index) {
        return this.item.getImageBounds(index);
    }

    private boolean hasColumns() {
        return this.item.getParent().getColumnCount() != 0;
    }

    private boolean isValidOrderIndex(int currentIndex) {
        return currentIndex < this.item.getParent().getColumnOrder().length;
    }

    @Override
    protected boolean scrollCellIntoView(int columnIndex) {
        this.item.getParent().showItem(this.item);
        if (this.hasColumns()) {
            this.item.getParent().showColumn(this.item.getParent().getColumn(columnIndex));
        }

        return true;
    }
}
