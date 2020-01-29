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
 * Interface for formatting a PageNumber instance into a printable string.
 * 
 * @author Matthew Hall
 */
public interface PageNumberFormat {
	/**
	 * Returns a formatted String representing the pageNumber argument.
	 * 
	 * @param pageNumber
	 *            the page number to be formatted into a String.
	 * @return a formatted String representing the pageNumber argument.
	 */
	public String format(PageNumber pageNumber);
}
