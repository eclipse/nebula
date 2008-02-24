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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
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
 * @contributor Peter Centgraf (bugs 212071, 212073)
 * 
 */

public class GalleryItem extends Item {

	private static final String EMPTY_STRING = "";
	private String description = null;

	// This is managed by the Gallery
	/**
	 * Children of this item. Only used when groups are enabled.
	 */
	protected GalleryItem[] items = null;

	/**
	 * Bounds of this items in the current Gallery.
	 * 
	 * X and Y values are used for vertical or horizontal offset depending on
	 * the Gallery settings. Only used when groups are enabled.
	 * 
	 * Width and hei
	 */
	// protected Rectangle bounds = new Rectangle(0, 0, 0, 0);
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

	protected int[] selectionIndices = null;

	protected Font font;

	protected Color foreground;

	protected Color background;

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

	public GalleryItem(Gallery parent, int style, int index) {
		super(parent, style);
		this.parent = parent;

		if ((parent.getStyle() & SWT.VIRTUAL) > 0) {
			virtualGallery = true;
		} else {
			parent.addItem(this, index);
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

	public GalleryItem(GalleryItem parent, int style, int index) {
		super(parent, style);
		this.parent = parent.parent;
		this.parentItem = parent;
		if ((parent.getStyle() & SWT.VIRTUAL) > 0) {
			virtualGallery = true;
		} else {
			parent.addItem(this, index);
		}
	}

	/**
	 * Only work when the table was not created with SWT.VIRTUAL
	 * 
	 * @param item
	 */
	protected void addItem(GalleryItem item) {
		_addItem(item, -1);
	}

	protected void addItem(GalleryItem item, int position) {
		if (position < 0 || position > getItemCount()) {
			throw new IllegalArgumentException("ERROR_INVALID_RANGE ");
		}
		_addItem(item, position);
	}

	private void _addItem(GalleryItem item, int position) {
		// Items can only be added in a standard gallery (not using SWT.VIRTUAL)
		if (!virtualGallery) {

			// Insert item
			items = (GalleryItem[]) parent._arrayAddItem(items, item, position);

			// Update Gallery
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

	public GalleryItem[] getItems() {
		checkWidget();
		if (items == null)
			return new GalleryItem[0];

		GalleryItem[] itemsLocal = new GalleryItem[this.items.length];
		System.arraycopy(items, 0, itemsLocal, 0, this.items.length);

		return itemsLocal;
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

	public void setText(String text) {
		super.setText(text);
		parent.redraw(this);
	}

	public void setImage(Image image) {
		super.setImage(image);
		parent.redraw(this);
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
		parent.updateStructuralValues(false);
		parent.updateScrollBarsProperties();
		parent.redraw();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
		parent.redraw(this);
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
	 * Return the current bounds of the item. This method may return negative
	 * values if it is not visible.
	 * 
	 * @return
	 */
	public Rectangle getBounds() {
		// The y coords is relative to the client area because it may return
		// wrong values
		// on win32 when using the scroll bars. Instead, I use the absolute
		// position and make is relative using the current translation.

		if (parent.isVertical()) {
			return new Rectangle(x, y - parent.translate, width, height);
		} else {
			return new Rectangle(x - parent.translate, y, width, height);
		}
	}

	public Font getFont() {
		checkWidget();
		return font;
	}

	public void setFont(Font font) {
		checkWidget();
		if (font != null && font.isDisposed()) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		this.font = font;
		this.parent.redraw(this);
	}

	public Color getForeground() {
		checkWidget();
		return foreground;
	}

	public void setForeground(Color foreground) {
		checkWidget();
		if (foreground != null && foreground.isDisposed()) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		this.foreground = foreground;
		this.parent.redraw(this);
	}

	public Color getBackground() {
		checkWidget();
		return background;
	}

	public void setBackground(Color background) {
		checkWidget();
		if (background != null && background.isDisposed()) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		this.background = background;
		this.parent.redraw(this);
	}

	/**
	 * Reset item values to defaults.
	 */
	public void clear() {
		checkWidget();
		// Clear all attributes
		super.setText(EMPTY_STRING);
		super.setImage(null);
		this.font = null;
		background = null;
		foreground = null;

		// Force redraw
		this.parent.redraw(this);
	}

	public void clearAll() {
		clearAll(false);
	}

	public void clearAll(boolean all) {
		checkWidget();

		if (items == null)
			return;

		if (virtualGallery) {
			items = new GalleryItem[items.length];
		} else {
			for (int i = 0; i < items.length; i++) {
				if (items[i] != null) {
					if (all) {
						items[i].clearAll(true);
					}
					items[i].clear();
				}
			}
		}
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

	public void remove(int index) {
		checkWidget();
		parent._remove(this, index);

		parent.updateStructuralValues(false);
		parent.updateScrollBarsProperties();
		parent.redraw();
	}

	public void remove(GalleryItem item) {
		remove(indexOf(item));
	}

	/**
	 * Disposes the gallery Item. This method is call directly by gallery and
	 * should not be used by a client
	 */
	protected void _dispose() {
		removeFromParent();
		_disposeChildren();
		super.dispose();
	}

	protected void _disposeChildren() {
		if (items != null) {
			while (items != null) {
				if (items[0] != null) {
					items[0]._dispose();
				}
			}
		}
	}

	protected void removeFromParent() {
		if (parentItem != null) {
			int index = parent._indexOf(parentItem, this);
			parent._remove(parentItem, index);
		} else {
			int index = parent._indexOf(this);
			parent._remove(index);
		}
	}

	public void dispose() {
		checkWidget();

		removeFromParent();
		_disposeChildren();
		super.dispose();

		parent.updateStructuralValues(false);
		parent.updateScrollBarsProperties();
		parent.redraw();
	}
}
