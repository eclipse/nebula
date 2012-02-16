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
package org.eclipse.nebula.widgets.pagination.tree;

import org.eclipse.nebula.widgets.pagination.AbstractSortColumnSelectionListener;
import org.eclipse.nebula.widgets.pagination.PageableController;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * 
 * {@link SelectionListener} implementation to sort a tree column by using the
 * attached pagination controller of the SWT {@link Tree}.
 * 
 */
public class SortTreeColumnSelectionListener extends
		AbstractSortColumnSelectionListener {

	/**
	 * Constructor with property name and default sort (SWT.NONE).
	 * 
	 * @param propertyName
	 *            the sort property name.
	 */

	public SortTreeColumnSelectionListener(String propertyName) {
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
	public SortTreeColumnSelectionListener(String propertyName,
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
	public SortTreeColumnSelectionListener(String propertyName,
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
	public SortTreeColumnSelectionListener(String propertyName,
			int sortDirection, PageableController controller) {
		super(propertyName, sortDirection, controller);
	}

	@Override
	protected Tree getParent(SelectionEvent e) {
		// 1) Get tree column which fire this selection event
		TreeColumn treeColumn = (TreeColumn) e.getSource();
		// 2) Get the owner tree
		return treeColumn.getParent();
	}

	@Override
	protected void sort(SelectionEvent e) {
		// 1) Get tree column which fire this selection event
		TreeColumn treeColumn = (TreeColumn) e.getSource();
		// 2) Get the owner tree
		Tree tree = treeColumn.getParent();
		// 3) Modify the SWT Tree sort
		tree.setSortColumn(treeColumn);
		tree.setSortDirection(getSortDirection());
	}
}
