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
 * 
 * <p>
 * NOTE:  THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT.  THIS IS A PRE-RELEASE ALPHA 
 * VERSION.  USERS SHOULD EXPECT API CHANGES IN FUTURE VERSIONS.
 * </p> 
 *
 * @author Nicolas Richeton (nicolas.richeton@gmail.com)
 */
public abstract class AbstractGalleryGroupRenderer {

	protected boolean expanded;

	protected boolean drawVertically;

	public boolean isDrawVertically() {
		return drawVertically;
	}

	public void setDrawVertically(boolean drawVertically) {
		this.drawVertically = drawVertically;
	}

	/**
	 * true is the current group is expanded
	 * 
	 * @return
	 */
	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean selected) {
		this.expanded = selected;
	}

	abstract int getHeight(GC gc, GalleryGroup group);

	abstract void draw(GC gc, GalleryGroup group, int x, int y, int width, int height);

	abstract void dispose();

}
