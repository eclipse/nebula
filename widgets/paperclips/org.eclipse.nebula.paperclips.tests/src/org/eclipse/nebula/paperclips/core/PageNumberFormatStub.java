/*
 * Copyright (c) 2007 Matthew Hall and others.
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
package org.eclipse.nebula.paperclips.core;

import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.nebula.paperclips.core.page.PageNumber;
import org.eclipse.nebula.paperclips.core.page.PageNumberFormat;

@SuppressWarnings("restriction")
class PageNumberFormatStub implements PageNumberFormat {
	@Override
	public boolean equals(Object obj) {
		return Util.sameClass(this, obj);
	}

	public String format(PageNumber pageNumber) {
		return null;
	}
}