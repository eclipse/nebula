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
package org.eclipse.nebula.widgets.pagination.snippets.table.renderers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.nebula.widgets.pagination.PageableController;
import org.eclipse.nebula.widgets.pagination.collections.PageResultContentProvider;
import org.eclipse.nebula.widgets.pagination.collections.PageResultLoaderList;
import org.eclipse.nebula.widgets.pagination.renderers.navigation.ResultAndNavigationPageLinksRendererFactory;
import org.eclipse.nebula.widgets.pagination.snippets.model.Address;
import org.eclipse.nebula.widgets.pagination.snippets.model.Person;
import org.eclipse.nebula.widgets.pagination.table.PageableTable;
import org.eclipse.nebula.widgets.pagination.table.SortTableColumnSelectionListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * This sample display a list of model {@link Person} in a SWT Table with
 * pagination banner displayed with Page Results+Page Links on the top of the
 * SWT Table. The 2 columns which display the list of {@link Person} can be
 * clicked to sort the paginated list.
 * 
 */
public class OneRendererPaginationTableWithPageableTableExample {

	public static void main(String[] args) {

		Display display = new Display();
		Shell shell = new Shell(display);
		GridLayout layout = new GridLayout(1, false);
		shell.setLayout(layout);

		final List<Person> items = createList();

		int pageSize = 10;
		final PageableTable pageableTable = new PageableTable(shell,
				SWT.BORDER, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL
						| SWT.V_SCROLL, pageSize,
				PageResultContentProvider.getInstance(), null,
				ResultAndNavigationPageLinksRendererFactory.getFactory());
		pageableTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		pageableTable.setPageLoader(new PageResultLoaderList<Person>(items));

		// 2) Initialize the table viewer + SWT Table
		TableViewer viewer = pageableTable.getViewer();
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setLabelProvider(new LabelProvider());

		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		PageableController controller = pageableTable.getController();
		createColumns(viewer, controller);
		// 3) Set current page to 0 to refresh the table

		controller.setCurrentPage(0);

		shell.setSize(350, 250);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	private static void createColumns(final TableViewer viewer,
			PageableController controller) {

		// First column is for the first name
		TableViewerColumn col = createTableViewerColumn(viewer, "Name", 150);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Person p = (Person) element;
				return p.getName();
			}
		});
		col.getColumn().addSelectionListener(
				new SortTableColumnSelectionListener("name", controller));

		// Second column is for the adress
		col = createTableViewerColumn(viewer, "Adress", 150);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Person p = (Person) element;
				Address address = p.getAddress();
				if (address == null) {
					return "";
				}
				return address.getName();
			}
		});
		col.getColumn()
				.addSelectionListener(
						new SortTableColumnSelectionListener("address.name",
								controller));
	}

	private static List<Person> createList() {
		List<Person> names = new ArrayList<Person>();
		for (int i = 1; i < 100; i++) {
			names.add(new Person("Name " + i, i < 25 ? "Adress "
					+ Math.random() : null));
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
