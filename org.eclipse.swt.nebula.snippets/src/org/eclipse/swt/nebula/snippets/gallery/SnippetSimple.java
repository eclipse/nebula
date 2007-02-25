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
package org.eclipse.swt.nebula.snippets.gallery;

import org.eclipse.nebula.widgets.gallery.DefaultGalleryGroupRenderer;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryItemRenderer;
import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This widget displays a simple gallery with some content.<br/> Scrolling is
 * vertical.<br/><br/>
 * 
 * <p>
 * NOTE: THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT. THIS IS A
 * PRE-RELEASE ALPHA VERSION. USERS SHOULD EXPECT API CHANGES IN FUTURE
 * VERSIONS.
 * </p>
 * 
 * @author Nicolas Richeton (nicolas.richeton@gmail.com)
 */

public class SnippetSimple {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		Gallery gallery = new Gallery(shell, SWT.V_SCROLL);

		// Renderers
		DefaultGalleryGroupRenderer gr = new DefaultGalleryGroupRenderer();
		DefaultGalleryItemRenderer ir = new DefaultGalleryItemRenderer();
		gallery.setGroupRenderer(gr);
		gallery.setItemRenderer(ir);

		GalleryItem i1 = new GalleryItem(gallery, SWT.NONE);
		i1.setText("Test");
		i1.setExpanded(true);
		i1 = new GalleryItem(i1, SWT.NONE);
		// Your image here
		// i1.setImage(eclipseImage);

		i1.setText("Child1");

		i1 = new GalleryItem(gallery, SWT.NONE);
		i1.setText("Test2");
		GalleryItem i2 = new GalleryItem(i1, SWT.NONE);
		// Your image here
		// i2.setImage(eclipseImage);
		i2.setText("Child1");

		i2 = new GalleryItem(i1, SWT.NONE);
		// Your image here
		// i2.setImage(eclipseImage);
		i2.setText("Child2");

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		display.dispose();
	}

}
