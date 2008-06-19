package org.eclipse.nebula.widgets.gallery.tests;

import junit.framework.TestCase;

import org.eclipse.nebula.widgets.gallery.DefaultGalleryGroupRenderer;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryItemRenderer;
import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Bug216204Test extends TestCase {

	private Shell s = null;
	private Display d = null;

	protected void setUp() throws Exception {
		d = new Display();
		s = new Shell(d, SWT.NONE);
		super.setUp();
	}

	protected void tearDown() throws Exception {
		d.dispose();
		super.tearDown();
	}

	public void testBug212182OnGallery() {
		Gallery g = new Gallery(s, SWT.V_SCROLL);

		// Set Renderers
		DefaultGalleryGroupRenderer gr = new DefaultGalleryGroupRenderer();
		g.setGroupRenderer(gr);

		DefaultGalleryItemRenderer ir = new DefaultGalleryItemRenderer();
		g.setItemRenderer(ir);

		// Create an item
		GalleryItem item1 = new GalleryItem(g, SWT.NONE);

		g.setSelection(new GalleryItem[] { item1 });

		GalleryItem[] selection = g.getSelection();

		assertEquals(1, selection.length);
		assertEquals(item1, selection[0]);

		// Dispose item
		item1.dispose();
		selection = g.getSelection();

		assertEquals(0, selection.length);

		// Clean
		g.dispose();
	}

}
