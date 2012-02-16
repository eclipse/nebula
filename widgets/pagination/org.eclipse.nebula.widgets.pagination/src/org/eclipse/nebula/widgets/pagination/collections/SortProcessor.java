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
package org.eclipse.nebula.widgets.pagination.collections;

import java.util.List;

import org.eclipse.swt.SWT;

/**
 * Sort processor used to sort a list.
 */
public interface SortProcessor {

	/**
	 * Sort the given list by using the given sort property name and direction.
	 * 
	 * @param list
	 *            the list to sort.
	 * @param sortPropertyName
	 *            the sort property name.
	 * @param sortDirection
	 *            the sort direction {@link SWT.UP}, {@link SWT.DOWN,
	 *            {@link SWT.NONE} .
	 */
	void sort(List<?> list, String sortPropertyName, int sortDirection);
}
