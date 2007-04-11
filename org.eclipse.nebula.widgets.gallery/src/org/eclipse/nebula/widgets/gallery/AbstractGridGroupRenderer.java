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
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Item;

/**
 * 
 * <p>
 * NOTE: THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT. THIS IS A
 * PRE-RELEASE ALPHA VERSION. USERS SHOULD EXPECT API CHANGES IN FUTURE
 * VERSIONS.
 * </p>
 * 
 * @author Nicolas Richeton (nicolas.richeton@gmail.com)
 */

public abstract class AbstractGridGroupRenderer extends AbstractGalleryGroupRenderer {
	static final int DEFAULT_SIZE = 96;

	protected int minMargin;

	protected int margin;

	protected boolean autoMargin;

	protected int itemWidth = DEFAULT_SIZE;

	protected int itemHeight = DEFAULT_SIZE;

	static public String H_COUNT = "g.h";

	static public String V_COUNT = "g.v";

	public int getMinMargin() {
		return minMargin;
	}

	public int getItemWidth() {
		return itemWidth;
	}

	public void setItemWidth(int itemWidth) {
		this.itemWidth = itemWidth;
	}

	public int getItemHeight() {
		return itemHeight;
	}

	public void setItemHeight(int itemHeight) {
		this.itemHeight = itemHeight;
	}

	public void setItemSize(int width, int height) {
		this.itemHeight = height;
		this.itemWidth = width;
	}

	public void setMinMargin(int minMargin) {
		this.minMargin = minMargin;
	}

	public boolean isAutoMargin() {
		return autoMargin;
	}

	public void setAutoMargin(boolean autoMargin) {
		this.autoMargin = autoMargin;
		if (gallery != null)
			gallery.updateStructuralValues(true);
	}

	protected int calculateMargins(int size, int count, int itemSize) {
		int margin = this.minMargin;
		margin += Math.round((float) (size - this.minMargin - (count * (itemSize + this.minMargin))) / (count + 1));
		return margin;
	}

	protected Point getSize(int nbx, int nby, int itemSizeX, int itemSizeY, int minMargin, int autoMargin) {
		int x = 0, y = 0;

		x = nbx * itemSizeX + (nbx - 1) * margin + 2 * minMargin;
		y = nby * itemSizeY + (nby + 1) * minMargin;

		return new Point(x, y);
	}

	/**
	 * Draw a child item. Only used when useGroup is true.
	 * 
	 * @param gc
	 * @param index
	 * @param selected
	 * @param parent
	 */
	protected void drawItem(GC gc, int index, boolean selected, GalleryItem parent, int offset) {

		if (Gallery.DEBUG)
			System.out.println("Draw item ? " + index);

		if (index < parent.getItemCount()) {
			int hCount = ((Integer) parent.getData(H_COUNT)).intValue();
			int vCount = ((Integer) parent.getData(V_COUNT)).intValue();

			if (Gallery.DEBUG)
				System.out.println("hCount :  " + hCount + " vCount : " + vCount);

			int posX, posY;
			if (gallery.isVertical()) {
				posX = index % hCount;
				posY = (index - posX) / hCount;
			} else {
				posY = index % vCount;
				posX = (index - posY) / vCount;
			}

			Item item = parent.getItem(index);

			// No item ? return
			if (item == null)
				return;

			int xPixelPos, yPixelPos;
			if (gallery.isVertical()) {
				xPixelPos = posX * (itemWidth + margin) + margin;
				yPixelPos = posY * (itemHeight + minMargin) - gallery.translate + minMargin + ((parent == null) ? 0 : (parent.y) + offset);
			} else {
				yPixelPos = posY * (itemHeight + margin) + margin;
				xPixelPos = posX * (itemWidth + minMargin) - gallery.translate + minMargin + ((parent == null) ? 0 : (parent.x) + offset);
			}

			GalleryItem gItem = (GalleryItem) item;
			gItem.x = xPixelPos;
			gItem.y = yPixelPos + gallery.translate;
			gItem.height = itemHeight;
			gItem.width = itemWidth;

			gallery.sendPaintItemEvent(item, index, gc, xPixelPos, yPixelPos, this.itemWidth, this.itemHeight);

			if (gallery.getItemRenderer() != null) {
				// gc.setClipping(xPixelPos, yPixelPos, itemWidth, itemHeight);
				gallery.getItemRenderer().setSelected(selected);
				if (Gallery.DEBUG)
					System.out.println("itemRender.draw");
				Rectangle oldClipping = gc.getClipping();

				gc.setClipping(oldClipping.intersection(new Rectangle(xPixelPos, yPixelPos, itemWidth, itemHeight)));
				gallery.getItemRenderer().draw(gc, gItem, index, xPixelPos, yPixelPos, itemWidth, itemHeight);
				gc.setClipping(oldClipping);
				if (Gallery.DEBUG)
					System.out.println("itemRender done");
			}

		}
	}

