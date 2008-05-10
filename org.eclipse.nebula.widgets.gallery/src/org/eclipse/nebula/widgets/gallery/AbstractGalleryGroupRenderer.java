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

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

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
public abstract class AbstractGalleryGroupRenderer {

	protected Gallery gallery;

	protected boolean expanded;

	protected boolean drawVertically;

	public boolean isDrawVertically() {
		return this.drawVertically;
	}

	public void setDrawVertically(boolean drawVertically) {
		this.drawVertically = drawVertically;
	}

	/**
	 * Get the expand/collapse state of the current group
	 * 
	 * @return true is the current group is expanded
	 */
	public boolean isExpanded() {
		return this.expanded;
	}

	public void setExpanded(boolean selected) {
		this.expanded = selected;
	}

	/**
	 * This method is called before drawing the first item. It can be used to
	 * calculate some values (like font metrics) that will be used for each
	 * item.
	 * 
	 * @param gc
	 */
	public void preDraw(GC gc) {
		// Nothing required here. This method can be overridden when needed.
	}

	/**
	 * Group size informations can be retrieved from group. Clipping
	 * informations
	 * 
	 * @param gc
	 * @param group
	 * @param x
	 * @param y
	 */
	public abstract void draw(GC gc, GalleryItem group, int x, int y,
			int clipX, int clipY, int clipWidth, int clipHeight);

	public abstract void dispose();

	/**
	 * Returns the item that should be selected when the current item is 'item'
	 * and the 'key' is pressed
	 * 
	 * @param item
	 * @param key
	 * @return
	 */
	public abstract GalleryItem getNextItem(GalleryItem item, int key);

	/**
	 * This method is called before the layout of the first item. It can be used
	 * to calculate some values (like font metrics) that will be used for each
	 * item.
	 * 
	 * @param gc
	 */
	public void preLayout(GC gc) {
		// Nothing required here. This method can be overridden when needed.
	}

	/**
	 * This method is called on each root item when the Gallery changes (resize,
	 * item addition or removal) in order to update the gallery size.
	 * 
	 * The implementation must update the item internal size (px) using
	 * setGroupSize(item, size); before returning.
	 * 
	 * @param gc
	 * @param group
	 */
	public abstract void layout(GC gc, GalleryItem group);

	/**
	 * Returns the item at coords relative to the parent group.
	 * 
	 * @param group
	 * @param coords
	 * @return
	 */
	public abstract GalleryItem getItem(GalleryItem group, Point coords);

	/**
	 * Returns the size of a group.
	 * 
	 * @param item
	 * @return
	 */
	public abstract Rectangle getSize(GalleryItem item);

	public abstract boolean mouseDown(GalleryItem group, MouseEvent e,
			Point coords);

	public Gallery getGallery() {
		return this.gallery;
	}

	public void setGallery(Gallery gallery) {
		this.gallery = gallery;
	}

	protected Point getGroupSize(GalleryItem item) {
		return new Point(item.width, item.height);

	}

	protected Point getGroupPosition(GalleryItem item) {
		return new Point(item.x, item.y);
	}

	protected void setGroupSize(GalleryItem item, Point size) {
		item.width = size.x;
		item.height = size.y;
	}

	/**
	 * @return true if Debug mode is enabled
	 */
	protected boolean isDebugMode() {
		return Gallery.DEBUG;
	}

	/**
	 * Notifies the Gallery that the control expanded/collapsed state has
	 * changed.
	 * 
	 * @param group
	 */
	protected void notifyTreeListeners(GalleryItem group) {
		gallery.notifyTreeListeners(group, group.isExpanded());
	}

	/**
	 * Forces an update of
	 * 
	 * @param keeplocation
	 */
	protected void updateStructuralValues(boolean keeplocation) {
		gallery.updateStructuralValues(keeplocation);
	}

	protected void updateScrollBarsProperties() {
		gallery.updateScrollBarsProperties();
	}

}
