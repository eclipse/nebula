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
import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * <p>
 * NOTE: THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT. THIS IS A
 * PRE-RELEASE ALPHA VERSION. USERS SHOULD EXPECT API CHANGES IN FUTURE
 * VERSIONS.
 * </p>
 * 
 * @author Nicolas Richeton
 */

public class SnippetCustomDraw {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		Gallery gallery = new Gallery(shell, SWT.V_SCROLL);

		// Enable custom drawing
		gallery.setItemRenderer(null);

		DefaultGalleryGroupRenderer groupRenderer = new DefaultGalleryGroupRenderer();
		groupRenderer.setMinMargin(3);
		gallery.setGroupRenderer(groupRenderer);

		gallery.addListener(SWT.PaintItem, new Listener() {

			public void handleEvent(Event e) {
				GalleryItem item = (GalleryItem) e.item;
				e.gc.setBackground(Display.getDefault().getSystemColor(
						SWT.COLOR_GRAY));
				e.gc.fillRectangle(e.x, e.y, e.width, e.height);
				e.gc.drawText(item.getText(), e.x, e.y);
			}

		});

		GalleryItem i1 = new GalleryItem(gallery, SWT.NONE);
		i1.setText("Group 1"); //$NON-NLS-1$

		i1 = new GalleryItem(i1, SWT.NONE);
		i1.setText("Test1-1"); //$NON-NLS-1$

		i1 = new GalleryItem(gallery, SWT.NONE);
		i1.setText("Group 2"); //$NON-NLS-1$

		i1 = new GalleryItem(i1, SWT.NONE);
		i1.setText("Test2-1"); //$NON-NLS-1$

		i1 = new GalleryItem(gallery, SWT.NONE);
		i1.setText("Group 3"); //$NON-NLS-1$

		i1 = new GalleryItem(i1, SWT.NONE);
		i1.setText("Test3-1"); //$NON-NLS-1$

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

}
