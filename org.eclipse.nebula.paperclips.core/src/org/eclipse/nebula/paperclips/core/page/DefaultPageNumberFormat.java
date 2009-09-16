/*
 * Copyright (c) 2006-2008 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */
package org.eclipse.nebula.paperclips.core.page;

import java.text.MessageFormat;

import org.eclipse.nebula.paperclips.core.Messages;
import org.eclipse.nebula.paperclips.core.internal.util.Util;

/**
 * The default PageNumberFormat used by PageNumberPrints.
 * <p>
 * This class formats page numbers as "Page x of y".
 * 
 * @author Matthew Hall
 */
public final class DefaultPageNumberFormat implements PageNumberFormat {
	private static MessageFormat messageFormat = new MessageFormat(Messages
			.getString(Messages.PAGE_X_OF_Y));

	public String format(PageNumber pageNumber) {
		return messageFormat.format(new Object[] {
				new Integer(pageNumber.getPageNumber() + 1),
				new Integer(pageNumber.getPageCount()) });
	}

	public boolean equals(Object obj) {
		return Util.sameClass(this, obj);
	}

	public int hashCode() {
		return 47 * 41;
	}
}