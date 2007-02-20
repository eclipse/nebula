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

import org.eclipse.swt.widgets.Item;

/**
 * Gallery Item<br/>
 * 
 * <p>
 * NOTE:  THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT.  THIS IS A PRE-RELEASE ALPHA 
 * VERSION.  USERS SHOULD EXPECT API CHANGES IN FUTURE VERSIONS.
 * </p> 
 *
 * @author Nicolas Richeton (nicolas.richeton@gmail.com)
 */
public class GalleryItem extends Item {

	public GalleryItem(Gallery parent, int style) {
		super(parent, style);
		parent.addItem(this);
	}

	public GalleryItem(GalleryGroup parent, int style) {
		super(parent, style);
		parent.addItem(this);
	}

}