	protected int[] getVisibleItems(GalleryItem group, int x, int y, int clipX, int clipY, int clipWidth, int clipHeight, int offset) {

		int hCount = ((Integer) group.getData(H_COUNT)).intValue();
		int vCount = ((Integer) group.getData(V_COUNT)).intValue();

		int firstLine = (clipY - y - offset - minMargin) / (itemHeight + minMargin);
		if (firstLine < 0)
			firstLine = 0;

		int firstItem = firstLine * hCount;
		if (Gallery.DEBUG)
			System.out.println("First line : " + firstLine);

		int lastLine = (clipY - y - offset + clipHeight - minMargin) / (itemHeight + minMargin);

		if (lastLine < firstLine)
			lastLine = firstLine;

		if (Gallery.DEBUG)
			System.out.println("Last line : " + lastLine);

		int lastItem = (lastLine + 1) * hCount;

		// exit if no item selected
		if (lastItem - firstItem == 0)
			return null;

		int[] indexes = new int[lastItem - firstItem];
		for (int i = 0; i < (lastItem - firstItem); i++) {
			indexes[i] = firstItem + i;
		}

		return indexes;
	}

	/**
	 * Calculate how many items are displayed horizontally and vertically.
	 * 
	 * @param size
	 * @param nbItems
	 * @param itemSize
	 * @return
	 */
	protected Point gridLayout(int size, int nbItems, int itemSize) {
		int x = 0, y = 0;

		if (nbItems == 0)
			return new Point(x, y);

		x = (size - minMargin) / (itemSize + minMargin);
		if (x > 0) {
			y = (int) Math.ceil((double) nbItems / (double) x);
		} else {
			// Show at least one item;
			y = nbItems;
			x = 1;
		}

		return new Point(x, y);
	}

	public void dispose() {
	}

	boolean mouseDown(GalleryItem group, MouseEvent e, Point coords) {
		return false;
	}

	public void preLayout(GC gc) {
		margin = minMargin;
		super.preLayout(gc);
	}

	protected Rectangle getSize(GalleryItem item, int offset) {

		GalleryItem parent = item.getParentItem();
		if (parent != null) {
			int index = parent.indexOf(item);

			int hCount = ((Integer) parent.getData(H_COUNT)).intValue();
			int vCount = ((Integer) parent.getData(V_COUNT)).intValue();

			if (Gallery.DEBUG)
				System.out.println("hCount :  " + hCount + " vCount : " + vCount);

			int posX = index % hCount;
			int posY = (index - posX) / hCount;

			int xPixelPos = posX * (itemWidth + margin) + margin;
			int yPixelPos = posY * (itemHeight + minMargin) + minMargin + ((parent == null) ? 0 : (parent.y) + offset);

			return new Rectangle(xPixelPos, yPixelPos, this.itemWidth, this.itemHeight);
		}
		return null;
	}

	/**
	 * Get item at pixel position
	 * 
	 * @param coords
	 * @return
	 */
	protected GalleryItem getItem(GalleryItem group, Point coords, int offset) {
		if (Gallery.DEBUG)
			System.out.println("getitem " + coords.x + " " + coords.y);

		int itemNb;
		if (gallery.isVertical()) {
			Integer tmp = (Integer) group.getData(H_COUNT);
			if (tmp == null)
				return null;
			int hCount = tmp.intValue();

			// Calculate the "might be" position
			int posX = (coords.x - minMargin) / (itemWidth + margin);

			// Check if the users clicked on the X margin.
			if ((coords.x - minMargin) % (itemWidth + margin) > itemWidth) {
				return null;
			}

			if (posX >= hCount) // Nothing there
				return null;

			if (coords.y - group.y - minMargin < offset)
				return null;

			int posY = (coords.y - group.y - offset - minMargin) / (itemHeight + minMargin);

			// Check if the users clicked on the Y margin.
			if (((coords.y - group.y - offset - minMargin) % (itemHeight + minMargin)) > itemHeight) {
				return null;
			}
			itemNb = posX + posY * hCount;
		} else {
			Integer tmp = (Integer) group.getData(V_COUNT);
			if (tmp == null)
				return null;
			int vCount = tmp.intValue();

			// Calculate the "might be" position
			int posY = (coords.y - minMargin) / (itemHeight + margin);

			// Check if the users clicked on the X margin.
			if ((coords.y - minMargin) % (itemHeight + margin) > itemHeight) {
				return null;
			}

			if (posY >= vCount) // Nothing there
				return null;

			if (coords.x - group.x - minMargin < offset)
				return null;

			int posX = (coords.x - group.x - offset - minMargin) / (itemWidth + minMargin);

			// Check if the users clicked on the X margin.
			if (((coords.x - group.x - offset - minMargin) % (itemWidth + minMargin)) > itemWidth) {
				return null;
			}
			itemNb = posY + posX * vCount;
		}
		if (Gallery.DEBUG)
			System.out.println("Item found : " + itemNb);

		if (itemNb < group.getItemCount()) {
			return group.getItem(itemNb);
		}

		return null;
	}

