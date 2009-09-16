/*
 * Copyright (c) 2005 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
