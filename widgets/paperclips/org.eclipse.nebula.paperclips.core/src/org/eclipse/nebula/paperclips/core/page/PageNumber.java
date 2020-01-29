/*
 * Copyright (c) 2005 Matthew Hall and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */
package org.eclipse.nebula.paperclips.core.page;

/**
 * Instances of this class represent a page index in the output of a PagePrint.
 * 
 * @author Matthew Hall
 */
public interface PageNumber {
	/**
	 * Returns the zero-based page index.
	 * 
	 * @return the zero-based page index.
	 */
	public int getPageNumber();

	/**
	 * Returns the total number of pages. Note that this method may not return
	 * an accurate value until all pages have been laid out. Therefore this
	 * method should not be used inside
	 * {@link PageDecoration#createPrint(PageNumber)}.
	 * 
	 * @return the total number of pages.
	 */
	public int getPageCount();
}