	private GalleryItem goLeft(GalleryItem group, int pos) {
		pos--;
		if (pos < 0)
			return this.getFirstItem(this.getPreviousGroup(group), END);
		else
			return group.getItem(pos);
	}

	private GalleryItem goRight(GalleryItem group, int pos) {
		pos++;
		if (pos >= group.getItemCount())
			return this.getFirstItem(this.getNextGroup(group), START);
		else
			return group.getItem(pos);
	}

	private GalleryItem goUp(GalleryItem group, int pos, int hCount) {
		int colPos = pos % hCount;
		pos -= hCount;
		if (pos < 0)
			return this.getItemAt(this.getPreviousGroup(group), colPos, END);
		else
			return group.getItem(pos);
	}

	private GalleryItem goDown(GalleryItem group, int pos, int hCount) {
		int colPos = pos % hCount;
		pos += hCount;
		if (pos >= group.getItemCount())
			return this.getItemAt(this.getNextGroup(group), colPos, START);
		else
			return group.getItem(pos);

	}

	public GalleryItem getNextItem(GalleryItem item, int key) {

		if (item.getParentItem() == null) {
			// Key navigation is only available for child items ATM
			return null;
		}
		GalleryItem group = item.getParentItem();
		int pos = group.indexOf(item);
		GalleryItem next = null;

		if (gallery.isVertical()) {
			int hCount = ((Integer) group.getData(H_COUNT)).intValue();
			switch (key) {
			case SWT.ARROW_LEFT:
				next = goLeft(group, pos);
				break;

			case SWT.ARROW_RIGHT:
				next = goRight(group, pos);
				break;

			case SWT.ARROW_UP:
				next = goUp(group, pos, hCount);
				break;

			case SWT.ARROW_DOWN:
				next = goDown(group, pos, hCount);
				break;

			}
		} else {
			int vCount = ((Integer) group.getData(V_COUNT)).intValue();
			switch (key) {
			case SWT.ARROW_LEFT:
				next = goUp(group, pos, vCount);
				break;

			case SWT.ARROW_RIGHT:
				next = goDown(group, pos, vCount);
				break;

			case SWT.ARROW_UP:
				next = goLeft(group, pos);
				break;

			case SWT.ARROW_DOWN:
				next = goRight(group, pos);
				break;

			}
		}

		return next;
	}

	private GalleryItem getPreviousGroup(GalleryItem group) {
		int gPos = gallery.indexOf(group);
		while (gPos > 0) {
			GalleryItem newGroup = gallery.getItem(gPos - 1);
			if (newGroup.isExpanded())
				return newGroup;
			gPos--;
		}

		return null;
	}

	private GalleryItem getNextGroup(GalleryItem group) {
		int gPos = gallery.indexOf(group);
		while (gPos < gallery.getItemCount() - 1) {
			GalleryItem newGroup = gallery.getItem(gPos + 1);
			if (newGroup.isExpanded())
				return newGroup;
			gPos++;
		}

		return null;
	}

	private final int END = 0;

	private final int START = 1;

	private GalleryItem getFirstItem(GalleryItem group, int from) {
		if (group == null)
			return null;

		switch (from) {
		case END:
			return group.getItem(group.getItemCount() - 1);

		case START:
		default:
			return group.getItem(0);
		}

	}

	/**
	 * Return the child item of group which is at column 'pos' starting from
	 * direction. If this item doesn't exists, returns the nearest item.
	 * 
	 * @param group
	 * @param pos
	 * @param from
	 *            START or END
	 * @return
	 */
	private GalleryItem getItemAt(GalleryItem group, int pos, int from) {
		if (group == null)
			return null;

		int hCount = ((Integer) group.getData(H_COUNT)).intValue();
		int offset = 0;
		switch (from) {
		case END:
			// Last item column
			int endPos = group.getItemCount() % hCount;

			// If last item column is 0, the line is full
			if (endPos == 0) {
				endPos = hCount - 1;
				offset--;
			}

			// If there is an item at column 'pos'
			if (pos < endPos) {
				int nbLines = (group.getItemCount() / hCount) + offset;
				return group.getItem(nbLines * hCount + pos);
			}

			// Get the last item.
			return group.getItem((group.getItemCount() / hCount + offset) * hCount + endPos - 1);

		case START:
		default:
			if (pos >= group.getItemCount())
				return group.getItem(group.getItemCount() - 1);

			return group.getItem(pos);

		}

	}

}
