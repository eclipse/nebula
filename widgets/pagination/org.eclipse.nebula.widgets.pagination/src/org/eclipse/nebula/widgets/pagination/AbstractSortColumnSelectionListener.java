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
package org.eclipse.nebula.widgets.pagination;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Widget;

/**
 * 
 * Abstract class to sort a widget (table tree etc...) column by using the
 * attached pagination controller of the SWT parent (table tree...).
 * 
 */
public abstract class AbstractSortColumnSelectionListener extends
		AbstractPageControllerSelectionListener<PageableController> {

	/** property name used to sort **/
	private final String sortPropertyName;
	/** the sort direction **/
	private int sortDirection;

	/**
	 * Constructor with property name and default sort (SWT.NONE).
	 * 
	 * @param propertyName
	 *            the sort property name.
	 */

	public AbstractSortColumnSelectionListener(String propertyName) {
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
	public AbstractSortColumnSelectionListener(String propertyName,
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
	public AbstractSortColumnSelectionListener(String propertyName,
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
	public AbstractSortColumnSelectionListener(String propertyName,
			int sortDirection, PageableController controller) {
		super(controller);
		this.sortPropertyName = propertyName;
		this.sortDirection = sortDirection;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		Widget parent = getParent(e);
		// 4) Compute the (inverse) sort direction
		sortDirection = sortDirection == SWT.DOWN ? SWT.UP : SWT.DOWN;
		// 5) Modify the sort of the page controller
		super.getController(parent).setSort(sortPropertyName, sortDirection);
		// 6) Modify the SWT Table sort
		sort(e);
	}

	/**
	 * Returns the property name used to sort.
	 * 
	 * @return the sort property name.
	 */
	public String getSortPropertyName() {
		return sortPropertyName;
	}

	/**
	 * Returns the sort direction {@link SWT.UP}, {@link SWT.DOWN}.
	 * 
	 * @return
	 */
	public int getSortDirection() {
		return sortDirection;
	}

	/**
	 * Returns the parent of the sorted column (ex Table for TableColumn, Tree
	 * for TreeColumn).
	 * 
	 * @param e
	 * @return
	 */
	protected abstract Widget getParent(SelectionEvent e);

	/**
	 * Sort the column od the parent of the sorted column (ex Table for
	 * TableColumn, Tree for TreeColumn).
	 * 
	 * @param e
	 */
	protected abstract void sort(SelectionEvent e);

}
