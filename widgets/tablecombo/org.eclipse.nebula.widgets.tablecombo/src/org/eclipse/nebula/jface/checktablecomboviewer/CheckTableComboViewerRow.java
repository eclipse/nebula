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
 * few modifications to reference the CheckTableComboViewerRow row instead of a
 * standard TableViewer row.
 */
public class CheckTableComboViewerRow extends ViewerRow {
	private TableItem item;

	/**
	 * Create a new instance of the receiver from item.
	 *
	 * @param item
	 */
	CheckTableComboViewerRow(final TableItem item) {
		this.item = item;
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#getBounds(int)
	 */
	@Override
	public Rectangle getBounds(final int columnIndex) {
		return item.getBounds(columnIndex);
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#getBounds()
	 */
	@Override
	public Rectangle getBounds() {
		return item.getBounds();
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#getItem()
	 */
	@Override
	public Widget getItem() {
		return item;
	}

	void setItem(final TableItem item) {
		this.item = item;
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return item.getParent().getColumnCount();
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#getBackground(int)
	 */
	@Override
	public Color getBackground(final int columnIndex) {
		return item.getBackground(columnIndex);
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#getFont(int)
	 */
	@Override
	public Font getFont(final int columnIndex) {
		return item.getFont(columnIndex);
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#getForeground(int)
	 */
	@Override
	public Color getForeground(final int columnIndex) {
		return item.getForeground(columnIndex);
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#getImage(int)
	 */
	@Override
	public Image getImage(final int columnIndex) {
		return item.getImage(columnIndex);
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#getText(int)
	 */
	@Override
	public String getText(final int columnIndex) {
		return item.getText(columnIndex);
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#setBackground(int,
	 *      org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void setBackground(final int columnIndex, final Color color) {
		item.setBackground(columnIndex, color);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFont(final int columnIndex, final Font font) {
		item.setFont(columnIndex, font);
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#setForeground(int,
	 *      org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void setForeground(final int columnIndex, final Color color) {
		item.setForeground(columnIndex, color);
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#setImage(int,
	 *      org.eclipse.swt.graphics.Image)
	 */
	@Override
	public void setImage(final int columnIndex, final Image image) {
		final Image oldImage = item.getImage(columnIndex);
		if (oldImage != image) {
			item.setImage(columnIndex, image);
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#setText(int, java.lang.String)
	 */
	@Override
	public void setText(final int columnIndex, final String text) {
		item.setText(columnIndex, text == null ? "" : text); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#getControl()
	 */
	@Override
	public Control getControl() {
		return item.getParent();
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#getNeighbor(int, boolean)
	 */
	@Override
	public ViewerRow getNeighbor(final int direction, final boolean sameLevel) {
		if (direction == ViewerRow.ABOVE) {
			return getRowAbove();
		} else if (direction == ViewerRow.BELOW) {
			return getRowBelow();
		} else {
			throw new IllegalArgumentException("Illegal value of direction argument."); //$NON-NLS-1$
		}
	}

	private ViewerRow getRowAbove() {
		final int index = item.getParent().indexOf(item) - 1;

		if (index >= 0) {
			return new CheckTableComboViewerRow(item.getParent().getItem(index));
		}

		return null;
	}

	private ViewerRow getRowBelow() {
		final int index = item.getParent().indexOf(item) + 1;

		if (index < item.getParent().getItemCount()) {
			final TableItem tmp = item.getParent().getItem(index);
			if (tmp != null) {
				return new CheckTableComboViewerRow(tmp);
			}
		}

		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#getTreePath()
	 */
	@Override
	public TreePath getTreePath() {
		return new TreePath(new Object[] { item.getData() });
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#clone()
	 */
	@Override
	public Object clone() {
		return new CheckTableComboViewerRow(item);
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#getElement()
	 */
	@Override
	public Object getElement() {
		return item.getData();
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#getVisualIndex(int)
	 */
	@Override
	public int getVisualIndex(final int creationIndex) {
		final int[] order = item.getParent().getColumnOrder();

		for (int i = 0; i < order.length; i++) {
			if (order[i] == creationIndex) {
				return i;
			}
		}

		return super.getVisualIndex(creationIndex);
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#getCreationIndex(int)
	 */
	@Override
	public int getCreationIndex(final int visualIndex) {
		if (item != null && !item.isDisposed() && hasColumns() && isValidOrderIndex(visualIndex)) {
			return item.getParent().getColumnOrder()[visualIndex];
		}
		return super.getCreationIndex(visualIndex);
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#getTextBounds(int)
	 */
	@Override
	public Rectangle getTextBounds(final int index) {
		return item.getTextBounds(index);
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#getImageBounds(int)
	 */
	@Override
	public Rectangle getImageBounds(final int index) {
		return item.getImageBounds(index);
	}

	private boolean hasColumns() {
		return item.getParent().getColumnCount() != 0;
	}

	private boolean isValidOrderIndex(final int currentIndex) {
		return currentIndex < item.getParent().getColumnOrder().length;
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#scrollCellIntoView(int)
	 */
	@Override
	protected boolean scrollCellIntoView(final int columnIndex) {
		item.getParent().showItem(item);
		if (hasColumns()) {
			item.getParent().showColumn(item.getParent().getColumn(columnIndex));
		}

		return true;
	}
}