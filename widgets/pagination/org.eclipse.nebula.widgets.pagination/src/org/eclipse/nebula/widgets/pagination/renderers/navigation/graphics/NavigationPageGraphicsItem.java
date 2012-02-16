/*******************************************************************************
 * Copyright (C) 2011 Angelo Zerr <angelo.zerr@gmail.com>, Pascal Leclercq <pascal.leclercq@gmail.com>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo ZERR - initial API and implementation
 *     Pascal Leclercq - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.pagination.renderers.navigation.graphics;

import org.eclipse.nebula.widgets.pagination.PaginationHelper;
import org.eclipse.nebula.widgets.pagination.Resources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Item;

/**
 * Navigation page item (ex Previous, Next, page links etc).
 * 
 */
public class NavigationPageGraphicsItem extends Item {

	public static final int PREVIOUS = PaginationHelper.SEPARATOR - 1;
	public static final int NEXT = PaginationHelper.SEPARATOR - 2;
	private final int index;

	private Rectangle bounds;
	private boolean enabled;

	public NavigationPageGraphicsItem(NavigationPageGraphics parent, int index) {
		super(parent, SWT.NONE);
		this.index = index;
		boolean enabled = true;
		if (isSeparator()) {
			enabled = false;
		} else {
			super.setText((index + 1) + "");
		}
		setEnabled(enabled);
	}

	/**
	 * Returns the index of the page item.
	 * 
	 * @return
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Set bounds of the item.
	 * 
	 * @param bounds
	 */
	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	/**
	 * Returns <code>true</code> if the point specified by the arguments is
	 * inside the area specified by the receiver, and <code>false</code>
	 * otherwise.
	 * 
	 * @param x
	 *            the x coordinate of the point to test for containment
	 * @param y
	 *            the y coordinate of the point to test for containment
	 * @return <code>true</code> if the rectangle contains the point and
	 *         <code>false</code> otherwise
	 */
	public boolean contains(int x, int y) {
		if (bounds == null) {
			return false;
		}
		return bounds.contains(x, y);
	}

	/**
	 * Returns the bounds for the item. It can be null if bounds was not
	 * computed.
	 * 
	 * @return
	 */
	public Rectangle getBounds() {
		return bounds;
	}

	/**
	 * Returns true if the item is "..." and false otherwise.
	 * 
	 * @return
	 */
	public boolean isSeparator() {
		return index == PaginationHelper.SEPARATOR;
	}

	/**
	 * Returns true if item is Previous and false otherwise.
	 * 
	 * @return
	 */
	public boolean isPrevious() {
		return index == PREVIOUS;
	}

	/**
	 * Returns true if item is Next and false otherwise.
	 * 
	 * @return
	 */
	public boolean isNext() {
		return index == NEXT;
	}

	/**
	 * Set enabled of the item.o
	 * 
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Returns the enabled of the item.
	 * 
	 * @return
	 */
	public boolean isEnabled() {
		return enabled;
	}
}
