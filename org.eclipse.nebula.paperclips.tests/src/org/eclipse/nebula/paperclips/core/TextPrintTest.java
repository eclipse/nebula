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

import org.eclipse.nebula.paperclips.core.text.TextPrint;
import org.eclipse.nebula.paperclips.core.text.TextStyle;
import org.eclipse.swt.SWT;

public class TextPrintTest extends TestCase {
	public void testEquals() {
		TextPrint text1 = new TextPrint("text");
		TextPrint text2 = new TextPrint("text");
		assertEquals(text1, text2);

		text1.setStyle(new TextStyle().align(SWT.CENTER));
		assertFalse(text1.equals(text2));
		text2.setStyle(new TextStyle().align(SWT.CENTER));
		assertEquals(text1, text2);

		text1.setWordSplitting(false);
		assertFalse(text1.equals(text2));
		text2.setWordSplitting(false);
		assertEquals(text1, text2);
	}
}
