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

import org.eclipse.nebula.widgets.pagination.IPageLoader;
import org.eclipse.nebula.widgets.pagination.PageableController;

/**
 * Implementation of {@link IPageLoader} with java {@link List}.
 * 
 */
public class PageLoaderList implements IPageLoader<PageResult<?>> {

	private List<?> items;

	public PageLoaderList(List<?> items) {
		this.items = items;
	}

	public void setItems(List<?> items) {
		this.items = items;
	}

	public List<?> getItems() {
		return items;
	}

	public PageResult<?> loadPage(PageableController controller) {
		return PageListHelper.createPage(items, controller);
	}

}
