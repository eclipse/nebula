package org.eclipse.nebula.widgets.gallery.tests;

import junit.framework.TestCase;

import org.eclipse.nebula.widgets.gallery.DefaultGalleryGroupRenderer;
import org.eclipse.nebula.widgets.gallery.DefaultGalleryItemRenderer;
import org.eclipse.nebula.widgets.gallery.Gallery;
import org.eclipse.nebula.widgets.gallery.GalleryItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Bug212182Test extends TestCase {

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

		// Check for NPE or Null
		GalleryItem[] items = g.getItems();
		assertNotNull(items);
		assertEquals(0, items.length);

		g.dispose();
	}

	public void testBug212182OnGalleryItem() {
		Gallery g = new Gallery(s, SWT.V_SCROLL);

		// Set Renderers
		DefaultGalleryGroupRenderer gr = new DefaultGalleryGroupRenderer();
		g.setGroupRenderer(gr);

		DefaultGalleryItemRenderer ir = new DefaultGalleryItemRenderer();
		g.setItemRenderer(ir);

		// Create an item
		GalleryItem item = new GalleryItem(g, SWT.None);

		// Check for NPE or null
		GalleryItem[] items = item.getItems();
		assertNotNull(items);
		assertEquals(0, items.length);

		g.dispose();
	}

}
