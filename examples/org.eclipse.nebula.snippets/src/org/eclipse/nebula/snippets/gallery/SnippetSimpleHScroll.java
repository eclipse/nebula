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

import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.nebula.widgets.gallery.ListItemRenderer;
import org.eclipse.nebula.widgets.gallery.NoGroupRenderer;
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

public class SnippetSimpleHScroll {

	public static void main(String[] args) {
		Display display = new Display();
		Image itemImage = new Image(display, Program
				.findProgram("jpg").getImageData()); //$NON-NLS-1$

		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		Gallery gallery = new Gallery(shell, SWT.H_SCROLL);

		// Renderers
		NoGroupRenderer gr = new NoGroupRenderer();
		gr.setMinMargin(2);
		gr.setItemHeight(24);
		gr.setItemWidth(256);
		gr.setAutoMargin(true);
		gallery.setGroupRenderer(gr);

		ListItemRenderer ir = new ListItemRenderer();
		gallery.setItemRenderer(ir);

		GalleryItem group = new GalleryItem(gallery, SWT.NONE);

		for (int i = 0; i < 50; i++) {
			GalleryItem item = new GalleryItem(group, SWT.NONE);
			if (itemImage != null) {
				item.setImage(itemImage);
			}
			item.setText("Item " + i); //$NON-NLS-1$
		}

		group = new GalleryItem(gallery, SWT.NONE);

		for (int i = 0; i < 50; i++) {
			GalleryItem item = new GalleryItem(group, SWT.NONE);
			if (itemImage != null) {
				item.setImage(itemImage);
			}
			item.setText("Item " + i); //$NON-NLS-1$
		}

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		display.dispose();
	}

}
