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

package org.eclipse.nebula.paperclips.core.grid.internal;

import org.eclipse.nebula.paperclips.core.border.BorderPainter;
import org.eclipse.nebula.paperclips.core.grid.GridMargins;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

class DefaultGridMargins implements GridMargins {
	private final BorderPainter border;
	private final Point cellSpacing;
	private final Rectangle cellPadding;
	private final int headerClosedSpacing;
	private final int headerOpenSpacing;
	private final int footerClosedSpacing;
	private final int footerOpenSpacing;

	DefaultGridMargins(BorderPainter border, Point cellSpacing,
			Rectangle cellPadding, int headerClosedSpacing,
			int headerOpenSpacing, int footerClosedSpacing,
			int footerOpenSpacing) {
		this.border = border;
		this.cellSpacing = cellSpacing;
		this.cellPadding = cellPadding;
		this.headerClosedSpacing = headerClosedSpacing;
		this.headerOpenSpacing = headerOpenSpacing;
		this.footerClosedSpacing = footerClosedSpacing;
		this.footerOpenSpacing = footerOpenSpacing;
	}

	public int getLeft() {
		return border.getLeft() + cellPadding.x;
	}

	public int getHorizontalSpacing() {
		return cellSpacing.x + cellPadding.x + cellPadding.width;
	}

	public int getRight() {
		return border.getRight() + cellPadding.width;
	}

	public int getHeaderTop() {
		return border.getTop(false) + cellPadding.y;
	}

	public int getHeaderVerticalSpacing() {
		return cellSpacing.y + cellPadding.y + cellPadding.height;
	}

	public int getBodyTop(boolean headerPresent, boolean open) {
		return headerPresent ? open ? headerOpenSpacing : headerClosedSpacing
				+ cellPadding.y : open ? border.getTop(true) : border
				.getTop(false)
				+ cellPadding.y;
	}

	public int getBodyVerticalSpacing() {
		return cellSpacing.y + cellPadding.y + cellPadding.height;
	}

	public int getBodyBottom(boolean footerPresent, boolean open) {
		return footerPresent ? open ? footerOpenSpacing : footerClosedSpacing
				+ cellPadding.height : open ? border.getBottom(true) : border
				.getBottom(false)
				+ cellPadding.height;
	}

	public int getFooterVerticalSpacing() {
		return cellSpacing.y + cellPadding.y + cellPadding.height;
	}

	public int getFooterBottom() {
		return border.getBottom(false) + cellPadding.height;
	}
}