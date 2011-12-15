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
package org.eclipse.nebula.paperclips.core;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * A piece of a Print, which is capable of drawing itself on a graphics device.
 * PrintPiece objects are created by a PrintIterator.
 * 
 * @author Matthew
 */
public interface PrintPiece {
	/**
	 * Returns the dimensions of this PrintPiece, in pixels.
	 * 
	 * @return the dimensions of this PrintPiece, in pixels.
	 */
	public Point getSize();

	/**
	 * Draws this PrintPiece on the given graphics device, at the given
	 * coordinates.
	 * 
	 * @param gc
	 *            a graphics context for the graphics device.
	 * @param x
	 *            the x coordinate where this PrintPiece will be drawn.
	 * @param y
	 *            the x coordinate where this PrintPiece will be drawn.
	 */
	public void paint(GC gc, int x, int y);

	/**
	 * Disposes the system resources allocated by this PrintPiece. The dispose
	 * method is <b>not</b> a permanent disposal of a PrintPiece. It is intended
	 * to reclaim system resources, however future calls to paint(GC,int,int)
	 * may require that the resources be allocated again.
	 */
	public void dispose();
}
