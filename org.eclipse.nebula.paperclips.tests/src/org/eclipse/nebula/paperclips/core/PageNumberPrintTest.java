/*
 * Copyright (c) 2007 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */
package org.eclipse.nebula.paperclips.core;

import junit.framework.TestCase;

import org.eclipse.nebula.paperclips.core.internal.util.Util;
import org.eclipse.nebula.paperclips.core.page.PageNumber;
import org.eclipse.nebula.paperclips.core.page.PageNumberPrint;
import org.eclipse.nebula.paperclips.core.text.TextStyle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

public class PageNumberPrintTest extends TestCase {
	public void testConstructor_illegalArguments() {
		try {
			new PageNumberPrint(null);
			fail();
		} catch (IllegalArgumentException expected) {
		}

		try {
			new PageNumberPrint(new PageNumberStub(0), (TextStyle) null);
			fail();
		} catch (IllegalArgumentException expected) {
		}

		assertEquals(SWT.LEFT, new PageNumberPrint(new PageNumberStub(0), 0)
				.getAlign());
	}

	public void testEquals() {
		PageNumberPrint pageNumber1 = new PageNumberPrint(new PageNumberStub(0));
		PageNumberPrint pageNumber2 = new PageNumberPrint(new PageNumberStub(0));
		assertEquals(pageNumber1, pageNumber2);

		pageNumber1.setAlign(SWT.CENTER);
		assertFalse(pageNumber1.equals(pageNumber2));
		pageNumber2.setAlign(SWT.CENTER);
		assertEquals(pageNumber1, pageNumber2);

		pageNumber1.setFontData(new FontData("Arial", 12, SWT.BOLD));
		assertFalse(pageNumber1.equals(pageNumber2));
		pageNumber2.setFontData(new FontData("Arial", 12, SWT.BOLD));
		assertEquals(pageNumber1, pageNumber2);

		pageNumber1.setPageNumber(new PageNumberStub(1));
		assertFalse(pageNumber1.equals(pageNumber2));
		pageNumber2.setPageNumber(new PageNumberStub(1));
		assertEquals(pageNumber1, pageNumber2);

		pageNumber1.setPageNumberFormat(new PageNumberFormatStub());
		assertFalse(pageNumber1.equals(pageNumber2));
		pageNumber2.setPageNumberFormat(new PageNumberFormatStub());
		assertEquals(pageNumber1, pageNumber2);
	}

	static class PageNumberStub implements PageNumber {
		private int id;

		public PageNumberStub(int id) {
			this.id = id;
		}

		public boolean equals(Object obj) {
			if (!Util.sameClass(this, obj))
				return false;

			PageNumberStub that = (PageNumberStub) obj;
			return this.id == that.id;
		}

		public int getPageCount() {
			return 0;
		}

		public int getPageNumber() {
			return 0;
		}
	}
}
