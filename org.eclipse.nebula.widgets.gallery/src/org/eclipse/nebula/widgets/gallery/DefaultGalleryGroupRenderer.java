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

import org.eclipse.swt.SWT;
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
public class DefaultGalleryGroupRenderer extends AbstractGridGroupRenderer {

	private int fontHeight = 0;

	private int titleHeight = fontHeight + 5;

	private int offset = minMargin + titleHeight;

	// True if margins have already been calculated. Prevents
	// margins calculation for each group
	boolean marginCalculated = false;

	void dispose() {

	}

	void draw(GC gc, GalleryItem group, int x, int y, int clipX, int clipY, int clipWidth, int clipHeight) {
		// TODO: finish drawing.

		// Title background
		gc.setBackground(gallery.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND));
		gc.fillRoundRectangle(x, y, group.width, titleHeight, 10, 10);

		// Color for text
		gc.setForeground(gallery.getDisplay().getSystemColor(SWT.COLOR_TITLE_FOREGROUND));

		// Title text
		String text = "";
		text += group.getText();
		text += " (" + group.getItemCount() + ")";
		gc.drawText(text, x + 2 + 16, y + 2);

		// Toggle Button
		text = expanded ? "-" : "+";
		gc.drawText(text, x + 2, y + 2);

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
			// gc.setFont(gallery.getDisplay().getSystemFont());
		}
		// Get font height
		fontHeight = gc.getFontMetrics().getHeight();

		// Compute title height & grid offset
		titleHeight = fontHeight + 5;
		offset = titleHeight + minMargin;

		if (gcCreated)
			gc.dispose();
	}

	public GalleryItem getItem(GalleryItem group, Point coords) {
		return super.getItem(group, coords, offset);
	}

	boolean mouseDown(GalleryItem group, MouseEvent e, Point coords) {

		if (coords.y - group.y <= titleHeight) {

			if (coords.x <= 20) {
				// Toogle 
				group.setExpanded(!group.isExpanded());
				if (!group.isExpanded()) {
					group.deselectAll();
				}
				gallery.updateStructuralValues(false);
				gallery.updateScrollBarsProperties();

			} else {
				group.selectAll();
				gallery.notifySelectionListeners(group, gallery.indexOf(group));
			}
			gallery.redraw();
			return false;

		}
		return true;
	}

	public Rectangle getSize(GalleryItem item) {
		Rectangle r =super.getSize(item, offset) ;
		
		return r;
	}
}
