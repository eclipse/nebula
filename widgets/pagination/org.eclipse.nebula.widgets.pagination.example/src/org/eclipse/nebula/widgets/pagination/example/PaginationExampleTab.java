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
package org.eclipse.nebula.widgets.pagination.example;

import java.util.Locale;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.nebula.examples.AbstractExampleTab;
import org.eclipse.nebula.widgets.pagination.collections.PageResultContentProvider;
import org.eclipse.nebula.widgets.pagination.example.model.NebulaWidget;
import org.eclipse.nebula.widgets.pagination.example.model.Person;
import org.eclipse.nebula.widgets.pagination.example.services.NebulaWidgetServices;
import org.eclipse.nebula.widgets.pagination.renderers.navigation.ResultAndNavigationPageGraphicsRenderer;
import org.eclipse.nebula.widgets.pagination.renderers.navigation.ResultAndNavigationPageGraphicsRendererFactory;
import org.eclipse.nebula.widgets.pagination.renderers.navigation.graphics.BlackNavigationPageGraphicsConfigurator;
import org.eclipse.nebula.widgets.pagination.renderers.navigation.graphics.BlueNavigationPageGraphicsConfigurator;
import org.eclipse.nebula.widgets.pagination.renderers.navigation.graphics.GreenNavigationPageGraphicsConfigurator;
import org.eclipse.nebula.widgets.pagination.renderers.pagesize.PageSizeComboRenderer;
import org.eclipse.nebula.widgets.pagination.table.PageableTable;
import org.eclipse.nebula.widgets.pagination.table.SortTableColumnSelectionListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * Demonstrates the Nebula Pagination Control
 * 
 * @author Angelo ZERR
 */
public class PaginationExampleTab extends AbstractExampleTab {

	private static final String BUNDLE = "org.eclipse.nebula.widgets.pagination.example";

	private PageableTable pageableTable;

	@Override
	public Control createControl(Composite parent) {

		Composite body = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		body.setLayout(layout);

		// 1) Create pageable table with 10 items per page
		// This SWT Component create internally a SWT Table+JFace TreeViewer
		int pageSize = 10;
		pageableTable = new PageableTable(
				body,
				SWT.BORDER,
				SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL,
				pageSize,
				PageResultContentProvider.getInstance(),
				ResultAndNavigationPageGraphicsRendererFactory.getBlueFactory(),
				null);
		pageableTable.setLayoutData(new GridData(GridData.FILL_BOTH));

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
		pageableTable.setPageLoader(NebulaWidgetServices.getInstance());
		pageableTable.setCurrentPage(0);

		return body;
	}

	private static void createColumns(final TableViewer viewer) {

		// First column is the widget name
		TableViewerColumn col = createTableViewerColumn(viewer, "Widget", 150);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				NebulaWidget w = (NebulaWidget) element;
				return w.getName();
			}
		});
		col.getColumn().addSelectionListener(
				new SortTableColumnSelectionListener("name"));

		// Second column is the committer
		col = createTableViewerColumn(viewer, "Commiter", 150);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				NebulaWidget w = (NebulaWidget) element;
				Person p = w.getCommitter();
				return p.getFirstName() + " " + p.getLastName();
			}
		});
		col.getColumn().addSelectionListener(
				new SortTableColumnSelectionListener("committer.firstName"));
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

	@Override
	public void createParameters(Composite parent) {
		parent.setLayout(new GridLayout(2, true));
		getSettings(parent);
	}

	private void getSettings(Composite parent) {
		// Style combo
		Label label = new Label(parent, SWT.NONE);
		label.setText("Style:");
		final Combo styleCombo = new Combo(parent, SWT.READ_ONLY);
		styleCombo.setItems(new String[] { "Blue", "Green", "Black" });
		styleCombo.select(0);
		styleCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (styleCombo.getText().equals("Blue")) {
					((ResultAndNavigationPageGraphicsRenderer) pageableTable
							.getCompositeTop()).getNavigationPage()
							.setConfigurator(
									BlueNavigationPageGraphicsConfigurator
											.getInstance());
				} else if (styleCombo.getText().equals("Green")) {
					((ResultAndNavigationPageGraphicsRenderer) pageableTable
							.getCompositeTop())
							.setConfigurator(GreenNavigationPageGraphicsConfigurator
									.getInstance());
				} else {
					((ResultAndNavigationPageGraphicsRenderer) pageableTable
							.getCompositeTop())
							.setConfigurator(BlackNavigationPageGraphicsConfigurator
									.getInstance());
				}

			}
		});
		styleCombo.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Locale
		Label localeLabel = new Label(parent, SWT.NONE);
		localeLabel.setText("Locale:");
		localeLabel.setLayoutData(new GridData());
		final Combo localeCombo = new Combo(parent, SWT.READ_ONLY);
		localeCombo.setItems(new String[] { "en", "fr" });
		localeCombo.select(Locale.getDefault().equals(Locale.FRANCE) ? 1 : 0);
		localeCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		localeCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String locale = localeCombo.getText();
				if ("fr".equals(locale)) {
					pageableTable.setLocale(Locale.FRENCH);
				} else {
					pageableTable.setLocale(Locale.ENGLISH);
				}
			}
		});

	}

	@Override
	public String[] createLinks() {
		String[] links = { "<a href=\"http://angelozerr.wordpress.com/2012/01/06/nebula_pagination/\" >Pagination Control Article</a>" };
		return links;
	}

}
