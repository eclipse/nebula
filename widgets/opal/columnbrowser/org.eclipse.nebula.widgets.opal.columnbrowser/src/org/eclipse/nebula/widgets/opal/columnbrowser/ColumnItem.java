/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - Initial
 * implementation and API
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.columnbrowser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.opal.commons.OpalItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;

/**
 * Instances of this object are items manipulated by the ColumnBrowser widget.
 * ColumnItems are part of a tree structure .
 *
 * @see OpalItem
 */
public class ColumnItem extends OpalItem {

	private final ColumnBrowserWidget widget;
	private final ColumnItem parent;
	private final List<ColumnItem> children;

	/**
	 * Constructs a new instance of this class given its parent. The item is added
	 * to the end of the items maintained by its parent.
	 *
	 * @param widget the widget that will contain this item (can not be null)
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public ColumnItem(final ColumnBrowserWidget widget) {
		if (widget == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}

		if (widget.isDisposed()) {
			SWT.error(SWT.ERROR_WIDGET_DISPOSED);
		}

		this.widget = widget;
		parent = null;
		children = new ArrayList<ColumnItem>();

		if (widget.getRootItem() != null) {
			widget.getRootItem().children.add(this);
		}
		widget.updateContent();
	}

	/**
	 * Constructs a new instance of this class given its parent. The item is added
	 * at a given position in the items'list maintained by its parent.
	 *
	 * @param widget the widget that will contain this item (can not be null)
	 * @param index the position
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public ColumnItem(final ColumnBrowserWidget widget, final int index) {

		if (widget == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}

		if (widget.isDisposed()) {
			SWT.error(SWT.ERROR_WIDGET_DISPOSED);
		}

		this.widget = widget;
		parent = null;
		children = new ArrayList<ColumnItem>();
		widget.getRootItem().children.add(index, this);
		widget.updateContent();
	}

	/**
	 * Constructs a new instance of this class given its parent. The item is added
	 * to the end of the items maintained by its parent.
	 *
	 * @param widget the widget that will contain this item (can not be null)
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public ColumnItem(final ColumnItem parent) {

		if (parent == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}

		if (parent.widget.isDisposed()) {
			SWT.error(SWT.ERROR_WIDGET_DISPOSED);
		}

		widget = parent.widget;
		this.parent = parent;
		children = new ArrayList<ColumnItem>();
		parent.children.add(this);
		parent.widget.updateContent();
	}

	/**
	 * Constructs a new instance of this class given its parent. The item is added
	 * at a given position in the items'list maintained by its parent.
	 *
	 * @param widget the widget that will contain this item (can not be null)
	 * @param index the position
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public ColumnItem(final ColumnItem parent, final int index) {
		if (parent == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}

		if (parent.widget.isDisposed()) {
			SWT.error(SWT.ERROR_WIDGET_DISPOSED);
		}

		widget = parent.widget;
		this.parent = parent;
		children = new ArrayList<ColumnItem>();
		parent.children.add(index, this);
		parent.widget.updateContent();
	}

	/**
	 * Remove a given children of this object
	 *
	 * @param item the item to remove (can not be null)
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void remove(final ColumnItem item) {
		if (widget == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}

		if (widget.isDisposed()) {
			SWT.error(SWT.ERROR_WIDGET_DISPOSED);
		}
		children.remove(item);
		widget.updateContent();
	}

	/**
	 * Remove a children in a given position of this object
	 *
	 * @param index position of the children in the items'list
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void remove(final int index) {
		if (widget == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}

		if (widget.isDisposed()) {
			SWT.error(SWT.ERROR_WIDGET_DISPOSED);
		}
		children.remove(index);
		widget.updateContent();
	}

	/**
	 * Remove all children of this object
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void removeAll() {
		if (widget == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}

		if (widget.isDisposed()) {
			SWT.error(SWT.ERROR_WIDGET_DISPOSED);
		}
		children.clear();
		widget.updateContent();
	}

	/**
	 * Returns an item located at a given position
	 *
	 * @param index position
	 * @return the item located at the index position
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public ColumnItem getItem(final int index) {
		if (widget == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}

		if (widget.isDisposed()) {
			SWT.error(SWT.ERROR_WIDGET_DISPOSED);
		}
		return children.get(index);
	}

	/**
	 * @return the number of children
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public int getItemCount() {
		if (widget == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}

		if (widget.isDisposed()) {
			SWT.error(SWT.ERROR_WIDGET_DISPOSED);
		}
		return children.size();
	}

	/**
	 * @return all children of this item
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public ColumnItem[] getItems() {
		if (widget == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}

		if (widget.isDisposed()) {
			SWT.error(SWT.ERROR_WIDGET_DISPOSED);
		}
		return children.toArray(new ColumnItem[children.size()]);
	}

	/**
	 * @return the widget that holds this item
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public ColumnBrowserWidget getParent() {
		if (widget == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}

		if (widget.isDisposed()) {
			SWT.error(SWT.ERROR_WIDGET_DISPOSED);
		}
		return widget;
	}

	/**
	 * @return the parent item, of <code>null</code> if this item is the root node
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public ColumnItem getParentItem() {
		if (widget == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}

		if (widget.isDisposed()) {
			SWT.error(SWT.ERROR_WIDGET_DISPOSED);
		}
		return parent;
	}

	/**
	 * Return the position of a given item in children's list
	 *
	 * @param item item to find
	 * @return the position of the children, or -1 if <code>item</code> is a not a
	 *         children of this object
	 *
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public int indexOf(final ColumnItem item) {
		return children.indexOf(item);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (parent == null ? 0 : parent.hashCode());
		result = prime * result + (widget == null ? 0 : widget.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ColumnItem other = (ColumnItem) obj;
		if (children == null) {
			if (other.children != null) {
				return false;
			}
		} else if (!children.equals(other.children)) {
			return false;
		}
		if (parent == null) {
			if (other.parent != null) {
				return false;
			}
		} else if (!parent.equals(other.parent)) {
			return false;
		}
		if (widget == null) {
			if (other.widget != null) {
				return false;
			}
		} else if (!widget.equals(other.widget)) {
			return false;
		}
		return true;
	}

}
