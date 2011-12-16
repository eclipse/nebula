/*******************************************************************************
 * Copyright (c) 2007-2008 Peter Centgraf.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors :
 *    Peter Centgraf - initial implementation
 *******************************************************************************/
package org.eclipse.nebula.snippets.gallery;

import java.util.Arrays;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.nebula.jface.galleryviewer.GalleryTreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Simple visual test harness for GalleryTreeViewer.
 * 
 * @author Peter Centgraf
 * @since Dec 5, 2007
 */
public class SnippetGalleryViewerTester {

	protected static class GalleryTestContentProvider implements ITreeContentProvider {
		// implements IStructuredContentProvider { // Use this to test
		// FlatTreeContentProvider
		public static final int NUM_GROUPS = 10;
		public static final int NUM_ITEMS = 20;

		String[] groups = new String[NUM_GROUPS];
		String[][] items = new String[NUM_GROUPS][NUM_ITEMS];

		public GalleryTestContentProvider() {
			for (int i = 0; i < NUM_GROUPS; i++) {
				groups[i] = "Group " + (i + 1); //$NON-NLS-1$
				for (int j = 0; j < NUM_ITEMS; j++) {
					items[i][j] = "Item " + (j + 1); //$NON-NLS-1$
				}
			}
		}

		public Object[] getChildren(Object parentElement) {
			int idx = Arrays.asList(groups).indexOf(parentElement);
			return items[idx];
		}

		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			return ((String) element).startsWith("Group"); //$NON-NLS-1$
		}

		public Object[] getElements(Object inputElement) {
			return groups;
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	protected static class GalleryTestLabelProvider extends LabelProvider implements IColorProvider, IFontProvider {
		protected static Image itemImage = new Image(Display.getCurrent(), Program.findProgram("jpg").getImageData());

		public Image getImage(Object element) {
			return itemImage;
		}

		public Color getBackground(Object element) {
			String label = (String) element;
			if (Integer.parseInt(label.substring(label.indexOf(' ') + 1)) % 2 > 0) {
				return Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW);
			} else {
				return null;
			}
		}

		public Color getForeground(Object element) {
			String label = (String) element;
			if (Integer.parseInt(label.substring(label.indexOf(' ') + 1)) % 2 > 0) {
				return null;
			} else {
				return Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
			}
		}

		public Font getFont(Object element) {
			String label = (String) element;
			if (Integer.parseInt(label.substring(label.indexOf(' ') + 1)) % 2 > 0) {
				return null;
			} else {
				FontData sysFontData = Display.getCurrent().getSystemFont().getFontData()[0];
				sysFontData.setStyle(SWT.BOLD | SWT.ITALIC);
				return new Font(Display.getCurrent(), sysFontData);
			}
		}
	}

	protected static class OddNumbersFilter extends ViewerFilter {
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			try {
				String label = (String) element;
				return (Integer.parseInt(label.substring(label.indexOf(' ') + 1)) % 2 > 0);
			} catch (Exception e) {
				return true;
			}
		}
	}

	protected static final int WIDTH = 800;
	protected static final int HEIGHT = 600;
	protected Shell shell;

	public SnippetGalleryViewerTester() {
		// Initialize the containing Shell
		Display display = new Display();
		shell = new Shell(display);
		shell.setSize(WIDTH, HEIGHT);
		shell.setBackground(display.getSystemColor(SWT.COLOR_RED));
		GridLayoutFactory.fillDefaults().applyTo(shell);

		GalleryTreeViewer viewer = new GalleryTreeViewer(shell);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(viewer.getGallery());
		viewer.setContentProvider(new GalleryTestContentProvider());
		viewer.setLabelProvider(new GalleryTestLabelProvider());
		viewer.setComparator(new ViewerComparator());
		// viewer.addFilter(new OddNumbersFilter());
		viewer.setInput(new Object());

		// Show the Shell
		shell.open();
		shell.layout();

		// Run the event loop
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new SnippetGalleryViewerTester();
	}

}
