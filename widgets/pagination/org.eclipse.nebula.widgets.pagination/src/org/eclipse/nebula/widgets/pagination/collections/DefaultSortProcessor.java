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

import java.util.Collections;
import java.util.List;

/**
 * Default implementation of sort.
 * 
 */
public class DefaultSortProcessor implements SortProcessor {

	private static final SortProcessor INSTANCE = new DefaultSortProcessor();

	public static SortProcessor getInstance() {
		return INSTANCE;
	}

	protected DefaultSortProcessor() {

	}

	public void sort(List<?> list, String sortPropertyName, int sortDirection) {
		Collections.sort(list, new BeanComparator(sortPropertyName,
				sortDirection));
	}
}
