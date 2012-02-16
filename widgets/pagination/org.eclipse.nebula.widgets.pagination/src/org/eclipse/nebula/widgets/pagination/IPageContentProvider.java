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

import java.util.List;

import org.eclipse.nebula.widgets.pagination.collections.PageResult;

/**
 * When {@link IPageLoader} load paginated list, the method
 * {@link IPageLoader#loadPage(PageableController)} returns a pagination
 * structure (like {@link PageResult}, Spring Data Page etc...).
 * 
 * This interface is used to returns total elements and paginated list from the
 * returned pagination structure.
 */
public interface IPageContentProvider {

	/**
	 * Create an instance of {@link PageableController} with the given page
	 * size.
	 */
	PageableController createController(int pageSize);

	/**
	 * Returns the total amount of elements.
	 * 
	 * @return the total amount of elements
	 */
	long getTotalElements(Object page);

	/**
	 * Returns the page content as {@link List}.
	 * 
	 * @return
	 */
	List<?> getPaginatedList(Object page);

}
