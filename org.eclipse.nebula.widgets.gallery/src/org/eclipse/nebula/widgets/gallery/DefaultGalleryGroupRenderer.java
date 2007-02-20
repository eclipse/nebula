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

import org.eclipse.swt.graphics.GC;

/**
 * <p>
 * NOTE:  THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT.  THIS IS A PRE-RELEASE ALPHA 
 * VERSION.  USERS SHOULD EXPECT API CHANGES IN FUTURE VERSIONS.
 * </p> 
 *
 * @author Nicolas Richeton (nicolas.richeton@gmail.com)
 */
public class DefaultGalleryGroupRenderer extends AbstractGalleryGroupRenderer {

	@Override
	void dispose() {

	}

	@Override
	void draw(GC gc, GalleryGroup group, int x, int y, int width, int height) {
		// TODO: finish drawing.
		String text = null;
		if (expanded)
			text = "- ";
		else
			text = "+ ";

		text += group.getText();
		gc.drawText(text, x, y);
	}

	@Override
	int getHeight(GC gc, GalleryGroup group) {

		// Get font height
		int height = gc.getFontMetrics().getHeight();

		// Add some space for separator.
		height += 16;

		return height;
	}

}
