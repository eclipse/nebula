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

/**
 * 
 * Page result used to store pagination result information :
 * 
 * <ul>
 * <li>the total elements</li>
 * <li>the paginated list</li>
 * </ul>
 * 
 * @param <T>
 *            the type item of the paginated list.
 */
public class PageResult<T> {

	private final List<T> content;
	private final long totalElements;

	/**
	 * Constructor with the given paginated list and total elements.
	 * 
	 * @param content
	 * @param totalElements
	 */
	public PageResult(List<T> content, long totalElements) {
		this.totalElements = totalElements;
		this.content = content;
	}

	/**
	 * Returns the total amount of elements.
	 * 
	 * @return the total amount of elements
	 */
	public long getTotalElements() {
		return totalElements;
	}

	/**
	 * Returns the page content as {@link List}.
	 * 
	 * @return
	 */
	public List<T> getContent() {
		return content;
	}
}
