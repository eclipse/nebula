/*******************************************************************************
 * Copyright (c) 2006-2007 Nicolas Richeton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors :
 *    Nicolas Richeton (nicolas.richeton@gmail.com) - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.gallery;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Item;

/**
 * Gallery Item<br/>
 * 
 * <p>
 * NOTE: THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT. THIS IS A
 * PRE-RELEASE ALPHA VERSION. USERS SHOULD EXPECT API CHANGES IN FUTURE
 * VERSIONS.
 * </p>
 * 
 * @author Nicolas Richeton (nicolas.richeton@gmail.com)
 * 
 */

public class GalleryItem extends Item {

	private String description = null;

	// This is managed by the Gallery
	/**
	 * Children of this item. Only used when groups are enabled.
	 */
	protected GalleryItem[] items = null;

	/**
	 * Begining of the item. This value is for vertical or horizontal offset
	 * depending of the Gallery settings. Only used when groups are enabled.
	 */
	protected int x = 0;

	protected int y = 0;

	/**
	 * Size of the group, including its title.
	 */
	protected int width = 0;

	protected int height = 0;

	protected int marginBottom = 0;

	protected int hCount = 0;

	protected int vCount = 0;

	/**
	 * Last result of indexOf( GalleryItem). Used for optimisation.
	 */
	protected int lastIndexOf = 0;

	/**
	 * itemCount stores the number of children of this group. It is used when
	 * the Gallery was created with SWT.VIRTUAL
	 */
	private int itemCount = 0;

	/**
	 * True if the Gallery was created wih SWT.VIRTUAL
	 */
	private boolean virtualGallery;

	private Gallery parent;

	private GalleryItem parentItem;

	private int[] selectionIndices = null;

	/**
	 * 
	 */
	private boolean expanded;

	public Gallery getParent() {
		return parent;
	}

	protected void setParent(Gallery parent) {
		this.parent = parent;
	}

	public GalleryItem getParentItem() {
		return parentItem;
	}

	protected void setParentItem(GalleryItem parentItem) {
		this.parentItem = parentItem;
	}

	public GalleryItem(Gallery parent, int style) {
		super(parent, style);
		this.parent = parent;

		if ((parent.getStyle() & SWT.VIRTUAL) > 0) {
			virtualGallery = true;
		} else {
			parent.addItem(this);
		}

	}

	public GalleryItem(GalleryItem parent, int style) {
		super(parent, style);
		this.parent = parent.parent;
		this.parentItem = parent;
		if ((parent.getStyle() & SWT.VIRTUAL) > 0) {
			virtualGallery = true;
		} else {
			parent.addItem(this);
		}
	}

	/**
	 * Only work when the table was created with SWT.VIRTUAL
	 * 
	 * @param item
	 */
	protected void addItem(GalleryItem item) {
		if (!virtualGallery) {
			if (items == null) {
				items = new GalleryItem[1];
			} else {
				GalleryItem[] newItems = new GalleryItem[items.length + 1];
				System.arraycopy(items, 0, newItems, 0, items.length);
				items = newItems;
			}
			items[items.length - 1] = item;
			parent.updateStructuralValues(false);
			parent.updateScrollBarsProperties();

		}
	}

	/**
	 * Returns the number of items contained in the receiver that are direct
	 * item children of the receiver.
	 * 
	 * @return
	 */
	public int getItemCount() {
		if (virtualGallery)
			return itemCount;

		if (items == null)
			return 0;

		return items.length;
	}

	/**
	 * Only work when the table was created with SWT.VIRTUAL
	 * 
	 * @param itemCount
	 */
	public void setItemCount(int count) {
		if (virtualGallery) {
			if (count == 0) {
				// No items
				items = null;
			} else {
				// At least one item, create a new array and copy data from the
				// old one.
				GalleryItem[] newItems = new GalleryItem[count];
				if (items != null) {
					System.arraycopy(items, 0, newItems, 0, Math.min(count, items.length));
				}
				items = newItems;
			}
			this.itemCount = count;

		}

	}

	/**
	 * Searches the receiver's list starting at the first item (index 0) until
	 * an item is found that is equal to the argument, and returns the index of
	 * that item. <br/> If SWT.VIRTUAL is used and the item has not been used
	 * yet, the item is created and a SWT.SetData event is fired.
	 * 
	 * @param index :
	 *            index of the item.
	 * @return : the GalleryItem or null if index is out of bounds
	 */
	public GalleryItem getItem(int index) {
		checkWidget();
		return parent._getItem(this, index);
	}

	/**
	 * Returns the index of childItem within this item or -1 if childItem is not
	 * found. The search is only one level deep.
	 * 
	 * @param childItem
	 * @return
	 */
	public int indexOf(GalleryItem childItem) {
		checkWidget();

		return parent._indexOf(this, childItem);
	}

	/**
	 * Returns true if the receiver is expanded, and false otherwise.
	 * 
	 * @return
	 */
	public boolean isExpanded() {
		return expanded;
	}

	/**
	 * Sets the expanded state of the receiver.
	 * 
	 * @param expanded
	 */
	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Deselect all children of this item
	 */
	protected void deselectAll() {
		checkWidget();
		_deselectAll();
		parent.redraw();
	}

	protected void _deselectAll() {
		this.selectionIndices = null;
		if (items == null)
			return;
		for (int i = 0; i < items.length; i++) {
			if (items[i] != null)
				items[i]._deselectAll();
		}
	}

	protected void _addSelection(GalleryItem item) {
		// Deselect all items is multi selection is disabled
		if (!parent.multi) {
			_deselectAll();
		}

		if (item.getParentItem() == this) {
			if (selectionIndices == null) {
				selectionIndices = new int[1];
			} else {
				int[] oldSelection = selectionIndices;
				selectionIndices = new int[oldSelection.length + 1];
				System.arraycopy(oldSelection, 0, selectionIndices, 0, oldSelection.length);
			}
			selectionIndices[selectionIndices.length - 1] = indexOf(item);

		}
	}

	protected boolean isSelected(GalleryItem item) {
		if (item == null)
			return false;

		if (item.getParentItem() == this) {
			if (selectionIndices == null)
				return false;

			int index = indexOf(item);
			for (int i = 0; i < selectionIndices.length; i++) {
				if (selectionIndices[i] == index)
					return true;
			}
		}
		return false;
	}

	protected void select(int from, int to) {
		if (Gallery.DEBUG)
			System.out.println("GalleryItem.select(  " + from + "," + to + ")");

		for (int i = from; i <= to; i++) {
			GalleryItem item = getItem(i);
			parent._addSelection(item);
			item._selectAll();
		}
	}

	/**
	 * Return the current bounds of the item.
	 * 
	 * @return
	 */
	public Rectangle getBounds() {
		// The y coords is relative to the client area because it may return
		// wrong values
		// on win32 when using the scroll bars. Instead, I use the absolute
		// position and make is relative using the current translation.

		// TODO : support horizontal mode.
		// TODO : handle cases when the item is not on screen.
		return new Rectangle(x, y - parent.translate, width, height);
	}

	/**
	 * Selects all of the items in the receiver.
	 */
	public void selectAll() {
		checkWidget();
		_selectAll();
		parent.redraw();
	}

	protected void _selectAll() {
		select(0, this.getItemCount() - 1);
	}
}
