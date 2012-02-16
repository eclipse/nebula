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
package org.eclipse.nebula.widgets.pagination.table;

import org.eclipse.nebula.widgets.pagination.AbstractSortColumnSelectionListener;
import org.eclipse.nebula.widgets.pagination.PageableController;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * 
 * {@link SelectionListener} implementation to sort a table column by using the
 * attached pagination controller of the SWT {@link Table}.
 * 
 */
public class SortTableColumnSelectionListener extends
		AbstractSortColumnSelectionListener {

	/**
	 * Constructor with property name and default sort (SWT.NONE).
	 * 
	 * @param propertyName
	 *            the sort property name.
	 */

	public SortTableColumnSelectionListener(String propertyName) {
		this(propertyName, SWT.NONE, null);
	}

	/**
	 * Constructor with property name and default sort (SWT.NONE).
	 * 
	 * @param propertyName
	 *            the sort property name.
	 * @param controller
	 *            the controller to update when sort is applied.
	 */
	public SortTableColumnSelectionListener(String propertyName,
			PageableController controller) {
		this(propertyName, SWT.NONE, controller);
	}

	/**
	 * Constructor with property name and sort direction.
	 * 
	 * @param propertyName
	 *            the sort property name.
	 * @param sortDirection
	 *            the sort direction {@link SWT.UP}, {@link SWT.DOWN}.
	 */
	public SortTableColumnSelectionListener(String propertyName,
			int sortDirection) {
		this(propertyName, sortDirection, null);
	}

	/**
	 * Constructor with property name and sort direction.
	 * 
	 * @param propertyName
	 *            the sort property name.
	 * @param sortDirection
	 *            the sort direction {@link SWT.UP}, {@link SWT.DOWN}.
	 * @param controller
	 *            the controller to update when sort is applied.
	 */
	public SortTableColumnSelectionListener(String propertyName,
			int sortDirection, PageableController controller) {
		super(propertyName, sortDirection, controller);
	}

	@Override
	protected Table getParent(SelectionEvent e) {
		// 1) Get table column which fire this selection event
		TableColumn tableColumn = (TableColumn) e.getSource();
		// 2) Get the owner table
		return tableColumn.getParent();
	}

	@Override
	protected void sort(SelectionEvent e) {
		// 1) Get table column which fire this selection event
		TableColumn tableColumn = (TableColumn) e.getSource();
		// 2) Get the owner table
		Table table = tableColumn.getParent();
		// 3) Modify the SWT Table sort
		table.setSortColumn(tableColumn);
		table.setSortDirection(getSortDirection());
	}
}
