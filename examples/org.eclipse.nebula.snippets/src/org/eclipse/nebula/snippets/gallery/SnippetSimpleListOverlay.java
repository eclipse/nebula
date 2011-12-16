/*******************************************************************************
 * Copyright (c) 2006-2007 Nicolas Richeton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors :
 *    Nicolas Richeton (nicolas.richeton@gmail.com) - initial implementation
 *******************************************************************************/
package org.eclipse.nebula.snippets.gallery;

import org.eclipse.nebula.widgets.gallery.AbstractGalleryItemRenderer;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryGroupRenderer;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryItemRenderer;
import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.nebula.widgets.gallery.ListItemRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This widget displays a simple gallery with some content.<br/>
 * Scrolling is vertical.<br/>
 * <br/>
 * 
 * <p>
 * NOTE: THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT. THIS IS A
 * PRE-RELEASE ALPHA VERSION. USERS SHOULD EXPECT API CHANGES IN FUTURE
 * VERSIONS.
 * </p>
 * 
 * @author Nicolas Richeton (nicolas.richeton@gmail.com)
 */

public class SnippetSimpleListOverlay {

	public static void main(String[] args) {
		Display display = new Display();
		Image[] itemImages = {
				new Image(display, Program.findProgram("jpg").getImageData()), //$NON-NLS-1$
				new Image(display, Program.findProgram("mov").getImageData()), //$NON-NLS-1$
				new Image(display, Program.findProgram("mp3").getImageData()), //$NON-NLS-1$
				new Image(display, Program.findProgram("txt").getImageData()) }; //$NON-NLS-1$

		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		Gallery gallery = new Gallery(shell, SWT.V_SCROLL | SWT.MULTI);

		// Renderers
		DefaultGalleryGroupRenderer gr = new DefaultGalleryGroupRenderer();
		gr.setMinMargin(2);
		gr.setItemHeight(82);
		gr.setItemWidth(200);
		gr.setAutoMargin(true);
		gallery.setGroupRenderer(gr);

		ListItemRenderer ir = new ListItemRenderer();
		gallery.setItemRenderer(ir);

		for (int g = 0; g < 2; g++) {
			GalleryItem group = new GalleryItem(gallery, SWT.NONE);
			group.setText("Group " + g); //$NON-NLS-1$
			group.setExpanded(true);

			for (int i = 0; i < 50; i++) {
				GalleryItem item = new GalleryItem(group, SWT.NONE);
				if (itemImages[0] != null) {
					item.setImage(itemImages[0]);
				}
				item.setText("Item " + i); //$NON-NLS-1$
				Image[] over = { itemImages[0] };
				Image[] over2 = { itemImages[1], itemImages[2] };
				Image[] over3 = { itemImages[3] };
				item.setData(AbstractGalleryItemRenderer.OVERLAY_BOTTOM_RIGHT,
						over3);
				item.setData(AbstractGalleryItemRenderer.OVERLAY_BOTTOM_LEFT,
						over);
				item.setData(AbstractGalleryItemRenderer.OVERLAY_TOP_RIGHT,
						over);
				item.setData(AbstractGalleryItemRenderer.OVERLAY_TOP_LEFT,
						over2);

			}
		}

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		for (int i = 0; i < itemImages.length; i++) {
			itemImages[i].dispose();
		}
		
		display.dispose();
	}
}
