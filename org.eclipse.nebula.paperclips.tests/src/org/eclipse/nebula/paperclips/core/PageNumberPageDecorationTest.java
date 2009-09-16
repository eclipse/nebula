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

import org.eclipse.nebula.paperclips.core.page.PageNumberPageDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

public class PageNumberPageDecorationTest extends TestCase {
	public void testEquals() {
		PageNumberPageDecoration decoration1 = new PageNumberPageDecoration();
		PageNumberPageDecoration decoration2 = new PageNumberPageDecoration();
		assertEquals(decoration1, decoration2);

		decoration1.setRGB(new RGB(127, 127, 127));
		assertFalse(decoration1.equals(decoration2));
		decoration2.setRGB(new RGB(127, 127, 127));
		assertEquals(decoration1, decoration2);

		decoration1.setAlign(SWT.CENTER);
		assertFalse(decoration1.equals(decoration2));
		decoration2.setAlign(SWT.CENTER);
		assertEquals(decoration1, decoration2);

		decoration1.setFontData(new FontData("Arial", 12, SWT.BOLD));
		assertFalse(decoration1.equals(decoration2));
		decoration2.setFontData(new FontData("Arial", 12, SWT.BOLD));
		assertEquals(decoration1, decoration2);

		decoration1.setFormat(new PageNumberFormatStub());
		assertFalse(decoration1.equals(decoration2));
		decoration2.setFormat(new PageNumberFormatStub());
		assertEquals(decoration1, decoration2);
	}
}
