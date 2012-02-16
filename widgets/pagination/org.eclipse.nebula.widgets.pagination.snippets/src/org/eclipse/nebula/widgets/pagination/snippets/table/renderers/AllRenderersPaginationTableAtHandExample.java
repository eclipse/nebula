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
import java.util.Locale;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.nebula.widgets.pagination.IPageLoader;
import org.eclipse.nebula.widgets.pagination.PageLoaderStrategyHelper;
import org.eclipse.nebula.widgets.pagination.PageableController;
import org.eclipse.nebula.widgets.pagination.collections.PageResultLoaderList;
import org.eclipse.nebula.widgets.pagination.collections.PageResult;
import org.eclipse.nebula.widgets.pagination.collections.PageResultContentProvider;
import org.eclipse.nebula.widgets.pagination.renderers.navigation.NavigationPageComboRenderer;
import org.eclipse.nebula.widgets.pagination.renderers.navigation.NavigationPageScaleRenderer;
import org.eclipse.nebula.widgets.pagination.renderers.navigation.ResultAndNavigationPageGraphicsRenderer;
import org.eclipse.nebula.widgets.pagination.renderers.navigation.ResultAndNavigationPageLinksRenderer;
import org.eclipse.nebula.widgets.pagination.renderers.navigation.graphics.BlackNavigationPageGraphicsConfigurator;
import org.eclipse.nebula.widgets.pagination.renderers.navigation.graphics.GreenNavigationPageGraphicsConfigurator;
import org.eclipse.nebula.widgets.pagination.renderers.pagesize.PageSizeComboRenderer;
import org.eclipse.nebula.widgets.pagination.snippets.model.Address;
import org.eclipse.nebula.widgets.pagination.snippets.model.Person;
import org.eclipse.nebula.widgets.pagination.table.SortTableColumnSelectionListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * This sample display the whole renderer (scale, combo, GC, links tec) provided
 * by the Nebula Pagination. You can too change the locale to refresh the label
 * of the navigation page.
 * 
 * This demo shows you that the whole renderer are linked to the pagination
 * controller which fire events as soon as it changes (selected page changed
 * page size changed, local changed, etc). For instance if you move the slider
 * to change page index, the tabel is refreshed and the other navigation page
 * are refreshed.
 * 
 */
public class AllRenderersPaginationTableAtHandExample {

	public static void main(String[] args) {

		Display display = new Display();
		Shell shell = new Shell(display);
		GridLayout layout = new GridLayout(2, false);
		shell.setLayout(layout);

		final List<Person> items = createList();

		Composite left = new Composite(shell, SWT.NONE);
		left.setLayoutData(new GridData(GridData.FILL_BOTH));
		left.setLayout(new GridLayout());

		// Left panel
		Table table = new Table(left, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));

		// 2) Initialize the table viewer + SWT Table
		TableViewer viewer = new TableViewer(table);
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setLabelProvider(new LabelProvider());

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// 3) Create Table columns with sort of paginated list.
		int pageSize = 10;
		final PageableController controller = new PageableController(pageSize);
		final IPageLoader<PageResult<Person>> pageLoader = new PageResultLoaderList<Person>(items);
		controller.addPageChangedListener(PageLoaderStrategyHelper
				.createLoadPageAndReplaceItemsListener(controller, viewer,
						pageLoader, PageResultContentProvider.getInstance(), null));

		createColumns(viewer, controller);

		// Right Panel
		Composite right = new Composite(shell, SWT.NONE);
		right.setLayoutData(new GridData(GridData.FILL_BOTH));
		right.setLayout(new GridLayout());

		NavigationPageComboRenderer pageComboDecorator = new NavigationPageComboRenderer(
				right, SWT.NONE, controller);
		pageComboDecorator
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		NavigationPageScaleRenderer pageScaleDecorator = new NavigationPageScaleRenderer(
				right, SWT.NONE, controller);
		pageScaleDecorator
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		ResultAndNavigationPageLinksRenderer resultAndPageLinksDecorator = new ResultAndNavigationPageLinksRenderer(
				right, SWT.NONE, controller);
		resultAndPageLinksDecorator.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));

		PageSizeComboRenderer pageSizeComboDecorator = new PageSizeComboRenderer(
				right, SWT.NONE, controller, new Integer[] { 5, 10, 50, 100,
						200 });
		pageSizeComboDecorator.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));

		ResultAndNavigationPageGraphicsRenderer resultAndPageButtonsDecorator = new ResultAndNavigationPageGraphicsRenderer(
				right, SWT.NONE, controller);
		resultAndPageButtonsDecorator.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));

		ResultAndNavigationPageGraphicsRenderer black = new ResultAndNavigationPageGraphicsRenderer(
				right, SWT.NONE, controller,
				BlackNavigationPageGraphicsConfigurator.getInstance());
		black.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		ResultAndNavigationPageGraphicsRenderer green = new ResultAndNavigationPageGraphicsRenderer(
				right, SWT.NONE, controller,
				GreenNavigationPageGraphicsConfigurator.getInstance());
		green.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Locale
		Composite localeComposite = new Composite(right, SWT.NONE);
		localeComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		localeComposite.setLayout(new GridLayout(2, false));
		Label localeLabel = new Label(localeComposite, SWT.NONE);
		localeLabel.setText("Locale:");
		localeLabel.setLayoutData(new GridData());
		final Combo localeCombo = new Combo(localeComposite, SWT.READ_ONLY);
		localeCombo.setItems(new String[] { "en", "fr" });
		localeCombo.select(Locale.getDefault().equals(Locale.FRANCE) ? 1 : 0);
		localeCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		localeCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String locale = localeCombo.getText();
				if ("fr".equals(locale)) {
					controller.setLocale(Locale.FRENCH);
				} else {
					controller.setLocale(Locale.ENGLISH);
				}
			}
		});
		// 3) Set current page to 0 to refresh the table

		controller.setCurrentPage(0);

		shell.setSize(800, 250);
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
