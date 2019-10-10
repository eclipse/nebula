/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    rmcamara@us.ibm.com - initial API and implementation
 *    Tom Schindl <tom.schindl@bestsolution.at> - various significant contributions
 *    											  bug fix in: 191216
 *    Mirko Paturzo <mirko.paturzo@exeura.eu> - improvement (bugfix in 387366)
 *******************************************************************************/

package org.eclipse.nebula.jface.gridviewer;

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

/**
 * GridViewerRow is the concrete implementation of the part that represents
 * items in a Grid.
 */
public class GridViewerRow extends ViewerRow {
	private GridItem item;

	/**
	 * Create a new instance of the receiver.
	 *
	 * @param item
	 *            GridItem source.
	 */
	GridViewerRow(GridItem item) {
		this.item = item;
	}

	/** {@inheritDoc} */
	@Override
	public Rectangle getBounds(int columnIndex) {
		if (columnIndex == Integer.MAX_VALUE) {
			// TODO Provide implementation for GridItem
			return null;
		} else {
			if (!item.getParent().getColumn(columnIndex).isVisible()) {
				return new Rectangle(0, 0, 0, 0);
			} else {
				return item.getBounds(columnIndex);
			}

		}
	}

	/** {@inheritDoc} */
	@Override
	public Rectangle getBounds() {
		// TODO This is not correct. Update once item returns the correct information.
		return item.getBounds(0);
	}

	/** {@inheritDoc} */
	@Override
	public int getColumnCount() {
		return item.getParent().getColumnCount();
	}

	/** {@inheritDoc} */
	@Override
	public Color getBackground(int columnIndex) {
		if (columnIndex == Integer.MAX_VALUE) {
			// TODO Provide implementation for GridItem
			return null;
		} else {
			return item.getBackground(columnIndex);
		}
	}

	/** {@inheritDoc} */
	@Override
	public Font getFont(int columnIndex) {
		if (columnIndex == Integer.MAX_VALUE) {
			// TODO Provide implementation for GridItem
			return null;
		} else {
			return item.getFont(columnIndex);
		}
	}

	/** {@inheritDoc} */
	@Override
	public Color getForeground(int columnIndex) {
		if (columnIndex == Integer.MAX_VALUE) {
			// TODO Provide implementation for GridItem
			return null;
		} else {
			return item.getForeground(columnIndex);
		}
	}

	/** {@inheritDoc} */
	@Override
	public Image getImage(int columnIndex) {
		if (columnIndex == Integer.MAX_VALUE) {
			// TODO Provide implementation for GridItem
			return null;
		} else {
			return item.getImage(columnIndex);
		}

	}

	/** {@inheritDoc} */
	@Override
	public String getText(int columnIndex) {
		if (columnIndex == Integer.MAX_VALUE) {
			return item.getHeaderText();
		} else {
			return item.getText(columnIndex);
		}

	}

	/** {@inheritDoc} */
	@Override
	public void setBackground(int columnIndex, Color color) {
		if (columnIndex == Integer.MAX_VALUE) {
			item.setHeaderBackground(color);
		} else {
			item.setBackground(columnIndex, color);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void setFont(int columnIndex, Font font) {
		if (columnIndex == Integer.MAX_VALUE) {
			item.setHeaderFont(font);
		} else {
			item.setFont(columnIndex, font);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void setForeground(int columnIndex, Color color) {
		if (columnIndex == Integer.MAX_VALUE) {
			item.setHeaderForeground(color);
		} else {
			item.setForeground(columnIndex, color);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void setImage(int columnIndex, Image image) {
		if (columnIndex == Integer.MAX_VALUE) {
			item.setHeaderImage(image);
		} else {
			item.setImage(columnIndex, image);
		}

	}

	/** {@inheritDoc} */
	@Override
	public void setText(int columnIndex, String text) {
		if (columnIndex == Integer.MAX_VALUE) {
			item.setHeaderText(text);
		} else {
			item.setText(columnIndex, text == null ? "" : text); //$NON-NLS-1$
		}
	}

	/** {@inheritDoc} */
	@Override
	public Control getControl() {
		return item.getParent();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ViewerRow getNeighbor(int direction, boolean sameLevel) {
		if (direction == ViewerRow.ABOVE) {
			return getRowAbove();
		} else if (direction == ViewerRow.BELOW) {
			return getRowBelow();
		} else {
			throw new IllegalArgumentException("Illegal value of direction argument."); //$NON-NLS-1$
		}
	}

	private ViewerRow getRowAbove() {
		int index = item.getRowIndex() - 1;

		if (index >= 0) {
			return new GridViewerRow(item.getParent().getItem(index));
		}

		return null;
	}

	private ViewerRow getRowBelow() {
		int index = item.getRowIndex() + 1;

		if (index < item.getParent().getItemCount()) {
			GridItem tmp = item.getParent().getItem(index);
			// Maybe this is a dummy item!!

			if (tmp != null && !tmp.isDisposed() && tmp.isVisible() && tmp.getData() != null) {
				return new GridViewerRow(tmp);
			}
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TreePath getTreePath() {
		return new TreePath(new Object[] { item.getData() });
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object clone() {
		return new GridViewerRow(item);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getElement() {
		return item.getData();
	}

	void setItem(GridItem item) {
		this.item = item;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Widget getItem() {
		return item;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getVisualIndex(int creationIndex) {
		int[] order = item.getParent().getColumnOrder();

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
		if (item != null && !item.isDisposed() && hasColumns() && isValidOrderIndex(visualIndex)) {
			return item.getParent().getColumnOrder()[visualIndex];
		}
		return super.getCreationIndex(visualIndex);
	}

	// public Rectangle getTextBounds(int index) {
	// return item.getTextBounds(index);
	// }
	//
	// /* (non-Javadoc)
	// * @see org.eclipse.jface.viewers.ViewerRow#getImageBounds(int)
	// */
	// public Rectangle getImageBounds(int index) {
	// return item.getImageBounds(index);
	// }

	private boolean hasColumns() {
		return this.item.getParent().getColumnCount() != 0;
	}

	private boolean isValidOrderIndex(int currentIndex) {
		return currentIndex < this.item.getParent().getColumnOrder().length;
	}

	/**
	 * Check if the column of the cell is part of is visible
	 *
	 * @param columnIndex
	 *            the column index
	 *
	 * @return <code>true</code> if the column is visible
	 */
	@Override
	protected boolean isColumnVisible(int columnIndex) {
		return item.getParent().getColumn(columnIndex).isVisible();
	}
}
