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

import org.eclipse.nebula.widgets.gallery.DefaultGalleryGroupRenderer;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryItemRenderer;
import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
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

public class SnippetSimpleGroupImage {

	private static final String ICON_FILE = "icon.png"; //$NON-NLS-1$

	public static void main(String[] args) {
		Display display = new Display();
		Image itemImage;
		if (SnippetSimpleGroupImage.class.getResource(ICON_FILE) != null) {
			itemImage = new Image(display, SnippetSimpleGroupImage.class
					.getResourceAsStream(ICON_FILE));

		} else {
			itemImage = new Image(display, Program.findProgram("jpg") //$NON-NLS-1$
					.getImageData());
		}

		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		Gallery gallery = new Gallery(shell, SWT.V_SCROLL | SWT.MULTI);

		// Renderers
		DefaultGalleryGroupRenderer gr = new DefaultGalleryGroupRenderer();
		gr.setMinMargin(2);
		gr.setItemHeight(156);
		gr.setItemWidth(172);

		gr.setMaxImageHeight(64);
		gr.setMaxImageWidth(72);
		gr.setAutoMargin(true);
		gallery.setGroupRenderer(gr);
		gallery.setLowQualityOnUserAction(true);
		gallery.setHigherQualityDelay(500);

		DefaultGalleryItemRenderer ir = new DefaultGalleryItemRenderer();
		gallery.setItemRenderer(ir);

		for (int g = 0; g < 3; g++) {
			GalleryItem group = new GalleryItem(gallery, SWT.NONE);
			group.setText("Group " + g); //$NON-NLS-1$
			group.setExpanded(true);
			group.setImage(itemImage);

			if (g > 0)
				group.setText(1, "Description line 1"); //$NON-NLS-1$

			if (g > 1)
				group.setText(2, "Description line 2"); //$NON-NLS-1$

			for (int i = 0; i < 50; i++) {
				GalleryItem item = new GalleryItem(group, SWT.NONE);
				if (itemImage != null) {
					item.setImage(itemImage);
				}
				item.setText("Item " + i); //$NON-NLS-1$
			}
		}

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		if (itemImage != null)
			itemImage.dispose();
		display.dispose();
	}
}
