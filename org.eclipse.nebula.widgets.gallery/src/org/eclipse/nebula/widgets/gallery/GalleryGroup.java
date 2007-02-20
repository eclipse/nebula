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

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Item;

/**
 * Gallery Group
 * 
 * <p>
 * NOTE:  THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT.  THIS IS A PRE-RELEASE ALPHA 
 * VERSION.  USERS SHOULD EXPECT API CHANGES IN FUTURE VERSIONS.
 * </p> 
 *
 * @author Nicolas Richeton (nicolas.richeton@gmail.com)
 */
public class GalleryGroup extends Item {

	Vector children = null;

	/**
	 * itemCount stores the number of children of this group. It is used when
	 * the Gallery was created with SWT.VIRTUAL
	 */
	int itemCount = 0;

	/**
	 * True if the Gallery was created wih SWT.VIRTUAL
	 */
	boolean virtualGallery;

	public GalleryGroup(Gallery parent, int style) {
		super(parent, style);

		if ((parent.getStyle() & SWT.VIRTUAL) > 0) {
			virtualGallery = true;
		} else {
			children = new Vector();
			parent.addItem(this);
		}
	}

	/**
	 * Only work when the table was created with SWT.VIRTUAL
	 * 
	 * @param item
	 */
	protected void addItem(GalleryItem item) {
		if (virtualGallery) {
			children.add(item);
		}
	}

	public int getItemCount() {
		if (virtualGallery)
			return itemCount;
		return children.size();
	}

	/**
	 * Only work when the table was created with SWT.VIRTUAL
	 * 
	 * @param itemCount
	 */
	public void setItemCount(int itemCount) {
		if (virtualGallery)
			this.itemCount = itemCount;
	}

}
