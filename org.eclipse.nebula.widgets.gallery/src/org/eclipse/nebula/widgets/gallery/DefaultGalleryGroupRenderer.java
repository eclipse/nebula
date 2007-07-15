/*******************************************************************************
 * Copyright (c) 2006-2007 Nicolas Richeton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors :
 *    Nicolas Richeton (nicolas.richeton@gmail.com) - initial API and implementation
 *    Richard Michalsky - bug 195443
 *******************************************************************************/
package org.eclipse.nebula.widgets.gallery;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * <p>
 * NOTE: THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT. THIS IS A
 * PRE-RELEASE ALPHA VERSION. USERS SHOULD EXPECT API CHANGES IN FUTURE
 * VERSIONS.
 * </p>
 * 
 * @author Nicolas Richeton (nicolas.richeton@gmail.com)
 * @contributor Richard Michalsky (bug 195443)
 * 
 */
public class DefaultGalleryGroupRenderer extends AbstractGridGroupRenderer {

	private int fontHeight = 0;

	private int titleHeight = fontHeight + 5;

	private int offset = minMargin + titleHeight;

	private Color titleForeground;

	private Color titleBackground;

	// True if margins have already been calculated. Prevents
	// margins calculation for each group
	boolean marginCalculated = false;

	private Font font = null;

	public DefaultGalleryGroupRenderer() {
		// Set defaults
		titleForeground = Display.getDefault().getSystemColor(SWT.COLOR_TITLE_FOREGROUND);
		titleBackground = Display.getDefault().getSystemColor(SWT.COLOR_TITLE_BACKGROUND);
	}

	void draw(GC gc, GalleryItem group, int x, int y, int clipX, int clipY, int clipWidth, int clipHeight) {
		// TODO: finish drawing.

		// Title background
		gc.setBackground(titleBackground);
		gc.fillRectangle(x, y, group.width, titleHeight);

		// Color for text
		gc.setForeground(titleForeground);

		// Title text
		String text = "";
		text += group.getText();
		text += " (" + group.getItemCount() + ")";
		gc.setFont(font);
		gc.drawText(text, x + titleHeight + 2, y + 2);

		// Toggle Button
		AbstractRenderer c = new TreeNodeToggleRenderer();
		c.setExpanded(expanded);

		int xShift = getShift(titleHeight, c.getSize().x);
		int yShift = getShift(titleHeight, c.getSize().y);
		c.setBounds(x + xShift, y + yShift, 100, 100);
		c.paint(gc, group);

		// Display item
		if (expanded) {
			int[] indexes = getVisibleItems(group, x, y, clipX, clipY, clipWidth, clipHeight, offset);

			if (indexes != null && indexes.length > 0) {
				for (int i = indexes.length - 1; i >= 0; i--) {

					boolean selected = group.isSelected(group.getItem(indexes[i]));
					if (Gallery.DEBUG)
						System.out.println("Selected : " + selected + " index : " + indexes[i] + "item : " + group.getItem(indexes[i]));
					drawItem(gc, indexes[i], selected, group, offset);

				}
			}
		}
	}

	void layout(GC gc, GalleryItem group) {

		int countLocal = group.getItemCount();

		if (gallery.isVertical()) {
			int sizeX = group.width;
			group.height = offset + 3 * minMargin;

			if (expanded) {
				Point l = gridLayout(sizeX, countLocal, itemWidth);
				int hCount = l.x;
				int vCount = l.y;

				if (autoMargin && hCount > 0) {

					// If margins have not been calculated
					if (!marginCalculated) {
						// Calculate best margins
						margin = calculateMargins(sizeX, hCount, itemWidth);

						marginCalculated = true;

						if (Gallery.DEBUG)
							System.out.println("margin " + margin);

					}

				}

				Point s = this.getSize(hCount, vCount, itemWidth, itemHeight, minMargin, margin);
				group.height += s.y;

				if (Gallery.DEBUG)
					System.out.println("group.height " + group.height);

				group.setData(H_COUNT, new Integer(hCount));
				group.setData(V_COUNT, new Integer(vCount));
				if (Gallery.DEBUG)
					System.out.println("Hnb" + hCount + "Vnb" + vCount);
			}

		} else {
			// Horizontal
			int sizeY = group.height;
			group.width = offset;

			Point l = gridLayout(sizeY, countLocal, itemHeight);
			int vCount = l.x;
			int hCount = l.y;
			if (autoMargin) {
				// Calculate best margins
				margin = calculateMargins(sizeY, vCount, itemHeight);
			}

			Point s = this.getSize(hCount, vCount, itemWidth, itemHeight, minMargin, margin);
			group.width += s.x;

			group.setData(H_COUNT, new Integer(hCount));
			group.setData(V_COUNT, new Integer(vCount));
		}

	}

	public void preDraw(GC gc) {
		pre(gc);
	}

	public void preLayout(GC gc) {
		this.marginCalculated = false;
		pre(gc);
		super.preLayout(gc);
	}

	private void pre(GC gc) {
		boolean gcCreated = false;
		if (gc == null) {
			gc = new GC(gallery, SWT.NONE);
			gcCreated = true;
		}

		// Get font height
		gc.setFont(font);
		fontHeight = gc.getFontMetrics().getHeight();

		// Compute title height & grid offset
		titleHeight = fontHeight + 5;
		offset = titleHeight + minMargin;

		if (gcCreated)
			gc.dispose();
	}

	private int getShift(int totalWidth, int width) {
		int xShift = totalWidth - width;
		if (xShift < 0)
			xShift = 0;
		xShift = xShift >> 1;
		return xShift;
	}

	public GalleryItem getItem(GalleryItem group, Point coords) {
		// Cannot select an item if the group is not expanded
		if (!group.isExpanded())
			return null;

		return super.getItem(group, coords, offset);
	}

	boolean mouseDown(GalleryItem group, MouseEvent e, Point coords) {

		if (coords.y - group.y <= titleHeight) {

			if (coords.x <= titleHeight) {
				// Toogle
				group.setExpanded(!group.isExpanded());
				if (!group.isExpanded()) {
					group.deselectAll();
				}
				gallery.updateStructuralValues(false);
				gallery.updateScrollBarsProperties();

			} else {
				if ((e.stateMask & SWT.MOD1) == 0) {
					gallery.deselectAll();
				}
				group.selectAll();
				gallery.notifySelectionListeners(group, gallery.indexOf(group));
			}
			gallery.redraw();
			return false;

		}
		return true;
	}

	public Rectangle getSize(GalleryItem item) {
		Rectangle r = super.getSize(item, offset);

		return r;
	}

	public Color getTitleForeground() {
		return titleForeground;
	}

	public void setTitleForeground(Color titleColor) {
		this.titleForeground = titleColor;
	}

	public Color getTitleBackground() {
		return titleBackground;
	}

	public void setTitleBackground(Color titleBackground) {
		this.titleBackground = titleBackground;
	}

	/**
	 * Returns the font used for drawing the group title or <tt>null</tt> if
	 * system font is used.
	 * 
	 * @return the font
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * Set the font for drawing the group title or <tt>null</tt> to use system
	 * font.
	 * 
	 * @param font
	 *            the font to set
	 */
	public void setFont(Font font) {
		if (this.font != font) {
			this.font = font;
			if (getGallery() != null)
				getGallery().redraw();
		}
	}
}
