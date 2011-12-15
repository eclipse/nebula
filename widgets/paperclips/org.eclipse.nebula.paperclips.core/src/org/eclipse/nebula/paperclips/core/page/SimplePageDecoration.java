/*
 * Copyright (c) 2006 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */
package org.eclipse.nebula.paperclips.core.page;

import org.eclipse.nebula.paperclips.core.Print;
import org.eclipse.nebula.paperclips.core.internal.util.Util;

/**
 * A PageDecoration which displays the same decoration on every page (ignoring
 * the page number).
 * <p>
 * Typically the page number will be in either the header or footer, but not in
 * both. Often the page number is the only thing that changes from page to page
 * in a header. Use this class for a header or footer which does not display the
 * page number.
 * 
 * @author Matthew Hall
 */
public class SimplePageDecoration implements PageDecoration {
	private final Print print;

	/**
	 * Constructs a BasicPageDecoration.
	 * 
	 * @param print
	 *            the decoration which will appear on every page.
	 */
	public SimplePageDecoration(Print print) {
		Util.notNull(print);
		this.print = print;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((print == null) ? 0 : print.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimplePageDecoration other = (SimplePageDecoration) obj;
		if (print == null) {
			if (other.print != null)
				return false;
		} else if (!print.equals(other.print))
			return false;
		return true;
	}

	public Print createPrint(PageNumber pageNumber) {
		return print;
	}
}
