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

import org.eclipse.nebula.paperclips.core.PaperClips;
import org.eclipse.nebula.paperclips.core.PrintIterator;
import org.eclipse.nebula.paperclips.core.PrintPiece;
import org.eclipse.nebula.paperclips.core.border.BorderPainter;
import org.eclipse.nebula.paperclips.core.border.BorderPrint;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

public class BorderIterator implements PrintIterator {
	private final BorderPainter border;

	private PrintIterator target;
	private boolean opened;

	public BorderIterator(BorderPrint print, Device device, GC gc) {
		this.border = print.getBorder().createPainter(device, gc);

		this.target = print.getTarget().iterator(device, gc);
		this.opened = false;
	}

	public BorderIterator(BorderIterator that) {
		this.border = that.border;

		this.target = that.target.copy();
		this.opened = that.opened;
	}

	public boolean hasNext() {
		return target.hasNext();
	}

	public Point minimumSize() {
		return addBorderMargin(target.minimumSize());
	}

	public Point preferredSize() {
		return addBorderMargin(target.preferredSize());
	}

	private Point addBorderMargin(Point targetSize) {
		return new Point(targetSize.x + border.getWidth(), targetSize.y
				+ border.getMaxHeight());
	}

	public PrintPiece next(int width, int height) {
		if (!hasNext())
			PaperClips.error("No more content"); //$NON-NLS-1$

		PrintPiece piece = next(width, height, false /* closed bottom border */);

		if (piece == null)
			piece = next(width, height, true /* open bottom border */);

		if (piece != null)
			opened = true;

		return piece;
	}

	private PrintPiece next(int width, int height, boolean bottomBorderOpen) {
		// Adjust iteration area for border dimensions.
		width -= border.getWidth();
		height -= border.getHeight(opened, bottomBorderOpen);
		if (width < 0 || height < 0)
			return null;

		PrintIterator iter = target.copy();
		PrintPiece piece = PaperClips.next(iter, width, height);
		if (piece == null)
			return null;

		if (bottomBorderOpen && !iter.hasNext()) {
			// The target content was consumed, but the bottom border is open
			// (suggesting that there is more
			// content): find the largest piece that *doesn't* consume all the
			// target's content, and show it with
			// an open bottom border.
			piece.dispose();
			piece = getTallestPieceNotCompletelyConsumingTarget(width, height);
			if (piece == null)
				return null;
		} else if (!bottomBorderOpen && iter.hasNext()) {
			// Bottom border is closed but the target has more content: fail so
			// calling method can try again with
			// an open bottom border.
			piece.dispose();
			return null;
		} else {
			this.target = iter;
		}

		// Decorate the target print piece with border
		piece = new BorderPiece(piece, border, opened, bottomBorderOpen);

		return piece;
	}

	private PrintPiece getTallestPieceNotCompletelyConsumingTarget(
			final int width, final int height) {
		int low = 0;
		int high = height - 1;

		PrintIterator bestIterator = null;
		PrintPiece bestPiece = null;
		while (low + 1 < high) {
			int testHeight = (low + high + 1) / 2;

			PrintIterator testIterator = target.copy();
			PrintPiece testPiece = PaperClips.next(testIterator, width,
					testHeight);

			if (testPiece == null) {
				low = testHeight + 1;
			} else if (testIterator.hasNext()) {
				low = testHeight;

				if (bestPiece != null)
					bestPiece.dispose();
				bestIterator = testIterator;
				bestPiece = testPiece;
			} else { // !testIterator.hasNext()
				high = testPiece.getSize().y - 1;
			}
		}

		if (bestPiece != null)
			this.target = bestIterator;
		return bestPiece;
	}

	public PrintIterator copy() {
		return new BorderIterator(this);
	}
}