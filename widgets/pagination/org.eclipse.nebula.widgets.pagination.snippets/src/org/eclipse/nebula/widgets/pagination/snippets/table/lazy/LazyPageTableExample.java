/*******************************************************************************
 * Copyright (C) 2011 Angelo Zerr <angelo.zerr@gmail.com>, Pascal Leclercq <pascal.leclercq@gmail.com>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo ZERR - initial API and implementation
 *     Pascal Leclercq - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.pagination.snippets.table.lazy;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.nebula.widgets.pagination.IPageLoader;
import org.eclipse.nebula.widgets.pagination.LazyItemsSelectionListener;
import org.eclipse.nebula.widgets.pagination.PageLoaderStrategyHelper;
import org.eclipse.nebula.widgets.pagination.PageableController;
import org.eclipse.nebula.widgets.pagination.collections.PageResultLoaderList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * This sample display a SWT Table with 10 items the first time. If you click on
 * the item 10, 10 items are loaded and added to the SWT Table.
 * 
 */
public class LazyPageTableExample {

	public static void main(String[] args) {

		Display display = new Display();
		Shell shell = new Shell(display);
		GridLayout layout = new GridLayout(1, false);
		shell.setLayout(layout);

		// Decsription label
		createDescriptionLabel(shell);

		// Create table + table viewer + columns
		final TableViewer viewer = createTableWithStandardMean(shell);

		// Create page loader (to load paginated list) and pageable controller.
		int pageSize = 10;
		final List<String> items = createList();
		final IPageLoader pageLoader = new PageResultLoaderList(items);
		final PageableController controller = new PageableController(pageSize);

		// Add listener to the controller to call page loader when controller
		// change the current page (when last item of teh table is selected).
		controller.addPageChangedListener(PageLoaderStrategyHelper
				.createLoadPageAndAddItemsListener(controller, viewer,
						pageLoader, null));

		// Add lazy selection listener to call the page loader when teh last
		// item of the table is selected.
		viewer.getTable().addSelectionListener(
				new LazyItemsSelectionListener(controller));

		// 3) Set current page to 0 to refresh the table
		controller.setCurrentPage(0);

		shell.setSize(360, 250);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	private static void createDescriptionLabel(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setText("Please select the last item of the table to load the next paginated data.");
	}

	private static TableViewer createTableWithStandardMean(Composite parent) {
		final Table table = new Table(parent, SWT.BORDER | SWT.MULTI
				| SWT.H_SCROLL | SWT.V_SCROLL);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));

		final TableViewer viewer = new TableViewer(table);
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setLabelProvider(new LabelProvider());
		createColumns(viewer);
		return viewer;
	}

	private static void createColumns(final TableViewer viewer) {

		// First column is for the first name
		TableViewerColumn col = createTableViewerColumn(viewer, "Name", 150);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				String p = (String) element;
				return p;
			}
		});
	}

	private static List<String> createList() {
		List<String> names = new ArrayList<String>();
		for (int i = 1; i < 2012; i++) {
			names.add("Name " + i);
		}
		return names;
	}

	private static TableViewerColumn createTableViewerColumn(
			TableViewer viewer, String title, int bound) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer,
				SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

}
