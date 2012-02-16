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
package org.eclipse.nebula.widgets.pagination.snippets.table;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.nebula.widgets.pagination.IPageLoaderHandler;
import org.eclipse.nebula.widgets.pagination.PageableController;
import org.eclipse.nebula.widgets.pagination.collections.PageResult;
import org.eclipse.nebula.widgets.pagination.collections.PageResultLoaderList;
import org.eclipse.nebula.widgets.pagination.snippets.model.Address;
import org.eclipse.nebula.widgets.pagination.snippets.model.Person;
import org.eclipse.nebula.widgets.pagination.table.PageableTable;
import org.eclipse.nebula.widgets.pagination.table.SortTableColumnSelectionListener;
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
 * This sample is the same than {@link ModelSortPageableTableExample} and use
 * {@link IPageLoaderHandler} to display a message while paginated list is
 * loaded. The load of the paginated list take a long time (1000ms) (emulate
 * that with Thread.sleep). On bottom of the table, a "Loading..." message is
 * displayed.
 * 
 */
public class ModelSortPageableTableWorkInProcessExample {

	public static void main(String[] args) {

		Display display = new Display();
		Shell shell = new Shell(display);
		GridLayout layout = new GridLayout(1, false);
		shell.setLayout(layout);

		final List<Person> items = createList();

		// 1) Create pageable table with 10 items per page
		// This SWT Component create internally a SWT Table+JFace TreeViewer
		int pageSize = 10;
		final PageableTable pageableTable = new PageableTable(shell,
				SWT.BORDER, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL
						| SWT.V_SCROLL, pageSize) {
			@Override
			protected Composite createCompositeBottom(Composite parent) {
				Composite bottom = new LoadingComposite(parent);
				bottom.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				return bottom;
			}
		};
		final LoadingComposite loadingComposite = (LoadingComposite) pageableTable
				.getCompositeBottom();
		pageableTable.setLayoutData(new GridData(GridData.FILL_BOTH));

		pageableTable
				.setPageLoaderHandler(new IPageLoaderHandler<PageableController>() {

					long time = 0;

					public void onBeforePageLoad(PageableController controller) {
						time = System.currentTimeMillis();
						String text = "Loading...";
						System.err.println(text);
						loadingComposite.setText(text);
					}

					public boolean onAfterPageLoad(
							PageableController controller, Throwable e) {
						String text = "Loaded with "
								+ (System.currentTimeMillis() - time) + "(ms)";
						System.err.println(text);
						loadingComposite.setText(text);
						return true;
					}
				});

		// 2) Initialize the table viewer + SWT Table
		TableViewer viewer = pageableTable.getViewer();
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setLabelProvider(new LabelProvider());

		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// 3) Create Table columns with sort of paginated list.
		createColumns(viewer);

		// 3) Set current page to 0 to refresh the table
		pageableTable.setPageLoader(new PageResultLoaderList<Person>(items) {
			@Override
			public PageResult<Person> loadPage(PageableController pageable) {
				if (pageableTable.isVisible()) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				return super.loadPage(pageable);
			}
		});
		pageableTable.setCurrentPage(0);

		shell.setSize(400, 300);
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
				Person p = (Person) element;
				return p.getName();
			}
		});
		col.getColumn().addSelectionListener(
				new SortTableColumnSelectionListener("name"));

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
		col.getColumn().addSelectionListener(
				new SortTableColumnSelectionListener("address.name"));
	}

	private static List<Person> createList() {
		List<Person> names = new ArrayList<Person>();
		for (int i = 1; i < 2012; i++) {
			names.add(new Person("Name " + i, i < 100 ? "Adress "
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

	private static class LoadingComposite extends Composite {

		private Label label;

		public LoadingComposite(Composite parent) {
			super(parent, SWT.NONE);
			super.setLayout(new GridLayout());
			label = new Label(this, SWT.NONE);
			label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		}

		public void setText(String text) {
			label.setText(text);
		}

	}
}
