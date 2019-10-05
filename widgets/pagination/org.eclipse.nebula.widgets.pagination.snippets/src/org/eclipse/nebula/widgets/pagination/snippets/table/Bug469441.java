/*******************************************************************************
 * Copyright (C) 2011 Angelo Zerr <angelo.zerr@gmail.com>, Pascal Leclercq <pascal.leclercq@gmail.com>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Angelo ZERR - initial API and implementation
 * Pascal Leclercq - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.pagination.snippets.table;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.nebula.widgets.pagination.IPageLoaderHandler;
import org.eclipse.nebula.widgets.pagination.PageableController;
import org.eclipse.nebula.widgets.pagination.collections.PageResultLoaderList;
import org.eclipse.nebula.widgets.pagination.snippets.model.Bug469441Person;
import org.eclipse.nebula.widgets.pagination.table.PageableTable;
import org.eclipse.nebula.widgets.pagination.table.SortTableColumnSelectionListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class Bug469441 {

	private static final String[] TOTAL_ITEMS = new String[] { "5", "200", "1000", "2012" };
	private static List<TableEditor> editors = new ArrayList<>();

	public static void main(String[] args) {

		Display display = new Display();
		Shell shell = new Shell(display);
		GridLayout layout = new GridLayout(2, false);
		shell.setLayout(layout);

		Label totalLabel = new Label(shell, SWT.NONE);
		totalLabel.setText("Total:");
		final Combo combo = new Combo(shell, SWT.READ_ONLY);
		combo.setItems(TOTAL_ITEMS);
		combo.select(3);
		int pageSize = 10;
		final PageableTable pageableTable = new PageableTable(shell, SWT.BORDER, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL, pageSize);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;
		pageableTable.setLayoutData(gridData);

		pageableTable.setPageLoaderHandler(new IPageLoaderHandler<PageableController>() {

			@Override
			public void onBeforePageLoad(PageableController controller) {
				for (TableEditor editor:editors) {
					editor.getEditor().dispose();
					editor.dispose();
				}
				editors.clear();
			}

			@Override
			public boolean onAfterPageLoad(PageableController controller, Throwable e) {
				return false;
			}
		});


		// 2) Initialize the table viewer SWT Table
		TableViewer viewer = pageableTable.getViewer();
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setLabelProvider(new LabelProvider());

		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// 3) Create Table columns with sort of paginated list.
		createColumns(viewer);

		// 4) Set the page loader used to load a page (sublist of String)
		// according the page index selected, the page size etc.
		List<Bug469441Person> items = createList(combo);
		final PageResultLoaderList<Bug469441Person> pageLoader = new PageResultLoaderList<>(items);
		pageableTable.setPageLoader(pageLoader);

		// 5) Set current page to 0 to refresh the table
		pageableTable.setCurrentPage(0);

		combo.addListener(SWT.Selection, e -> {
			List<Bug469441Person> _items = createList(combo);
			pageLoader.setItems(_items);
			pageableTable.refreshPage(true);
		});


		shell.setSize(400, 250);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	private static void createColumns(final TableViewer viewer) {

		// First column is for the first name
		TableViewerColumn col = createTableViewerColumn(viewer, "Name", 150);
		col.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				Bug469441Person p = (Bug469441Person) element;
				return p.getFirstName();
			}

			@Override
			public void update(ViewerCell cell) {
				Bug469441Person obj = (Bug469441Person) cell.getItem().getData();

				TableItem item = (TableItem) cell.getItem();
				TableEditor editor = new TableEditor(item.getParent());
				editor.grabHorizontal = true;
				editor.grabVertical = true;

				Button button = new Button(viewer.getTable(), SWT.NONE);
				button.setText(obj.getFirstName());
				editor.setEditor(button, item, cell.getColumnIndex());
				editor.layout();

				editors.add(editor);
			}

		});
		col.getColumn().addSelectionListener(new SortTableColumnSelectionListener("name"));

	}

	private static List<Bug469441Person> createList(Combo combo) {
		int total = Integer.valueOf(TOTAL_ITEMS[combo.getSelectionIndex()]);
		List<Bug469441Person> names = new ArrayList<>();
		for (int i = 1; i < total; i++) {
			names.add(new Bug469441Person("Name " + i, i < 100 ? "Address " + Math.random() : null));
		}
		return names;
	}

	private static TableViewerColumn createTableViewerColumn(TableViewer viewer, String title, int bound) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

}
