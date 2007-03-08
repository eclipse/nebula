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

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * NoGroup Renderer <br/> This group renderer does not draw group decoration.
 * Only items are displayed.<br/> All groups are considered as expanded<br/>
 * The visual aspect is the same as the first version of the gallery widget.<br/><br/>
 * 
 * <p>
 * NOTE: THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT. THIS IS A
 * PRE-RELEASE ALPHA VERSION. USERS SHOULD EXPECT API CHANGES IN FUTURE
 * VERSIONS.
 * </p>
 * 
 * @author Nicolas Richeton (nicolas.richeton@gmail.com)
 */
public class NoGroupRenderer extends AbstractGridGroupRenderer {

	static int OFFSET = 0;


	void draw(GC gc, GalleryItem group, int x, int y, int clipX, int clipY, int clipWidth, int clipHeight) {

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

	
	void layout(GC gc, GalleryItem group) {

		int countLocal = group.getItemCount();

		if (gallery.isVertical()) {
			int sizeX = group.width;
			group.height = OFFSET;

			Point l = gridLayout(sizeX, countLocal, itemWidth);
			int hCount = l.x;
			int vCount = l.y;
			if (autoMargin) {
				// Calculate best margins
				margin = calculateMargins(sizeX, hCount, itemWidth);
			}

			Point s = this.getSize(hCount, vCount, itemWidth, itemHeight, minMargin, margin);
			group.height += s.y;

			group.setData(H_COUNT, new Integer(hCount));
			group.setData(V_COUNT, new Integer(vCount));
		}

	}

	public GalleryItem getItem(GalleryItem group, Point coords) {
		return super.getItem(group, coords, OFFSET);
	}


	boolean mouseDown(GalleryItem group, MouseEvent e, Point coords) {
		// Do nothing
		return true;
	}

	public Rectangle getSize( GalleryItem item){
		return super.getSize(item, OFFSET);	
		}
}
