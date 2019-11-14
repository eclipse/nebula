/*******************************************************************************
 * Copyright (c) 2012 Laurent CARON.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent CARON (laurent.caron@gmail.com) - initial API and
 * implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.starrating;

import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

/**
 * Instances of this class represent a star displayed by the StarRating
 * component
 */
class Star {
	private static final String SMALL_STAR_MARKED_FOCUS = "mark-focus16.png";
	private static final String SMALL_STAR_MARKED = "mark16.png";
	private static final String SMALL_STAR_FOCUS = "focus16.png";
	private static final String SMALL_STAR = "16.png";
	private static final String BIG_STAR_MARKED_FOCUS = "mark-focus32.png";
	private static final String BIG_STAR_MARKED = "mark32.png";
	private static final String BIG_STAR_FOCUS = "focus32.png";
	private static final String BIG_STAR = "32.png";
	boolean hover;
	boolean marked;
	Rectangle bounds;
	Image defaultImage;
	Image hoverImage;
	Image selectedImage;
	Image selectedHoverImage;
	private StarRating parent;

	void dispose() {
		defaultImage.dispose();
		hoverImage.dispose();
		selectedImage.dispose();
		selectedHoverImage.dispose();
	}

	void draw(final GC gc, final int x, final int y) {
		Image image;
		if (!parent.isEnabled()) {
			image = defaultImage;
		} else {
			if (marked) {
				if (hover) {
					image = selectedHoverImage;
				} else {
					image = selectedImage;
				}
			} else {
				if (hover) {
					image = hoverImage;
				} else {
					image = defaultImage;
				}
			}
		}

		gc.drawImage(image, x, y);
		bounds = new Rectangle(x, y, image.getBounds().width, image.getBounds().height);
	}

	static Star initBig(final StarRating parent) {
		final Star star = new Star();
		star.parent = parent;
		star.defaultImage = SWTGraphicUtil.createImageFromFile("images/stars/" + BIG_STAR);
		star.hoverImage = SWTGraphicUtil.createImageFromFile("images/stars/" + BIG_STAR_FOCUS);
		star.selectedImage = SWTGraphicUtil.createImageFromFile("images/stars/" + BIG_STAR_MARKED);
		star.selectedHoverImage = SWTGraphicUtil.createImageFromFile("images/stars/" + BIG_STAR_MARKED_FOCUS);
		return star;
	}

	static Star initSmall(final StarRating parent) {
		final Star star = new Star();
		star.parent = parent;
		star.defaultImage = SWTGraphicUtil.createImageFromFile("images/stars/" + SMALL_STAR);
		star.hoverImage = SWTGraphicUtil.createImageFromFile("images/stars/" + SMALL_STAR_FOCUS);
		star.selectedImage = SWTGraphicUtil.createImageFromFile("images/stars/" + SMALL_STAR_MARKED);
		star.selectedHoverImage = SWTGraphicUtil.createImageFromFile("images/stars/" + SMALL_STAR_MARKED_FOCUS);
		return star;
	}
}
