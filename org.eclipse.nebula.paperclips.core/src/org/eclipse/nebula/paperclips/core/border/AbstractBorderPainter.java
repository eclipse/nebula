/*
 * Copyright (c) 2005 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */
package org.eclipse.nebula.paperclips.core.border;

import org.eclipse.swt.graphics.GC;

/**
 * Abstract implementation of BorderPainter providing implementation of helper
 * methods.
 * 
 * @author Matthew Hall
 */
public abstract class AbstractBorderPainter implements BorderPainter {
	/**
	 * Paints a border around the specified region. Depending on the type of
	 * border, the top and bottom of may be painted differently depending on the
	 * values of <code>topOpen</code> and <code>bottomOpen</code>.
	 */
	public abstract void paint(GC gc, int x, int y, int width, int height,
			boolean topOpen, boolean bottomOpen);

	/**
	 * Returns the border inset, in pixels, from the left.
	 */
	public abstract int getLeft();

	/**
	 * Returns the border inset, in pixels, from the right.
	 */
	public abstract int getRight();

	/**
	 * Returns the sum of the left and right border insets.
	 */
	public final int getWidth() {
		return getLeft() + getRight();
	}

	/**
	 * Returns the border inset, in pixels, from the top.
	 */
	public abstract int getTop(boolean open);

	/**
	 * Returns the border inset, in pixels, from the bottom.
	 */
	public abstract int getBottom(boolean open);

	/**
	 * Returns the sum of the top and bottom border insets.
	 */
	public final int getHeight(boolean topOpen, boolean bottomOpen) {
		return getTop(topOpen) + getBottom(bottomOpen);
	}

	/**
	 * Returns the sum of the maximum top and bottom border insets.
	 */
	public final int getMaxHeight() {
		return Math.max(getTop(false), getTop(true))
				+ Math.max(getBottom(false), getBottom(true));
	}
}
