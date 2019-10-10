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
package org.eclipse.nebula.widgets.pagination;

import java.util.List;

import org.eclipse.nebula.widgets.pagination.collections.PageResultLoaderList;
import org.eclipse.nebula.widgets.pagination.collections.PageResult;

/**
 * Classes which implement this interface provide methods which load paginated
 * list by using information about pagination (sort, page index etc) coming from
 * the {@link PageableController}.
 * 
 * <p>
 * If you wish to manage pagination with Java {@link List} in memory you can use
 * {@link PageResultLoaderList}.
 * 
 * </p>
 * <p>
 * For better design {@link IPageLoader} should be implemented by the Service
 * Layer or DAO (Repository) layer. If you wish to manage pagination with JPA,
 * Spring Data JPA can be very helpful.
 * </p>
 * 
 * @see http://www.springsource.org/spring-data
 */
public interface IPageLoader<T> {

	/**
	 * Load the paginated list by using the {@link PageableController}
	 * information about pagination (sort, page index etc) and returns a page
	 * result which contains the paginated list and the total elements (ex:
	 * {@link PageResult}, Spring Data Page, etc).
	 * 
	 * @param controller
	 *            information about pagination.
	 * @return a pagination structure which contains the paginated list and the
	 *         total elements.
	 */
	T loadPage(PageableController controller);
}
