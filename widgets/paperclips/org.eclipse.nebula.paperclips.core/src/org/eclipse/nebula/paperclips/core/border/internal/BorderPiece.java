/*
 * Copyright (c) 2007 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */

package org.eclipse.nebula.paperclips.core.border.internal;

import org.eclipse.nebula.paperclips.core.PrintPiece;
import org.eclipse.nebula.paperclips.core.border.BorderPainter;
import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

public class BorderPiece implements PrintPiece {
	private final PrintPiece target;

	private final BorderPainter border;

	private final boolean topOpen;

	private final boolean bottomOpen;

	private final Point size;

	public BorderPiece(PrintPiece target, BorderPainter border,
			boolean topOpen, boolean bottomOpen) {
		Util.notNull(target, border);
		this.target = target;
		this.border = border;

		this.topOpen = topOpen;
		this.bottomOpen = bottomOpen;

		Point targetSize = target.getSize();
		this.size = new Point(targetSize.x + border.getWidth(), targetSize.y
				+ border.getHeight(topOpen, bottomOpen));
	}

	public Point getSize() {
		return new Point(size.x, size.y);
	}

	public void paint(GC gc, int x, int y) {
		border.paint(gc, x, y, size.x, size.y, topOpen, bottomOpen);
		target.paint(gc, x + border.getLeft(), y + border.getTop(topOpen));
	}

	public void dispose() {
		border.dispose();
		target.dispose();
	}
}