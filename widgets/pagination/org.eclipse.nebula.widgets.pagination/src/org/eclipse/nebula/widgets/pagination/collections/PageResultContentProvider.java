/*******************************************************************************
 * Copyright (C) 2011 Angelo Zerr <angelo.zerr@gmail.com>, Pascal Leclercq <pascal.leclercq@gmail.com>
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Angelo ZERR - initial API and implementation
 *     Pascal Leclercq - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.pagination.collections;

import java.util.List;

import org.eclipse.nebula.widgets.pagination.IPageContentProvider;
import org.eclipse.nebula.widgets.pagination.PageableController;

/**
 * Implementation of {@link IPageContentProvider} to retrieves pagination
 * information (total elements and paginated list) from the pagination structure
 * {@link PageResult}.
 *
 */
public class PageResultContentProvider implements IPageContentProvider {

	private static final IPageContentProvider INSTANCE = new PageResultContentProvider();

	/**
	 * Returns the singleton of {@link PageResultContentProvider}.
	 *
	 * @return
	 */
	public static IPageContentProvider getInstance() {
		return INSTANCE;
	}

	/**
	 * @see
	 * org.eclipse.nebula.widgets.pagination.IPageContentProvider#createController
	 * (int)
	 */
	public PageableController createController(int pageSize) {
		return new PageableController(pageSize);
	}

	/**
	 * @see
	 * org.eclipse.nebula.widgets.pagination.IPageContentProvider#getTotalElements
	 * (java.lang.Object)
	 */
	public long getTotalElements(Object page) {
		return ((PageResult<?>) page).getTotalElements();
	}

	/**
	 * @see
	 * org.eclipse.nebula.widgets.pagination.IPageContentProvider#getPaginatedList
	 * (java.lang.Object)
	 */
	public List<?> getPaginatedList(Object page) {
		return ((PageResult<?>) page).getContent();
	}

}
