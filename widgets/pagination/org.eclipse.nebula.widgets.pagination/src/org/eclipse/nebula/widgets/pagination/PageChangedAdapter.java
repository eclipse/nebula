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

import java.util.Locale;

/**
 * This adapter class provides default implementations for the methods described
 * by the {@link IPageChangedListener} interface.
 * 
 * @see IPageChangedListener
 */
public class PageChangedAdapter implements IPageChangedListener {

	public void pageIndexChanged(int oldPageIndex, int newPageIndex,
			PageableController controller) {
		// Do nothing
	}

	public void totalElementsChanged(long oldTotalElements,
			long newTotalElements, PageableController controller) {
		// Do nothing
	}

	public void sortChanged(String oldPopertyName, String propertyName,
			int oldSortDirection, int sortDirection,
			PageableController controller) {
		// Do nothing
	}

	public void pageSizeChanged(int oldPageSize, int newPageSize,
			PageableController paginationController) {
		// Do nothing
	}

	public void localeChanged(Locale oldLocale, Locale newLocale,
			PageableController paginationController) {
		// Do nothing
	}

}
