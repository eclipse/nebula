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
 * Angelo ZERR - initial API and implementation
 * Pascal Leclercq - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.pagination.collections;

import java.util.List;

import org.eclipse.nebula.widgets.pagination.PageableController;
import org.eclipse.swt.SWT;

/**
 * Helper to create implementation of {@link PageResult} from a Java
 * {@link List}.
 * 
 */
public class PageListHelper {

	public static <T> PageResult<T> createPage(List<T> list, PageableController controller) {
		return createPage(list, controller, DefaultSortProcessor.getInstance());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> PageResult<T> createPage(List<T> list, PageableController controller, SortProcessor processor) {
		int sortDirection = controller.getSortDirection();
		if (sortDirection != SWT.NONE) {
			// Sort the list
			processor.sort(list, controller.getSortPropertyName(), sortDirection);
		}
		int totalSize = list.size();
		int pageSize = controller.getPageSize();
		int pageIndex = controller.getPageOffset();

		int fromIndex = pageIndex;
		int toIndex = pageIndex + pageSize;
		if (toIndex > totalSize) {
			toIndex = totalSize;
		}
		if (fromIndex > totalSize) {
			fromIndex=totalSize - totalSize%pageSize;
		}
		List<?> content = list.subList(fromIndex, toIndex);
		return new PageResult(content, totalSize);
	}

}
