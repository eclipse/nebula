/*
 * Copyright (c) 2006 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */
package org.eclipse.nebula.paperclips.core.internal.piece;

import org.eclipse.nebula.paperclips.core.PrintPiece;
import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * A blank PrintPiece of a predetermined size
 * 
 * @author matt
 */
public class EmptyPiece implements PrintPiece {
	private final Point size;

	/**
	 * @param size
	 */
	public EmptyPiece(Point size) {
		Util.notNull(size);
		this.size = size;
	}

	public Point getSize() {
		return new Point(size.x, size.y);
	}

	public void paint(GC gc, int x, int y) {
		// Nothing to paint
	}

	public void dispose() {
		// Nothing to dispose
	}
}