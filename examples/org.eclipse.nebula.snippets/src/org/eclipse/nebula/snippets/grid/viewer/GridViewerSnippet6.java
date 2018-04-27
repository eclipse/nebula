/*******************************************************************************
 * Copyright (c) 2006 Tom Schindl and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tom Schindl - initial API and implementation
 *     IBM - Improvement for Bug 159625 [Snippets] Update Snippet011CustomTooltips to reflect new API
 *******************************************************************************/

package org.eclipse.nebula.snippets.grid.viewer;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Explore New API: JFace custom tooltips drawing.
 *
 * @author Tom Schindl <tom.schindl@bestsolution.at>
 * @since 3.3
 */
public class GridViewerSnippet6 {
	private static class MyContentProvider implements
			IStructuredContentProvider {

		public Object[] getElements(final Object inputElement) {
			return new String[] { "one", "two", "three", "four", "five", "six",
					"seven", "eight", "nine", "ten" };
		}

		public void dispose() {

		}

		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {

		}
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		final ImageRegistry reg = new ImageRegistry(display);
		reg.put("ICON", ImageDescriptor.createFromFile(GridViewerSnippet6.class, "th_vertical.gif"));

		final GridTableViewer v = new GridTableViewer(shell, SWT.FULL_SELECTION|SWT.H_SCROLL|SWT.V_SCROLL);
		v.getGrid().setLinesVisible(true);
		v.getGrid().setHeaderVisible(true);
		v.setContentProvider(new MyContentProvider());
		v.getGrid().setColumnScrolling(true);
		v.getGrid().setRowHeaderVisible(true);
		v.setRowHeaderLabelProvider(new CellLabelProvider() {

			@Override
			public void update(final ViewerCell cell) {
				cell.setImage(reg.get("ICON"));
				cell.setText(cell.getElement().toString());
			}

		});
		ColumnViewerToolTipSupport.enableFor(v,ToolTip.NO_RECREATE);

		final CellLabelProvider labelProvider = new CellLabelProvider() {

			@Override
			public String getToolTipText(final Object element) {
				return "Tooltip (" + element + ")";
			}

			@Override
			public Point getToolTipShift(final Object object) {
				return new Point(5, 5);
			}

			@Override
			public int getToolTipDisplayDelayTime(final Object object) {
				return 2000;
			}

			@Override
			public int getToolTipTimeDisplayed(final Object object) {
				return 5000;
			}

			@Override
			public void update(final ViewerCell cell) {
				cell.setText(cell.getElement().toString());

			}
		};

		column(v, labelProvider, "Column 1");
		column(v, labelProvider, "Column 2");
		column(v, labelProvider, "Column 3");

		v.setInput("");

		shell.setSize(200, 200);
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		display.dispose();
	}

	private static GridViewerColumn column(final GridTableViewer v, final CellLabelProvider labelProvider,
			final String columnName) {
		final GridViewerColumn column2 = new GridViewerColumn(v, SWT.NONE);
		column2.setLabelProvider(labelProvider);
		column2.getColumn().setText(columnName);
		column2.getColumn().setWidth(300);
		return column2;
	}

}
