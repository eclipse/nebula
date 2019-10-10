/*******************************************************************************
 * Copyright (c) 2007-2008 Peter Centgraf.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors :
 *    Peter Centgraf - initial implementation
 *******************************************************************************/
package org.eclipse.nebula.jface.galleryviewer;

import java.util.LinkedList;

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

/**
 * ViewerRow adapter for the Nebula Gallery widget.
 * 
 * @author Peter Centgraf
 * @since Dec 5, 2007
 */
public class GalleryViewerRow extends ViewerRow {

	protected GalleryItem item;

	/**
	 * Constructs a ViewerRow adapter for a GalleryItem.
	 * 
	 * @param item
	 *            the GalleryItem to adapt
	 */
	public GalleryViewerRow(GalleryItem item) {
		this.item = item;
	}

	public void setItem(GalleryItem item) {
		this.item = item;
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#clone()
	 */
	public Object clone() {
		return new GalleryViewerRow(item);
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#getBackground(int)
	 */
	public Color getBackground(int columnIndex) {
		// XXX: should this use getBackgroundColor() instead?
		return item.getBackground();
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#getBounds()
	 */
	public Rectangle getBounds() {
		return item.getBounds();
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#getBounds(int)
	 */
	public Rectangle getBounds(int columnIndex) {
		return item.getBounds();
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#getColumnCount()
	 */
	public int getColumnCount() {
		return 0;
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#getControl()
	 */
	public Control getControl() {
		return item.getParent();
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#getElement()
	 */
	public Object getElement() {
		return item.getData();
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#getFont(int)
	 */
	public Font getFont(int columnIndex) {
		return item.getFont();
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#getForeground(int)
	 */
	public Color getForeground(int columnIndex) {
		return item.getForeground();
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#getImage(int)
	 */
	public Image getImage(int columnIndex) {
		return item.getImage();
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#getItem()
	 */
	public Widget getItem() {
		return item;
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#getNeighbor(int, boolean)
	 */
	public ViewerRow getNeighbor(int direction, boolean sameLevel) {
		if (direction == ViewerRow.ABOVE) {
			// TODO: handle grouping
			return getRowAbove();
		} else if (direction == ViewerRow.BELOW) {
			// TODO: handle grouping
			return getRowBelow();
		} else {
			throw new IllegalArgumentException(
					"Illegal value of direction argument."); //$NON-NLS-1$
		}
	}

	protected ViewerRow getRowAbove() {
		if (item.getParentItem() == null) {
			int index = item.getParent().indexOf(item) - 1;

			if (index >= 0) {
				return new GalleryViewerRow(item.getParent().getItem(index));
			}
		} else {
			GalleryItem parentItem = item.getParentItem();
			int index = parentItem.indexOf(item) - 1;

			if (index >= 0) {
				return new GalleryViewerRow(parentItem.getItem(index));
			}
		}

		return null;
	}

	protected ViewerRow getRowBelow() {
		if (item.getParentItem() == null) {
			int index = item.getParent().indexOf(item) + 1;

			if (index < item.getParent().getItemCount()) {
				GalleryItem tmp = item.getParent().getItem(index);
				if (tmp != null) {
					return new GalleryViewerRow(tmp);
				}
			}
		} else {
			GalleryItem parentItem = item.getParentItem();
			int index = parentItem.indexOf(item) + 1;

			if (index < parentItem.getItemCount()) {
				GalleryItem tmp = parentItem.getItem(index);
				if (tmp != null) {
					return new GalleryViewerRow(tmp);
				}
			}
		}

		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#getText(int)
	 */
	public String getText(int columnIndex) {
		return item.getText();
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#getTreePath()
	 */
	public TreePath getTreePath() {
		LinkedList<Object> path = new LinkedList<>();
		path.add(item.getData());

		GalleryItem curItem = item;
		while (curItem.getParentItem() != null) {
			path.addFirst(curItem.getParentItem().getData());
			curItem = curItem.getParentItem();
		}
		return new TreePath(path.toArray());
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#setBackground(int,
	 *      org.eclipse.swt.graphics.Color)
	 */
	public void setBackground(int columnIndex, Color color) {
		item.setBackground(color);
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#setFont(int,
	 *      org.eclipse.swt.graphics.Font)
	 */
	public void setFont(int columnIndex, Font font) {
		item.setFont(font);
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#setForeground(int,
	 *      org.eclipse.swt.graphics.Color)
	 */
	public void setForeground(int columnIndex, Color color) {
		item.setForeground(color);
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#setImage(int,
	 *      org.eclipse.swt.graphics.Image)
	 */
	public void setImage(int columnIndex, Image image) {
		Image oldImage = item.getImage();
		if (image != oldImage) {
			item.setImage(image);
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerRow#setText(int, java.lang.String)
	 */
	public void setText(int columnIndex, String text) {
		item.setText(text == null ? "" : text);
	}

}
