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

	static int OFFSET = 30;

	// True if margins have already been calculated. Prevents
	// margins calculation for each group
	boolean marginCalculated = false;

	void dispose() {

	}

	void draw(GC gc, GalleryItem group, int x, int y, int clipX, int clipY, int clipWidth, int clipHeight) {
		// TODO: finish drawing.

		gc.setBackground(gallery.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND));

		gc.fillRoundRectangle(x, y, group.width, this.fontHeight + 5, 10, 10);
		String text = null;
		if (expanded)
			text = "- ";
		else
			text = "+ ";

		text += group.getText();
		text += " (" + group.getItemCount() + ")";

		gc.setForeground(gallery.getDisplay().getSystemColor(SWT.COLOR_TITLE_FOREGROUND));
		gc.drawText(text, x + 2, y + 2);

		if (expanded) {
			int[] indexes = getVisibleItems(group, x, y, clipX, clipY, clipWidth, clipHeight, OFFSET);

			if (indexes != null && indexes.length > 0) {
				for (int i = indexes.length - 1; i >= 0; i--) {

					boolean selected = group.isSelected(group.getItem(indexes[i]));
					if (Gallery.DEBUG)
						System.out.println("Selected : " + selected + " index : " + indexes[i] + "item : " + group.getItem(indexes[i]));
					drawItem(gc, indexes[i], selected, group, OFFSET);

				}
			}
		}
	}

	void layout(GC gc, GalleryItem group) {

		int countLocal = group.getItemCount();

		if (gallery.isVertical()) {
			int sizeX = group.width;
			group.height = getTitleHeight() + OFFSET;

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

	private int getTitleHeight() {
		return fontHeight + 10;
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

		if (gcCreated)
			gc.dispose();
	}

	public GalleryItem getItem(GalleryItem group, Point coords) {
		//
		return super.getItem(group, coords, OFFSET);
	}

	boolean mouseDown(GalleryItem group, MouseEvent e, Point coords) {
		// TODO Auto-generated method stub

		if (coords.y - group.y <= OFFSET) {
			group.setExpanded(!group.isExpanded());
			if (!group.isExpanded()) {
				group.deselectAll();
			}
			gallery.updateStructuralValues(false);
			gallery.updateScrollBarsProperties();
			gallery.redraw();
			return false;
		}
		return true;
	}

}
