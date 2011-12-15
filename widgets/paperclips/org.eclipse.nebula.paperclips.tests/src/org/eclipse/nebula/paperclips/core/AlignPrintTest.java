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

import org.eclipse.nebula.paperclips.core.AlignPrint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;

public class AlignPrintTest extends TestCase {
	public void testConstructor_nullArgument() {
		try {
			new AlignPrint(null, SWT.DEFAULT, SWT.DEFAULT);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException expected) {
		}
	}

	public void testConstructor_applyDefaultAlignments() {
		AlignPrint print = new AlignPrint(new PrintStub(), SWT.DEFAULT,
				SWT.DEFAULT);
		Point alignment = print.getAlignment();
		assertEquals(SWT.LEFT, alignment.x);
		assertEquals(SWT.TOP, alignment.y);
	}

	public void testEquals() {
		AlignPrint print = new AlignPrint(new PrintStub(0), SWT.CENTER,
				SWT.BOTTOM);
		assertEquals(print, new AlignPrint(new PrintStub(0), SWT.CENTER,
				SWT.BOTTOM));
		assertFalse(print.equals(new AlignPrint(new PrintStub(1), SWT.DEFAULT,
				SWT.DEFAULT)));
		assertFalse(print.equals(new AlignPrint(new PrintStub(0), SWT.CENTER,
				SWT.DEFAULT)));
		assertFalse(print.equals(new AlignPrint(new PrintStub(0), SWT.DEFAULT,
				SWT.CENTER)));
	}
}
