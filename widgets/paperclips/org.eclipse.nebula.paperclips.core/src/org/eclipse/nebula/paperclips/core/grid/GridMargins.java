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
package org.eclipse.nebula.paperclips.core.grid;

/**
 * An interface for informing a GridPrint what cell margins to use for the
 * GridLook.
 * 
 * @author Matthew Hall
 */
public interface GridMargins {
	/**
	 * Returns the margin, in pixels, at the left side of the grid.
	 * 
	 * @return the margin, in pixels, at the left side of the grid.
	 */
	public int getLeft();

	/**
	 * Returns the horizontal spacing, in pixels, between grid cells.
	 * 
	 * @return the horizontal spacing, in pixels, between grid cells.
	 */
	public int getHorizontalSpacing();

	/**
	 * Returns the margin, in pixels, at the right side of the grid.
	 * 
	 * @return the margin, in pixels, at the right side of the grid.
	 */
	public int getRight();

	/**
	 * Returns the margin, in pixels, at the top of the header cells. If a grid
	 * has no header cells, this value is ignored.
	 * 
	 * @return the margin, in pixels, at the top of the header cells.
	 */
	public int getHeaderTop();

	/**
	 * Returns the vertical spacing, in pixels, between rows in the header.
	 * 
	 * @return the vertical spacing, in pixels, between rows in the header.
	 */
	public int getHeaderVerticalSpacing();

	/**
	 * Returns the margin, in pixels, at the top of the body cells. If a header
	 * is present, this is the spacing, in pixels, between the last header row
	 * and the first body row. If a header is not present, this is the margin,
	 * in pixels, at the top of the grid.
	 * 
	 * @param headerPresent
	 *            whether a header is present.
	 * @param open
	 *            whether the top row of body cells are "open." That is, whether
	 *            the top row was started on a previous page and is continuing
	 *            on this page. A GridLook may choose to show a visual
	 *            indication for cells that were "opened" on previous pages.
	 * @return the margin, in pixels, at the top of the body cells.
	 */
	public int getBodyTop(boolean headerPresent, boolean open);

	/**
	 * Returns the vertical spacing, in pixels, between rows in the body.
	 * 
	 * @return the vertical spacing, in pixels, between rows in the body.
	 */
	public int getBodyVerticalSpacing();

	/**
	 * Returns the margin, in pixels, at the bottom of the body cells. If a
	 * footer is present, this is the spacing, in pixels, between the last body
	 * row and the first footer row. If a header is not present, this is the
	 * margin, in pixels, at the bottom of the grid.
	 * 
	 * @param footerPresent
	 *            whether a footer is present.
	 * @param open
	 *            whether the bottom row of body cells are "open." That is,
	 *            whether the bottom row still has more content to display on
	 *            the next page. A GridLook may choose to show a visual
	 *            indication for cells that will be "continued" on the next
	 *            page.
	 * @return the margin, in pixels, at the bottom of the body cells.
	 */
	public int getBodyBottom(boolean footerPresent, boolean open);

	/**
	 * Returns the vertical spacing, in pixels, between rows in the footer.
	 * 
	 * @return the vertical spacing, in pixels, between rows in the footer.
	 */
	public int getFooterVerticalSpacing();

	/**
	 * Returns the margin, in pixels, at the bottom of the footer cells. If a
	 * grid has no footer cells, this value is ignored.
	 * 
	 * @return the margin, in pixels, at the bottom of the footer cells.
	 */
	public int getFooterBottom();
}
