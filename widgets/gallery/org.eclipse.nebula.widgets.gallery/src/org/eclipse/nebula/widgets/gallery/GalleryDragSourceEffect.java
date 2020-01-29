/*******************************************************************************
 * Copyright (c) 2006-2007 Nicolas Richeton.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors :
 *    Nicolas Richeton (nicolas.richeton@gmail.com) - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.gallery;

import org.eclipse.swt.dnd.DragSourceEffect;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.graphics.Image;

/**
 * <p>
 * Visual effect for drag and drop operators on GalleryItem. This effect has to
 * be set in
 * {@link org.eclipse.swt.dnd.DragSource#setDragSourceEffect(DragSourceEffect)}
 * </p>
 * 
 * <p>
 * NOTE: THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT.
 * </p>
 * 
 * @see org.eclipse.swt.dnd.DragSource#setDragSourceEffect(DragSourceEffect)
 * 
 * @author Nicolas Richeton (nicolas.richeton@gmail.com)
 */
public class GalleryDragSourceEffect extends DragSourceEffect {
	Gallery g = null;

	/**
	 * Creates the drag source effect.
	 * 
	 * @param gallery
	 */
	public GalleryDragSourceEffect(Gallery gallery) {
		super(gallery);
		g = gallery;
	}

	/**
	 * @seeorg.eclipse.swt.dnd.DragSourceAdapter#dragStart(org.eclipse.swt.dnd. DragSourceEvent)
	 */
	public void dragStart(DragSourceEvent event) {
		GalleryItem[] selection = g.getSelection();
		if (selection != null && selection.length > 0) {
			Image img = selection[0].getImage();
			if (img != null) {
				event.image = img;
			}
		}
	}
}
